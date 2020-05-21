package a2.mobile.mobileapp.handlers;

import android.content.Context;
import android.util.Log;

import com.mapbox.api.directions.v5.models.BannerInstructions;
import com.mapbox.api.directions.v5.models.BannerText;
import com.mapbox.api.directions.v5.models.LegStep;
import com.mapbox.services.android.navigation.v5.routeprogress.RouteStepProgress;

import java.text.DecimalFormat;
import java.util.List;

public class NavigationHandler {
    private static StringBuilder currentMessage = new StringBuilder();
    private static String target;
    private static Double distance;

    public static String generateInstructionMessage(
            LegStep currentStep,
            RouteStepProgress stepProgress) {

        StringBuilder message = new StringBuilder();
        message
                .append("In ")
                .append(new DecimalFormat("#.##").format(
                        stepProgress.getDistanceRemaining()
                ));

        List<BannerInstructions> instructions = currentStep.bannerInstructions();

        assert instructions != null;
        for (BannerInstructions i : instructions) {
            BannerText instruction = i.primary();
            String instructionType = instruction.type();
            String modifier = instruction.modifier();

            if (instructionType != null && modifier != null) {
                instructionType = instructionType.toLowerCase();
                modifier = modifier.toLowerCase();

                if ("turn".equals(instructionType)) {
                    if (modifier.contains("sharp") || modifier.contains("slight")) {
                        message
                                .append("m make a ")
                                .append(modifier)
                                .append(" ")
                                .append(instructionType);
                    } else if (modifier.equals("straight")) {
                        message.append("Continue straight");
                    } else if (modifier.equals("uturn")) {
                        message.append("m make a U-turn");
                    } else {
                        message.append("m turn ").append(modifier);
                    }
                }
            }

            if (instruction.text().contains("arrive")) {

            }

            message
                    .append(" towards ")
                    .append(instruction.text())
                    .append(".");
        }

        Log.e("Step", message.toString());
        currentMessage = message;
        distance = stepProgress.getDistanceRemaining();

        return currentMessage.toString();
    }

    public static void updateDistanceRemaining(int distanceRemaining, Context context) {
        // TODO: send distance to glasses.
        Log.e("Distance Remaining", "Distaince: " + distanceRemaining);
    }

    public static String generateArrivalMessage() {
        return "You have arrived at your destination. You can see it on the ";
    }
}
