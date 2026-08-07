package group17_b07summer2026;

import android.net.Uri;

/**
 * Allows for writing to the Supabase image db.
 */
public class ImageDatabaseWriter{

    /**
     * Callback used to return the URL of an uploaded image
     */
    public interface UrlCallback {

        /**
         * Called upon image upload success or failure
         *
         * @param url the URL of the uploaded image, or null if the upload fails
         */
        void getUrl(String url);
    }
    /**
     *
     * @param imageUploader A SupabaseImageUploader that must be created when the URI is obtained with SupabaseImageUploader imageUploader(getContext());
     * @param uri The URI of the image to add
     * @param LOT The LOT of the Artifact
     * @param urlCallback A function that will be called with the url string as input
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
