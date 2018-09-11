package rakshaapp.in.yuvakranti.rakshaapp;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;


import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import rakshaapp.in.yuvakranti.rakshaapp.app.Config;
import rakshaapp.in.yuvakranti.rakshaapp.helper.PrefManager;
import rakshaapp.in.yuvakranti.rakshaapp.service.HttpService;

public class RegistrationActivity extends AppCompatActivity implements View.OnClickListener{

    private EditText inputName,inputFatherName,inputMobile,inputAadhar,inputEmail;
    private Button btnSave,btnVerifyOtp;
    private ProgressBar profileProgress,otpProgress;
    private EditText inputOtp;
    private LinearLayout otpLayout;
    private RelativeLayout registrationLayout;
    private Button  btnToLogin;
    private PrefManager prefManager;

    private static final int STORAGE_PERMISSION_CODE=100;
    private static final int REQUEST_PERMISSION_SETTING=101;
    private SharedPreferences permissionStatus;
    private boolean sentToSettings = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        prefManager=new PrefManager(this);
        if (!prefManager.isFirstTimeLaunch()){
            launchHomeScreen();
            finish();
        }

        setContentView(R.layout.activity_registration);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        permissionStatus = getSharedPreferences("permissionStatus",MODE_PRIVATE);

        inputName=findViewById(R.id.input_name);
        inputFatherName=findViewById(R.id.input_father_name);
        inputMobile=findViewById(R.id.input_mobile);
        inputAadhar=findViewById(R.id.input_adhar_no);
        inputEmail=findViewById(R.id.input_email);

        inputOtp=findViewById(R.id.input_otp);
        btnSave=findViewById(R.id.btn_save);
        btnVerifyOtp=findViewById(R.id.btn_verify_otp);
        btnToLogin=findViewById(R.id.btn_to_login);
        profileProgress=findViewById(R.id.profile_progress_bar);
        otpProgress=findViewById(R.id.otp_progress_bar);
        otpLayout=findViewById(R.id.otp_linear_layout);
        registrationLayout=findViewById(R.id.registration_relative_layout);

        btnSave.setOnClickListener(this);
        btnVerifyOtp.setOnClickListener(this);
        btnToLogin.setOnClickListener(this);

