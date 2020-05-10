package a2.mobile.mobileapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import a2.mobile.mobileapp.R;
import a2.mobile.mobileapp.common.login.RouteDetailsCard;

public class RouteDetailsAdapter extends RecyclerView.Adapter<RouteDetailsAdapter.ViewHolder> {
    private final List<RouteDetailsCard> routeDetailsCards;

    private LayoutInflater inflater;

    public RouteDetailsAdapter(Context context, List<RouteDetailsCard> routeDetailsCards) {
        this.routeDetailsCards = routeDetailsCards;
        this.inflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View rootView = inflater.inflate(R.layout.route_detail_card, parent, false);

        return new ViewHolder(rootView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        RouteDetailsCard card = routeDetailsCards.get(position);

        holder.icon.setText(card.icon);
        holder.title.setText(card.title);
    }

    @Override
    public int getItemCount() {
        return routeDetailsCards.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView icon;
        TextView title;

        ViewHolder(@NonNull View itemView) {
            super(itemView);

            icon = itemView.findViewById(R.id.icon);
            title = itemView.findViewById(R.id.title);
        }
    }
}
