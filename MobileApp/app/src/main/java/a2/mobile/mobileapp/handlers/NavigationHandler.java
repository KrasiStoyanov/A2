package a2.mobile.mobileapp.handlers;

import android.util.Log;

import com.mapbox.api.directions.v5.models.BannerInstructions;
import com.mapbox.api.directions.v5.models.BannerText;
import com.mapbox.api.directions.v5.models.LegStep;

import java.util.List;

import a2.mobile.mobileapp.data.classes.PointOfInterest;
import a2.mobile.mobileapp.enums.PointOfInterestPriorities;

public class NavigationHandler {
    private static String direction;
    private static String iconName;
    private static String target;
    private static double distance;

    private static String interestPointTitle;
    private static String interestPointDescription;
    private static PointOfInterestPriorities interestPointPriority;

    public static void updateDirection(LegStep currentStep) {
        List<BannerInstructions> instructions = currentStep.bannerInstructions();

        if (instructions != null && instructions.size() > 0) {
            BannerInstructions mostImportantInstruction = instructions.get(0);
            BannerText primaryInstruction = mostImportantInstruction.primary();

            direction = primaryInstruction.type();
            updateIconName();
        }
    }

    private static void updateIconName() {
        iconName = direction;
    }

    public static void updateDistanceRemaining(int distanceRemaining) {
        distance = distanceRemaining;
    }

    public static void updateInterestPoint(PointOfInterest interestPoint) {
        interestPointTitle = interestPoint.title;
        interestPointDescription = interestPoint.interest;
        interestPointPriority = interestPoint.getPriority();
    }
}
