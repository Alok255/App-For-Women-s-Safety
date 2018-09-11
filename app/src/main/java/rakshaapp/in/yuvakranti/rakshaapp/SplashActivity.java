package rakshaapp.in.yuvakranti.rakshaapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import rakshaapp.in.yuvakranti.rakshaapp.helper.PrefManager;

public class SplashActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        PrefManager prefManager = new PrefManager(this);

        final boolean isRegistration= prefManager.isFirstTimeLaunch();
        final boolean isHelp= prefManager.isFirstTimeLaunch();

        int SPLASH_TIME_OUT = 3000;
        new Handler().postDelayed(new Runnable(){

            @Override
            public void run() {

                if (isRegistration==true){
                    startActivity(new Intent(SplashActivity.this,RegistrationActivity.class));
                }else if (isHelp==false){
                    Intent i = new Intent(SplashActivity.this, HelpActivity.class);
                    startActivity(i);
                }
                finish();

            }

        }, SPLASH_TIME_OUT);
    }
}
