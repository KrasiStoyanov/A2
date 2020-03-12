package a2.mobile.mobileapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder> {
    private Point startPoint;
    private DestinationPoint targetDestination;
    private List<PointOfInterest> pointsOfInterest;

    private LayoutInflater inflater;

    Adapter(Context context, Point startPoint, DestinationPoint targetDestination, List<PointOfInterest> pointsOfInterest){
        this.startPoint = startPoint;
        this.targetDestination = targetDestination;
        this.pointsOfInterest = pointsOfInterest;

        this.inflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.list_view, parent,false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        PointOfInterest pointOfInterest = pointsOfInterest.get(position);
        List<String> coordinates = new ArrayList<>();

        for (Double coordinate : pointOfInterest.coordinates) {
            coordinates.add(coordinate.toString());
        }

        holder.coordinates.setText(StringUtils.join(coordinates));
        holder.interest.setText(pointOfInterest.interest);
    }

    @Override
    public int getItemCount() {
        return pointsOfInterest.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView coordinates;
        TextView interest;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            coordinates = itemView.findViewById(R.id.title);
            interest = itemView.findViewById(R.id.description);
        }
    }
}