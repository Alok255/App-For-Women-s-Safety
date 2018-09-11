package rakshaapp.in.yuvakranti.rakshaapp.service;


import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import rakshaapp.in.yuvakranti.rakshaapp.HelpActivity;
import rakshaapp.in.yuvakranti.rakshaapp.app.AppController;
import rakshaapp.in.yuvakranti.rakshaapp.app.Config;


public class HttpService extends IntentService{
    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    private static final String TAG=HttpService.class.getSimpleName();

    public HttpService() {
        super(HttpService.class.getSimpleName());
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

        if (intent!=null){
            String otp=intent.getStringExtra("otp");
            verifyOtp(otp);
        }
    }

    private void verifyOtp(final String otp){
        StringRequest stringRequest=new StringRequest(Request.Method.POST, Config.URL_VERIFY_OTP, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    JSONObject responseObj=new JSONObject(response);

                    boolean error=responseObj.getBoolean("error");
                    String message=responseObj.getString("message");

                    if (!error){
                        JSONObject profileObj=responseObj.getJSONObject("profile");

                        String name=profileObj.getString("name");
                        String father=profileObj.getString("father_name");
                        String mobile=profileObj.getString("mobile");
                        String aadhar=profileObj.getString("adhar");
                        String email=profileObj.getString("email");

                        Intent intent=new Intent(HttpService.this, HelpActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);

                        Toast.makeText(getApplicationContext(),"In HttpService :"+message,Toast.LENGTH_SHORT).show();
                    }else {
                        Toast.makeText(getApplicationContext(),"In HttpService :"+message,Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(),"In HttpService exception :"+e.getMessage(),Toast.LENGTH_SHORT).show();

                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(),"onErrorResponse HttpService :"+error.getMessage(),Toast.LENGTH_SHORT).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String ,String >params=new HashMap<>();
                params.put("otp",otp);
                return params;
            }
        };

        AppController.getInstance().addToRequestQueue(stringRequest);
    }
}
