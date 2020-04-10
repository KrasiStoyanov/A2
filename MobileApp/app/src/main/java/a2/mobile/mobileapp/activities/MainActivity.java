package a2.mobile.mobileapp.activities;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.Menu;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.mapbox.android.core.permissions.PermissionsListener;
import com.mapbox.android.core.permissions.PermissionsManager;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.location.LocationComponent;
import com.mapbox.mapboxsdk.location.LocationComponentActivationOptions;
import com.mapbox.mapboxsdk.location.modes.CameraMode;
import com.mapbox.mapboxsdk.location.modes.RenderMode;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.Style;
import com.mapbox.mapboxsdk.offline.OfflineManager;

import java.util.List;

import a2.mobile.mobileapp.R;
import a2.mobile.mobileapp.constants.SceneConstants;
import a2.mobile.mobileapp.data.Data;
import a2.mobile.mobileapp.data.classes.location.LocationViewModel;
import a2.mobile.mobileapp.fragments.MainActivityFragment;
import a2.mobile.mobileapp.fragments.MainActivityMapFragment;
import a2.mobile.mobileapp.handlers.MapHandler;
import a2.mobile.mobileapp.utils.MapUtils;


public class MainActivity extends AppCompatActivity
        implements OnMapReadyCallback, PermissionsListener {

    private static final String TAG = "Main Activity";

    private MapView mapView;
    private PermissionsManager permissionsManager;

    @SuppressLint("StaticFieldLeak")
    public static OfflineManager offlineMapManager;
    public static LocationViewModel locationViewModel;

    public static RecyclerView recyclerView;
    @SuppressLint("StaticFieldLeak")
    public static MainActivityFragment sceneManager;
    @SuppressLint("StaticFieldLeak")
    public static MainActivityMapFragment mapManager;

    @SuppressLint("StaticFieldLeak")
    public static ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Mapbox access token is configured here. This needs to be called either in your application
        // object or in the same activity which contains the mapview.
        Mapbox.getInstance(this, getString(R.string.mapbox_access_token));
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

        mapView = findViewById(R.id.mapbox);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);

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
     * When the mapbox map is ready set up location permissions, download the offline region
     * and switch scenes to the default one.
     *
     * @param mapboxMap The mapbox map
     */
    @Override
    public void onMapReady(@NonNull MapboxMap mapboxMap) {
        MapUtils.storeMapInstance(mapboxMap);
        offlineMapManager = OfflineManager.getInstance(MainActivity.this);

        // Set up the map global style.
        MapUtils.setMapStyle(this);

        // Download the offline region - Nijmegen.
        MapHandler.downloadOfflineRegion(this);

        // Enable location detection.
        MapUtils.map.setStyle(MapUtils.getMapStyle(), this::enableLocationComponent);

        // After the Mapbox map has been generated show the routes list.
        sceneManager.switchScene(SceneConstants.DEFAULT_SCENE_TO_LOAD);
    }

    /**
     * Enable live location tracking and focus the camera on the current location.
     *
     * @param loadedMapStyle The currently loaded map style
     */
    private void enableLocationComponent(@NonNull Style loadedMapStyle) {
        // Check if permissions are enabled and if not request
        if (PermissionsManager.areLocationPermissionsGranted(this)) {

            // Get an instance of the component
            LocationComponent locationComponent = MapUtils.map.getLocationComponent();

            // Activate with options
            locationComponent.activateLocationComponent(
                    LocationComponentActivationOptions.builder(
                            this,
                            loadedMapStyle
                    ).build()
            );

            // Enable to make component visible
            locationComponent.setLocationComponentEnabled(true);

            // Set the component's camera mode
            locationComponent.setCameraMode(CameraMode.TRACKING);

            // Set the component's render mode
            locationComponent.setRenderMode(RenderMode.COMPASS);
        } else {
            permissionsManager = new PermissionsManager(this);
            permissionsManager.requestLocationPermissions(this);
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

        permissionsManager.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onExplanationNeeded(List<String> permissionsToExplain) {
        Toast.makeText(
                this,
                "Please provide the application with location access!",
                Toast.LENGTH_LONG).show();
    }

    @Override
    public void onPermissionResult(boolean granted) {
        if (granted) {
            MapUtils.map.getStyle(this::enableLocationComponent);
        } else {
            Toast.makeText(
                    this,
                    "User permission not granted!",
                    Toast.LENGTH_LONG).show();

            finish();
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
     * When the user presses the back button, switch scenes.
     */
    @Override
    public void onBackPressed() {
        sceneManager.goBack();
    }
}
