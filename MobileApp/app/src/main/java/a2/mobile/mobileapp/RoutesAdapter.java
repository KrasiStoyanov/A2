package a2.mobile.mobileapp;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class RoutesAdapter extends RecyclerView.Adapter<RoutesAdapter.ViewHolder> {
    private final String TAG = "Routes Adapter";

    private final Context context;
    private final List<Route> routesList;

    private LayoutInflater inflater;

    RoutesAdapter(Context context, List<Route> routesList) {
        this.context = context;
        this.routesList = routesList;

        this.inflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View rootView = inflater.inflate(R.layout.route_option, parent, false);

        return new ViewHolder(rootView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Route route = routesList.get(position);

        holder.title.setText(route.title);
        Log.e(TAG, "Title " + route.title
                + "; Point A " + route.startPoint.coordinates.toString()
                + "; Point B " + route.endPoint.coordinates.toString()
        );
    }

    @Override
    public int getItemCount() {
        return routesList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView title;

        ViewHolder(@NonNull View itemView) {
            super(itemView);

            title = itemView.findViewById(R.id.title);
        }
    }
}
