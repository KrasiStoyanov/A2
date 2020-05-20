package a2.mobile.mobileapp.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Response;
import com.mapbox.api.directions.v5.DirectionsCriteria;
import com.mapbox.api.directions.v5.models.DirectionsResponse;
import com.mapbox.api.directions.v5.models.DirectionsRoute;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.services.android.navigation.ui.v5.NavigationLauncher;
import com.mapbox.services.android.navigation.ui.v5.NavigationLauncherOptions;
import com.mapbox.services.android.navigation.ui.v5.NavigationView;
import com.mapbox.services.android.navigation.ui.v5.NavigationViewOptions;
import com.mapbox.services.android.navigation.ui.v5.OnNavigationReadyCallback;
import com.mapbox.services.android.navigation.ui.v5.listeners.NavigationListener;
import com.mapbox.services.android.navigation.ui.v5.listeners.RouteListener;
import com.mapbox.services.android.navigation.v5.navigation.MapboxNavigation;
import com.mapbox.services.android.navigation.v5.navigation.NavigationRoute;
import com.mapbox.services.android.navigation.v5.routeprogress.ProgressChangeListener;
import com.mapbox.services.android.navigation.v5.routeprogress.RouteProgress;
import com.vuzix.connectivity.sdk.Connectivity;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import a2.mobile.mobileapp.R;
import a2.mobile.mobileapp.enums.Scenes;
import a2.mobile.mobileapp.handlers.NavigationHandler;
import a2.mobile.mobileapp.utils.MapUtils;
import retrofit2.Call;
import retrofit2.Callback;

public class TestMapActivity extends AppCompatActivity implements OnNavigationReadyCallback,
        NavigationListener, RouteListener, ProgressChangeListener {

    private NavigationView navigationView;
    private boolean dropoffDialogShown;
    private Location lastKnownLocation;

    private List<Point> points = new ArrayList<>();
    //Test
    private static final String ACTION_SEND = "a2.mobile.mobileapp.SEND";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e("Instance", "State " + Mapbox.hasInstance());

        if (!Mapbox.hasInstance()) {
            Mapbox.getInstance(this, getString(R.string.mapbox_access_token));
    }

        setContentView(R.layout.activity_test_map);
        points = MapUtils.currentNavigationPoints;

        navigationView = findViewById(R.id.navigation);
        navigationView.onCreate(savedInstanceState);
        navigationView.initialize(this);

        //  Test broadcast
        Intent sendIntent = new Intent(ACTION_SEND);
        sendIntent.setPackage("com.example.bladeapp");
        sendIntent.putExtra("my_string_extra", "Krasi Thank you");
        Connectivity.get(this).sendBroadcast(sendIntent);
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
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public void onNavigationReady(boolean isRunning) {
        fetchRoute(points.remove(0), points.remove(0));

    }

    @Override
    public void onCancelNavigation() {
        finish();
    }

    @Override
    public void onNavigationFinished() {
    }

    @Override
    public void onNavigationRunning() {
    }

    @Override
    public boolean allowRerouteFrom(Point offRoutePoint) {
        return false;
    }

    @Override
    public void onOffRoute(Point offRoutePoint) {

    }

    @Override
    public void onRerouteAlong(DirectionsRoute directionsRoute) {

    }

    @Override
    public void onFailedReroute(String errorMessage) {

    }

    @Override
    public void onArrival() {
    }

    @Override
    public void onProgressChange(@NotNull Location location, @NotNull RouteProgress routeProgress) {
        lastKnownLocation = location;
    }

    private void startNavigation(DirectionsRoute directionsRoute) {
        NavigationViewOptions navigationViewOptions = setupOptions(directionsRoute);
        navigationView.startNavigation(navigationViewOptions);
    }

    private void fetchRoute(Point origin, Point destination) {
        NavigationRoute.Builder options = NavigationRoute.builder(this)
                .accessToken(Mapbox.getAccessToken())
                .origin(origin)
                .destination(destination)
                .alternatives(true)
                .profile(DirectionsCriteria.PROFILE_WALKING);

        for (Point point : points) {
            options.addWaypoint(point);
        }

        options.build().getRoute(new Callback<DirectionsResponse>() {
            @Override
            public void onResponse(
                    @NonNull Call<DirectionsResponse> call,
                    @NonNull retrofit2.Response<DirectionsResponse> response) {

                startNavigation(response.body().routes().get(0));
            }

            @Override
            public void onFailure(Call<DirectionsResponse> call, Throwable t) {

            }
        });
    }

    private NavigationViewOptions setupOptions(DirectionsRoute directionsRoute) {
        dropoffDialogShown = false;

        NavigationViewOptions.Builder options = NavigationViewOptions.builder();
        options.directionsRoute(directionsRoute)
                .navigationListener(this)
                .progressChangeListener(this)
                .routeListener(this)
                .shouldSimulateRoute(true);

        return options.build();
    }

    private Point getLastKnownLocation() {
        return Point.fromLngLat(lastKnownLocation.getLongitude(), lastKnownLocation.getLatitude());
    }
}