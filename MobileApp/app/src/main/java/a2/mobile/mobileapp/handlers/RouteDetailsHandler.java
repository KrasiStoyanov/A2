package a2.mobile.mobileapp.handlers;

import android.content.Context;
import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;

import a2.mobile.mobileapp.R;
import a2.mobile.mobileapp.activities.MainActivity;
import a2.mobile.mobileapp.adapters.RouteDetailsAdapter;
import a2.mobile.mobileapp.common.SpacesItemDecoration;
import a2.mobile.mobileapp.common.login.RouteDetailsCard;
import a2.mobile.mobileapp.constants.MapConstants;
import a2.mobile.mobileapp.data.Data;
import a2.mobile.mobileapp.data.classes.Point;
import a2.mobile.mobileapp.data.classes.Route;

public class RouteDetailsHandler {

    /**
     * Render the list of route detail cards.
     * @param context The context
     * @param rootView The view
     * @param routeDetailsCards The list of route detail cards to render
     */
    private static void handleRouteDetailsCards(Context context, View rootView,
                                                List<RouteDetailsCard> routeDetailsCards) {

        RecyclerView routeDetailsHolder = rootView.findViewById(R.id.route_details_cards);
        RouteDetailsAdapter routeDetailsAdapter = new RouteDetailsAdapter(context, routeDetailsCards);

        routeDetailsHolder.setAdapter(routeDetailsAdapter);

        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        layoutManager.setOrientation(RecyclerView.HORIZONTAL);

        routeDetailsHolder.setLayoutManager(layoutManager);
        routeDetailsHolder.addItemDecoration(new SpacesItemDecoration(20));
    }

    /**
     * On route selection render the needed data and focus the map on the route.
     * @param context The context
     * @param view The view
     */
    public static void handleRouteSelection(Context context, View view) {
        List<RouteDetailsCard> routeDetailsCards = generateRouteDetailsCards();

        handleRouteDetailsCards(context, view, routeDetailsCards);
    }

    /**
     * Generate cards with information about the route.
     * @return A list of the generated cards
     */
    private static List<RouteDetailsCard> generateRouteDetailsCards() {
        RouteDetailsCard distance = new RouteDetailsCard(R.string.icon_distance, "350m");

        String pointsOfInterestTitle = " points of interest";
        int pointsOfInterestCount = Data.pointsOfInterest.size();
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
}
