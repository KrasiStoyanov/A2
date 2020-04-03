package a2.mobile.mobileapp.handlers;

import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.PolyUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import a2.mobile.mobileapp.R;
import a2.mobile.mobileapp.activities.MainActivity;
import a2.mobile.mobileapp.constants.MapConstants;
import a2.mobile.mobileapp.data.Data;
import a2.mobile.mobileapp.data.classes.Point;
import a2.mobile.mobileapp.data.classes.Route;

public class MapHandler {
    public static JSONObject currentRouteObject;
    public static String currentRouteDistance;

    /**
     * Set up the Direction API URL and render the outcome - a JSON object with the route path.
     *
     */
    public static void setupRouteDirectionsAPI() {
        // Initialize a new RequestQueue instance
        RequestQueue requestQueue = Volley.newRequestQueue(Data.context);

        String url = MapHandler.generateUrl();
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, response -> {

                    try {
                        //routes element
                        JSONArray routes = response.getJSONArray("routes");
                        JSONObject routesJSONObject = routes.getJSONObject(0);

                        //Legs element
                        JSONArray leg = routesJSONObject.getJSONArray("legs");
                        JSONObject distance = leg.getJSONObject(0).getJSONObject("distance");
                        currentRouteDistance = distance.getString("text");
                        Log.e("distance",currentRouteDistance);
                        MapHandler.renderRoutePath(routesJSONObject);
                        currentRouteObject = routesJSONObject;
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }, error -> Log.d("Route Details Handler", "error"));

        requestQueue.add(jsonObjectRequest);
    }

    /**
     * Render the route path based on the provided JSON Object.
     *
     * @param routeObject The JSON Object to get the path from
     */
    public static void renderRoutePath(JSONObject routeObject) {
        Iterator<String> keys = routeObject.keys();

        while (keys.hasNext()) {
            String key = keys.next();

            if (key.equals(MapConstants.DIRECTIONS_ROUTE_PATH_OBJECT_KEY)) {
                JSONObject polyline;
                String points = "";

                try {
                    polyline = routeObject.getJSONObject(key);
                    points = polyline.getString(MapConstants.DIRECTIONS_ROUTE_POINTS_OBJECT_KEY);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                List<LatLng> latLngList = new ArrayList<>();
                latLngList.addAll(PolyUtil.decode(points.trim().replace(
                        "\\\\",
                        "\\"
                )));

                MainActivity.map.addPolyline(new PolylineOptions()
                        .color(R.color.primary)
                        .width(20f)
                        .clickable(false)
                        .addAll(latLngList)
                );

                break;
            }
        }
    }

    /**
     * Generate the Directions API URL based on the start and end point of the selected route.
     *
     * @return The generated URL
     */
    public static String generateUrl() {
        StringBuilder startPointString = new StringBuilder();
        StringBuilder endPointString = new StringBuilder();

        List<Double> startPointCoordinates = Data.selectedRoute.startPoint.coordinates;
        List<Double> endPointCoordinates = Data.selectedRoute.endPoint.coordinates;

        for (int index = 0; index < startPointCoordinates.size(); index += 1) {
            String startCoordinate = startPointCoordinates.get(index).toString();
            String endCoordinate = endPointCoordinates.get(index).toString();

            startPointString.append(startCoordinate);
            endPointString.append(endCoordinate);

            if (index == 0) {
                startPointString.append(MapConstants.URL_QUERY_COMA_SEPERATOR);
                endPointString.append(MapConstants.URL_QUERY_COMA_SEPERATOR);
            }
        }

        return MapConstants.generateDirectionsUrl(
                startPointString.toString(),
                endPointString.toString()
        );
    }

    /**
     * Focus the Google Map on the route's start and end point area.
     */
    public static void focusMapOnRoute() {
        MainActivity.map.clear();

        Route route = Data.selectedRoute;
        LatLngBounds.Builder builder = new LatLngBounds.Builder();

        // Move GoogleMaps camera to the route area.
        MarkerOptions startMarker = generateRouteMarker(route.startPoint);
        builder.include(startMarker.getPosition());

        MainActivity.map.addMarker(startMarker);

        MarkerOptions endMarker = generateRouteMarker(route.endPoint);
        builder.include(endMarker.getPosition());

        MainActivity.map.addMarker(endMarker);

        LatLngBounds latLngBounds = builder.build();
        CameraUpdate cameraUpdate = CameraUpdateFactory
                .newLatLngBounds(latLngBounds, MapConstants.MAP_FOCUS_PADDING);

        MainActivity.map.animateCamera(cameraUpdate);
    }

    /**
     * Generate a route marker and add it to the Google Map.
     *
     * @param point the current point that holds the coordinates
     */
    private static MarkerOptions generateRouteMarker(Point point) {
        List<Double> coordinates = point.coordinates;
        String title = point.title;
        String interest = point.interest;

        // Create a new instance of a marker based on the coordinates from the point of interest.
        LatLng coordinatesMarker = new LatLng(coordinates.get(0), coordinates.get(1));
        MarkerOptions marker = new MarkerOptions();

        // Settings for the marker.
        marker.position(coordinatesMarker);
        marker.title(title);
        marker.snippet(interest);

        return marker;
    }
}