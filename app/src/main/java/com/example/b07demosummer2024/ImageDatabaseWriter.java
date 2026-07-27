package com.example.b07demosummer2024;

import android.net.Uri;

import java.util.concurrent.CompletableFuture;

/**
 * Allows for writing to the supabase image db.
 */
public class ImageDatabaseWriter{

    public interface UrlCallback {
        void getUrl(String url);
    }

    /**
     *
     * @param imageUploader A SupabaseImageUploader that must be created when the URI is obtained with SupabaseImageUploader imageUploader(getContext());
     * @param uri The URI of the image to add
     * @param LOT The LOT of the Artifact
     * @param urlCallback A function that will be cllaed with the url string as input
     */
    void addToDatabase(SupabaseImageUploader imageUploader, Uri uri, String LOT, UrlCallback urlCallback) {
            imageUploader.uploadImage(uri, LOT, new SupabaseImageUploader.UploadCallback() {
                        @Override
                        public void onSuccess(String publicUrl) {
                            if (urlCallback != null) {
                                urlCallback.getUrl(publicUrl);
                            }
                        }

                        @Override
                        public void onError(String message) {
                            if (urlCallback != null) {
                                urlCallback.getUrl(null);
                            }
                        }
                    });
    }
}
