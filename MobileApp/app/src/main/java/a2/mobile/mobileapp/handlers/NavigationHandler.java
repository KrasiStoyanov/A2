package a2.mobile.mobileapp.handlers;

import android.content.Context;
import android.util.Log;
import android.view.View;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;

import org.json.JSONObject;

import a2.mobile.mobileapp.R;
import a2.mobile.mobileapp.activities.MainActivity;
import a2.mobile.mobileapp.data.classes.location.LocationLiveData;
import a2.mobile.mobileapp.data.classes.location.LocationModel;
import a2.mobile.mobileapp.fragments.MainActivityFragment;

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
            Log.e("Navigation Handler", "Longitude " + locationModel.getLongitude()
                    + " Latitude " + locationModel.getLatitude());
        };

        locationData.observe((LifecycleOwner) context, locationObserver);
        view.findViewById(R.id.button_exit_navigation)
                .setOnClickListener(v -> stopNavigation());
    }

    /**
     * Stop the navigation system by removing all observers on the current location.
     */
    public static void stopNavigation() {
        MainActivity.locationViewModel.getLocationData().removeObserver(locationObserver);
        MainActivity.sceneManager.switchScene(R.layout.scene_route_deails);
    }
}
