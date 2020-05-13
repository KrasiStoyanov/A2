package a2.mobile.mobileapp.utils;

import android.content.Context;

import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class FileUtils {
    /**
     * Convert InputStream variables to File ones.
     *
     * @param inputStream The variable that will be converted to a File
     * @return The converted File variable
     */
    public static File inputStreamToFile(InputStream inputStream) {
        File convertedFile = null;

        try {
            convertedFile = File.createTempFile("prefix", "suffix");
            convertedFile.deleteOnExit();

            FileOutputStream fileOutputStream = new FileOutputStream(convertedFile);
            IOUtils.copy(inputStream, fileOutputStream);

            fileOutputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return convertedFile;
    }

    /**
     * Get the contents of a local JSON file.
     *
     * @param context  The current context
     * @param filename The name of the file
     * @return The file's content in String format.
     */
    static String loadJSONFromAsset(Context context, String filename) {
        String json;

        try {
            InputStream inputStream = context.getAssets().open(filename);
            int size = inputStream.available();
            byte[] buffer = new byte[size];

            inputStream.read(buffer);
            inputStream.close();

            json = new String(buffer, StandardCharsets.UTF_8);
        } catch (IOException ex) {
            ex.printStackTrace();

            return null;
        }

        return json;
    }
}
