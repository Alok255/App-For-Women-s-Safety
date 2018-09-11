package rakshaapp.in.yuvakranti.rakshaapp;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import rakshaapp.in.yuvakranti.rakshaapp.app.AppController;
import rakshaapp.in.yuvakranti.rakshaapp.app.Config;
import rakshaapp.in.yuvakranti.rakshaapp.helper.PrefManager;
import rakshaapp.in.yuvakranti.rakshaapp.service.LocationService;
import rakshaapp.in.yuvakranti.rakshaapp.service.LockService;

public class HelpActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,GoogleApiClient.OnConnectionFailedListener{

    private Button btnUpdate;
    private EditText inputNumber1,inputNumber2;
    private TextView updateLocationText,locationChangedText;
    private ProgressBar updateProgress;

    private String newID,number1, number2, address;
    private PrefManager manager;

    private static final int MY_PERMISSION_REQUEST_ACCESS_FINE_LOCATION = 1;

    private static final int STORAGE_PERMISSION_CODE=100;
    private static final int REQUEST_PERMISSION_SETTING=101;

    private String []permissionRequired=new String[]{Manifest.permission.SEND_SMS,Manifest.permission.CALL_PHONE,
            Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION};

    private SharedPreferences permissionStatus;
    private boolean sentToSettings = false;

    //todo : capture location
    private double latitude, longitude;
    private GoogleApiClient googleApiClient;
    private LocationRequest locationRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        manager = new PrefManager(this);

        setContentView(R.layout.activity_help);

        newID = manager.getMobileId();
        permissionStatus = getSharedPreferences("permissionStatus",MODE_PRIVATE);