        requestStoragePermission();
    }

    private void launchHomeScreen(){
        prefManager.setFirstTimeLaunch(false);
        startActivity(new Intent(RegistrationActivity.this,HelpActivity.class));
        finish();
    }

    private void requestStoragePermission(){
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE)== PackageManager.PERMISSION_GRANTED){
            return;
        }

        if (ActivityCompat.shouldShowRequestPermissionRationale(this,android.Manifest.permission.READ_EXTERNAL_STORAGE)){

        } else if (permissionStatus.getBoolean(Manifest.permission.READ_EXTERNAL_STORAGE,false)){
            sentToSettings = true;
            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            Uri uri = Uri.fromParts("package", getPackageName(), null);
            intent.setData(uri);
            startActivityForResult(intent, REQUEST_PERMISSION_SETTING);

            Toast.makeText(getBaseContext(), "Go to Permission to Grant  Photos", Toast.LENGTH_LONG).show();

        }


        ActivityCompat.requestPermissions(this,new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE},STORAGE_PERMISSION_CODE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_registration, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_about) {
            startActivity(new Intent(RegistrationActivity.this,AboutActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_save:
                validateForm();
                break;
            case R.id.btn_verify_otp:
                verifyOtp();
                break;
            case R.id.btn_to_login:
                startActivity(new Intent(RegistrationActivity.this,LoginActivity.class));
                prefManager.setFirstTimeLaunch(false);
                finish();
                break;
        }
    }

    private void validateForm(){

        String name=inputName.getText().toString().trim();
        String father=inputFatherName.getText().toString().trim();
        String aadhar=inputAadhar.getText().toString().trim();
        String mobile = inputMobile.getText().toString().trim();
        String email=inputEmail.getText().toString().trim();

        if (name.isEmpty()){
            inputName.setError("Please input name");
        }
        if (father.isEmpty()){
            inputFatherName.setError("Please input father's name");
        }
        if (aadhar.isEmpty()){
            inputAadhar.setError("Please input Aadhar");
        }
        if (mobile.isEmpty()){
            inputMobile.setError("Please input mobile number");
        }
        if (email.isEmpty()){
            inputEmail.setError("Please input email");
        }else if(!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()){

            inputEmail.setError("Please input proper email address");
        }

        if (!name.isEmpty() && !father.isEmpty() && !aadhar.isEmpty() && !mobile.isEmpty() && !email.isEmpty()){

            inputName.setEnabled(false);
            inputAadhar.setEnabled(false);
            inputFatherName.setEnabled(false);
            inputEmail.setEnabled(false);
            inputMobile.setEnabled(false);
            btnSave.setEnabled(false);

            //Toast.makeText(RegistrationActivity.this,Config.URL_REQUEST_SMS,Toast.LENGTH_SHORT).show();

            getRegister(name,father,mobile,aadhar,email);
            // launchHomeScreen();
        }
    }

    private void getRegister(final String name, final String father, final String mobile, final String aadhar, final String email){

        profileProgress.setVisibility(View.VISIBLE);

        StringRequest request=new StringRequest(Request.Method.POST, Config.URL_REQUEST_SMS, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {

                try {
                    JSONObject responseObj = new JSONObject(response);

                    boolean error = responseObj.getBoolean("error");
                    String message = responseObj.getString("message");

                    if (!error) {
                        otpLayout.setVisibility(View.VISIBLE);
                        registrationLayout.setEnabled(false);
                        Toast.makeText(RegistrationActivity.this,message,Toast.LENGTH_SHORT).show();
                        prefManager.setMobileId(mobile);
                    }else {
                        Toast.makeText(RegistrationActivity.this,message,Toast.LENGTH_SHORT).show();
                        enable();
                    }
                    profileProgress.setVisibility(View.GONE);
                } catch (JSONException e) {
                    Toast.makeText(RegistrationActivity.this,"JsonException :"+e.getMessage(),Toast.LENGTH_SHORT).show();
                    profileProgress.setVisibility(View.GONE);
                    enable();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(RegistrationActivity.this,"ErrorListener :"+error.getMessage(),Toast.LENGTH_SHORT).show();
                profileProgress.setVisibility(View.GONE);
                Log.e("ErrorListener", "Error: " + error.getMessage());
                enable();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String ,String >params= new HashMap<>();
                params.put("name",name);
                params.put("father_name",father);
                params.put("mobile",mobile);
                params.put("adhar",aadhar);
                params.put("email",email);

                Log.e("getParams","in get params");
                return params;
            }
        };

        RequestQueue queue= Volley.newRequestQueue(this);
        queue.add(request);
    }
    private void verifyOtp(){

        String otp=inputOtp.getText().toString().trim();

        if (!otp.isEmpty()){
            Intent intent=new Intent(getApplicationContext(), HttpService.class);
            intent.putExtra("otp",otp);
            startService(intent);
            launchHomeScreen();
            otpProgress.setVisibility(View.VISIBLE);
        }else{
            Toast.makeText(RegistrationActivity.this,"Please enter OTP",Toast.LENGTH_SHORT).show();
            otpProgress.setVisibility(View.GONE);
        }
    }

    private void enable(){
        inputName.setEnabled(true);
        inputAadhar.setEnabled(true);
        inputFatherName.setEnabled(true);
        inputEmail.setEnabled(true);
        inputMobile.setEnabled(true);
        btnSave.setEnabled(true);
    }
    @Override
    protected void onStop() {
        super.onStop();
        inputName.setEnabled(true);
        inputAadhar.setEnabled(true);
        inputFatherName.setEnabled(true);
        inputEmail.setEnabled(true);
        inputMobile.setEnabled(true);
    }
}