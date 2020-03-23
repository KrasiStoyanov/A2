package a2.mobile.mobileapp;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

class RoutesHandler {
    static void handleRoutes(Context context, List<Route> routesList) {
        View view = ((Activity) context).getWindow().getDecorView()
                .findViewById(android.R.id.content);

        RecyclerView routesListHolder = view.findViewById(R.id.routes_list);
        RoutesAdapter routesAdapter = new RoutesAdapter(context, routesList);

        routesListHolder.setAdapter(routesAdapter);

        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        routesListHolder.setLayoutManager(layoutManager);
    }
}
