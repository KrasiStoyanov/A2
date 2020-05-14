package a2.mobile.mobileapp.data;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import a2.mobile.mobileapp.data.classes.Point;
import a2.mobile.mobileapp.data.classes.PointOfInterest;
import a2.mobile.mobileapp.data.classes.Route;
import a2.mobile.mobileapp.utils.DataUtils;
import a2.mobile.mobileapp.utils.FileUtils;
import a2.mobile.mobileapp.utils.MapUtils;
import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.WorkbookSettings;

public class Data {
    @SuppressLint("StaticFieldLeak")
    public static Context context = null;

    public static Point startPoint = null;
    public static List<PointOfInterest> pointsOfInterest = new ArrayList<>();
    public static List<Route> routes = new ArrayList<>();

    private static File fetchedFile = null;
    public static Route selectedRoute = null;

    /**
     * Access a local data file based on the provided filename.
     *
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
     *
     * @param fileUrl                The URL from which to fetch the desired file
     * @param fileUrlWithoutFileName The file URL without the name and extension of the file
     */
    public static void fetchDataFile(String fileUrl, String fileUrlWithoutFileName) {
        new DownloadFile(Data.context, fileUrl, fileUrlWithoutFileName);
    }

    /**
     * Store the data extracted from the provided file.
     *
     * @param file The file from which the data will be extracted
     */
    static void storeData(File file) {
        if (fetchedFile != null) return;

        WorkbookSettings workbookSettings = new WorkbookSettings();
        workbookSettings.setGCDisabled(true);

        if (file != null && file.exists()) {
            try {
                Workbook workbook = Workbook.getWorkbook(file);
//                Data.storePointsOfInterest(workbook);
                Data.storeRoute(workbook);

                workbook.close();
            } catch (Exception e) {
                e.printStackTrace();
            }

            Data.fetchedFile = file;

            // TODO: Create a UI handler to show output.
        }
    }

    /**
     * Get the desired route from the list of routes using its UUID.
     *
     * @param id the UUID to use when filtering the routes list
     * @return The found route/null
     */
    public static Route getRoute(Object id) {
        for (Route route : routes) {
            if (route.id.equals(id)) {
                return route;
            }
        }

        return null;
    }

    /**
     * Process points of interest and add it to the list of points of interests.
     *
     * @param row The row from which to process the data
     */
    private static PointOfInterest generatePointOfInterest(Cell[] row) {
        String title = row[0].getContents();
        String interest = row[1].getContents();
        String coordinates = row[2].getContents();
        String typeOfBuilding = row[3].getContents();

        List<Integer> locationZones = new ArrayList<>();
        String[] zonesText = row[4].getContents().split(", ");
        for (String zone : zonesText) {
            locationZones.add(Integer.parseInt(zone));
        }

        List<Double> parsedCoordinates = DataUtils.stringToCoordinates(coordinates);

        return new PointOfInterest(
                parsedCoordinates,
                locationZones,
                title,
                interest,
                typeOfBuilding
        );
    }

    private static void storeRoute(Workbook workbook) {
        Sheet routesSheet = workbook.getSheet(0);

        for (int index = 0; index < routesSheet.getRows(); index += 1) {
            Route route = new Route();
            Cell[] row = routesSheet.getRow(index);

            route.title = row[0].getContents();
            route.startPoint = generateRoutePoints(row, 1);
            route.endPoint = generateRoutePoints(row, 3);
            route.zone = Integer.parseInt(row[5].getContents());

            route.pointsOfInterest = new ArrayList<>();
            for (PointOfInterest pointOfInterest : pointsOfInterest) {
                if (pointOfInterest.getLocationZones().indexOf(route.zone) > -1) {
                    route.pointsOfInterest.add(pointOfInterest);
                }
            }

            Data.routes.add(route);
        }
    }

    private static Point generateRoutePoints(Cell[] row, int coordinatesIndex) {
        String coordinates = row[coordinatesIndex].getContents();
        String title = row[coordinatesIndex + 1].getContents();

        return new Point(
                DataUtils.stringToCoordinates(coordinates),
                title
        );
    }

    public static void storePointsOfInterestDataSet(String dataSetJSONString) {
        try {
            JSONObject dataSetJSON = new JSONObject(dataSetJSONString);
            JSONArray featuresArray = dataSetJSON.getJSONArray("features");

            for (int index = 0; index < featuresArray.length(); index += 1) {
                JSONObject feature = featuresArray.getJSONObject(index);
                JSONObject properties = feature.getJSONObject("properties");
                JSONArray coordinates = feature.getJSONObject("geometry")
                        .getJSONArray("coordinates");

                String title = properties.getString("title");
                String interest = properties.getString("interest");

                List<Integer> locationZones = new ArrayList<>();
                locationZones.add(properties.getInt("zone"));

                List<Double> coordinatesList = new ArrayList<>();
                coordinatesList.add(coordinates.getDouble(0));
                coordinatesList.add(coordinates.getDouble(1));

                pointsOfInterest.add(new PointOfInterest(
                        coordinatesList,
                        locationZones,
                        title,
                        interest,
                        ""
                ));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
