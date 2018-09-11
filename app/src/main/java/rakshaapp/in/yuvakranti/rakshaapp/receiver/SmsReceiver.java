package rakshaapp.in.yuvakranti.rakshaapp.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;

import rakshaapp.in.yuvakranti.rakshaapp.app.Config;
import rakshaapp.in.yuvakranti.rakshaapp.service.HttpService;


public class SmsReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        final Bundle bundle=intent.getExtras();

        try {
            if (bundle!=null){
                Object[]pdusObj=(Object[])bundle.get("pdus");

                for (Object aPdusObj:pdusObj){
                    SmsMessage currentMessage=SmsMessage.createFromPdu((byte[])aPdusObj);
                    String senderMessage=currentMessage.getDisplayOriginatingAddress();
                    String message=currentMessage.getDisplayMessageBody();

                    if (!senderMessage.toLowerCase().contains(Config.SMS_ORIGIN.toLowerCase())){
                        return;
                    }

                    String verificationCode=getVerificationCode(message);

                    Intent httpIntent=new Intent(context, HttpService.class);
                    httpIntent.putExtra("otp",verificationCode);
                    context.startService(httpIntent);
                }
            }
        }catch (Exception e){
            Log.e("SmsReceiver",e.getMessage());
        }
    }

    private String getVerificationCode(String message){
        String code=null;
        int index=message.indexOf(Config.OTP_DELIMITER);

        if (index!=-1){
            int start=index+2;
            int length=6;
            code=message.substring(start,start+length);
            return code;
        }
        return code;
    }
}