package a2.mobile.mobileapp.handlers;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;

import org.json.JSONObject;

import a2.mobile.mobileapp.R;
import a2.mobile.mobileapp.activities.MainActivity;
import a2.mobile.mobileapp.data.classes.location.LocationLiveData;
import a2.mobile.mobileapp.data.classes.location.LocationModel;
import a2.mobile.mobileapp.fragments.MainActivityFragment;
import a2.mobile.mobileapp.fragments.MainActivityMapFragment;

public class NavigationHandler {
    private static Observer<LocationModel> locationObserver;

    /**
     * Initiate the navigation system by observing the user's current location.
     *
     * @param context         The activity context
     * @param view            The activity view
     * @param routeDirections The routeDirections object to use for directions
     */
    public static void startNavigation(Context context, View view, JSONObject routeDirections) {
        LocationLiveData locationData = MainActivity.locationViewModel.getLocationData();
        boolean hasActiveObservers = locationData.hasActiveObservers();

        if (hasActiveObservers)
            return;

        locationObserver = locationModel -> {
            // TODO: Observe location.
        };

        locationData.observe((LifecycleOwner) context, locationObserver);
        view.findViewById(R.id.button_exit_navigation)
                .setOnClickListener(v -> stopNavigation());

        // TODO: Remove this.
        MainActivity.mapManager.updateCurrentDirection(R.string.icon_corner_up_left);
        TextView title = view.findViewById(R.id.title);
        title.setText("Turn left onto Professor Uilkensweg");

        TextView subtitle = view.findViewById(R.id.subtitle);
        subtitle.setText("230m");
        MainActivity.mapManager.collapseContentHolder();
    }

    /**
     * Stop the navigation system by removing all observers on the current location.
     */
    public static void stopNavigation() {
        MainActivity.locationViewModel.getLocationData().removeObserver(locationObserver);
        MainActivity.sceneManager.switchScene(R.layout.scene_route_deails);
        MainActivity.mapManager.setUpViewAfterNavigationExit();
    }
}
