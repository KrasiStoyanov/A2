package a2.mobile.mobileapp.handlers;

import android.app.Activity;
import android.content.Context;
import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import a2.mobile.mobileapp.R;
import a2.mobile.mobileapp.adapters.PointsOfInterestAdapter;
import a2.mobile.mobileapp.data.classes.PointOfInterest;

public class PointsOfInterestHandler {
    public static void handlePointsOfInterest(
            Context context,
            View rootView,
            List<PointOfInterest> pointsOfInterest) {

        View view = ((Activity) context).getWindow().getDecorView()
                .findViewById(android.R.id.content);

        RecyclerView pointsOfInterestHolder = view.findViewById(R.id.points_of_interest_list);
        PointsOfInterestAdapter pointsOfInterestAdapter = new PointsOfInterestAdapter(
                context,
                rootView,
                pointsOfInterest
        );

        pointsOfInterestHolder.setAdapter(pointsOfInterestAdapter);

        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        pointsOfInterestHolder.setLayoutManager(layoutManager);
    }
}
