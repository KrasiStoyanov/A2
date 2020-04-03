package a2.mobile.mobileapp.handlers;

import android.content.Context;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import a2.mobile.mobileapp.R;
import a2.mobile.mobileapp.activities.MainActivity;
import a2.mobile.mobileapp.adapters.RouteDetailsAdapter;
import a2.mobile.mobileapp.common.SpacesItemDecoration;
import a2.mobile.mobileapp.common.login.RouteDetailsCard;
import a2.mobile.mobileapp.data.Data;

public class RouteDetailsHandler {
    /**
     * On route selection render the needed data and focus the map on the route.
     *
     * @param context The context
     * @param view    The view
     */
    public static void handleRouteSelection(Context context, View view) {
        fillRouteDetailPlaceholders(view);
        onRouteOptionButtonsClick(view);

        List<RouteDetailsCard> routeDetailsCards = generateRouteDetailsCards();
        handleRouteDetailsCards(context, view, routeDetailsCards);
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

    /**
     * Generate cards with information about the route.
     *
     * @return A list of the generated cards
     */
    private static List<RouteDetailsCard> generateRouteDetailsCards() {
        RouteDetailsCard distance = new RouteDetailsCard(R.string.icon_distance, MapHandler.currentRouteDistance);

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
    }

    private static void onRouteOptionButtonsClick(View view) {
        CardView navigateButton = view.findViewById(R.id.button_navigate);
        navigateButton.setOnClickListener(v -> {
            MainActivity.sceneManager.switchScene(R.layout.scene_navigation);
        });

        CardView viewRoutesButton = view.findViewById(R.id.button_view_routes);
        viewRoutesButton.setOnClickListener(v -> {
            MainActivity.sceneManager.switchScene(R.layout.scene_points_of_interest);
        });
    }
}
