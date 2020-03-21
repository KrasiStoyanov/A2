package a2.mobile.mobileapp;

import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.net.PlacesClient;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

public class LogIn extends FragmentActivity implements OnMapReadyCallback {

    private final String TAG = "Log In";
    private GoogleMap map;

    // The entry point to the Places API.
    private PlacesClient placesClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);

        // Construct a PlacesClient
        Places.initialize(getApplicationContext(), getString(R.string.google_maps_key));
        placesClient = Places.createClient(this);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment fragment = fragmentManager.findFragmentById(R.id.map_log_in);

        SupportMapFragment mapFragment = (SupportMapFragment) fragment;
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        List<AuthenticationOption> authenticationOptions = new ArrayList<>();

        // Check if the device has a fingerprint sensor.
        PackageManager packageManager = this.getPackageManager();
        if (packageManager.hasSystemFeature(PackageManager.FEATURE_FINGERPRINT)) {
            AuthenticationOption fingerprintCard = new AuthenticationOption(
                    R.string.fingerprint,
                    R.drawable.icon_fingerprint
            );

            authenticationOptions.add(fingerprintCard);
        }

        AuthenticationOption passcodeCard = new AuthenticationOption(
                R.string.passcode,
                R.drawable.icon_passcode
        );

        authenticationOptions.add(passcodeCard);

        RecyclerView authenticationOptionsHolder = findViewById(R.id.authentication_recycler_view);
        LogInAdapter authenticationAdapter = new LogInAdapter(this, authenticationOptions);
        GridLayoutManager layoutManager = new GridLayoutManager(this, 2);

        authenticationOptionsHolder.setAdapter(authenticationAdapter);
        authenticationOptionsHolder.setLayoutManager(layoutManager);
        authenticationOptionsHolder.addItemDecoration(new SpacesItemDecoration(20));
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
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;

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
}
