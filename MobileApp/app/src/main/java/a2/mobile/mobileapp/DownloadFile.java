package a2.mobile.mobileapp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class DownloadFile {
    private static final String TAG = "Download File";

    private Context context;
    private String url = "";
    private String fileName = "";

    public File downloadedFile;

    public DownloadFile(Context context, String url, String pathWithoutFileName) {
        this.context = context;
        this.url = url;
        this.fileName = this.url.replace(pathWithoutFileName, "");

        new DownloadTask().execute();
    }

    @SuppressLint("StaticFieldLeak")
    private class DownloadTask extends AsyncTask<Void, Void, Void> {
        File apkStorage = null;
        File outputFile = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            // TODO: Notify the user with a progress bar/toast that their download started.
        }

        @Override
        protected void onPostExecute(Void result) {
            try {
                if (downloadedFile != null) {
                    Data.storeData(downloadedFile);
                    // TODO: Notify the user that their download is complete.
                } else {
                    Log.e(TAG, FileConstants.DOWNLOAD_FAILED);
                    // TODO: Notify the user that their download failed.
                }
            } catch (Exception e) {
                e.printStackTrace();

                Log.e(TAG, FileConstants.DOWNLOAD_ERROR + " " + e.getLocalizedMessage());
                // TODO: Notify the user that their download failed.
            }

            super.onPostExecute(result);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                URL fileUrl = new URL(url);

                HttpURLConnection connection = (HttpURLConnection) fileUrl.openConnection();
                connection.setRequestMethod("GET");
                connection.connect();

                // Prevent bad connection from happening.
                int responseCode = connection.getResponseCode();
                if (responseCode != FileConstants.HTTP_OK) {
                    Log.e(TAG, FileConstants.SERVER_RETURNED_HTTP_CODE + " " + responseCode);
                    // TODO: Notify the user that their download failed.
                }

                // If the device has allowed SD Card writing, write the file onto it.
                FileOutputStream outputStream = null;
                CheckForSDCard checkForSDCard = new CheckForSDCard();
                if (checkForSDCard.canWriteOnSDCard()) {
                    if (checkForSDCard.isSDCardPresent()) {
                        apkStorage = new File(Environment.getExternalStorageDirectory() + "/" + FileConstants.DOWNLOAD_FILE_DIRECTORY);
                    } else {
                        Log.e(TAG, FileConstants.NO_SD_CARD_FOUND);
                        // TODO: Notify the user that they don't have an SD Card in their device.
                    }

                    // Get file if SD Card is present.
                    if (!apkStorage.exists()) {
                        apkStorage.mkdirs();
                        Log.e(TAG, FileConstants.DIRECTORY_CREATED);
                        // TODO: Notify the user that a new directory has been created for their file.
                    }

                    outputFile = new File(apkStorage, fileName);

                    // Create a new File if not present.
                    if (!outputFile.exists()) {
                        outputFile.createNewFile();
                        Log.e(TAG, FileConstants.FILE_CREATED);
                    }

                    outputStream = new FileOutputStream(outputFile);
                }

                InputStream inputStream = connection.getInputStream();
                downloadedFile = FileUtils.inputStreamToFile(inputStream);

                // Close all connections after task completion.
                if (outputStream != null) {
                    outputStream.close();
                }

                inputStream.close();
            } catch (Exception e) {
                e.printStackTrace();
                outputFile = null;

                Log.e(TAG, FileConstants.DOWNLOAD_ERROR + " " + e.getMessage());
                // TODO: Notify the user that their download failed.
            }

            return null;
        }

    }
}
