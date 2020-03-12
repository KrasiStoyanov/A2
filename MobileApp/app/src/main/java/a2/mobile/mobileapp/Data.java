package a2.mobile.mobileapp;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.FileAsyncHttpResponseHandler;

import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.HttpConnection;
import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.WorkbookSettings;

public class Data {
    public static Context context = null;

    public static Point startPoint = null;
    public static DestinationPoint targetDestination = null;
    public static List<PointOfInterest> pointsOfInterest = new ArrayList<>();

    public static File fetchedFile = null;

    /**
     * Access a local data file based on the provided filename.
     * @return The found file
     */
    public static File getDataFile(String filename) {
        File convertedFile = null;

        try {
            AssetManager assetManager = Data.context.getAssets();
            InputStream inputStream = assetManager.open(filename);

            convertedFile = FileUtils.inputStreamToFile(inputStream);
            Data.storeData(convertedFile);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return convertedFile;
    }

    /**
     * Get en excel data file from an external storage device (online).
     * @param fileUrl The URL from which to fetch the desired file
     * @param fileUrlWithoutFileName The file URL without the name and extension of the file
     */
    public static void fetchDataFile(String fileUrl, String fileUrlWithoutFileName) {
        new DownloadFile(Data.context, fileUrl, fileUrlWithoutFileName);
    }

    /**
     * Store the data extracted from the provided file.
     * @param file The file from which the data will be extracted
     */
    public static void storeData(File file) {
        WorkbookSettings workbookSettings = new WorkbookSettings();
        workbookSettings.setGCDisabled(true);

        if (file != null && file.exists()) {
            try {
                Workbook workbook = Workbook.getWorkbook(file);
                Sheet sheet = workbook.getSheet(0);

                for (int index = 0; index < sheet.getRows(); index += 1) {
                    Cell[] row = sheet.getRow(index);

                    Data.storePointOfInterest(row);
                }

                workbook.close();
            } catch (Exception e) {
                e.printStackTrace();
            }

            Data.fetchedFile = file;

            // TODO: Create a UI handler to show output.
        }
    }

    /**
     * Process points of interest and add it to the list of points of interests.
     * @param row The row from which to process the data
     * @return The newly created `PointOfInterest` instance
     */
    private static PointOfInterest storePointOfInterest(Cell[] row) {
        String coordinates = row[0].getContents();
        String title = row[1].getContents();
        String interest = row[2].getContents();

        String[] splitCoordinates = coordinates.split(", ");
        List<Double> parsedCoordinates = new ArrayList<>();

        for (String splitCoordinate : splitCoordinates) {
            Double coordinate = Double.parseDouble(splitCoordinate);
            parsedCoordinates.add(coordinate);
        }

        PointOfInterest pointOfInterest = new PointOfInterest(parsedCoordinates, title, interest);
        Data.pointsOfInterest.add(pointOfInterest);

        return pointOfInterest;
    }
}
