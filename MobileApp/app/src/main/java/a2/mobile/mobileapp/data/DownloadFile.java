package a2.mobile.mobileapp.data;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import a2.mobile.mobileapp.activities.MainActivity;
import a2.mobile.mobileapp.common.CheckForSDCard;
import a2.mobile.mobileapp.constants.FileConstants;
import a2.mobile.mobileapp.utils.FileUtils;

public class DownloadFile {
    private static final String TAG = "Download File";
    public File downloadedFile;
    private Context context;
    private String url = "";
    private String fileName = "";

    public DownloadFile(Context context, String url, String pathWithoutFileName) {
        this.context = context;
        this.url = url;
        this.fileName = this.url.replace(pathWithoutFileName, "");

        new DownloadTask().execute();
    }

    @SuppressLint("StaticFieldLeak")
    private class DownloadTask extends AsyncTask<Void, Void, Boolean> {
        File apkStorage = null;
        File outputFile = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            // TODO: Notify the user with a progress bar/toast that their download started.
        }

        @Override
        protected void onPostExecute(Boolean taskComplete) {
            if (taskComplete) {
                if (downloadedFile != null) {
                    Data.storeData(downloadedFile);

                    // TODO: Notify the user that their download is complete.
                } else {
                    Log.e(TAG, FileConstants.DOWNLOAD_FAILED);
                    // TODO: Notify the user that their download failed.
                }
            } else {
                Log.e(TAG, FileConstants.DOWNLOAD_ERROR);
                // TODO: Notify the user that their download failed.
            }

            MainActivity activity = (MainActivity) context;
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            try {
                URL fileUrl = new URL(url);

                // Establish the HTTP File connection.
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

                // Store the downloaded file.
                InputStream inputStream = connection.getInputStream();
                downloadedFile = FileUtils.inputStreamToFile(inputStream);

                // Close all connections after task completion.
                if (outputStream != null) {
                    outputStream.close();
                }

                inputStream.close();

                return true;
            } catch (Exception e) {
                e.printStackTrace();
                outputFile = null;

                Log.e(TAG, FileConstants.DOWNLOAD_ERROR + " " + e.getMessage());
                // TODO: Notify the user that their download failed.

                return false;
            }
        }

    }
}
