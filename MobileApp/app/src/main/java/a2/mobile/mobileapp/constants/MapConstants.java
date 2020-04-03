package a2.mobile.mobileapp.constants;

import com.google.android.gms.maps.model.LatLng;

public class MapConstants {
    // Global keys
    public static final String KEY_CAMERA_POSITION = "camera_position";
    public static final String KEY_LOCATION = "location";
    public static final String KEY_API = "AIzaSyD-K7BXFSfMYhL-uS5GrlO7buKRBzHqdCM";

    // Google Maps settings
    public static final LatLng DEFAULT_LOCATION = new LatLng(51.842175, 5.859508);
    public static final int DEFAULT_ZOOM = 15;
    public static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    public static final int MAP_FOCUS_PADDING = 200; // Offset from the edges of the map in pixels.

    public static final String DEFAULT_DIRECTIONS_MODE = "walking";
    public static final String DIRECTIONS_ROUTE_PATH_OBJECT_KEY = "overview_polyline";
    public static final String DIRECTIONS_ROUTE_POINTS_OBJECT_KEY = "points";

    // Location settings
    public static final int INTERVAL_BETWEEN_LOCATION_UPDATE = 200;
    public static final int INTERVAL_FASTEST_BETWEEN_LOCATION_UPDATE = 1000;

    // Directions URL settings
    public static final String DIRECTIONS_URL_PATHNAME = "https://maps.googleapis.com/maps/api/directions/json?";
    public static final String DRIECTIONS_URL_QUERY_ORIGIN = "origin=";
    public static final String DRIECTIONS_URL_QUERY_DESTINATION = "destination=";
    public static final String DRIECTIONS_URL_QUERY_MODE = "mode=";
    public static final String DRIECTIONS_URL_QUERY_KEY = "key=";

    public static final String URL_QUERY_AND_SEPARATOR = "&";
    public static final String URL_QUERY_COMA_SEPERATOR = ",%20";

    public static String generateDirectionsUrl(String originText, String destinationText) {
        String origin = DRIECTIONS_URL_QUERY_ORIGIN + originText;
        String destination = DRIECTIONS_URL_QUERY_DESTINATION + destinationText;
        String mode = DRIECTIONS_URL_QUERY_MODE + DEFAULT_DIRECTIONS_MODE;
        String key = DRIECTIONS_URL_QUERY_KEY + KEY_API;

        return DIRECTIONS_URL_PATHNAME + origin
                + URL_QUERY_AND_SEPARATOR + destination
                + URL_QUERY_AND_SEPARATOR + mode
                + URL_QUERY_AND_SEPARATOR + key;
    }
}
