package a2.mobile.mobileapp.data.classes.location;

import androidx.lifecycle.LiveData;

public class LocationModel extends LiveData<LocationModel> {
    private double longitude;
    private double latitude;

    LocationModel(double longitude, double latitude) {
        this.longitude = longitude;
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public double getLatitude() {
        return latitude;
    }
}
