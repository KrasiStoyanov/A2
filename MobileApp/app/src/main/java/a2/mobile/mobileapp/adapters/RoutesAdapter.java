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

import java.util.List;

import a2.mobile.mobileapp.R;
import a2.mobile.mobileapp.activities.MainActivity;
import a2.mobile.mobileapp.data.Data;
import a2.mobile.mobileapp.data.classes.Route;

public class RoutesAdapter extends RecyclerView.Adapter<RoutesAdapter.ViewHolder> {
    private final String TAG = "Routes Adapter";

    private final Context context;
    private View rootView;
    private final List<Route> routesList;

    private LayoutInflater inflater;

    public RoutesAdapter(
            Context context,
            View rootView,
            List<Route> routesList) {

        this.context = context;
        this.rootView = rootView;
        this.routesList = routesList;

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

        holder.layout.setTag(route.id);
        holder.layout.setOnClickListener(view -> {
            Data.selectedRoute = route;
            MainActivity.sceneManager.switchScene(R.layout.scene_route_deails);
        });
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
}
