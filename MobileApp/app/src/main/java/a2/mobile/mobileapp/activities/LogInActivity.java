package a2.mobile.mobileapp.activities;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.SupportMapFragment;
import com.mapbox.mapboxsdk.maps.UiSettings;

import java.util.ArrayList;
import java.util.List;

import a2.mobile.mobileapp.R;
import a2.mobile.mobileapp.adapters.LogInAdapter;
import a2.mobile.mobileapp.common.SpacesItemDecoration;
import a2.mobile.mobileapp.common.login.AuthenticationOption;
import a2.mobile.mobileapp.constants.MapConstants;
import a2.mobile.mobileapp.data.Data;

public class LogInActivity extends FragmentActivity implements OnMapReadyCallback {

    private final String TAG = "Log In";
    public static InputMethodManager INPUT_METHOD_MANAGER;

    private List<AuthenticationOption> authenticationOptions = new ArrayList<>();
    private MapboxMap map;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Mapbox access token is configured here. This needs to be called either in your application
        // object or in the same activity which contains the mapview.
        Mapbox.getInstance(this, MapConstants.MAPBOX_API);
        setContentView(R.layout.activity_log_in);

        // Initial context set up of the Data handler.
        Data.context = this;

        INPUT_METHOD_MANAGER = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment fragment = fragmentManager.findFragmentById(R.id.map_log_in);

        SupportMapFragment mapFragment = (SupportMapFragment) fragment;
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        generateAuthenticationOptions();
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(@NonNull MapboxMap mapboxMap) {
        map = mapboxMap;

        map.moveCamera(CameraUpdateFactory.newLatLngZoom(
                MapConstants.DEFAULT_LOCATION,
                MapConstants.DEFAULT_ZOOM
        ));

        disableUserInteraction();
    }

    /**
     * Prevent the user from moving the map.
     * The map is used for showcase purposes.
     */
    private void disableUserInteraction() {
        UiSettings mapUiSettings = map.getUiSettings();

        mapUiSettings.setZoomGesturesEnabled(false);
        mapUiSettings.setScrollGesturesEnabled(false);
        mapUiSettings.setTiltGesturesEnabled(false);
        mapUiSettings.setRotateGesturesEnabled(false);
    }

    /**
     * Generate the authentication option CardViews.
     */
    private void generateAuthenticationOptions() {
        // Check if the device has a fingerprint sensor.
        PackageManager packageManager = this.getPackageManager();
        if (packageManager.hasSystemFeature(PackageManager.FEATURE_FINGERPRINT)) {
            AuthenticationOption fingerprintCard = new AuthenticationOption(
                    R.string.fingerprint_id,
                    R.string.fingerprint,
                    R.drawable.icon_fingerprint,
                    new Intent(LogInActivity.this, MainActivity.class)
            );

            authenticationOptions.add(fingerprintCard);
        }

        AuthenticationOption passcodeCard = new AuthenticationOption(
                R.string.passcode_id,
                R.string.passcode,
                R.drawable.icon_passcode,
                new Intent(LogInActivity.this, MainActivity.class)
        );

        authenticationOptions.add(passcodeCard);

        RecyclerView authenticationOptionsHolder = findViewById(R.id.authentication_recycler_view);
        LogInAdapter authenticationAdapter = new LogInAdapter(this, authenticationOptions);
        GridLayoutManager layoutManager = new GridLayoutManager(this, 2);

        authenticationOptionsHolder.setAdapter(authenticationAdapter);
        authenticationOptionsHolder.setLayoutManager(layoutManager);
        authenticationOptionsHolder.addItemDecoration(new SpacesItemDecoration(20));

        setNoAccountLink();
    }

    /**
     * Make the desired part of the message to be a link.
     */
    private void setNoAccountLink() {
        // Store the message from the resources and find the index of the word.
        String noAccountMessage = getResources().getString(R.string.welcome_no_account);
        int indexOfClickableWord = noAccountMessage.indexOf("here");

        // Create a clickable span and override its methods.
        SpannableString spannableString = new SpannableString(noAccountMessage);
        ClickableSpan clickableSpan = new ClickableSpan() {

            @Override
            public void onClick(@NonNull View view) {
                startActivity(new Intent(LogInActivity.this, MainActivity.class));
            }

            @Override
            public void updateDrawState(@NonNull TextPaint ds) {
                super.updateDrawState(ds);
                ds.setUnderlineText(false);
            }
        };

        // Create the hyperlink based on the index of the word.
        spannableString.setSpan(
                clickableSpan,
                indexOfClickableWord,
                noAccountMessage.length(),
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        );

        TextView noAccountTextView = findViewById(R.id.body_welcome_no_account);

        noAccountTextView.setText(spannableString);
        noAccountTextView.setMovementMethod(LinkMovementMethod.getInstance());
        noAccountTextView.setHighlightColor(Color.TRANSPARENT);
    }
}
