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
import a2.mobile.mobileapp.enums.Scenes;

public class NavigationAdapter extends RecyclerView.Adapter<NavigationAdapter.ViewHolder> {
    private List<Route> directionsList;

    private LayoutInflater inflater;

    public NavigationAdapter(Context context, List<Route> directionsList) {
        this.directionsList = directionsList;
        this.inflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public NavigationAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.route_option, parent, false);

        return new NavigationAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NavigationAdapter.ViewHolder holder, int position) {
        Route route = directionsList.get(position);

        holder.title.setText(route.title);

        String pointsText = route.startPoint.title + " - " + route.endPoint.title;
        holder.points.setText(pointsText);

        if (position == getItemCount() - 1) {
            holder.layout.setBackgroundResource(0);
        }

        holder.layout.setTag(route.id);
        holder.layout.setOnClickListener(view -> {
            Data.selectedRoute = route;
            MainActivity.sceneManager.switchScene(Scenes.routeDetails);
        });
    }

    @Override
    public int getItemCount() {
        return directionsList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ConstraintLayout layout;
        TextView title;
        TextView points;
        Button button;

        ViewHolder(@NonNull View itemView) {
            super(itemView);

            layout = itemView.findViewById(R.id.direction);
            title = itemView.findViewById(R.id.current_direction);
            points = itemView.findViewById(R.id.streets);
            button = itemView.findViewById(R.id.icon);
        }
    }
}
