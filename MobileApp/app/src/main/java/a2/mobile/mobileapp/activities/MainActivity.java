package a2.mobile.mobileapp.activities;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.SupportMapFragment;
import com.mapbox.mapboxsdk.offline.OfflineManager;

import a2.mobile.mobileapp.R;
import a2.mobile.mobileapp.constants.MapConstants;
import a2.mobile.mobileapp.constants.SceneConstants;
import a2.mobile.mobileapp.data.Data;
import a2.mobile.mobileapp.data.classes.location.LocationLiveData;
import a2.mobile.mobileapp.data.classes.location.LocationViewModel;
import a2.mobile.mobileapp.fragments.MainActivityFragment;
import a2.mobile.mobileapp.fragments.MainActivityMapFragment;
import a2.mobile.mobileapp.handlers.MapHandler;


public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final String TAG = "Main Activity";

    public static MapboxMap map;
    public static OfflineManager offlineMapManager;
    public static LocationViewModel locationViewModel;
    private CameraPosition cameraPosition;

    // The entry point to the Places API.
    private PlacesClient placesClient;

    // The entry point to the Fused Location Provider.
    private FusedLocationProviderClient fusedLocationProviderClient;
    private boolean locationPermissionGranted;

    // The geographical location where the device is currently located. That is, the last-known
    // location retrieved by the Fused Location Provider.
    private Location lastKnownLocation;

    public static RecyclerView recyclerView;
    public static MainActivityFragment sceneManager;
    public static MainActivityMapFragment mapManager;

    public static ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Mapbox access token is configured here. This needs to be called either in your application
        // object or in the same activity which contains the mapview.
        Mapbox.getInstance(this, MapConstants.MAPBOX_API);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recyclerView);
        progressBar = findViewById(R.id.progressBar);

        Data.context = this;
        Data.getDataFile("points_of_interest.xls");

//        downloadComplete();

//        String fileUrl = "https://github.com/KrasiStoyanov/Robocop/raw/master/MobileApp/app/src/main/assets/points_of_interest.xls";
//        String fileUrlWithoutFileName = "https://github.com/KrasiStoyanov/Robocop/raw/master/MobileApp/app/src/main/assets/";
//
//        Data.fetchDataFile(fileUrl, fileUrlWithoutFileName);

        // Retrieve location and camera position from saved instance state.
        if (savedInstanceState != null) {
            lastKnownLocation = savedInstanceState.getParcelable(MapConstants.KEY_LOCATION);
            cameraPosition = savedInstanceState.getParcelable(MapConstants.KEY_CAMERA_POSITION);
        }

        // Construct a PlacesClient
        Places.initialize(getApplicationContext(), getString(R.string.google_maps_key));
        placesClient = Places.createClient(this);

        // Construct a FusedLocationProviderClient.
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        // Build the map.
        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment fragment = fragmentManager.findFragmentById(R.id.mapbox);

        SupportMapFragment mapFragment = (SupportMapFragment) fragment;
        if (mapFragment != null) {
            mapFragment.onCreate(savedInstanceState);
            mapFragment.getMapAsync(this);
        }

        // Initialize scene and map managers.
        sceneManager = new MainActivityFragment(this);
        mapManager = new MainActivityMapFragment(this, findViewById(R.id.map_holder));

        if (savedInstanceState == null) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

            transaction.replace(R.id.content_holder, sceneManager);
            transaction.commit();
        }
    }

    /**
     * Sets up the options menu.
     *
     * @param menu The options menu.
     * @return Boolean.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.current_place_menu, menu);

        return true;
    }

    /**
     * Gets the current location of the device, and positions the map's camera.
     */
    private void getDeviceLocation() {
        /*
         * Get the best and most recent location of the device, which may be null in rare
         * cases when a location is not available.
         */
        try {
            if (locationPermissionGranted) {
                locationViewModel = new LocationViewModel(getApplication());

                LocationLiveData locationData = locationViewModel.getLocationData();
                Task<Location> locationResult = locationData.getFusedLocationClient()
                        .getLastLocation();

                locationResult.addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Set the map's camera position to the current location of the device.
                        lastKnownLocation = task.getResult();

                        if (lastKnownLocation != null) {
                            LatLng lastKnownLocationCoordinates = new LatLng(
                                    lastKnownLocation.getLatitude(),
                                    lastKnownLocation.getLongitude()
                            );

                            map.easeCamera(CameraUpdateFactory.newLatLngZoom(
                                    lastKnownLocationCoordinates,
                                    MapConstants.DEFAULT_ZOOM)
                            );
                        }
                    } else {
                        Log.d(TAG, "Current location is null. Using defaults.");
                        Log.e(TAG, "Exception: %s", task.getException());

                        map.easeCamera(CameraUpdateFactory.newLatLngZoom(
                                MapConstants.DEFAULT_LOCATION,
                                MapConstants.DEFAULT_ZOOM)
                        );
                    }
                });
            }
        } catch (SecurityException e) {
            Log.e("Exception: %s", e.getMessage());
        }
    }

    /**
     * Prompts the user for permission to use the device location.
     */
    private void getLocationPermission() {
        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */

        int locationPermission = ContextCompat.checkSelfPermission(
                this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION
        );

        if (locationPermission == PackageManager.PERMISSION_GRANTED) {
            locationPermissionGranted = true;
        } else {
            String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION};
            ActivityCompat.requestPermissions(
                    this,
                    permissions,
                    MapConstants.PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    /**
     * Handles the result of the request for location permissions.
     */
    @Override
    public void onRequestPermissionsResult(
            int requestCode,
            @NonNull String[] permissions,
            @NonNull int[] grantResults) {

        locationPermissionGranted = false;

        if (requestCode == MapConstants.PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION) {
            // If request is cancelled, the result arrays are empty.
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                locationPermissionGranted = true;
            }
        }
    }

    /**
     * When the user presses the back button, switch scenes.
     */
    @Override
    public void onBackPressed() {
        sceneManager.goBack();
    }

    /**
     * When the mapbox map is ready set up location permissions, download the offline region
     * and switch scenes to the default one.
     *
     * @param mapboxMap The mapbox map
     */
    @Override
    public void onMapReady(@NonNull MapboxMap mapboxMap) {
        map = mapboxMap;
        offlineMapManager = OfflineManager.getInstance(MainActivity.this);

        // Prompt the user for permission.
        getLocationPermission();

        // Get the current location of the device and set the position of the map.
        getDeviceLocation();

        // Download the offline region - Nijmegen.
        MapHandler.downloadOfflineRegion(this);

        // After the Mapbox map has been generated show the routes list.
        sceneManager.switchScene(SceneConstants.DEFAULT_SCENE_TO_LOAD);
    }
}
