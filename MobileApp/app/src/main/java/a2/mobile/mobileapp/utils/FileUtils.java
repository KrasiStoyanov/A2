package a2.mobile.mobileapp.utils;

import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

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
}
