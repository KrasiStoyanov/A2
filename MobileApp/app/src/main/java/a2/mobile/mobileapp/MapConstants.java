package a2.mobile.mobileapp;

import com.google.android.gms.maps.model.LatLng;

public class MapConstants {
    // Global keys
    public static final String KEY_CAMERA_POSITION = "camera_position";
    public static final String KEY_LOCATION = "location";

    // Google Maps settings
    public static final LatLng DEFAULT_LOCATION = new LatLng(51.842175, 5.859508);
    public static final int DEFAULT_ZOOM = 15;
    public static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
}
