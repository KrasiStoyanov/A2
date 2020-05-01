package a2.mobile.mobileapp.activities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.AbsoluteSizeSpan;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;

import com.mapbox.android.core.permissions.PermissionsListener;
import com.mapbox.android.core.permissions.PermissionsManager;
import com.mapbox.api.directions.v5.DirectionsCriteria;
import com.mapbox.api.directions.v5.MapboxDirections;
import com.mapbox.api.directions.v5.models.BannerInstructions;
import com.mapbox.api.directions.v5.models.BannerText;
import com.mapbox.api.directions.v5.models.DirectionsResponse;
import com.mapbox.api.directions.v5.models.DirectionsRoute;
import com.mapbox.api.directions.v5.models.LegStep;
import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;
import com.mapbox.geojson.LineString;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.location.LocationComponent;
import com.mapbox.mapboxsdk.location.LocationComponentActivationOptions;
import com.mapbox.mapboxsdk.location.modes.CameraMode;
import com.mapbox.mapboxsdk.location.modes.RenderMode;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.Style;
import com.mapbox.mapboxsdk.maps.UiSettings;
import com.mapbox.mapboxsdk.plugins.annotation.Symbol;
import com.mapbox.mapboxsdk.plugins.annotation.SymbolManager;
import com.mapbox.mapboxsdk.style.layers.LineLayer;
import com.mapbox.mapboxsdk.style.layers.Property;
import com.mapbox.mapboxsdk.style.layers.SymbolLayer;
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource;
import com.mapbox.navigator.BannerInstruction;
import com.mapbox.services.android.navigation.ui.v5.NavigationLauncher;
import com.mapbox.services.android.navigation.ui.v5.NavigationLauncherOptions;
import com.mapbox.services.android.navigation.ui.v5.NavigationView;
import com.mapbox.services.android.navigation.ui.v5.NavigationViewOptions;
import com.mapbox.services.android.navigation.ui.v5.OnNavigationReadyCallback;
import com.mapbox.services.android.navigation.ui.v5.listeners.BannerInstructionsListener;
import com.mapbox.services.android.navigation.ui.v5.listeners.InstructionListListener;
import com.mapbox.services.android.navigation.ui.v5.listeners.NavigationListener;
import com.mapbox.services.android.navigation.ui.v5.listeners.SpeechAnnouncementListener;
import com.mapbox.services.android.navigation.ui.v5.voice.SpeechAnnouncement;
import com.mapbox.services.android.navigation.v5.navigation.NavigationRoute;
import com.mapbox.services.android.navigation.v5.routeprogress.ProgressChangeListener;
import com.mapbox.services.android.navigation.v5.routeprogress.RouteLegProgress;
import com.mapbox.services.android.navigation.v5.routeprogress.RouteProgress;
import com.mapbox.services.android.navigation.v5.routeprogress.RouteStepProgress;

import java.util.ArrayList;
import java.util.List;

import a2.mobile.mobileapp.R;
import a2.mobile.mobileapp.constants.MapConstants;
import a2.mobile.mobileapp.handlers.RouteDetailsHandler;
import a2.mobile.mobileapp.utils.MapUtils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

import static com.mapbox.core.constants.Constants.PRECISION_6;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconAllowOverlap;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconIgnorePlacement;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconImage;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconOffset;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconSize;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.lineCap;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.lineColor;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.lineJoin;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.lineWidth;

