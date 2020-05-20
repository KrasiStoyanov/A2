package a2.mobile.mobileapp.activities;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.mapbox.api.directions.v5.DirectionsCriteria;
import com.mapbox.api.directions.v5.models.BannerInstructions;
import com.mapbox.api.directions.v5.models.BannerText;
import com.mapbox.api.directions.v5.models.DirectionsResponse;
import com.mapbox.api.directions.v5.models.DirectionsRoute;
import com.mapbox.api.directions.v5.models.LegStep;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.navigator.BannerInstruction;
import com.mapbox.services.android.navigation.ui.v5.NavigationView;
import com.mapbox.services.android.navigation.ui.v5.NavigationViewOptions;
import com.mapbox.services.android.navigation.ui.v5.OnNavigationReadyCallback;
import com.mapbox.services.android.navigation.ui.v5.listeners.BannerInstructionsListener;
import com.mapbox.services.android.navigation.ui.v5.listeners.NavigationListener;
import com.mapbox.services.android.navigation.ui.v5.listeners.RouteListener;
import com.mapbox.services.android.navigation.v5.navigation.NavigationRoute;
import com.mapbox.services.android.navigation.v5.routeprogress.ProgressChangeListener;
import com.mapbox.services.android.navigation.v5.routeprogress.RouteLegProgress;
import com.mapbox.services.android.navigation.v5.routeprogress.RouteProgress;
import com.mapbox.services.android.navigation.v5.routeprogress.RouteStepProgress;
import com.vuzix.connectivity.sdk.Connectivity;

import com.vuzix.connectivity.sdk.Connectivity;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import a2.mobile.mobileapp.R;
import a2.mobile.mobileapp.handlers.NavigationHandler;
import a2.mobile.mobileapp.utils.MapUtils;
import retrofit2.Call;
import retrofit2.Callback;

public class TestMapActivity extends AppCompatActivity implements OnNavigationReadyCallback,
        NavigationListener, RouteListener, ProgressChangeListener {

    private NavigationView navigationView;

    private Location lastKnownLocation;
    private LegStep previousStep;
    private int previousDistance = 0;

    private List<Point> points = new ArrayList<>();
    //Test
    private static final String ACTION_SEND = "a2.mobile.mobileapp.SEND";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!Mapbox.hasInstance()) {
            Mapbox.getInstance(this, getString(R.string.mapbox_access_token));
    }

        setContentView(R.layout.activity_test_map);
        points = new ArrayList<>(MapUtils.currentNavigationPoints);

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
        RouteLegProgress legProgress = routeProgress.currentLegProgress();

        // TODO: See how the message can be displayed only on a new instruction. Maybe check if the current waypoint is the same or see if there isn't something built-in, for example the bannerListInstructionListener
        // TODO: Only generate message when the initial instruction for the coming maneuver is received. Update only the distance remaining.

        assert legProgress != null;
        LegStep step = legProgress.currentStep();
        RouteStepProgress stepProgress = legProgress.currentStepProgress();

        if (step != null && stepProgress != null) {
            if (!step.equals(previousStep)) {
                String instructionMessage = NavigationHandler.generateInstructionMessage(
                        step,
                        stepProgress
                );

                previousStep = step;
            }

            int distance = roundCurrentDistanceRemaining(stepProgress);
            boolean isDistanceValid = validateCurrentDistanceRemaining(distance);
            if (isDistanceValid && distance != previousDistance) {
                NavigationHandler.updateDistanceRemaining(distance, this);

                previousDistance = distance;
            }
        }

        lastKnownLocation = location;
    }

    private int roundCurrentDistanceRemaining(RouteStepProgress stepProgress) {
        double distanceRemaining = stepProgress.getDistanceRemaining() == null ?
                previousDistance : stepProgress.getDistanceRemaining();

        int distance = (int) Math.round(distanceRemaining);

        distance = distance - (distance % 50);
        if (distance > 0 && distance <= 350) {
            return distance;
        } else if (distance > 500) {
            if (distance % 100 == 0) {
                return  distance;
            }
        }

        return distance;
    }

    private boolean validateCurrentDistanceRemaining(int distanceRemaining) {
        if (distanceRemaining >= 0 && distanceRemaining <= 500) {
            return distanceRemaining % 50 == 0;
        } else if (distanceRemaining > 500) {
            return distanceRemaining % 100 == 0;
        }

        return false;
    }

    private void startNavigation(DirectionsRoute directionsRoute) {
        NavigationViewOptions navigationViewOptions = setupOptions(directionsRoute);
        navigationView.startNavigation(navigationViewOptions);
    }

    private void fetchRoute(Point origin, Point destination) {
        NavigationRoute.Builder options = NavigationRoute.builder(this)
                .accessToken(getResources().getString(R.string.mapbox_access_token))
                .origin(origin)
                .destination(destination)
                .alternatives(true)
                .voiceUnits(DirectionsCriteria.METRIC)
                .profile(DirectionsCriteria.PROFILE_WALKING);

        for (Point point : points) {
            options.addWaypoint(point);
        }

        options.build().getRoute(new Callback<DirectionsResponse>() {
            @Override
            public void onResponse(
                    @NonNull Call<DirectionsResponse> call,
                    @NonNull retrofit2.Response<DirectionsResponse> response) {

                assert response.body() != null;
                startNavigation(response.body().routes().get(0));
            }

            @Override
            public void onFailure(@NonNull Call<DirectionsResponse> call, @NonNull Throwable t) {

            }
        });
    }

    private NavigationViewOptions setupOptions(DirectionsRoute directionsRoute) {
        NavigationViewOptions.Builder options = NavigationViewOptions.builder();
        options.directionsRoute(directionsRoute)
                .navigationListener(this)
                .progressChangeListener(this)
                .routeListener(this)
                .shouldSimulateRoute(true);

        return options.build();
    }
}