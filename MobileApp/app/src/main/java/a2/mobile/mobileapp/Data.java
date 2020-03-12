package a2.mobile.mobileapp;

import android.content.Context;
import android.content.res.AssetManager;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.FileAsyncHttpResponseHandler;

import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;
import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.WorkbookSettings;

public class Data {
    private Context context;

    private Point startPoint;
    private DestinationPoint targetDestination;
    private List<PointOfInterest> pointsOfInterest;

    private File fetchedFile;

    Data(Context context) {
        this.context = context;

        this.startPoint = new Point();
        this.targetDestination = new DestinationPoint();
        this.pointsOfInterest = new ArrayList<>();
    }

    /**
     * Get a reference to the start point of the navigation process.
     * @return The start point
     */
    public Point getStartPoint() {
        return this.startPoint;
    }

    /**
     * Get a reference to the target destination.
     * @return The target destination
     */
    public DestinationPoint getTargetDestination() {
        return this.targetDestination;
    }

    /**
     * Get a reference to the points of interest.
     * @return The points of interest
     */
    public List<PointOfInterest> getPointsOfInterest() {
        return this.pointsOfInterest;
    }

    /**
     * Get a reference to the external file.
     * @return The fetched file
     */
    public File getFetchedFile() {
        return this.fetchedFile;
    }

    /**
     * Access a local data file based on the provided filename.
     * @return The found file
     */
    public File getDataFile(String filename) {
        File convertedFile = null;

        try {
            AssetManager assetManager = this.context.getAssets();
            InputStream inputStream = assetManager.open(filename);

            convertedFile = inputStreamToFile(inputStream);
            storeData(convertedFile);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return convertedFile;
    }

    /**
     * Get en excel data file from an external storage device (online).
     * @param url The URL from which to fetch the desired file
     * @return The fetched file
     */
    public File fetchDataFile(String url) {
        AsyncHttpClient client = new AsyncHttpClient();
        client.get(url, new FileAsyncHttpResponseHandler(this.context) {

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, File file) {
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, File file) {
                storeData(file);
            }
        });

        return this.fetchedFile;
    }

    /**
     * Store the data extracted from the provided file.
     * @param file The file from which the data will be extracted
     */
    private void storeData(File file) {
        WorkbookSettings workbookSettings = new WorkbookSettings();
        workbookSettings.setGCDisabled(true);

        if (file != null) {
            try {
                Workbook workbook = Workbook.getWorkbook(file);
                Sheet sheet = workbook.getSheet(0);

                for (int index = 0; index < sheet.getRows(); index += 1) {
                    Cell[] row = sheet.getRow(index);

                    storePointOfInterest(row);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            this.fetchedFile = file;
        }
    }

    /**
     * Process points of interest and add it to the list of points of interests.
     * @param row The row from which to process the data
     * @return The newly created `PointOfInterest` instance
     */
    private PointOfInterest storePointOfInterest(Cell[] row) {
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
        pointsOfInterest.add(pointOfInterest);

        return pointOfInterest;
    }

    /**
     * Convert InputStream variables to File ones.
     * @param inputStream The variable that will be converted to a File
     * @return The converted File variable
     */
    private File inputStreamToFile(InputStream inputStream) {
        File convertedFile = null;

        try {
            convertedFile = File.createTempFile("prefix", "suffix");
            convertedFile.deleteOnExit();

            FileOutputStream fileOutputStream = new FileOutputStream(convertedFile);
            IOUtils.copy(inputStream, fileOutputStream);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return convertedFile;
    }
}
