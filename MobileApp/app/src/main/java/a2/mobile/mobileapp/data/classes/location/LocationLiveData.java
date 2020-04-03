package a2.mobile.mobileapp.data.classes.location;

import android.content.Context;
import android.location.Location;

import androidx.lifecycle.LiveData;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import a2.mobile.mobileapp.constants.MapConstants;

public class LocationLiveData extends LiveData<LocationModel> {
    private FusedLocationProviderClient fusedLocationClient;

    private LocationCallback locationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult result) {
            if (result == null)
                return;

            for (Location location : result.getLocations()) {
                setLocationData(location);
            }
        }
    };

    LocationLiveData(Context context) {
        this.fusedLocationClient = LocationServices.getFusedLocationProviderClient(context);
    }

    public FusedLocationProviderClient getFusedLocationClient() {
        return fusedLocationClient;
    }

    @Override
    public void onInactive() {
        super.onInactive();
        fusedLocationClient.removeLocationUpdates(locationCallback);
    }

    @Override
    public void onActive() {
        super.onActive();

        fusedLocationClient.getLastLocation().addOnSuccessListener(this::setLocationData);
        startLocationUpdates();
    }

    private void setLocationData(Location location) {
        setValue(new LocationModel(
                location.getLongitude(),
                location.getLatitude()
        ));
    }

    private void startLocationUpdates() {
        fusedLocationClient.requestLocationUpdates(
                LocationRequestSingleton.getRequest(),
                locationCallback,
                null
        );
    }

    static class LocationRequestSingleton {
        private static LocationRequest request = null;

        static LocationRequest getRequest() {
            if (request == null) {
                request = LocationRequest.create();

                request.setInterval(MapConstants.INTERVAL_BETWEEN_LOCATION_UPDATE);
                request.setFastestInterval(MapConstants.INTERVAL_FASTEST_BETWEEN_LOCATION_UPDATE);
                request.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            }

            return request;
        }
    }
}
