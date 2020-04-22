package a2.mobile.mobileapp.views;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;

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
import com.mapbox.services.android.navigation.v5.navigation.MapboxNavigation;

import java.util.List;

import a2.mobile.mobileapp.R;
import a2.mobile.mobileapp.activities.MainActivity;
import a2.mobile.mobileapp.constants.SceneConstants;
import a2.mobile.mobileapp.handlers.MapHandler;
import a2.mobile.mobileapp.utils.MapUtils;
import a2.mobile.mobileapp.utils.NavigationUtils;

public class MapViewPartial extends LinearLayout
        implements OnMapReadyCallback {

    private Context context;
    private MapView mapView;
    public static PermissionsManager permissionsManager;

    @SuppressLint("StaticFieldLeak")
    public static OfflineManager offlineMapManager;

    public MapViewPartial(Context rootContext, AttributeSet attributeSet) {
        super(rootContext, attributeSet);

        Mapbox.getInstance(rootContext, rootContext.getString(R.string.mapbox_access_token));
        inflate(rootContext, R.layout.custom_map_view, this);

        context = rootContext;
        mapView = findViewById(R.id.mapbox);
        mapView.getMapAsync(this);
    }

    @Override
    public void onMapReady(@NonNull MapboxMap mapboxMap) {
        MapUtils.storeMapInstance(mapboxMap);
        MapUtils.storeNavigationInstance(
                new MapboxNavigation(context, context.getString(R.string.mapbox_access_token))
        );

        offlineMapManager = OfflineManager.getInstance(context);

        // Set up the map global style.
        MapUtils.setMapStyle(context);

        // Download the offline region - Nijmegen.
        MapHandler.downloadOfflineRegion(context);

        // Enable location detection.
        MapUtils.map.setStyle(MapUtils.getMapStyle(), style -> MapViewPartial.enableLocationComponent(
                context,
                style
        ));

        // After the Mapbox map has been generated show the routes list.
        MainActivity.sceneManager.switchScene(SceneConstants.DEFAULT_SCENE_TO_LOAD);
    }

    /**
     * Enable live location tracking and focus the camera on the current location.
     *
     * @param loadedMapStyle The currently loaded map style
     */
    public static void enableLocationComponent(Context context, @NonNull Style loadedMapStyle) {
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
                                MapUtils.map.getStyle(style -> MapViewPartial.enableLocationComponent(
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
