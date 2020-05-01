package a2.mobile.mobileapp.handlers;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;

import com.mapbox.services.android.navigation.ui.v5.NavigationLauncher;
import com.mapbox.services.android.navigation.ui.v5.NavigationLauncherOptions;
import com.mapbox.services.android.navigation.v5.navigation.MapboxNavigation;
import com.mapbox.services.android.navigation.v5.navigation.NavigationRoute;

import org.json.JSONObject;

import a2.mobile.mobileapp.R;
import a2.mobile.mobileapp.activities.MainActivity;
import a2.mobile.mobileapp.data.classes.location.LocationLiveData;
import a2.mobile.mobileapp.data.classes.location.LocationModel;
import a2.mobile.mobileapp.utils.MapUtils;

public class NavigationHandler {
    private static Observer<LocationModel> locationObserver;

    @SuppressLint("StaticFieldLeak")
    private static View rootView;
    private static CardView directionCardView;
    private static TextView iconTextView;

    /**
     * Initiate the navigation system by observing the user's current location.
     *
     * @param context         The activity context
     * @param view            The activity view
     * @param routeDirections The routeDirections object to use for directions
     */
    public static void startNavigation(Context context, View view, JSONObject routeDirections) {
//        LocationLiveData locationData = MainActivity.locationViewModel.getLocationData();
//        boolean hasActiveObservers = locationData.hasActiveObservers();
//
//        if (hasActiveObservers)
//            return;
//
//        rootView = ((Activity)context).findViewById(R.id.activity_main);
//        directionCardView = rootView.findViewById(R.id.current_direction);
//        iconTextView = rootView.findViewById(R.id.icon);
//
//        setUpViewForNavigation();
//
//        locationObserver = locationModel -> {
//            // TODO: Observe location.
//        };
//
//        locationData.observe((LifecycleOwner) context, locationObserver);
//        view.findViewById(R.id.button_exit_navigation).setOnClickListener(v -> {
//            stopNavigation();
//            MainActivity.sceneManager.switchScene(R.layout.scene_route_deails);
//        });
//
//        view.findViewById(R.id.button_view_points_of_interest).setOnClickListener(v -> {
//            stopNavigation();
//            MainActivity.sceneManager.switchScene(R.layout.scene_points_of_interest);
//        });
//
//        // TODO: Remove this.
//        updateCurrentDirection(R.string.icon_corner_up_left);
//        TextView title = view.findViewById(R.id.title);
//        title.setText("Turn left onto Professor Uilkensweg");
//
//        TextView subtitle = view.findViewById(R.id.subtitle);
//        subtitle.setText("230m");
//        collapseContentHolder();

        // Create a NavigationLauncherOptions object to package everything together
        Log.e("Route", "Route " + MapUtils.navigationRoute);
        NavigationLauncherOptions options = NavigationLauncherOptions.builder()
                .directionsRoute(MapUtils.navigationRoute)
                .shouldSimulateRoute(true)
                .build();

        // Call this method with Context from within an Activity
        NavigationLauncher.startNavigation((Activity)context, options);
    }

    private static void setUpViewForNavigation() {
        directionCardView.setVisibility(View.VISIBLE);

        collapseContentHolder();
    }

    private static void collapseContentHolder() {
        // TODO: Collapse content holder and change/rotate the icon.
    }

    private static void updateCurrentDirection(int icon) {
        iconTextView.setText(icon);
    }

    /**
     * Stop the navigation system by removing all observers on the current location.
     */
    public static void stopNavigation() {
        MainActivity.locationViewModel.getLocationData().removeObserver(locationObserver);
        setUpViewAfterNavigationExit();
    }

    private static void setUpViewAfterNavigationExit() {
        directionCardView.setVisibility(View.GONE);
    }
}
