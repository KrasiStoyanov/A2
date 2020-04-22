package a2.mobile.mobileapp.utils;

import android.content.Context;
import android.graphics.BitmapFactory;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

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
import com.mapbox.services.android.navigation.v5.navigation.MapboxNavigation;
import com.mapbox.services.android.navigation.v5.navigation.NavigationRoute;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import a2.mobile.mobileapp.R;
import a2.mobile.mobileapp.constants.MapConstants;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

import static com.mapbox.core.constants.Constants.PRECISION_6;
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
    private static MapboxNavigation navigation;
    static DirectionsRoute navigationRoute;

    private static GeoJsonSource mapMarkerSource;
    private static GeoJsonSource mapRouteSource;

    private static List<Feature> routeMarkers = new ArrayList<>();

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
     * Store the mapbox navigation instance.
     *
     * @param mapboxNavigation The mapbox navigation instance
     */
    public static void storeNavigationInstance(MapboxNavigation mapboxNavigation) {
        navigation = mapboxNavigation;
    }

    /**
     * Set the global style of the map
     *
     * @param context The MainActivity context
     */
    public static void setMapStyle(Context context) {
        mapStyle = new Style.Builder()
                .fromUri(Style.MAPBOX_STREETS)
                .withImage(MapConstants.MAP_MARKER_ID, BitmapFactory.decodeResource(
                        context.getResources(), R.drawable.red_marker
                ));

        map.setStyle(mapStyle, style -> {
            UiSettings uiSettings = map.getUiSettings();

            uiSettings.setCompassEnabled(true);
            uiSettings.setAllGesturesEnabled(true);
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
    public static void clearRouteMarkers() {
        map.setStyle(mapStyle, style -> {
            style.removeSource(MapConstants.MAP_ROUTE_LAYER_SOURCE_ID);
            style.removeLayer(MapConstants.MAP_ROUTE_LAYER_ID);

            style.removeSource(MapConstants.MAP_ROUTE_MARKER_LAYER_SOURCE_ID);
            style.removeLayer(MapConstants.MAP_ROUTE_MARKER_LAYER_ID);
        });

        routeMarkers.clear();
    }

    /**
     * Render the route markers list in a newly created route symbol layer.
     */
    public static void updateRoute(Context context, Point origin, Point destination) {
        mapMarkerSource = new GeoJsonSource(
                MapConstants.MAP_ROUTE_MARKER_LAYER_SOURCE_ID,
                FeatureCollection.fromFeatures(new Feature[]{
                        Feature.fromGeometry(origin),
                        Feature.fromGeometry(destination)
                })
        );

        mapRouteSource = new GeoJsonSource(MapConstants.MAP_ROUTE_LAYER_SOURCE_ID);
        map.setStyle(mapStyle, style -> {
            style.addSource(mapRouteSource);
            style.addSource(mapMarkerSource);

            style.addLayer(setRouteLayer(context));
            style.addLayer(setRouteMarkerLayer());

            renderRouteLayer(context, origin, destination);
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
        NavigationRoute.builder(context)
                .accessToken(context.getString(R.string.mapbox_access_token))
                .origin(origin)
                .destination(destination)
                .alternatives(true)
                .build()
                .getRoute(new Callback<DirectionsResponse>() {
                    @Override
                    public void onResponse(
                            @NonNull Call<DirectionsResponse> call,
                            @NonNull Response<DirectionsResponse> response) {

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

                                navigationRoute = currentRoute;
                            }
                        });
                    }

                    @Override
                    public void onFailure(
                            @NonNull Call<DirectionsResponse> call,
                            @NonNull Throwable t) {

                    }
                });
    }
}
