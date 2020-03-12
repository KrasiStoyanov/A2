package a2.mobile.mobileapp;

import java.util.List;
import java.util.UUID;

public class DestinationPoint extends Point {
    private UUID id;
    private List<PointOfInterest> pointsOfInterest;

    DestinationPoint() {
        this.id = UUID.randomUUID();
    }

    public DestinationPoint(List<Double> coordinates) {
        this.id = UUID.randomUUID();

        this.coordinates = coordinates;
    }

    public DestinationPoint(List<Double> coordinates, String interest) {
        this.id = UUID.randomUUID();

        this.coordinates = coordinates;
        this.interest = interest;
    }

    public UUID getId() {
        return this.id;
    }

    public List<PointOfInterest> getPointsOfInterest() {
        return this.pointsOfInterest;
    }
}
