package a2.mobile.mobileapp.views;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.AbsoluteSizeSpan;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.mapbox.api.directions.v5.models.BannerInstructions;
import com.mapbox.api.directions.v5.models.DirectionsRoute;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.services.android.navigation.ui.v5.NavigationView;
import com.mapbox.services.android.navigation.ui.v5.NavigationViewOptions;
import com.mapbox.services.android.navigation.ui.v5.OnNavigationReadyCallback;
import com.mapbox.services.android.navigation.ui.v5.listeners.BannerInstructionsListener;
import com.mapbox.services.android.navigation.ui.v5.listeners.InstructionListListener;
import com.mapbox.services.android.navigation.ui.v5.listeners.NavigationListener;
import com.mapbox.services.android.navigation.ui.v5.listeners.SpeechAnnouncementListener;
import com.mapbox.services.android.navigation.ui.v5.voice.SpeechAnnouncement;
import com.mapbox.services.android.navigation.v5.routeprogress.ProgressChangeListener;
import com.mapbox.services.android.navigation.v5.routeprogress.RouteProgress;

import a2.mobile.mobileapp.R;
import a2.mobile.mobileapp.utils.MapUtils;
import a2.mobile.mobileapp.utils.NavigationUtils;

public class NavigationViewPartial extends CoordinatorLayout
        implements OnNavigationReadyCallback, NavigationListener, ProgressChangeListener,
        InstructionListListener, SpeechAnnouncementListener, BannerInstructionsListener {

    private static final Point ORIGIN = Point.fromLngLat(
            6.527253,
            53.244792
    );

    private static final Point DESTINATION = Point.fromLngLat(
            6.531705,
            53.240256
    );

    private Context context;
    @SuppressLint("StaticFieldLeak")
    private static NavigationView navigationView;

    private static boolean instructionListShown = false;

    public NavigationViewPartial(Context rootContext, AttributeSet attributeSet) {
        super(rootContext, attributeSet);

        inflate(rootContext, R.layout.custom_navigation_view, this);

        context = rootContext;

        CameraPosition initialPosition = new CameraPosition.Builder()
                .target(new LatLng(ORIGIN.latitude(), ORIGIN.longitude()))
                .zoom(16)
                .build();

        navigationView = findViewById(R.id.mapbox_navigation);
        navigationView.initialize(this);
    }

    @Override
    public void onNavigationReady(boolean isRunning) {
        NavigationUtils.storeNavigationView(navigationView);
//        NavigationUtils.startNavigation(context);
        startNavigation(MapUtils.navigationRoute);
    }

    @Override
    public void onCancelNavigation() {

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
        setSpeed(location);
    }

    @Override
    public BannerInstructions willDisplay(BannerInstructions instructions) {
        return instructions;
    }

    @Override
    public void onInstructionListVisibilityChanged(boolean visible) {

    }

    @Override
    public SpeechAnnouncement willVoice(SpeechAnnouncement announcement) {
        return SpeechAnnouncement.builder()
                .announcement("All announcements will be the same.")
                .build();
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

//        navigationView.startNavigation(options.build());
    }

    private void setSpeed(Location location) {
        String string = String.format("%d\nMPH", (int) (location.getSpeed() * 2.2369));
        int mphTextSize = context.getResources().getDimensionPixelSize(R.dimen.body_font_size);
        int speedTextSize = context.getResources().getDimensionPixelSize(R.dimen.h4);

        SpannableString spannableString = new SpannableString(string);
        spannableString.setSpan(new AbsoluteSizeSpan(mphTextSize),
                string.length() - 4, string.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);

        spannableString.setSpan(new AbsoluteSizeSpan(speedTextSize),
                0, string.length() - 3, Spanned.SPAN_INCLUSIVE_INCLUSIVE);

        TextView speedWidget = ((Activity)context).findViewById(R.id.title);
        speedWidget.setText(spannableString);
        if (!instructionListShown) {
            speedWidget.setVisibility(View.VISIBLE);
        }
    }

    /**
     * Stop the navigation.
     */
    public static void stopNavigation() {
        navigationView.stopNavigation();
    }
}
