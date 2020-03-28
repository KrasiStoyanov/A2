package a2.mobile.mobileapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;

import a2.mobile.mobileapp.R;
import a2.mobile.mobileapp.common.login.RouteDetailsCard;
import a2.mobile.mobileapp.constants.MapConstants;
import a2.mobile.mobileapp.data.Data;
import a2.mobile.mobileapp.data.classes.Point;
import a2.mobile.mobileapp.data.classes.Route;
import a2.mobile.mobileapp.fragments.MainActivityFragment;
import a2.mobile.mobileapp.handlers.RouteDetailsHandler;

public class RoutesAdapter extends RecyclerView.Adapter<RoutesAdapter.ViewHolder> {
    private final String TAG = "Routes Adapter";

    private final Context context;
    private View rootView;
    private final List<Route> routesList;
    private GoogleMap map;

    private LayoutInflater inflater;

    public RoutesAdapter(
            Context context,
            View rootView,
            List<Route> routesList,
            GoogleMap map) {

        this.context = context;
        this.rootView = rootView;
        this.routesList = routesList;
        this.map = map;

        this.inflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.route_option, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Route route = routesList.get(position);

        holder.title.setText(route.title);

        String pointsText = route.startPoint.title + " - " + route.endPoint.title;
        holder.points.setText(pointsText);

        if (position == getItemCount() - 1) {
            holder.layout.setBackgroundResource(0);
        }

        holder.layout.setOnClickListener(this::onRouteClick);
        holder.layout.setTag(route.id);
    }

    @Override
    public int getItemCount() {
        return routesList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ConstraintLayout layout;
        TextView title;
        TextView points;
        Button button;

        ViewHolder(@NonNull View itemView) {
            super(itemView);

            layout = itemView.findViewById(R.id.route_option);
            title = itemView.findViewById(R.id.title);
            points = itemView.findViewById(R.id.points);
            button = itemView.findViewById(R.id.call_to_action_button);
        }
    }

    private void onRouteClick(View view) {
        MainActivityFragment.switchScene(R.layout.route_deails_scene);

        View routeOption = view.findViewById(R.id.route_option);
        Route route = Data.getRoute(routeOption.getTag());

        assert route != null;
        RouteDetailsAdapter.fillRouteDetailPlaceholders(rootView, route);

        LatLngBounds.Builder builder = new LatLngBounds.Builder();

        // Move GoogleMaps camera to the route area.
        MarkerOptions startMarker = generateRouteMarkers(route.startPoint);
        builder.include(startMarker.getPosition());

        MarkerOptions endMarker = generateRouteMarkers(route.endPoint);
        builder.include(endMarker.getPosition());

        LatLngBounds latLngBounds = builder.build();
        CameraUpdate cameraUpdate = CameraUpdateFactory
                .newLatLngBounds(latLngBounds, MapConstants.MAP_FOCUS_PADDING);

        map.animateCamera(cameraUpdate);

        // TODO: This should be made into functions later, not hardcoded!
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

        RouteDetailsHandler.handleRouteDetails(context, rootView, routeDetailsCards);
        // TODO: The above code block should be made into functions later, not hardcoded!
    }

    private MarkerOptions generateRouteMarkers(Point point) {
        List<Double> coordinates = point.coordinates;

        // Create a new instance of a marker based on the coordinates from the point of interest.
        LatLng coordinatesMarker = new LatLng(coordinates.get(0), coordinates.get(1));
        MarkerOptions marker = new MarkerOptions();
        marker.position(coordinatesMarker);

        return marker;
    }
}
