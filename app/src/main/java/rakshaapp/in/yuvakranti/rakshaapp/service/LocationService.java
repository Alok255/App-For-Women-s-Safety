package rakshaapp.in.yuvakranti.rakshaapp.service;

import android.Manifest;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class LocationService extends Service implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private static final String TAG = LocationService.class.getSimpleName();

    GoogleApiClient mLocationClient;
    LocationRequest mLocationRequest = new LocationRequest();

    public static final String ACTION_LOCATION_BROADCAST = LocationService.class.getName() + "LocationBroadcast";
    public static final String EXTRA_LATITUDE = "extra_latitude";
    public static final String EXTRA_LONGITUDE = "extra_longitude";
    public static final String ADDRESS_TEXT="address_text";

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        mLocationClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(30000);

        int priority = LocationRequest.PRIORITY_HIGH_ACCURACY;
        mLocationRequest.setPriority(priority);
        mLocationClient.connect();

        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(mLocationClient, mLocationRequest, this);

        Log.d(TAG, "Connected to Google API");
    }


    @Override
    public void onConnectionSuspended(int i) {

        Log.d(TAG,"Connection Suspended");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

        Log.d(TAG,"On Location failed");
    }

    @Override
    public void onLocationChanged(Location location) {

        Log.d(TAG,"On location changed");

        if (location!=null){

            Log.d(TAG,"== location!=null");

            String add = "No Location Found";

            Geocoder geocoder = new Geocoder(this, Locale.getDefault());
            List<Address> addressList;

            try {
                addressList = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                add = addressList.get(0).getAddressLine(0);
                Log.d("Address",add);

            } catch (IOException e) {
                e.printStackTrace();
            }

            sendMessageToUi(String.valueOf(location.getLatitude()),String.valueOf(location.getLongitude()),add);
        }
    }

    private void sendMessageToUi(String lat,String lon,String add){

        Log.d(TAG,"Sending info");

        Intent intent=new Intent(ACTION_LOCATION_BROADCAST);
        intent.putExtra(EXTRA_LATITUDE,lat);
        intent.putExtra(EXTRA_LONGITUDE,lon);
        intent.putExtra(ADDRESS_TEXT,add);

        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);

    }
}