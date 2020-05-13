package a2.mobile.mobileapp.activities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.Menu;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

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

import a2.mobile.mobileapp.BuildConfig;
import a2.mobile.mobileapp.R;
import a2.mobile.mobileapp.constants.SceneConstants;
import a2.mobile.mobileapp.data.Data;
import a2.mobile.mobileapp.data.classes.location.LocationViewModel;
import a2.mobile.mobileapp.fragments.MainActivityFragment;
import a2.mobile.mobileapp.handlers.MapHandler;
import a2.mobile.mobileapp.utils.MapUtils;
import timber.log.Timber;


public class MainActivity extends AppCompatActivity
        implements OnMapReadyCallback, PermissionsListener {

    @SuppressLint("StaticFieldLeak")
    public static RelativeLayout loadingBackground;
    @SuppressLint("StaticFieldLeak")
    public static MainActivityFragment sceneManager;
    public static LocationViewModel locationViewModel;
    public static PermissionsManager permissionsManager;
    @SuppressLint("StaticFieldLeak")
    public static OfflineManager offlineMapManager;
    private MapView mapView;

    @SuppressLint("StaticFieldLeak")
    public static ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        }

        // Mapbox access token is configured here. This needs to be called either in your application
        // object or in the same activity which contains the mapview.
        Mapbox.getInstance(this, getString(R.string.mapbox_access_token));
        setContentView(R.layout.activity_main);

        locationViewModel = new LocationViewModel(getApplication());
        loadingBackground = findViewById(R.id.progress_layout);
        progressBar = findViewById(R.id.progress_bar);

        Data.context = this;
        Data.getDataFile("points_of_interest.xls");

        mapView = findViewById(R.id.mapbox);
        mapView.getMapAsync(this);

        // Initialize scene and map managers.
        sceneManager = new MainActivityFragment(this);

        if (savedInstanceState == null) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

            transaction.replace(R.id.content_holder, sceneManager);
            transaction.commit();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    /**
     * Handles the result of the request for location permissions.
     */
    @Override
    public void onRequestPermissionsResult(
            int requestCode,
            @NonNull String[] permissions,
            @NonNull int[] grantResults) {

        assert permissionsManager != null;
        permissionsManager.onRequestPermissionsResult(
                requestCode,
                permissions,
                grantResults
        );
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

    @Override
    public void onDestroy() {
        super.onDestroy();
//        mapManager.onDestroy();
    }

    @Override
    public void onExplanationNeeded(List<String> permissionsToExplain) {

    }

    @Override
    public void onPermissionResult(boolean granted) {

    }

    @Override
    public void onMapReady(@NonNull MapboxMap mapboxMap) {
        MapUtils.storeMapInstance(mapboxMap);
        offlineMapManager = OfflineManager.getInstance(this);

        // Set up the map global style.
        MapUtils.setMapStyle(this);

        // Download the offline region - Nijmegen.
        MapHandler.downloadOfflineRegion(this);

        // Enable location detection.
        MapUtils.map.setStyle(MapUtils.getMapStyle(), style -> enableLocationComponent(
                this,
                style
        ));

        // After the Mapbox map has been generated show the routes list.
        sceneManager.switchScene(SceneConstants.DEFAULT_SCENE_TO_LOAD);
    }

    /**
     * Enable live location tracking and focus the camera on the current location.
     *
     * @param loadedMapStyle The currently loaded map style
     */
    private void enableLocationComponent(Context context, @NonNull Style loadedMapStyle) {
        // Check if permissions are enabled and if not request
        if (PermissionsManager.areLocationPermissionsGranted(context)) {

            // Get an instance of the component
            LocationComponent locationComponent = MapUtils.map.getLocationComponent();

            // Activate with options
            locationComponent.activateLocationComponent(
                    LocationComponentActivationOptions.builder(
                            context,
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
            permissionsManager = new PermissionsManager(
                    new PermissionsListener() {

                        @Override
                        public void onExplanationNeeded(List<String> permissionsToExplain) {
                            Toast.makeText(
                                    context,
                                    "Please provide the application with location access!",
                                    Toast.LENGTH_LONG
                            ).show();
                        }

                        @Override
                        public void onPermissionResult(boolean granted) {
                            if (granted) {
                                MapUtils.map.getStyle(style -> enableLocationComponent(
                                        context,
                                        style
                                ));
                            } else {
                                Toast.makeText(
                                        context,
                                        "User permission not granted!",
                                        Toast.LENGTH_LONG
                                ).show();

                                ((Activity) context).finish();
                            }
                        }
                    });

            permissionsManager.requestLocationPermissions((Activity) context);
        }
    }
}
