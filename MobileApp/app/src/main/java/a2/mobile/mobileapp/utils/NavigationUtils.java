package a2.mobile.mobileapp.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.location.Location;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.AbsoluteSizeSpan;
import android.view.View;
import android.widget.TextView;

import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.services.android.navigation.ui.v5.NavigationView;
import com.mapbox.services.android.navigation.ui.v5.NavigationViewOptions;
import com.mapbox.services.android.navigation.ui.v5.listeners.NavigationListener;
import com.mapbox.services.android.navigation.v5.navigation.MapboxNavigation;

import a2.mobile.mobileapp.R;

public class NavigationUtils {
    @SuppressLint("StaticFieldLeak")
    private static View contentHolderView;
    @SuppressLint("StaticFieldLeak")
    private static MapView mapView;
    @SuppressLint("StaticFieldLeak")
    private static NavigationView navigationView;
    private static MapboxNavigation navigation;

    public static boolean isNavigationActive = false;

    private static boolean instructionListShown = false;

    /**
     * Store the root View instance.
     *
     * @param view The root View instance
     */
    public static void storeContentHolderView(View view) {
        contentHolderView = view;
    }

    /**
     * Store the Navigation View instance.
     *
     * @param view The Navigation View instance
     */
    public static void storeNavigationView(NavigationView view) {
        navigationView = view;
    }

    /**
     * Store the mapbox navigation instance.
     *
     * @param mapboxNavigation The mapbox navigation instance
     */
    public static void storeNavigationInstance(MapboxNavigation mapboxNavigation) {
        navigation = mapboxNavigation;
    }

    public static void startNavigation(Context context) {
        NavigationViewOptions options = NavigationViewOptions.builder()
                .directionsRoute(MapUtils.navigationRoute)
                .shouldSimulateRoute(true)
                .navigationListener(new NavigationListener() {
                    @Override
                    public void onCancelNavigation() {

                    }

                    @Override
                    public void onNavigationFinished() {

                    }

                    @Override
                    public void onNavigationRunning() {

                    }
                })
                .progressChangeListener(((location, routeProgress) -> setSpeed(context, location)))
                .build();

//        navigationView.startNavigation(options);

        isNavigationActive = true;
    }

    private static void setSpeed(Context context, Location location) {
        String string = String.format("%d\nMPH", (int) (location.getSpeed() * 2.2369));
        int mphTextSize = context.getResources().getDimensionPixelSize(R.dimen.body_font_size);
        int speedTextSize = context.getResources().getDimensionPixelSize(R.dimen.h4);

        SpannableString spannableString = new SpannableString(string);
        spannableString.setSpan(new AbsoluteSizeSpan(mphTextSize),
                string.length() - 4, string.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);

        spannableString.setSpan(new AbsoluteSizeSpan(speedTextSize),
                0, string.length() - 3, Spanned.SPAN_INCLUSIVE_INCLUSIVE);

        TextView speedWidget = contentHolderView.findViewById(R.id.title);
        speedWidget.setText(spannableString);
        if (!instructionListShown) {
            speedWidget.setVisibility(View.VISIBLE);
        }
    }
}
