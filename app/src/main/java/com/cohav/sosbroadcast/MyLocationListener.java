package com.cohav.sosbroadcast;

import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;

import java.util.Locale;

/**
 * Created by Shaul on 13/06/2017.
 */

public class MyLocationListener implements LocationListener {
    @Override
    public void onLocationChanged(Location location){

        String longitude = "Longitude" + location.getLongitude();
        String latitude = "Latidue" + location.getLatitude();
        /*------------Gets city name-------------*/
        String cityName = null;
        //Geocoder gcd = new Geocoder(, Locale.getDefault());
    }
    @Override
    public void onProviderDisabled(String provider) {}

    @Override
    public void onProviderEnabled(String provider) {}

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {}
}
