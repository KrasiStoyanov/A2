package a2.mobile.mobileapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import a2.mobile.mobileapp.R;
import a2.mobile.mobileapp.data.classes.PointOfInterest;
import a2.mobile.mobileapp.utils.MapUtils;

public class InterestPointAdapter
        extends RecyclerView.Adapter<InterestPointAdapter.ViewHolder> {

    private LayoutInflater inflater;
    private List<PointOfInterest> interestPoints;

    public InterestPointAdapter(Context context, List<PointOfInterest> interestPoints) {
        this.interestPoints = interestPoints;
        this.inflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public InterestPointAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        CardView view = (CardView) inflater.inflate(R.layout.interest_point, parent, false);

        return new InterestPointAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull InterestPointAdapter.ViewHolder holder, int position) {
        PointOfInterest pointOfInterest = interestPoints.get(position);

        holder.title.setText(pointOfInterest.title);
        holder.deleteButton.setOnClickListener(v -> {
            removeAt(position);
        });

        String interestText = pointOfInterest.typeOfBuilding + " - " + pointOfInterest.interest;
        holder.interest.setText(interestText);
    }

    @Override
    public int getItemCount() {
        return interestPoints.size();
    }

    private void removeAt(int position) {
        interestPoints.remove(position);

        notifyItemRemoved(position);
        notifyItemRangeChanged(position, interestPoints.size());
    }

    public void addItem(PointOfInterest item) {
        interestPoints.add(item);

        notifyItemInserted(interestPoints.size() - 1);
        notifyItemRangeChanged(interestPoints.size() - 1, interestPoints.size());
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ConstraintLayout layout;
        TextView title;
        TextView interest;
        CardView deleteButton;

        ViewHolder(@NonNull View itemView) {
            super(itemView);

            layout = itemView.findViewById(R.id.point_of_interest);
            title = itemView.findViewById(R.id.title);
            interest = itemView.findViewById(R.id.interest);
            deleteButton = itemView.findViewById(R.id.delete_option);
        }
    }
}
