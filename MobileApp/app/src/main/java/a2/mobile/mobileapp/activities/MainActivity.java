package a2.mobile.mobileapp.activities;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.maps.GeoApiContext;
import com.google.maps.android.PolyUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import a2.mobile.mobileapp.R;
import a2.mobile.mobileapp.constants.MapConstants;
import a2.mobile.mobileapp.data.Data;
import a2.mobile.mobileapp.data.classes.Point;
import a2.mobile.mobileapp.data.classes.Route;
import a2.mobile.mobileapp.fragments.MainActivityFragment;
import a2.mobile.mobileapp.fragments.RoutesHandler;




public class MainActivity extends AppCompatActivity
        implements OnMapReadyCallback ,View.OnClickListener
        {

    private static final String TAG = "Main Activity";
    private static final float POLYLINE_WIDTH = 1;
    private static final String LINE_BLUE = "lineBlue";
    private GeoApiContext GeoApiContext = null;
    private static final String MAPVIEW_BUNDLE_KEY = "MapViewBundleKey";
    private Polyline mMutablePolylineBlue;
    public static final String LAT_LNG_POINT = "latLngPoint";
    public static final String ENCODED_POINTS = "encodedPoints";
    private static final String LOG_TAG = MainActivity.class.getName();
    private static final String Bytesnet = "53.244755, 6.527290";
    private static final String ZernikeCampus = "53.240768, 6.534017";
    private TextView TextView;
    private MapView mMapView;

    private GoogleMap map;
    private CameraPosition cameraPosition;
    private static final int overview = 0;
    // The entry point to the Places API.
    private PlacesClient placesClient;

    // The entry point to the Fused Location Provider.
    private FusedLocationProviderClient fusedLocationProviderClient;

    private boolean locationPermissionGranted;

    //public String url = "https://maps.googleapis.com/maps/api/directions/json?\n" +
           // "origin=" + origin + "&destination=" + destination + "&mode=walking&key=YOUR_API_KEY";

    // The geographical location where the device is currently located. That is, the last-known
    // location retrieved by the Fused Location Provider.
    private Location lastKnownLocation;

    public static RecyclerView recyclerView;

    public ProgressBar progressBar;
    public TextView wait;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recyclerView);
        progressBar = findViewById(R.id.progressBar);
        wait = findViewById(R.id.wait);

        Data.context = this;
        Data.getDataFile("points_of_interest.xls");

//        downloadComplete();

