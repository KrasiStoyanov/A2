package a2.mobile.mobileapp.handlers;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.PolyUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import a2.mobile.mobileapp.R;
import a2.mobile.mobileapp.activities.MainActivity;
import a2.mobile.mobileapp.adapters.RouteDetailsAdapter;
import a2.mobile.mobileapp.common.SpacesItemDecoration;
import a2.mobile.mobileapp.common.login.RouteDetailsCard;
import a2.mobile.mobileapp.constants.MapConstants;
import a2.mobile.mobileapp.data.Data;

public class RouteDetailsHandler {
    /**
     * On route selection render the needed data and focus the map on the route.
     *
     * @param context The context
     * @param view    The view
     */
    public static void handleRouteSelection(Context context, View view) {
        setupRouteDirectionsAPI(view);
        fillRouteDetailPlaceholders(view);

        List<RouteDetailsCard> routeDetailsCards = generateRouteDetailsCards();
        handleRouteDetailsCards(context, view, routeDetailsCards);
    }

    /**
     * Render the list of route detail cards.
     *
     * @param context           The context
     * @param rootView          The view
     * @param routeDetailsCards The list of route detail cards to render
     */
    private static void handleRouteDetailsCards(Context context, View rootView,
                                                List<RouteDetailsCard> routeDetailsCards) {

        RecyclerView routeDetailsHolder = rootView.findViewById(R.id.route_details_cards);
        RouteDetailsAdapter routeDetailsAdapter = new RouteDetailsAdapter(
                context,
                rootView,
                routeDetailsCards
        );

        routeDetailsHolder.setAdapter(routeDetailsAdapter);

        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        layoutManager.setOrientation(RecyclerView.HORIZONTAL);

        routeDetailsHolder.setLayoutManager(layoutManager);
        routeDetailsHolder.addItemDecoration(new SpacesItemDecoration(20));

        CardView viewRoutesButton = rootView.findViewById(R.id.button_view_routes);
        viewRoutesButton.setOnClickListener(view -> {
            MainActivity.sceneManager.switchScene(R.layout.scene_points_of_interest);
        });
    }

    /**
     * Set up the Direction API URL and render the outcome - a JSON object with the route path.
     *
     * @param view The view
     */
    private static void setupRouteDirectionsAPI(View view) {
        // Initialize a new RequestQueue instance
        RequestQueue requestQueue = Volley.newRequestQueue(Data.context);

        String url = generateUrl();
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, response -> {

                    try {
                        JSONArray routes = response.getJSONArray("routes");
                        JSONObject routesJSONObject = routes.getJSONObject(0);

                        renderRoutePath(routesJSONObject);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }, error -> Log.d("Route Details Handler", "error"));

        requestQueue.add(jsonObjectRequest);
    }

    /**
     * Render the route path based on the provided JSON Object.
     *
     * @param routeObject The JSON Object to get the path from
     */
    private static void renderRoutePath(JSONObject routeObject) {
        Iterator<String> keys = routeObject.keys();

        while (keys.hasNext()) {
            String key = keys.next();

            if (key.equals(MapConstants.DIRECTIONS_ROUTE_PATH_OBJECT_KEY)) {
                JSONObject polyline;
                String points = "";

                try {
                    polyline = routeObject.getJSONObject(key);
                    points = polyline.getString(MapConstants.DIRECTIONS_ROUTE_POINTS_OBJECT_KEY);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                List<LatLng> latLngList = new ArrayList<>();
                latLngList.addAll(PolyUtil.decode(points.trim().replace(
                        "\\\\",
                        "\\"
                )));

                MainActivity.map.addPolyline(new PolylineOptions()
                        .color(R.color.primary)
                        .width(20f)
                        .clickable(false)
                        .addAll(latLngList)
                );

                break;
            }
        }
    }

    /**
     * Generate the Directions API URL based on the start and end point of the selected route.
     *
     * @return The generated URL
     */
    private static String generateUrl() {
        StringBuilder startPointString = new StringBuilder();
        StringBuilder endPointString = new StringBuilder();

        List<Double> startPointCoordinates = Data.selectedRoute.startPoint.coordinates;
        List<Double> endPointCoordinates = Data.selectedRoute.endPoint.coordinates;

        for (int index = 0; index < startPointCoordinates.size(); index += 1) {
            String startCoordinate = startPointCoordinates.get(index).toString();
            String endCoordinate = endPointCoordinates.get(index).toString();

            startPointString.append(startCoordinate);
            endPointString.append(endCoordinate);

            if (index == 0) {
                startPointString.append(MapConstants.URL_QUERY_COMA_SEPERATOR);
                endPointString.append(MapConstants.URL_QUERY_COMA_SEPERATOR);
            }
        }

        return MapConstants.generateDirectionsUrl(
                startPointString.toString(),
                endPointString.toString()
        );
    }

    /**
     * Generate cards with information about the route.
     *
     * @return A list of the generated cards
     */
    private static List<RouteDetailsCard> generateRouteDetailsCards() {
        RouteDetailsCard distance = new RouteDetailsCard(R.string.icon_distance, "350m");

        String pointsOfInterestTitle = " points of interest";
        int pointsOfInterestCount = Data.selectedRoute.pointsOfInterest.size();
        if (pointsOfInterestCount == 1) {
            pointsOfInterestTitle = " point of interest";
        }

        RouteDetailsCard pointsOfInterest = new RouteDetailsCard(
                R.string.icon_eye,
                pointsOfInterestCount + pointsOfInterestTitle
        );

        List<RouteDetailsCard> routeDetailsCards = new ArrayList<>();
        routeDetailsCards.add(distance);
        routeDetailsCards.add(pointsOfInterest);

        return routeDetailsCards;
    }

    /**
     * Fill the data received on the UI.
     */
    private static void fillRouteDetailPlaceholders(View view) {
        TextView routeTitle = view.findViewById(R.id.title);
        routeTitle.setText(Data.selectedRoute.title);

        EditText startPoint = view.findViewById(R.id.input_start_point);
        startPoint.setText(Data.selectedRoute.startPoint.title);

        EditText endPoint = view.findViewById(R.id.input_end_point);
        endPoint.setText(Data.selectedRoute.endPoint.title);
    }
}
