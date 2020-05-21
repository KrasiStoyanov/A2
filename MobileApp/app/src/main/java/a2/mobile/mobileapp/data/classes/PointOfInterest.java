package a2.mobile.mobileapp.data.classes;

import java.util.List;
import java.util.UUID;

import a2.mobile.mobileapp.enums.PointOfInterestPriorities;

public class PointOfInterest extends Point {
    public UUID id;
    private PointOfInterestPriorities priority;

    public PointOfInterest() {
        this.id = UUID.randomUUID();
    }

    public PointOfInterest(List<Double> coordinates) {
        this.id = UUID.randomUUID();

        this.coordinates = coordinates;
    }

    public PointOfInterest(
            List<Double> coordinates,
            List<Integer> locationZones,
            String title,
            String interest,
            String typeOfBuilding,
            PointOfInterestPriorities priority) {

        this.id = UUID.randomUUID();

        this.coordinates = coordinates;
        this.locationZones = locationZones;
        this.title = title;
        this.interest = interest;
        this.typeOfBuilding = typeOfBuilding;
        this.priority = priority;
    }

    public UUID getId() {
        return this.id;
    }

    public PointOfInterestPriorities getPriority() {
        return this.priority;
    }
}