public class TestMapActivity extends AppCompatActivity implements OnNavigationReadyCallback,
        NavigationListener, ProgressChangeListener, InstructionListListener, SpeechAnnouncementListener,
        BannerInstructionsListener {

    private MapboxMap map;
    private MapView mapView;
    private NavigationView navigationView;
    private PermissionsManager permissionsManager;

    private SymbolManager symbolManager;
    private Symbol symbol;
    private static final String ROUTE_LAYER_ID = "route-layer-id";
    private static final String ROUTE_SOURCE_ID = "route-source-id";
    private static final String ICON_LAYER_ID = "icon-layer-id";
    private static final String ICON_SOURCE_ID = "icon-source-id";
    private static final String RED_PIN_ICON_ID = "red-pin-icon-id";
    private static final Point ORIGIN = Point.fromLngLat(6.527295, 53.244789);
    private static final Point DESTINATION = Point.fromLngLat(6.534489, 53.241229);
    private static final int INITIAL_ZOOM = 16;
    private NavigationRoute client;
    private static final String TAG = "MainActivity";
    private DirectionsRoute currentRoute;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e("Instance", "State " + Mapbox.hasInstance());

        if (!Mapbox.hasInstance()) {
            Mapbox.getInstance(this, getString(R.string.mapbox_access_token));
        }

        setContentView(R.layout.activity_test_map);

        navigationView = findViewById(R.id.navigation);
        CameraPosition initialPosition = new CameraPosition.Builder()
                .target(new LatLng(ORIGIN.latitude(), ORIGIN.longitude()))
                .zoom(INITIAL_ZOOM)
                .build();

        navigationView.onCreate(savedInstanceState);
        navigationView.initialize(this, initialPosition);
    }

    @Override
    public void onNavigationReady(boolean isRunning) {
        new Thread(this::fetchRoute).start();
    }

    @Override
    public void onStart() {
        super.onStart();
        navigationView.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        navigationView.onResume();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        navigationView.onLowMemory();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        navigationView.onSaveInstanceState(outState);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        navigationView.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    public void onPause() {
        super.onPause();
        navigationView.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
        navigationView.onStop();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        navigationView.onDestroy();
    }

    @Override
    public void onCancelNavigation() {
        // Navigation canceled, finish the activity
        finish();
    }

    @Override
    public void onNavigationFinished() {
        // Intentionally empty
    }

    @Override
    public void onNavigationRunning() {
        // Intentionally empty
    }

    @Override
    public void onProgressChange(Location location, RouteProgress routeProgress) {
//        setSpeed(location, routeProgress);
    }

    @Override
    public SpeechAnnouncement willVoice(SpeechAnnouncement announcement) {
        return SpeechAnnouncement.builder()
                .announcement("All announcements will be the same.")
                .build();
    }

    @Override
    public BannerInstructions willDisplay(BannerInstructions instructions) {
        return instructions;
    }

    private void startNavigation(DirectionsRoute directionsRoute) {
        NavigationViewOptions.Builder options = NavigationViewOptions.builder()
                .navigationListener(this)
                .directionsRoute(directionsRoute)
                .shouldSimulateRoute(true)
                .progressChangeListener(this)
                .instructionListListener(this)
                .speechAnnouncementListener(this)
                .bannerInstructionsListener(this);

        navigationView.post(() -> navigationView.startNavigation((options.build())));
    }

    private void fetchRoute() {
//        startNavigation();
        NavigationRoute.builder(this)
                .accessToken(getResources().getString(R.string.mapbox_access_token))
                .origin(ORIGIN)
                .destination(DESTINATION)
                .alternatives(false)
                .build()
                .getRoute(new Callback<DirectionsResponse>() {
                    @Override
                    public void onResponse(
                            @NonNull Call<DirectionsResponse> call,
                            @NonNull Response<DirectionsResponse> response) {

                        assert response.body() != null;
                        DirectionsRoute directionsRoute = response.body().routes().get(0);
                        startNavigation(directionsRoute);
                    }

                    @Override
                    public void onFailure(
                            @NonNull Call<DirectionsResponse> call,
                            @NonNull Throwable t) {

                    }
                });
    }

    private void setSpeed(Location location, RouteProgress routeProgress) {
        @SuppressLint("DefaultLocale") String string = String.format(
                "%d\nMPH", (int) (location.getSpeed() * 2.2369)
        );

        int mphTextSize = getResources().getDimensionPixelSize(R.dimen.body_font_size);
        int speedTextSize = getResources().getDimensionPixelSize(R.dimen.h4);

        SpannableString spannableString = new SpannableString(string);
        spannableString.setSpan(new AbsoluteSizeSpan(mphTextSize),
                string.length() - 4, string.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);

        spannableString.setSpan(new AbsoluteSizeSpan(speedTextSize),
                0, string.length() - 3, Spanned.SPAN_INCLUSIVE_INCLUSIVE);

        TextView speedWidget = findViewById(R.id.title);
        speedWidget.setVisibility(View.VISIBLE);

        StringBuilder stringBuilder = new StringBuilder();

        RouteLegProgress legProgress = routeProgress.currentLegProgress();
        stringBuilder.append("In ").append(legProgress.upComingStep().distance());

        LegStep step = legProgress.currentStep();
        step.bannerInstructions().forEach(i -> {
            BannerText instruction = i.primary();
            String instructionType = instruction.type();
            String modifier = instruction.modifier();

            if (instructionType != null && modifier != null) {
                instructionType = instructionType.toLowerCase();
                modifier = modifier.toLowerCase();

                if ("turn".equals(instructionType)) {
                    if (modifier.contains("sharp") || modifier.contains("slight")) {
                        stringBuilder
                                .append("m make a ")
                                .append(modifier)
                                .append(" ")
                                .append(instructionType);
                    } else if (modifier.equals("straight")) {
                        stringBuilder.append("Continue straight");
                    } else if (modifier.equals("uturn")) {
                        stringBuilder.append("m make a U-turn");
                    } else {
                        stringBuilder.append("m turn ").append(modifier);
                    }
                }
            }

            stringBuilder
                    .append(" towards ")
                    .append(instruction.text())
                    .append(".");
        });

        Log.e("Step", stringBuilder.toString());
        speedWidget.setText(stringBuilder.toString());
    }

    @Override
    public void onInstructionListVisibilityChanged(boolean visible) {

    }


//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//
//        Mapbox.getInstance(this, getString(R.string.mapbox_access_token));
//
//        setContentView(R.layout.activity_test_map);
//
//        mapView = findViewById(R.id.mapbox);
//        mapView.onCreate(savedInstanceState);
//        mapView.getMapAsync(this);
//
//        navigationView = findViewById(R.id.navigation);
//
//        CardView button = findViewById(R.id.button);
//        button.setOnClickListener(v -> {
//            NavigationViewOptions options = NavigationViewOptions.builder()
//                    .directionsRoute(currentRoute)
//                    .shouldSimulateRoute(true)
//                    .build();
//
//            // Call this method with Context from within an Activity
//            Log.e("Navigation View", "A " + navigationView);
//            navigationView.startNavigation(options);
//        });
//    }
//
//    @Override
//    public void onMapReady(@NonNull MapboxMap mapboxMap) {
//        List<Feature> symbolLayerIconFeatureList = new ArrayList<>();
//        symbolLayerIconFeatureList.add(Feature.fromGeometry(
//                Point.fromLngLat(-122.082352, 37.419188)));
//
//        SymbolLayer routeLayer = new SymbolLayer(
//                MapConstants.MAP_ROUTE_LAYER_ID,
//                MapConstants.MAP_ROUTE_LAYER_SOURCE_ID
//        ).withProperties(
//                iconImage(MapConstants.MAP_MARKER_ID),
//                iconSize(0.5f),
//                iconAllowOverlap(true),
//                iconIgnorePlacement(true),
//                iconOffset(new Float[]{0f, -9f})
//        );
//
//        GeoJsonSource source = new GeoJsonSource(MapConstants.MAP_ROUTE_LAYER_SOURCE_ID,
//                FeatureCollection.fromFeatures(symbolLayerIconFeatureList)
//        );
//
//        Style.Builder mapStyle = new Style.Builder()
//                .fromUri(Style.MAPBOX_STREETS);
//
//        // Map is set up and the style has loaded. Now you can add data or make other map adjustments
//        map = mapboxMap;
//        map.setStyle(mapStyle, style -> {
//            UiSettings uiSettings = map.getUiSettings();
//            uiSettings.setCompassEnabled(true);
//
//            initSource(style);
//
//            initLayers(style);
//
//            // Set the destination location
//            getRoute(mapboxMap, origin, destination);
//            enableLocationComponent(style);
//        });
//
//    }
//
//    /**
//     * Add the route and marker sources to the map
//     */
//    private void initSource(@NonNull Style loadedMapStyle) {
//        loadedMapStyle.addSource(new GeoJsonSource(ROUTE_SOURCE_ID));
//
//        GeoJsonSource iconGeoJsonSource = new GeoJsonSource(ICON_SOURCE_ID, FeatureCollection.fromFeatures(new Feature[]{
//                Feature.fromGeometry(Point.fromLngLat(origin.longitude(), origin.latitude())),
//                Feature.fromGeometry(Point.fromLngLat(destination.longitude(), destination.latitude()))}));
//        loadedMapStyle.addSource(iconGeoJsonSource);
//    }
//
//    /**
//     * Add the route and marker icon layers to the map
//     */
//    private void initLayers(@NonNull Style loadedMapStyle) {
//        LineLayer routeLayer = new LineLayer(ROUTE_LAYER_ID, ROUTE_SOURCE_ID);
//
//        // Add the LineLayer to the map. This layer will display the directions route.
//        routeLayer.setProperties(
//                lineCap(Property.LINE_CAP_ROUND),
//                lineJoin(Property.LINE_JOIN_ROUND),
//                lineWidth(5f),
//                lineColor(ContextCompat.getColor(this, R.color.primary))
//        );
//
//        loadedMapStyle.addLayer(routeLayer);
//
//        // Add the red marker icon image to the map
//        loadedMapStyle.addImage(MapConstants.MAP_MARKER_ID, BitmapFactory.decodeResource(
//                getResources(),
//                R.drawable.red_marker
//        ));
//
//        // Add the red marker icon SymbolLayer to the map
//        loadedMapStyle.addLayer(new SymbolLayer(ICON_LAYER_ID, ICON_SOURCE_ID).withProperties(
//                iconImage(RED_PIN_ICON_ID),
//                iconIgnorePlacement(true),
//                iconAllowOverlap(true),
//                iconOffset(new Float[]{0f, -9f})
//        ));
//    }
//
//
//    private void enableLocationComponent(@NonNull Style loadedMapStyle) {
//        // Check if permissions are enabled and if not request
//        if (PermissionsManager.areLocationPermissionsGranted(this)) {
//
//            // Get an instance of the component
//            LocationComponent locationComponent = map.getLocationComponent();
//
//            // Activate with options
//            locationComponent.activateLocationComponent(
//                    LocationComponentActivationOptions.builder(
//                            this,
//                            loadedMapStyle
//                    ).build()
//            );
//
//            // Enable to make component visible
//            locationComponent.setLocationComponentEnabled(true);
//
//            // Set the component's camera mode
//            locationComponent.setCameraMode(CameraMode.TRACKING);
//
//            // Set the component's render mode
//            locationComponent.setRenderMode(RenderMode.COMPASS);
//        } else {
//            permissionsManager = new PermissionsManager(this);
//            permissionsManager.requestLocationPermissions(this);
//        }
//    }
//
//    @Override
//    public void onRequestPermissionsResult(
//            int requestCode,
//            @NonNull String[] permissions,
//            @NonNull int[] grantResults) {
//
//        permissionsManager.onRequestPermissionsResult(requestCode, permissions, grantResults);
//    }
//
//    @Override
//    public void onExplanationNeeded(List<String> permissionsToExplain) {
//        Toast.makeText(
//                this,
//                "Please provide the application with location access!",
//                Toast.LENGTH_LONG).show();
//    }
//
//    @Override
//    public void onPermissionResult(boolean granted) {
//        if (granted) {
//            map.getStyle(this::enableLocationComponent);
//        } else {
//            Toast.makeText(
//                    this,
//                    "User permission not granted!",
//                    Toast.LENGTH_LONG).show();
//
//            finish();
//        }
//    }
//
//    private void getRoute(MapboxMap mapboxMap, Point origin, Point destination) {
//        client = NavigationRoute.builder(this)
//                .accessToken(getResources().getString(R.string.mapbox_access_token))
//                .origin(origin)
//                .destination(destination)
//                .profile(DirectionsCriteria.PROFILE_DRIVING)
//                .build();
//
//        client.getRoute(new Callback<DirectionsResponse>() {
//            @Override
//            public void onResponse(
//                    @NonNull Call<DirectionsResponse> call,
//                    @NonNull Response<DirectionsResponse> response) {
//
//                // You can get the generic HTTP info about the response
//                Timber.e("Response code: %s", response.code());
//                if (response.body() == null) {
//                    Timber.e("No routes found, make sure you set the right user and access token.");
//                    return;
//                } else if (response.body().routes().size() < 1) {
//                    Timber.e("No routes found");
//                    return;
//                }
//
//                // Get the directions route
//                currentRoute = response.body().routes().get(0);
//
//                if (mapboxMap != null) {
//                    mapboxMap.getStyle(style -> {
//
//                        // Retrieve and update the source designated for showing the directions route
//                        GeoJsonSource source = style.getSourceAs(ROUTE_SOURCE_ID);
//
//                        // Create a LineString with the directions route's geometry and
//                        // reset the GeoJSON source for the route LineLayer source
//                        Log.e(" SADASDSAD ", " currentRoute " + currentRoute);
//                        if (source != null) {
//                            source.setGeoJson(LineString.fromPolyline(
//                                    currentRoute.geometry(),
//                                    PRECISION_6
//                            ));
//                        }
//                    });
//                }
//            }
//
//            @Override
//            public void onFailure(
//                    @NonNull Call<DirectionsResponse> call,
//                    @NonNull Throwable throwable) {
//
//                Toast.makeText(
//                        TestMapActivity.this,
//                        "Error: " + throwable.getMessage(),
//                        Toast.LENGTH_SHORT
//                ).show();
//            }
//        });
//    }
//
//
//    @Override
//    @SuppressWarnings({"MissingPermission"})
//    protected void onStart() {
//        super.onStart();
//        mapView.onStart();
//    }
//
//    @Override
//    protected void onResume() {
//        super.onResume();
//        mapView.onResume();
//    }
//
//    @Override
//    protected void onPause() {
//        super.onPause();
//        mapView.onPause();
//    }
//
//    @Override
//    protected void onStop() {
//        super.onStop();
//        mapView.onStop();
//    }
//
//    @Override
//    protected void onSaveInstanceState(@NonNull Bundle outState) {
//        super.onSaveInstanceState(outState);
//        mapView.onSaveInstanceState(outState);
//    }
//
//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        mapView.onDestroy();
//    }
//
//    @Override
//    public void onLowMemory() {
//        super.onLowMemory();
//        mapView.onLowMemory();
//    }
}