//        String fileUrl = "https://github.com/KrasiStoyanov/Robocop/raw/master/MobileApp/app/src/main/assets/points_of_interest.xls";
//        String fileUrlWithoutFileName = "https://github.com/KrasiStoyanov/Robocop/raw/master/MobileApp/app/src/main/assets/";
//
//        Data.fetchDataFile(fileUrl, fileUrlWithoutFileName);

        // Retrieve location and camera position from saved instance state.
        if (savedInstanceState != null) {
            lastKnownLocation = savedInstanceState.getParcelable(MapConstants.KEY_LOCATION);
            cameraPosition = savedInstanceState.getParcelable(MapConstants.KEY_CAMERA_POSITION);
        }

        // Construct a PlacesClient
        Places.initialize(getApplicationContext(), getString(R.string.google_maps_key));
        placesClient = Places.createClient(this);

        // Construct a FusedLocationProviderClient.
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        // Build the map.
        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment fragment = fragmentManager.findFragmentById(R.id.map);

        SupportMapFragment mapFragment = (SupportMapFragment) fragment;
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        if (savedInstanceState == null) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            MainActivityFragment mainActivityFragment = new MainActivityFragment();

            transaction.replace(R.id.content_holder, mainActivityFragment);
            transaction.commit();
        }
    }



    /**
     * Saves the state of the map when the activity is paused.
     */
    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        if (map != null) {
            outState.putParcelable(MapConstants.KEY_CAMERA_POSITION, map.getCameraPosition());
            outState.putParcelable(MapConstants.KEY_LOCATION, lastKnownLocation);

            super.onSaveInstanceState(outState);
        }
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
     * Manipulates the map when it's available.
     * This callback is triggered when the map is ready to be used.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        wait.setVisibility(View.GONE);
        progressBar.setVisibility(View.GONE);

        map = googleMap;
        setupGoogleMapScreenSettings(googleMap);
        //draw routes

        // Pin all start and end points of all routes on the map.
        for (Route route : Data.routes) {
            Point startPoint = route.startPoint;
            Point endPoint = route.endPoint;

            generateRouteMarker(startPoint);
            generateRouteMarker(endPoint);
        }

        // Use a custom info window adapter to handle multiple lines of text in the
        // info window contents.
        map.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {

            @Override
            // Return null here, so that getInfoContents() is called next.
            public View getInfoWindow(Marker arg0) {
                return null;
            }

            @Override
            public View getInfoContents(Marker marker) {
                // Inflate the layouts for the info window, title and snippet.
                LayoutInflater layoutInflater = getLayoutInflater();
                FrameLayout frameLayout = findViewById(R.id.map);

                View infoWindow = layoutInflater.inflate(R.layout.custom_info_contents, frameLayout, false);

                TextView title = infoWindow.findViewById(R.id.title);
                title.setText(marker.getTitle());

                TextView snippet = infoWindow.findViewById(R.id.snippet);
                snippet.setText(marker.getSnippet());

                return infoWindow;
            }
        });

        // Prompt the user for permission.
        getLocationPermission();

        // Turn on the My Location layer and the related control on the map.
        updateLocationUI();

        // Get the current location of the device and set the position of the map.
        getDeviceLocation();

        // After the Google Map has been generated show the routes list.
        showRoutes();
        FetchUrl();

    }
    private void setupGoogleMapScreenSettings(GoogleMap map) {
        map.setBuildingsEnabled(true);
        map.setIndoorEnabled(true);
        UiSettings mUiSettings = map.getUiSettings();
        mUiSettings.setZoomControlsEnabled(true);
        mUiSettings.setCompassEnabled(true);
        mUiSettings.setMyLocationButtonEnabled(true);
        mUiSettings.setScrollGesturesEnabled(true);
        mUiSettings.setZoomGesturesEnabled(true);
        mUiSettings.setTiltGesturesEnabled(true);
        mUiSettings.setRotateGesturesEnabled(true);
    }
    /**
     * Generate a route marker and add it to the Google Map.
     * @param point the current point that holds the coordinates
     */
    private void generateRouteMarker(Point point) {
        List<Double> coordinates = point.coordinates;
        String title = point.title;
        String interest = point.interest;

        // Create a new instance of a marker based on the coordinates from the point of interest.
        LatLng coordinatesMarker = new LatLng(coordinates.get(0), coordinates.get(1));
        MarkerOptions marker = new MarkerOptions();

        // Settings for the marker.
        marker.position(coordinatesMarker);
        marker.title(title);
        marker.snippet(interest);

        map.addMarker(marker);
    }

    private void FetchUrl(){
        // Initialize a new RequestQueue instance

        RequestQueue requestQueue = Volley.newRequestQueue(Data.context);
        List<LatLng> latLngList = new ArrayList<>();
        String url = "https://maps.googleapis.com/maps/api/directions/json?origin=53.244755,%206.527290&destination=53.240768,%206.534017&mode=walking&key=AIzaSyD-K7BXFSfMYhL-uS5GrlO7buKRBzHqdCM";
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                       // TextView.setText("Response: " + response.toString());
                         try{

                             for(int i =0; i<response.length();i++) {
                                 // Get the JSON array
                                 JSONArray routes = response.getJSONArray("routes");
                                 JSONObject polyline = routes.getJSONObject(i).getJSONObject("overview_polyline");
                                 String points = polyline.getString("points");
                                 Log.e(LOG_TAG,points);
                                 latLngList.addAll(PolyUtil.decode(points.trim().replace("\\\\", "\\")));
                                 mMutablePolylineBlue = map.addPolyline(new PolylineOptions()
                                         .color(getResources().getColor(R.color.colorPolyLineBlue))
                                         .width(20f)
                                         .clickable(false)
                                         .addAll(latLngList));


                             /*while(keys.hasNext()) {
                                 String key = keys.next();
                                 if (object.get(key) instanceof JSONObject) {
                                     // do something with jsonObject here
                                 }

                                 // Get current json object
                                 //JSONObject point = object.getJSONObject(i);

                                 // Get the current student (json object) data
                                 JSONArray points = object.getJSONArray("overview_polyline");
                                 String routes = points.getString("points");

                                 latLngList.addAll(PolyUtil.decode(routes.replace("\\\\", "\\")));
                                 Log.e("Sample Tag", "Coordinates " + latLngList);
                                 // Display the formatted json data in text view
                                 //TextView.append(points);

                             }*/
                             }
                             }catch (JSONException e){
                            e.printStackTrace();
                        }
                    }

                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d(LOG_TAG,"error");
                    }
                });

        requestQueue.add(jsonObjectRequest);



    }

    /**
     * Gets the current location of the device, and positions the map's camera.
     */
    private void getDeviceLocation(){
        /*
         * Get the best and most recent location of the device, which may be null in rare
         * cases when a location is not available.
         */
        try {
            if (locationPermissionGranted) {
                Task<Location> locationResult = fusedLocationProviderClient.getLastLocation();

                locationResult.addOnCompleteListener(this, new OnCompleteListener<Location>() {

                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        if (task.isSuccessful()) {
                            // Set the map's camera position to the current location of the device.
                            lastKnownLocation = task.getResult();

                            if (lastKnownLocation != null) {
                                LatLng lastKnownLocationCoordinates = new LatLng(
                                        lastKnownLocation.getLatitude(),
                                        lastKnownLocation.getLongitude()
                                );

                                map.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                        lastKnownLocationCoordinates,
                                        MapConstants.DEFAULT_ZOOM)
                                );
                            }
                        } else {
                            Log.d(TAG, "Current location is null. Using defaults.");
                            Log.e(TAG, "Exception: %s", task.getException());

                            map.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                    MapConstants.DEFAULT_LOCATION,
                                    MapConstants.DEFAULT_ZOOM)
                            );

                            map.getUiSettings().setMyLocationButtonEnabled(false);
                        }
                    }
                });
            }
        } catch (SecurityException e) {
            Log.e("Exception: %s", e.getMessage());
        }
    }

    /**
     * Prompts the user for permission to use the device location.
     */
    private void getLocationPermission() {
        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */

        int locationPermission = ContextCompat.checkSelfPermission(
                this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION
        );

        if (locationPermission == PackageManager.PERMISSION_GRANTED) {
            locationPermissionGranted = true;
        } else {
            String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION};
            ActivityCompat.requestPermissions(
                    this,
                    permissions,
                    MapConstants.PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    /**
     * Handles the result of the request for location permissions.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        locationPermissionGranted = false;

        if (requestCode == MapConstants.PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION) {
            // If request is cancelled, the result arrays are empty.
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                locationPermissionGranted = true;
            }
        }

        updateLocationUI();
    }

    /**
     * Updates the map's UI settings based on whether the user has granted location permission.
     */
    private void updateLocationUI() {
        if (map == null) {
            return;
        }

        try {
            if (locationPermissionGranted) {
                map.setMyLocationEnabled(true);
                map.getUiSettings().setMyLocationButtonEnabled(true);
            } else {
                map.setMyLocationEnabled(false);
                map.getUiSettings().setMyLocationButtonEnabled(false);

                lastKnownLocation = null;

                getLocationPermission();
            }
        } catch (SecurityException e) {
            Log.e("Exception: %s", e.getMessage());
        }
    }

    /**
     * Show all routes on the UI.
     */
    private void showRoutes() {
        RoutesHandler.handleRoutes(
                this,
                findViewById(R.id.scene_manager),
                Data.routes,
                map
        );
    }

    @Override
    public void onClick(View v) {

    }

}
