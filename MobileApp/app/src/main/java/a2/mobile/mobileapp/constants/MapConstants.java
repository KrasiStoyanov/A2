package a2.mobile.mobileapp.constants;

import com.mapbox.mapboxsdk.geometry.LatLng;

public class MapConstants {
    // Global keys
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

    // Location settings
    public static final int INTERVAL_BETWEEN_LOCATION_UPDATE = 200;
    public static final int INTERVAL_FASTEST_BETWEEN_LOCATION_UPDATE = 1000;
}
