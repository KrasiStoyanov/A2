package com.example.bladeapp.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bladeapp.Navigationpage;
import com.example.bladeapp.PopUpActivity;
import com.example.bladeapp.R;

import java.util.List;

public class NotificationIconAdapter extends RecyclerView.Adapter<NotificationIconAdapter.ViewHolder> {
    private Context context;
    private List<String[]> pointsOfInterest;

    private LayoutInflater inflater;

    public NotificationIconAdapter(Context context, List<String[]> pointsOfInterest) {
        this.context = context;
        this.pointsOfInterest = pointsOfInterest;

        this.inflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.notification_option, parent, false);

        return new ViewHolder(view);

    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final String[] pointOfInterest = pointsOfInterest.get(position);
        switch(pointOfInterest[pointOfInterest.length - 1]) {
            case "high":
                holder.icon.setTextColor(context.getColor(R.color.notification_icon_red));
                break;
            case "medium":
                holder.icon.setTextColor(context.getColor(R.color.notification_icon_orange));
                break;
            case "low":
                holder.icon.setTextColor(context.getColor(R.color.notification_icon_blue));
                break;
        }

        holder.icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, PopUpActivity.class);
                intent.putExtra("Point of Interest", pointOfInterest);

                Navigationpage.showNotificationPopup(2, intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return pointsOfInterest.size();
    }

    public void removeNotification(String title, String interest, String priority) {
        for (int index = 0; index < pointsOfInterest.size(); index += 1) {
            String[] pointOfInterest = pointsOfInterest.get(index);

            String currentTitle = pointOfInterest[0];
            String currentInterest = pointOfInterest[1];
            String currentPriority = pointOfInterest[pointOfInterest.length - 1];

            if (title.equals(currentTitle) &&
                    interest.equals(currentInterest) &&
                    priority.equals(currentPriority)) {

                pointsOfInterest.remove(pointOfInterest);
                notifyItemRemoved(index);
            }
        }
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView icon;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            icon = itemView.findViewById(R.id.notification_icon);

        }
    }
}
