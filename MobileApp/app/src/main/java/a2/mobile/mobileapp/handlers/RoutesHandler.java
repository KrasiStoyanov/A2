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

        View view = ((Activity) context).getWindow().getDecorView()
                .findViewById(android.R.id.content);

        RecyclerView routesListHolder = view.findViewById(R.id.routes_list);
        RoutesAdapter routesAdapter = new RoutesAdapter(
                context,
                rootView,
                routesList
        );

        routesListHolder.setAdapter(routesAdapter);

        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        routesListHolder.setLayoutManager(layoutManager);
    }
}
