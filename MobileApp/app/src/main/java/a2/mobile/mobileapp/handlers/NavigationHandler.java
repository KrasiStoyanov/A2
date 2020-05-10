package a2.mobile.mobileapp.handlers;

import android.app.Activity;

import com.mapbox.services.android.navigation.ui.v5.NavigationLauncher;
import com.mapbox.services.android.navigation.ui.v5.NavigationLauncherOptions;

import a2.mobile.mobileapp.utils.MapUtils;

public class NavigationHandler {
    public static void startNavigation(Activity activity) {
        if (MapUtils.navigationRoute != null) {
            NavigationLauncherOptions options = NavigationLauncherOptions.builder()
                    .directionsRoute(MapUtils.navigationRoute)
                    .shouldSimulateRoute(true)
                    .build();

            NavigationLauncher.startNavigation(
                    activity,
                    options
            );
        }
    }
}
