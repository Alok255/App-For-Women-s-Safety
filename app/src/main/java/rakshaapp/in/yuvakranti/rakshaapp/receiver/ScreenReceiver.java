package rakshaapp.in.yuvakranti.rakshaapp.receiver;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.telephony.SmsManager;
import android.util.Log;
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


public class ScreenReceiver extends BroadcastReceiver {

    public static boolean wasScreenOn = true;
    public static int count = 0;

    @Override
    public void onReceive(Context context, Intent intent) {

        PrefManager prefManager = new PrefManager(context);
        String number = prefManager.getPhoneNumber();
        String phoneNumber2 = prefManager.getPhoneNumber2();
        String mobileId=prefManager.getMobileId();
        String geoLocation = prefManager.getGeoTrace();
        String addressTrace=prefManager.getLocationTrace();


        if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
            wasScreenOn = false;
            count++;

            Log.e("Screen Off", "" + wasScreenOn + ":count:" + count);

        } else if (intent.getAction().equals(Intent.ACTION_SCREEN_ON)) {
            wasScreenOn = true;

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    count = 0;
                }
            }, 5000);

            if (count == 2) {
                Intent callIntent = new Intent(Intent.ACTION_CALL);
                callIntent.setData(Uri.parse("tel:" + number));
                callIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                if (ActivityCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                context.startActivity(callIntent);

                sendMessageOne(number,addressTrace,geoLocation,context);
                sendMessageTwo(phoneNumber2,addressTrace,geoLocation,context);
                updateTraceLocation(addressTrace,mobileId);


                count = 0;
            }
        }
    }

    private void sendMessageOne(String number,String address,String geo,Context context){
        try {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(number, null, "Help Me!!! \n"+address+"\n"+geo, null, null);
            Toast.makeText(context, "Sms Sent", Toast.LENGTH_SHORT).show();
            Log.e("Message","Sent");
        }catch (Exception e){
            Toast.makeText(context,"Failed",Toast.LENGTH_SHORT).show();
            Log.e("Message","Failed");
        }
    }

    private void sendMessageTwo(String number2,String address,String geo,Context context){
        try {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(number2, null, "Help Me!!! \n"+address+"\n"+geo, null, null);
            Toast.makeText(context, "Sms Sent", Toast.LENGTH_SHORT).show();
            Log.e("Message","Sent");
        }catch (Exception e){
            Toast.makeText(context,"Failed",Toast.LENGTH_SHORT).show();
            Log.e("Message","Failed");
        }
    }

    private void updateTraceLocation(final String address, final String mobile){

        StringRequest request=new StringRequest(Request.Method.POST, Config.UPDATE_TRACE_LOCATION, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                if (response==null){
                    Log.d("OnResponse","response is null");
                }else{
                    Log.d("onResponse","Successfully updated");
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Log.e("OnErrorResponse","Check Internet connection:"+error.getMessage());
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();

                params.put("mobile_id", mobile);
                params.put("trace_location", address);

                return params;
            }
        };
        AppController.getInstance().addToRequestQueue(request);
    }
}
