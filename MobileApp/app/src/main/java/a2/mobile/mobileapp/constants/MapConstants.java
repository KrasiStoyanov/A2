package a2.mobile.mobileapp.constants;

import com.mapbox.mapboxsdk.geometry.LatLng;

public class MapConstants {
    // Global keys
    public static final String KEY_CAMERA_POSITION = "camera_position";
    public static final String KEY_LOCATION = "location";
    public static final String KEY_API = "AIzaSyD-K7BXFSfMYhL-uS5GrlO7buKRBzHqdCM";
    public static final String MAPBOX_API = "pk.eyJ1Ijoia3Jhc2ktc3RveWFub3YiLCJhIjoiY2s4b2QyYmFuMDFtMDNocXA1d2kzY2E1cSJ9.EvNaJHhbCaIQF7UTFhoqWQ";

    // Mapbox Settings
    public static final String MAP_JSON_CHARSET = "UTF-8";
    public static final String MAP_JSON_FIELD_REGION_NAME = "FIELD_REGION_NAME";
    public static final String MAP_NAME = "Nijmegen";

    public static final Double MAP_REGION_MIN_LAT = 51.8064;
    public static final Double MAP_REGION_MAX_LAT = 51.8549;
    public static final Double MAP_REGION_MIN_LON = 5.8183;
    public static final Double MAP_REGION_MAX_LON = 5.8914;

    public static final int MIN_ZOOM = 10;
    public static final int MAX_ZOOM = 22;
    public static final int DEFAULT_ZOOM = MIN_ZOOM;

    // Mapbox symbols settings
    public static final String MAP_ROUTE_LAYER_ID = "Route Layer";
    public static final String MAP_ROUTE_LAYER_SOURCE_ID = "Route Layer Source ID";

    public static final String MAP_ROUTE_MARKER_LAYER_ID = "Route Marker Layer";
    public static final String MAP_ROUTE_MARKER_LAYER_SOURCE_ID = "Route Marker Layer Source ID";
    public static final String MAP_MARKER_ID = "Marker Icon";

    // Google Maps settings
    public static final LatLng DEFAULT_LOCATION = new LatLng(51.842175, 5.859508);
    public static final int MAP_FOCUS_PADDING = 20; // Offset from the edges of the map in pixels.

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
