package a2.mobile.mobileapp;

import com.google.android.gms.maps.model.LatLng;

public class MapConstants {
    // Global keys
    public static final String KEY_CAMERA_POSITION = "camera_position";
    public static final String KEY_LOCATION = "location";

    // Google Maps settings
    public static final LatLng DEFAULT_LOCATION = new LatLng(53.213240, 6.562364);
    public static final int DEFAULT_ZOOM = 15;
    public static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;

    public static final int M_MAX_ENTRIES = 5;
}
