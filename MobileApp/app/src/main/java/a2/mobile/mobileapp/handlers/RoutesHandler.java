package a2.mobile.mobileapp.handlers;

import android.app.Activity;
import android.content.Context;
import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import a2.mobile.mobileapp.R;
import a2.mobile.mobileapp.adapters.RoutesAdapter;
import a2.mobile.mobileapp.data.classes.Route;

public class RoutesHandler {
    public static void handleRoutes(
            Context context,
            View rootView,
            List<Route> routesList) {

        ((Activity) context).runOnUiThread(() -> {
            RecyclerView routesListHolder = rootView.findViewById(R.id.routes_list);
            RoutesAdapter routesAdapter = new RoutesAdapter(
                    context,
                    routesList
            );

            routesListHolder.setAdapter(routesAdapter);

            LinearLayoutManager layoutManager = new LinearLayoutManager(context);
            routesListHolder.setLayoutManager(layoutManager);
        });
    }
}
