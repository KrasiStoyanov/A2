package a2.mobile.mobileapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import a2.mobile.mobileapp.R;
import a2.mobile.mobileapp.data.classes.PointOfInterest;
import a2.mobile.mobileapp.utils.MapUtils;

public class PointsOfInterestAdapter
        extends RecyclerView.Adapter<PointsOfInterestAdapter.ViewHolder> {

    private LayoutInflater inflater;

    private List<PointOfInterest> pointsOfInterest;

    public PointsOfInterestAdapter(Context context, List<PointOfInterest> pointsOfInterest) {
        this.pointsOfInterest = pointsOfInterest;
        this.inflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public PointsOfInterestAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.point_of_interest, parent, false);

        return new PointsOfInterestAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PointsOfInterestAdapter.ViewHolder holder, int position) {
        PointOfInterest pointOfInterest = pointsOfInterest.get(position);

        holder.title.setText(pointOfInterest.title);
        holder.visibilitySwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            MapUtils.toggleInterestPointVisibility(pointOfInterest, isChecked);
        });

        String interestText = pointOfInterest.typeOfBuilding + " - " + pointOfInterest.interest;
        holder.interest.setText(interestText);

        if (position == getItemCount() - 1) {
            holder.layout.setBackgroundResource(0);
        }

        holder.layout.setTag(pointOfInterest.id);
    }

    @Override
    public int getItemCount() {
        return pointsOfInterest.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ConstraintLayout layout;
        TextView title;
        TextView interest;
        Switch visibilitySwitch;

        ViewHolder(@NonNull View itemView) {
            super(itemView);

            layout = itemView.findViewById(R.id.point_of_interest);
            title = itemView.findViewById(R.id.title);
            interest = itemView.findViewById(R.id.interest);
            visibilitySwitch = itemView.findViewById(R.id.visibility_switch);
        }
    }
}
