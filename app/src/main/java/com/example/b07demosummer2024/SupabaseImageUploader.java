package com.example.b07demosummer2024;

import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.webkit.MimeTypeMap;

import androidx.annotation.NonNull;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Uploads images to the Supabase using the URI of an image
 */
public class SupabaseImageUploader {

    /**
     * Callback used on upload
     */
    public interface UploadCallback {
        /**
         * On a successful upload callback with url
         * @param publicUrl of image
         */
        void onSuccess(String publicUrl);
        /**
         * On a failed upload callback with url
         * @param message of error
         */
        void onError(String message);
    }
    /**
     * Callback used on delete
     */
    public interface DeleteCallback {
        void onSuccess();
        void onError(String message);
    }

    private static final int MAX_IMAGE_BYTES = 10 * 1024 * 1024; // 10MB

    private final Context appContext;
    private final OkHttpClient client = new OkHttpClient();
    private final Handler mainHandler = new Handler(Looper.getMainLooper());
    private final String supabaseUrl;
    private final String supabaseAnonKey;
    private final String bucketName;

    /**
     * Creates a SupabaseImageUploader, and syncs url and anon key
     * @param context of android app for R.String constants
     */
    public SupabaseImageUploader(Context context) {
        appContext = context.getApplicationContext();
        supabaseUrl = appContext.getString(R.string.supabase_url).trim();
        supabaseAnonKey = appContext.getString(R.string.supabase_anon_key).trim();
        bucketName = appContext.getString(R.string.supabase_image_bucket).trim();
    }

