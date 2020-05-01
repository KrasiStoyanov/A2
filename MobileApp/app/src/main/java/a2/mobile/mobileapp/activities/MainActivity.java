package a2.mobile.mobileapp.activities;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.offline.OfflineManager;
import com.mapbox.services.android.navigation.v5.navigation.MapboxNavigation;

import java.util.ArrayList;

import a2.mobile.mobileapp.R;
import a2.mobile.mobileapp.data.Data;
import a2.mobile.mobileapp.data.classes.location.LocationViewModel;
import a2.mobile.mobileapp.fragments.MainActivityFragment;
import a2.mobile.mobileapp.fragments.MainActivityMapFragment;
import a2.mobile.mobileapp.handlers.RoutesHandler;
import a2.mobile.mobileapp.utils.NavigationUtils;
import a2.mobile.mobileapp.views.MapViewPartial;


public class MainActivity extends AppCompatActivity {

    private static final String TAG = "Main Activity";

    public static MapboxNavigation mapNavigation;

    public static RecyclerView recyclerView;
    @SuppressLint("StaticFieldLeak")
    public static MainActivityFragment sceneManager;
    @SuppressLint("StaticFieldLeak")
    public static MainActivityMapFragment mapManager;
    public static LocationViewModel locationViewModel;

    @SuppressLint("StaticFieldLeak")
    public static ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Mapbox access token is configured here. This needs to be called either in your application
        // object or in the same activity which contains the mapview.
        Mapbox.getInstance(this, getString(R.string.mapbox_access_token));
        setContentView(R.layout.activity_main);

        locationViewModel = new LocationViewModel(getApplication());
        recyclerView = findViewById(R.id.recyclerView);
        progressBar = findViewById(R.id.progressBar);

        Data.context = this;
        Data.getDataFile("points_of_interest.xls");

//        downloadComplete();

//        String fileUrl = "https://github.com/KrasiStoyanov/Robocop/raw/master/MobileApp/app/src/main/assets/points_of_interest.xls";
//        String fileUrlWithoutFileName = "https://github.com/KrasiStoyanov/Robocop/raw/master/MobileApp/app/src/main/assets/";
//
//        Data.fetchDataFile(fileUrl, fileUrlWithoutFileName);

        // Initialize scene and map managers.
        sceneManager = new MainActivityFragment(this);
        mapManager = new MainActivityMapFragment(this);

        if (savedInstanceState == null) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

            transaction.replace(R.id.content_holder, sceneManager);
            transaction.replace(R.id.map_scene_holder, mapManager);
            transaction.commit();
        }

//        NavigationUtils.storeContentHolderView(findViewById(R.id.content_holder));
    }

    /**
     * Handles the result of the request for location permissions.
     */
    @Override
    public void onRequestPermissionsResult(
            int requestCode,
            @NonNull String[] permissions,
            @NonNull int[] grantResults) {

        assert MapViewPartial.permissionsManager != null;
        MapViewPartial.permissionsManager.onRequestPermissionsResult(
                requestCode,
                permissions,
                grantResults
        );
    }

    /**
     * Sets up the options menu.
     *
     * @param menu The options menu.
     * @return Boolean.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.current_place_menu, menu);

        return true;
    }

    /**
     * When the user presses the back button, switch scenes.
     */
    @Override
    public void onBackPressed() {
        sceneManager.goBack();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapManager.onDestroy();
    }
}
