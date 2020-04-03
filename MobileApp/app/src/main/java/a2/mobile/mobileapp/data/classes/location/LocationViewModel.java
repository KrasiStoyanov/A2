package a2.mobile.mobileapp.data.classes.location;

import android.app.Application;

public class LocationViewModel {
    private LocationLiveData locationData;

    public LocationViewModel(Application application) {
        locationData = new LocationLiveData(application);
    }

    public LocationLiveData getLocationData() {
        return locationData;
    }
}
