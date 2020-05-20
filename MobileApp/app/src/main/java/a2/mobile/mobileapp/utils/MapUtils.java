package a2.mobile.mobileapp.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.mapbox.api.directions.v5.DirectionsCriteria;
import com.mapbox.api.directions.v5.models.DirectionsResponse;
import com.mapbox.api.directions.v5.models.DirectionsRoute;
import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;
import com.mapbox.geojson.LineString;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.Style;
import com.mapbox.mapboxsdk.maps.UiSettings;
import com.mapbox.mapboxsdk.style.layers.LineLayer;
import com.mapbox.mapboxsdk.style.layers.Property;
import com.mapbox.mapboxsdk.style.layers.SymbolLayer;
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource;
import com.mapbox.services.android.navigation.v5.navigation.NavigationRoute;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.concurrent.RunnableFuture;

import a2.mobile.mobileapp.R;
import a2.mobile.mobileapp.constants.MapConstants;
import a2.mobile.mobileapp.data.Data;
import a2.mobile.mobileapp.data.classes.PointOfInterest;
import retrofit2.Call;
import retrofit2.Callback;
import timber.log.Timber;

import static com.mapbox.core.constants.Constants.PRECISION_6;
import static com.mapbox.mapboxsdk.style.expressions.Expression.eq;
import static com.mapbox.mapboxsdk.style.expressions.Expression.get;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconAllowOverlap;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconIgnorePlacement;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconImage;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconOffset;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconSize;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.lineCap;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.lineColor;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.lineJoin;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.lineWidth;

public class MapUtils {
    public static MapboxMap map;
    private static Style.Builder mapStyle;
    public static DirectionsRoute navigationRoute;

    private static GeoJsonSource mapMarkerSource;
    private static GeoJsonSource mapRouteSource;

    private static JSONObject pointsOfInterestJSONObject;
    private static List<JSONObject> hiddenPointsOfInterestForCurrentRoute = new ArrayList<>();
    public static List<Point> currentNavigationPoints = new ArrayList<>();

    private static List<Feature> routeMarkers = new ArrayList<>();
    private static final Runnable clearRouteMarkersRunnable = () -> {
        map.getStyle(style -> {
            style.removeSource(MapConstants.MAP_ROUTE_LAYER_SOURCE_ID);
            style.removeLayer(MapConstants.MAP_ROUTE_LAYER_ID);

            style.removeSource(MapConstants.MAP_ROUTE_MARKER_LAYER_SOURCE_ID);
            style.removeLayer(MapConstants.MAP_ROUTE_MARKER_LAYER_ID);
        });

        routeMarkers.clear();
    };

    /**
     * Retrieve the current map style.
     *
     * @return The stored map style
     */
    public static Style.Builder getMapStyle() {
        return mapStyle;
    }

    /**
     * Globally store the loaded map instance.
     *
     * @param mapboxMap The loaded map
     */
    public static void storeMapInstance(MapboxMap mapboxMap) {
        map = mapboxMap;
    }