    /**
     * Uploads an image to the supabase, and runs a callback on completion
     * @param imageUri of image to upload
     * @param lotNumber of artifact (for unique identification of url)
     * @param callback method on completion following UploadCallback
     */
    public void uploadImage(Uri imageUri, String lotNumber, UploadCallback callback) {
        // Make sure the Supabase project URL, anon key, and bucket name were provided
        if (isBlank(supabaseUrl) || isBlank(supabaseAnonKey) || isBlank(bucketName)) {
            callback.onError("Image uploader not configured with URL, anon key, and bucket name");
            return;
        }

        // Get the selected file's MIME type, such as image/jpeg or image/png
        String mimeType = appContext.getContentResolver().getType(imageUri);
        if (isBlank(mimeType)) {
            mimeType = "";
        }
        if (!mimeType.startsWith("image/")) {
            callback.onError("Please select an image file.");
            return;
        }

        // Get the actual extension to append to the URL, such as jpg or png
        String extension = MimeTypeMap.getSingleton().getExtensionFromMimeType(mimeType);
        if (isBlank(extension)) {
            callback.onError("Unsupported image type.");
            return;
        }

        // Read image Uri into bytes for the upload request body
        byte[] imageBytes;
        try {
            imageBytes = readBytes(imageUri);
        } catch (IOException e) {
            callback.onError(e.getMessage() == null ? "Could not read selected image." : e.getMessage());
            return;
        }

        // Image file path, like artifacts/LOT123/1719000000000.jpg
        String filePath = buildFilePath(lotNumber, extension);

        // Private Storage API URL used for uploading the file
        HttpUrl uploadUrl = buildStorageUrl("storage/v1/object", filePath);
        if (uploadUrl == null) {
            callback.onError("Supabase URL is invalid.");
            return;
        }

        RequestBody requestBody = RequestBody.create(imageBytes, MediaType.parse(mimeType));
        Request request = new Request.Builder()
                .url(uploadUrl)
                .addHeader("apikey", supabaseAnonKey)
                .addHeader("Authorization", "Bearer " + supabaseAnonKey)
                .post(requestBody)
                .build();

        // Send the upload request async
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                // Network failure
                postError(callback, "Image upload failed: " + e.getMessage());
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) {
                try {
                    if (response.isSuccessful()) {
                        // Public URL that can be stored in Firebase and loaded with Glide
                        HttpUrl publicUrl = buildStorageUrl("storage/v1/object/public", filePath);
                        if (publicUrl == null) {
                            postError(callback, "Could not build image URL.");
                        } else {
                            postSuccess(callback, publicUrl.toString());
                        }
                    } else {
                        postError(callback, "Image upload failed with status " + response.code() + ".");
                    }
                } finally {
                    response.close();
                }
            }
        });
    }
    public void deleteImage(String publicUrl, DeleteCallback callback) {
        String filePath = extractFilePath(publicUrl);
        if (isBlank(filePath)) {
            callback.onError("Invalid image URL.");
            return;
        }
        HttpUrl deleteUrl = buildStorageUrl("storage/v1/object", filePath);
        if (deleteUrl == null) {
            callback.onError("Supabase URL is invalid.");
            return;
        }
        Request request = new Request.Builder()
                .url(deleteUrl)
                .addHeader("apikey", supabaseAnonKey)
                .addHeader(
                        "Authorization",
                        "Bearer " + supabaseAnonKey
                )
                .delete()
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                postDeleteError(callback, "Image deletion failed: " + e.getMessage());
            }
            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) {
                try {
                    if (response.isSuccessful()) {
                        postDeleteSuccess(callback);
                    }
                    else {
                        postDeleteError(callback, "Image deletion failed with status " + response.code() + ".");
                    }
                } finally {
                    response.close();
                }
            }
        });
    }
    private String extractFilePath(String publicUrl) {
        HttpUrl url = HttpUrl.parse(publicUrl);
        if (url == null) {
            return null;
        }
        String prefix = "/storage/v1/object/public/" + bucketName + "/";
        String path = url.encodedPath();
        if (!path.startsWith(prefix)) {
            return null;
        }
        return path.substring(prefix.length());
    }

    /**
     * Reads the bytes of the image and puts it into an array of bytes
     * @param imageUri to read the image bytes
     * @return image represented as bytes
     * @throws IOException on bad image stream
     */
    private byte[] readBytes(Uri imageUri) throws IOException {
        ContentResolver resolver = appContext.getContentResolver();
        try (InputStream inputStream = resolver.openInputStream(imageUri);
             ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            if (inputStream == null) {
                throw new IOException("No image stream.");
            }
            byte[] buffer = new byte[8192];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
                if (outputStream.size() > MAX_IMAGE_BYTES) {
                    throw new IOException("Image is larger than 10 MB.");
                }
            }
            return outputStream.toByteArray();
        }
    }

    /**
     * Creates the unique filepath for an image
     * @param lotNumber of artifact
     * @param extension of image type
     * @return A unique file path
     */
    private String buildFilePath(String lotNumber, String extension) {
        String safeLotNumber = lotNumber.replaceAll("[^A-Za-z0-9_-]", "_");
        return "artifacts/" + safeLotNumber + "/" + System.currentTimeMillis() + "." + extension;
    }

    /**
     * Creates the URL to the supabase storage
     * @param storagePath path to the storage
     * @param filePath part to the file
     * @return an HttpUrl of the storage
     */
    private HttpUrl buildStorageUrl(String storagePath, String filePath) {
        HttpUrl baseUrl = HttpUrl.parse(supabaseUrl);
        if (baseUrl == null) {
            return null;
        }
        return baseUrl.newBuilder()
                .addPathSegments(storagePath)
                .addPathSegment(bucketName)
                .addPathSegments(filePath)
                .build();
    }

    /**
     * On success, run onSuccess callback
     * @param callback to run
     * @param publicUrl to use as callback argument
     */
    private void postSuccess(UploadCallback callback, String publicUrl) {
        mainHandler.post(() -> callback.onSuccess(publicUrl));
    }

    /**
     * On failure, run onFailure callback
     * @param callback to run
     * @param message the error message to use as callback argument
     */
    private void postError(UploadCallback callback, String message) {
        mainHandler.post(() -> callback.onError(message));
    }
    private void postDeleteSuccess(DeleteCallback callback) {
        mainHandler.post(() -> callback.onSuccess());
    }

    private void postDeleteError(DeleteCallback callback, String message) {
        mainHandler.post(() -> callback.onError(message));
    }

    /**
     * Returns if a string is blank or not, accepting null strings.
     * @param value of string
     * @return boolean if the string is blank or not
     */
    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
