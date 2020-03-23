package a2.mobile.mobileapp;

import java.util.ArrayList;
import java.util.List;

public class DataUtils {
    public static List<Double> stringToCoordinates(String string) {
        List<Double> coordinates = new ArrayList<>();

        String[] splitCoordinates = string.split(", ");
        for (String splitCoordinate : splitCoordinates) {
            Double coordinate = Double.parseDouble(splitCoordinate);

            coordinates.add(coordinate);
        }

        return coordinates;
    }
}