        inputNumber1=findViewById(R.id.add_no_1_edit);
        inputNumber2=findViewById(R.id.add_no_2_edit);
        updateLocationText=findViewById(R.id.location_updates);
        updateProgress=findViewById(R.id.update_progress_bar);
        locationChangedText=findViewById(R.id.number_text_view);
        btnUpdate=findViewById(R.id.btn_update);
        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                numbersUpdate();
            }
        });

        inputNumber1.setText(manager.getPhoneNumber());
        inputNumber1.setEnabled(false);

        inputNumber2.setText(manager.getPhoneNumber2());
        inputNumber2.setEnabled(false);

        btnUpdate.setText(R.string.update);

        multiplePermission();

        //TODO: location
        isGooglePlayServicesAvailable();

        if (!isLocationEnabled()) {
            showLocationAlert();
        }
        locationRequest = new LocationRequest();
        long UPDATE_INTERVAL = 100000;
        locationRequest.setInterval(UPDATE_INTERVAL);
        long FASTEST_INTERVAL = 60000;
        locationRequest.setFastestInterval(FASTEST_INTERVAL);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        googleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API).build();

        Intent intent=new Intent(this,LocationService.class);
        startService(intent);

        backgroundLocationReceiver();
    }

    private void backgroundLocationReceiver(){

        LocalBroadcastManager.getInstance(this).registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                String lat=intent.getStringExtra(LocationService.EXTRA_LATITUDE);
                String lon=intent.getStringExtra(LocationService.EXTRA_LONGITUDE);

                if (lat!=null && lon!=null){
                    // Toast.makeText(HelpActivity.this,"Lat: "+lat+"\nLong: "+lon,Toast.LENGTH_LONG).show();

                    String geoUri="https://maps.google.com/maps?q=loc:"+lat+","+lon;
                    manager.setGeoTrace(geoUri);

                    getAddress(lat,lon);
                }else {
                    Toast.makeText(HelpActivity.this,"Location is null "+lon,Toast.LENGTH_LONG).show();
                }
            }
        },new IntentFilter(LocationService.ACTION_LOCATION_BROADCAST));
    }

    private void getAddress(String lt,String lng){
        double lati=Double.parseDouble(lt);
        double lang=Double.parseDouble(lng);
        String add;

        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        List<Address> addressList;

        try {
            addressList = geocoder.getFromLocation(lati, lang, 1);

            add = addressList.get(0).getAddressLine(0);

            Log.d("Address",add);
            //locationChangedText.setText(add);
            manager.setLocationTrace(add);
            // Toast.makeText(HelpActivity.this,add,Toast.LENGTH_LONG).show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @Override
    protected void onStart() {
        //TODO: location connected
        if (googleApiClient != null) {
            googleApiClient.connect();
        }
        super.onStart();
    }

    @Override
    protected void onStop() {
        //TODO: location disconnected
        if (googleApiClient != null) {
            googleApiClient.disconnect();
        }
        super.onStop();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_registration, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_about) {
            startActivity(new Intent(HelpActivity.this,AboutActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void backgroundService(){
        startService(new Intent(this, LockService.class));
    }

    private void numbersUpdate(){

        if (btnUpdate.getText().equals("Save")) {

            number1 = inputNumber1.getText().toString();
            // manager.setPhoneNumber(number1);

            number2 = inputNumber2.getText().toString();
            // manager.setPhoneNumber2(number2);

            if (number1.isEmpty()){
                inputNumber1.setError("Please input number");
                inputNumber1.setEnabled(true);
            }
            if (number2.isEmpty()){
                inputNumber2.setError("Please input number");
                inputNumber2.setEnabled(true);
            }
            if (!number1.isEmpty() && !number2.isEmpty()){
                manager.setPhoneNumber(number1);
                manager.setPhoneNumber2(number2);
                btnUpdate.setText(R.string.update);
                inputNumber1.setEnabled(false);
                inputNumber2.setEnabled(false);
                updateData();
                backgroundService();
            }

            //inputNumber1.setEnabled(false);
            // inputNumber2.setEnabled(false);

            //updateData();
            // backgroundService();

            // btnUpdate.setText(R.string.update);

        } else if (btnUpdate.getText().equals("Update Help Number")) {

            inputNumber1.setEnabled(true);
            inputNumber2.setEnabled(true);
            btnUpdate.setText(R.string.save);

            stopService(new Intent(this,LockService.class));
        }

    }

    private void updateData(){
        updateProgress.setVisibility(View.VISIBLE);

        StringRequest stringRequest=new StringRequest(Request.Method.POST, Config.updateUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                updateProgress.setVisibility(View.GONE);

                if (response==null){
                    Toast.makeText(HelpActivity.this,"No numbers updates",Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(HelpActivity.this, response, Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                updateProgress.setVisibility(View.GONE);
                Toast.makeText(HelpActivity.this, "Check internet connection: OR Location is not found", Toast.LENGTH_SHORT).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String, String> params = new HashMap<>();

                params.put("mobile_id", newID);
                params.put("mobile_1", number1);
                params.put("mobile_2", number2);
                params.put("location", address);

                return params;
            }
        };

        AppController.getInstance().addToRequestQueue(stringRequest);
    }

    private void multiplePermission(){

        if (ActivityCompat.checkSelfPermission(this,permissionRequired[0])!= PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(this,permissionRequired[1])!=PackageManager.PERMISSION_GRANTED  ||
                ActivityCompat.checkSelfPermission(this,permissionRequired[2])!=PackageManager.PERMISSION_GRANTED  ||
                ActivityCompat.checkSelfPermission(this,permissionRequired[3])!=PackageManager.PERMISSION_GRANTED ){

            if (ActivityCompat.shouldShowRequestPermissionRationale(this,permissionRequired[0]) || ActivityCompat.shouldShowRequestPermissionRationale(this,permissionRequired[1]) ||
                    ActivityCompat.shouldShowRequestPermissionRationale(this,permissionRequired[2])  || ActivityCompat.shouldShowRequestPermissionRationale(this,permissionRequired[3])){


            }
            else if (permissionStatus.getBoolean(permissionRequired[0],false)){
                sentToSettings = true;
                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                Uri uri = Uri.fromParts("package", getPackageName(), null);
                intent.setData(uri);
                startActivityForResult(intent, REQUEST_PERMISSION_SETTING);

                Toast.makeText(HelpActivity.this, "Go to Permissions to Grant  Sms, Phone call and Location", Toast.LENGTH_LONG).show();

            }

            ActivityCompat.requestPermissions(this,permissionRequired,STORAGE_PERMISSION_CODE);

        }
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
        Location lastLocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);

        if (lastLocation != null) {
            displayLocation(lastLocation);
        } else {
            Toast.makeText(HelpActivity.this, "No location found", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        switch (requestCode) {
            case MY_PERMISSION_REQUEST_ACCESS_FINE_LOCATION: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(HelpActivity.this, "Permission was granted", Toast.LENGTH_SHORT).show();

                    try {
                        LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, (com.google.android.gms.location.LocationListener) this);
                    } catch (SecurityException e) {
                        Toast.makeText(HelpActivity.this, "SecurityException: " + e.toString(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(HelpActivity.this, "Permission denied", Toast.LENGTH_SHORT).show();
                }
                return;
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void showLocationAlert() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Enable Location").setMessage("Your Location setting is set to off.\n Please Enable Location to use this app");
        builder.setPositiveButton("Location Settings", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
            }
        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.show();
    }

    private boolean isLocationEnabled() {
        LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        return manager.isProviderEnabled(LocationManager.GPS_PROVIDER) || manager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    private boolean isGooglePlayServicesAvailable() {
        final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);

        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(this, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                finish();
                Toast.makeText(HelpActivity.this, "This device is not supported", Toast.LENGTH_SHORT).show();
            }
            return false;
        }
        return true;
    }

    private void displayLocation(Location location) {
        latitude = location.getLatitude();
        longitude = location.getLongitude();

        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        List<Address> addressList;

        try {
            addressList = geocoder.getFromLocation(latitude, longitude, 1);

            address = addressList.get(0).getAddressLine(0);

            updateLocationText.setText(address + ":" + newID);
            manager.setLocation(address);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
