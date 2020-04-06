package a2.mobile.mobileapp.activities;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.geometry.LatLngBounds;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.Style;
import com.mapbox.mapboxsdk.offline.OfflineManager;
import com.mapbox.mapboxsdk.offline.OfflineRegion;
import com.mapbox.mapboxsdk.offline.OfflineRegionError;
import com.mapbox.mapboxsdk.offline.OfflineRegionStatus;
import com.mapbox.mapboxsdk.offline.OfflineTilePyramidRegionDefinition;

import org.json.JSONObject;

import java.util.Objects;

import a2.mobile.mobileapp.R;
import a2.mobile.mobileapp.constants.MapConstants;

public class TestMapActivity extends AppCompatActivity {

    private boolean isEndNotified;
    private ProgressBar progressBar;
    private MapView mapView;
    private OfflineManager offlineManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Mapbox access token is configured here. This needs to be called either in your application
        // object or in the same activity which contains the mapview.
        Mapbox.getInstance(this, MapConstants.MAPBOX_API);

        // This contains the MapView in XML and needs to be called after the access token is configured.
        setContentView(R.layout.activity_test_map);

        mapView = findViewById(R.id.mapbox);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(mapboxMap -> mapboxMap.setStyle(Style.OUTDOORS, style -> {

            // Set up the OfflineManager
            offlineManager = OfflineManager.getInstance(TestMapActivity.this);

            // Create a bounding box for the offline region
            LatLngBounds latLngBounds = new LatLngBounds.Builder()
                    .include(new LatLng(
                            MapConstants.MAP_REGION_MIN_LAT,
                            MapConstants.MAP_REGION_MIN_LON)
                    )
                    .include(new LatLng(
                            MapConstants.MAP_REGION_MAX_LAT,
                            MapConstants.MAP_REGION_MAX_LON)
                    )
                    .build();

            // Define the offline region
            OfflineTilePyramidRegionDefinition definition = new OfflineTilePyramidRegionDefinition(
                    style.getUri(),
                    latLngBounds,
                    MapConstants.MIN_ZOOM,
                    MapConstants.MAX_ZOOM,
                    TestMapActivity.this.getResources().getDisplayMetrics().density
            );

            // Set the metadata
            byte[] metadata;
            try {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put(MapConstants.MAP_JSON_FIELD_REGION_NAME, MapConstants.MAP_NAME);

                String json = jsonObject.toString();
                metadata = json.getBytes(MapConstants.MAP_JSON_CHARSET);
            } catch (Exception exception) {
                Log.e("Failed to encode metadata: %s", Objects.requireNonNull(exception.getMessage()));
                metadata = null;
            }

            // Create the region asynchronously
            if (metadata != null) {
                offlineManager.createOfflineRegion(
                        definition,
                        metadata,
                        new OfflineManager.CreateOfflineRegionCallback() {

                            @Override
                            public void onCreate(OfflineRegion offlineRegion) {
                                offlineRegion.setDownloadState(OfflineRegion.STATE_ACTIVE);

                                // Display the download progress bar
                                progressBar = findViewById(R.id.progress_bar);
                                startProgress();

                                observeDownloadingProcess(offlineRegion);
                            }

                            @Override
                            public void onError(String error) {
                                Log.e("Error: %s", error);
                            }
                        });
            }
        }));
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();

        if (offlineManager != null) {
            offlineManager.listOfflineRegions(new OfflineManager.ListOfflineRegionsCallback() {

                @Override
                public void onList(OfflineRegion[] offlineRegions) {
                    for (OfflineRegion offlineRegion : offlineRegions) {
                        offlineRegion.delete(new OfflineRegion.OfflineRegionDeleteCallback() {

                            @Override
                            public void onDelete() {
                                Toast.makeText(
                                        TestMapActivity.this,
                                        "Offline map deleted.",
                                        Toast.LENGTH_LONG
                                ).show();
                            }

                            @Override
                            public void onError(String error) {
                                Log.e("On delete error: %s", error);
                            }
                        });
                    }
                }

                @Override
                public void onError(String error) {
                    // Log.e("onListError: %s", error);
                }
            });
        }
    }

    /**
     * Monitor the downloading process using an observer.
     *
     * @param offlineRegion The region to observe while downloading.
     */
    private void observeDownloadingProcess(OfflineRegion offlineRegion) {
        offlineRegion.setObserver(new OfflineRegion.OfflineRegionObserver() {
            @Override
            public void onStatusChanged(OfflineRegionStatus status) {

                // Calculate the download percentage and update the progress bar
                double percentage = status.getRequiredResourceCount() >= 0
                        ? (100.0 * status.getCompletedResourceCount() / status.getRequiredResourceCount()) :
                        0.0;

                if (status.isComplete()) {
                    // Download complete
                    endProgress();
                } else if (status.isRequiredResourceCountPrecise()) {
                    // Switch to determinate state
                    setPercentage((int) Math.round(percentage));
                }
            }

            @Override
            public void onError(OfflineRegionError error) {
                // If an error occurs, print to logcat
                Log.e("onError reason: %s", error.getReason());
                Log.e("onError message: %s", error.getMessage());
            }

            @Override
            public void mapboxTileCountLimitExceeded(long limit) {
                // Notify if offline region exceeds maximum tile count
                Log.e("Mapbox tile count limit exceeded: %s", String.valueOf(limit));
            }
        });
    }

    // Progress bar methods
    private void startProgress() {

        // Start and show the progress bar
        isEndNotified = false;
        progressBar.setIndeterminate(true);
        progressBar.setVisibility(View.VISIBLE);
    }

    private void setPercentage(final int percentage) {
        progressBar.setIndeterminate(false);
        progressBar.setProgress(percentage);
    }

    private void endProgress() {
        // Don't notify more than once
        if (isEndNotified) {
            return;
        }

        // Stop and hide the progress bar
        isEndNotified = true;
        progressBar.setIndeterminate(false);
        progressBar.setVisibility(View.GONE);

        // Show a toast
        Toast.makeText(TestMapActivity.this, "Region downloaded successfully!", Toast.LENGTH_LONG).show();
    }
}