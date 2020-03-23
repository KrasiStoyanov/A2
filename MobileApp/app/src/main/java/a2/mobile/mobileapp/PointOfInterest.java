package a2.mobile.mobileapp;

import java.util.List;
import java.util.UUID;

public class PointOfInterest extends Point {
    public UUID id;

    public PointOfInterest() {
        this.id = UUID.randomUUID();
    }

    public PointOfInterest(List<Double> coordinates) {
        this.id = UUID.randomUUID();

        this.coordinates = coordinates;
    }

    PointOfInterest(List<Double> coordinates, String title, String interest) {
        this.id = UUID.randomUUID();

        this.coordinates = coordinates;
        this.title = title;
        this.interest = interest;
    }

    public UUID getId() {
        return this.id;
    }
}
