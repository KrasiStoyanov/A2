package a2.mobile.mobileapp.handlers;

import android.content.Context;
import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import a2.mobile.mobileapp.R;
import a2.mobile.mobileapp.adapters.RouteDetailsAdapter;
import a2.mobile.mobileapp.common.SpacesItemDecoration;
import a2.mobile.mobileapp.common.login.RouteDetailsCard;

public class RouteDetailsHandler {
    public static void handleRouteDetails(Context context, View rootView,
                                          List<RouteDetailsCard> routeDetailsCards) {

        RecyclerView routeDetailsHolder = rootView.findViewById(R.id.route_details_cards);
        RouteDetailsAdapter routeDetailsAdapter = new RouteDetailsAdapter(context, routeDetailsCards);

        routeDetailsHolder.setAdapter(routeDetailsAdapter);

        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        layoutManager.setOrientation(RecyclerView.HORIZONTAL);

        routeDetailsHolder.setLayoutManager(layoutManager);
        routeDetailsHolder.addItemDecoration(new SpacesItemDecoration(20));
    }

    public static void handleRouteSelection(Context context, View view) {

    }
}