    /**
     * Set the global style of the map
     *
     * @param context The MainActivity context
     */
    public static void setMapStyle(Context context) {
        String jsonObject = FileUtils.loadJSONFromAsset(
                context,
                "groningen.json"
        );

        String pointsOfInterest = FileUtils.loadJSONFromAsset(
                context,
                MapConstants.MAP_POINTS_OF_INTEREST_GEOJSON_FILENAME
        );

        assert jsonObject != null && pointsOfInterest != null;
        mapStyle = new Style.Builder()
                .fromJson(jsonObject)
                .withImage(MapConstants.MAP_MARKER_ID, BitmapFactory.decodeResource(
                        context.getResources(), R.drawable.red_marker
                ));

        try {
            pointsOfInterestJSONObject = new JSONObject(pointsOfInterest);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        map.setStyle(mapStyle, style -> {
            UiSettings uiSettings = map.getUiSettings();

            uiSettings.setCompassEnabled(true);
            uiSettings.setAllGesturesEnabled(true);

            GeoJsonSource pointsOfInterestSource = new GeoJsonSource(
                    MapConstants.MAP_POINTS_OF_INTEREST_LAYER_SOURCE_ID
            );

            pointsOfInterestSource.setGeoJson(pointsOfInterest);
            style.addSource(pointsOfInterestSource);

            // Get the data and store it locally.
            Data.storePointsOfInterestDataSet(pointsOfInterest);
            Data.getDataFile("points_of_interest.xls");
        });
    }

    /**
     * Create a new route symbol layer.
     *
     * @return The newly created symbol layer
     */
    private static LineLayer setRouteLayer(Context context) {

        return new LineLayer(
                MapConstants.MAP_ROUTE_LAYER_ID,
                MapConstants.MAP_ROUTE_LAYER_SOURCE_ID
        ).withProperties(
                lineCap(Property.LINE_CAP_ROUND),
                lineJoin(Property.LINE_JOIN_ROUND),
                lineWidth(5f),
                lineColor(ContextCompat.getColor(context, R.color.primary))
        );
    }

    /**
     * Create a new route symbol layer.
     *
     * @return The newly created symbol layer
     */
    private static SymbolLayer setRouteMarkerLayer() {

        return new SymbolLayer(
                MapConstants.MAP_ROUTE_MARKER_LAYER_ID,
                MapConstants.MAP_ROUTE_MARKER_LAYER_SOURCE_ID
        ).withProperties(
                iconImage(MapConstants.MAP_MARKER_ID),
                iconSize(0.5f),
                iconAllowOverlap(true),
                iconIgnorePlacement(true),
                iconOffset(new Float[]{0f, -9f})
        );
    }

    /**
     * Add route markers to the list that will be rendered later on.
     *
     * @param marker The marker to be added to the list
     */
    public static void addRouteMarker(@NonNull Feature marker) {
        routeMarkers.add(marker);
    }

    /**
     * Clear the map from the route symbol layer and the route markers.
     */
    public static void clearRouteMarkers(Context context) {
        RunnableFuture<Void> task = new FutureTask<>(clearRouteMarkersRunnable, null);
        ((Activity) context).runOnUiThread(task);
        try {
            task.get(); // this will block until Runnable completes
            currentNavigationPoints.clear();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }

    /**
     * Render the route markers list in a newly created route symbol layer.
     */
    public static void updateRoute(
            Context context,
            Point origin,
            Point destination,
            List<PointOfInterest> interestPoints) {

        // Clear all route markers on the map first if there are any.
        MapUtils.clearRouteMarkers(context);

        currentNavigationPoints.add(origin);
        for (PointOfInterest point : interestPoints) {
            Point a = Point.fromLngLat(point.coordinates.get(0), point.coordinates.get(1));
            currentNavigationPoints.add(Point.fromLngLat(
                    point.coordinates.get(0),
                    point.coordinates.get(1)
            ));
        }

        currentNavigationPoints.add(destination);

        map.getStyle(style -> {
            List<Feature> points = new ArrayList<>();
            points.add(Feature.fromGeometry(origin));
            for (a2.mobile.mobileapp.data.classes.Point interestPoint : interestPoints) {
                List<Double> coordinates = interestPoint.coordinates;
                Point point = Point.fromLngLat(coordinates.get(1), coordinates.get(0));
                Feature feature = Feature.fromGeometry(point);

                points.add(feature);
            }

//            points.add(Feature.fromGeometry(destination));
            mapMarkerSource = new GeoJsonSource(
                    MapConstants.MAP_ROUTE_MARKER_LAYER_SOURCE_ID,
                    FeatureCollection.fromFeatures(points)
            );

            mapRouteSource = new GeoJsonSource(MapConstants.MAP_ROUTE_LAYER_SOURCE_ID);
            SymbolLayer pointsOfInterestLayer = (SymbolLayer) style.getLayer(
                    "points of interest"
            );

            assert pointsOfInterestLayer != null;
            pointsOfInterestLayer.setFilter(eq(get("zone"), Data.selectedRoute.zone));

            style.addSource(mapRouteSource);
            style.addSource(mapMarkerSource);

            style.addLayer(setRouteLayer(context));
            style.addLayer(setRouteMarkerLayer());

            renderRouteLayer(context, origin, destination);
        });
    }

    public static void toggleInterestPointVisibility(Context context) {
        map.getStyle(style -> {
            GeoJsonSource markerSource = style
                    .getSourceAs(MapConstants.MAP_ROUTE_MARKER_LAYER_SOURCE_ID);


            Toast.makeText(context, "ASDASDASD" + markerSource, Toast.LENGTH_LONG).show();
            if (markerSource != null) {
                List<Feature> features = markerSource.querySourceFeatures(
                        get("marker")
                );
            }
        });
    }

    /**
     * Render the route line layer based on the origin and destination points provided.
     *
     * @param context     The MainActivity context
     * @param origin      The origin point
     * @param destination The destination point
     */
    private static void renderRouteLayer(Context context, Point origin, Point destination) {
        NavigationRoute.Builder options = NavigationRoute.builder(context)
                .accessToken(context.getString(R.string.mapbox_access_token))
                .origin(origin)
                .destination(destination)
                .alternatives(true)
                .profile(DirectionsCriteria.PROFILE_WALKING);

        for (Point point : currentNavigationPoints) {
            options.addWaypoint(point);
        }

        options.build().getRoute(new Callback<DirectionsResponse>() {
            @Override
            public void onResponse(
                    @NonNull Call<DirectionsResponse> call,
                    @NonNull retrofit2.Response<DirectionsResponse> response) {

                // You can get the generic HTTP info about the response
                Timber.e("Response code: %s", response.code());
                if (response.body() == null) {
                    Timber.e("No routes found, make sure you set the right user and access token.");
                    return;
                } else if (response.body().routes().size() < 1) {
                    Timber.e("No routes found");
                    return;
                }

                // Get the directions route
                DirectionsRoute currentRoute = response.body().routes().get(0);
                map.getStyle(style -> {
                    // Retrieve and update the source designated for showing the directions route
                    GeoJsonSource source = style.getSourceAs(MapConstants.MAP_ROUTE_LAYER_SOURCE_ID);

                    // Create a LineString with the directions route's geometry and
                    // reset the GeoJSON source for the route LineLayer source
                    if (source != null) {
                        source.setGeoJson(LineString.fromPolyline(
                                Objects.requireNonNull(currentRoute.geometry()),
                                PRECISION_6
                        ));
                    }
                });

                navigationRoute = currentRoute;
            }

            @Override
            public void onFailure(Call<DirectionsResponse> call, Throwable t) {

            }
        });
    }

    public static void toggleInterestPointVisibility(
            a2.mobile.mobileapp.data.classes.Point point,
            boolean visible) {

        map.getStyle(style -> {
            SymbolLayer pointsOfInterestLayer = (SymbolLayer) style.getLayer(
                    MapConstants.MAP_POINTS_OF_INTEREST_LAYER_ID
            );

            try {
                JSONArray features = pointsOfInterestJSONObject.getJSONArray("features");
                List<JSONObject> visiblePointsOfInterest = new ArrayList<>();

                for (int index = 0; index < features.length(); index += 1) {
                    JSONObject pointOfInterest = features.getJSONObject(index);
                    boolean pointOfInterestVisible = true;

                    int zone = pointOfInterest.getJSONObject("properties").getInt("zone");
                    String title = pointOfInterest.getJSONObject("properties")
                            .getString("title");

                    if (zone == Data.selectedRoute.zone) {

                        Log.e("Features", "Zone " + zone + "; Title " + title);
                        int indexOfPointOfInterest = hiddenPointsOfInterestForCurrentRoute
                                .indexOf(pointOfInterest);

                        if (indexOfPointOfInterest > -1) {
                            pointOfInterestVisible = false;
                        }

                        if (title.equals(point.title)) {
                            pointOfInterestVisible = visible;
                        }

                        if (pointOfInterestVisible) {
                            visiblePointsOfInterest.add(pointOfInterest);

                            if (indexOfPointOfInterest > -1) {
                                hiddenPointsOfInterestForCurrentRoute.remove(pointOfInterest);
                            }
                        } else {
                            if (indexOfPointOfInterest == -1) {
                                hiddenPointsOfInterestForCurrentRoute.add(pointOfInterest);
                            }
                        }
                    }
                }

                JSONObject object = new JSONObject();
                object.put("features", new JSONArray(visiblePointsOfInterest));
                object.put("type", "FeatureCollection");

//                pointsOfInterestLayer.withFilter(neq(get("title"), point.title));
                // TODO: Create an appropriate filter when toggling the visibility.
            } catch (JSONException e) {
                e.printStackTrace();
            }
        });
    }

    private static JSONObject removeJSONObjectFromJSONArray(JSONArray jsonArray, JSONObject jsonObject) {
        for (int index = 0; index < jsonArray.length(); index += 1) {
            try {
                JSONObject currentJSONObject = jsonArray.getJSONObject(index);
                if (currentJSONObject.equals(jsonObject)) {
                    jsonArray.remove(index);
                    return currentJSONObject;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return null;
    }

    public static List<PointOfInterest> getPointsOfInterestByZone(int zone) {
        List<PointOfInterest> pointsOfInterest = new ArrayList<>();
        map.getStyle(style -> {

        });

        return pointsOfInterest;
    }
}
