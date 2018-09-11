package rakshaapp.in.yuvakranti.rakshaapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

import rakshaapp.in.yuvakranti.rakshaapp.app.AppController;
import rakshaapp.in.yuvakranti.rakshaapp.app.Config;
import rakshaapp.in.yuvakranti.rakshaapp.helper.PrefManager;

public class LoginActivity extends AppCompatActivity {

    private EditText inputMobile;
    private Button btnLogin;
    private ProgressBar progressBar;
    private PrefManager prefManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        prefManager=new PrefManager(this);
        if (!prefManager.isFirstTimeLoginLaunch()){
            launchProfileScreen();
            finish();
        }

        setContentView(R.layout.activity_login);

        inputMobile=findViewById(R.id.input_mobile_login);
        progressBar=findViewById(R.id.login_progress);
        btnLogin=findViewById(R.id.btn_login);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mobile=inputMobile.getText().toString();

                if (mobile.isEmpty()){
                    inputMobile.setError("Please input mobile number");
                }else{
                    getLogin(mobile);
                    //prefManager.setMobileId(mobile);
                    //launchProfileScreen();
                }
            }
        });
    }

    private void launchProfileScreen(){
        prefManager.setIsFirstTimeLoginLaunch(false);
        startActivity(new Intent(LoginActivity.this,HelpActivity.class));
        finish();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        prefManager.setFirstTimeLaunch(true);
        startActivity(new Intent(LoginActivity.this,RegistrationActivity.class));
        finish();
    }

    private void getLogin(final String mobile){
        progressBar.setVisibility(View.VISIBLE);

        StringRequest request=new StringRequest(Request.Method.POST, Config.LOGIN_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                progressBar.setVisibility(View.GONE);
                if (response.equalsIgnoreCase("login success")){
                    launchProfileScreen();
                    prefManager.setMobileId(mobile);
                }else {
                    Toast.makeText(LoginActivity.this,response,Toast.LENGTH_SHORT).show();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(LoginActivity.this,"Check your internet connection", Toast.LENGTH_SHORT).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String ,String > params=new HashMap<>();
                params.put("mobile",mobile);
                return params;
            }
        };

        AppController.getInstance().addToRequestQueue(request);
    }
}
