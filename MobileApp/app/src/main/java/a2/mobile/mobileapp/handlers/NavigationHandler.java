package a2.mobile.mobileapp.handlers;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.mapbox.api.directions.v5.models.BannerInstructions;
import com.mapbox.api.directions.v5.models.BannerText;
import com.mapbox.api.directions.v5.models.LegStep;
import com.vuzix.connectivity.sdk.Connectivity;

import java.util.ArrayList;
import java.util.List;

import a2.mobile.mobileapp.R;
import a2.mobile.mobileapp.adapters.InterestPointAdapter;
import a2.mobile.mobileapp.adapters.RoutesAdapter;
import a2.mobile.mobileapp.constants.NavigationConstants;
import a2.mobile.mobileapp.data.classes.PointOfInterest;
import a2.mobile.mobileapp.data.classes.Route;
import a2.mobile.mobileapp.enums.PointOfInterestPriorities;

public class NavigationHandler {
    private static Context mContext;

    private static String direction;
    private static String iconName;
    private static String target;
    private static double distance;

    private static String interestPointTitle;
    private static String interestPointDescription;
    private static PointOfInterestPriorities interestPointPriority;

    private static InterestPointAdapter interestPointsAdapter;
    private static LinearLayoutManager linearLayoutManager;

    public static void storeContext(Context context) {
        mContext = context;
    }

    public static void updateDirection(LegStep currentStep) {
        List<BannerInstructions> instructions = currentStep.bannerInstructions();

        if (instructions != null && instructions.size() > 0) {
            BannerInstructions mostImportantInstruction = instructions.get(0);
            BannerText primaryInstruction = mostImportantInstruction.primary();

            direction = primaryInstruction.modifier();
            updateIconName();
        }
    }

    private static void updateIconName() {
        iconName = direction;

        sendDataToBladeApp("icon_name", new String[] { iconName });
    }

    public static void updateDistanceRemaining(int distanceRemaining) {
        distance = distanceRemaining;
        String formattedDistance = (int) distance + "m";
        if (distance >= 1000) {
            formattedDistance = (distance / 1000) + " km";
        }

        sendDataToBladeApp("distance_remaining", new String[] { formattedDistance });
    }

    public static void updateInterestPoint(
            PointOfInterest interestPoint,
            TextView noInterestPointsTextView) {

        interestPointTitle = interestPoint.title;
        interestPointDescription = interestPoint.interest;
        interestPointPriority = interestPoint.getPriority();

        sendDataToBladeApp("interest point title", new String[] {
                interestPointTitle,
                interestPointDescription,
                interestPointPriority.toString()
        });

        if (interestPointsAdapter != null) {
            if (noInterestPointsTextView.getVisibility() == View.VISIBLE) {
                noInterestPointsTextView.setVisibility(View.GONE);
            }

            interestPointsAdapter.addItem(interestPoint);
            linearLayoutManager.scrollToPositionWithOffset(
                    interestPointsAdapter.getItemCount() - 1,
                    0
            );
        }
    }

    private static void sendDataToBladeApp(String name, String[] data) {
        Intent sendIntent = new Intent(NavigationConstants.SEND_DATA_ACTION);
        sendIntent.setPackage("com.example.bladeapp");
        //sendIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        sendIntent.putExtra(name, data);

        Connectivity connectivity = Connectivity.get(mContext);
        if (connectivity.isAvailable()) {
            connectivity.sendBroadcast(sendIntent);
        }
    }

    public static void setUpInterestPointsAdapter(
            Context context,
            View rootView) {

        ((Activity) context).runOnUiThread(() -> {
            RecyclerView interestPointsHolder = rootView.findViewById(R.id.interest_points);
            interestPointsAdapter = new InterestPointAdapter(
                    context,
                    new ArrayList<>()
            );

            interestPointsHolder.setAdapter(interestPointsAdapter);

            linearLayoutManager = new LinearLayoutManager(
                    context,
                    RecyclerView.HORIZONTAL,
                    false
            );

            interestPointsHolder.setLayoutManager(linearLayoutManager);
        });
    }
}
