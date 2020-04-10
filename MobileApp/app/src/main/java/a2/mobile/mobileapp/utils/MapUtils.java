package a2.mobile.mobileapp.utils;

import android.content.Context;
import android.graphics.BitmapFactory;

import androidx.annotation.NonNull;

import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.Style;
import com.mapbox.mapboxsdk.maps.UiSettings;
import com.mapbox.mapboxsdk.style.layers.SymbolLayer;
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource;

import java.util.ArrayList;
import java.util.List;

import a2.mobile.mobileapp.R;
import a2.mobile.mobileapp.constants.MapConstants;

import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconAllowOverlap;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconIgnorePlacement;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconImage;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconOffset;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconSize;

public class MapUtils {
    public static MapboxMap map;
    private static Style.Builder mapStyle;
    private static GeoJsonSource mapGeoSource;

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
    private static SymbolLayer setRouteLayer() {

        return new SymbolLayer(
                MapConstants.MAP_ROUTE_LAYER_ID,
                MapConstants.MAP_ROUTE_LAYER_SROUCE_ID
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
            style.removeSource(MapConstants.MAP_ROUTE_LAYER_SROUCE_ID);
            style.removeLayer(MapConstants.MAP_ROUTE_LAYER_ID);
        });

        routeMarkers.clear();
    }

    /**
     * Render the route markers list in a newly created route symbol layer.
     */
    public static void updateRouteMarkers() {
        mapGeoSource = new GeoJsonSource(MapConstants.MAP_ROUTE_LAYER_SROUCE_ID,
                FeatureCollection.fromFeatures(routeMarkers)
        );

        map.setStyle(mapStyle, style -> {
            style.addSource(mapGeoSource);
            style.addLayer(setRouteLayer());
        });
    }
}
