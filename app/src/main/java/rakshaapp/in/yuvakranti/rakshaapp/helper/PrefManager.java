package rakshaapp.in.yuvakranti.rakshaapp.helper;

import android.content.Context;
import android.content.SharedPreferences;

public class PrefManager {

    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    private Context context;

    //shared pref mode
    private int PRIVATE_MODE=0;

    //shared preferences file name
    private static final String PREF_NAME="raksha-profile";

    private static final String IS_FIRST_TIME_LAUNCH="IsFirstTimeLaunch";

    private static final String IS_FIRST_TIME_LOGIN_LAUNCH="IsFirstTimeLoginLaunch";

    private static final String PHONE_NO="phoneNumber";

    private static final String PHONE_NO_2="phoneNumber2";

    private static final String MOBILE_ID="mobileId";

    private static final String LOCATION_PASS="location";

    private static final String LOCATION_TRACE="location_trace";

    private static final String GEO_TRACE="geo_trace";

    public PrefManager(Context context){
        this.context=context;
        preferences=context.getSharedPreferences(PREF_NAME,PRIVATE_MODE);
        editor=preferences.edit();
    }

    public void setFirstTimeLaunch(boolean isFirstTime){
        editor.putBoolean(IS_FIRST_TIME_LAUNCH,isFirstTime);
        editor.commit();
    }

    public boolean isFirstTimeLaunch(){
        return preferences.getBoolean(IS_FIRST_TIME_LAUNCH,true);
    }

    public void setPhoneNumber(String phoneNumber){
        editor.putString(PHONE_NO,phoneNumber);
        editor.commit();
    }

    public String getPhoneNumber(){
        return preferences.getString(PHONE_NO,"9827231851");
    }

    public void setPhoneNumber2(String phoneNumber2){
        editor.putString(PHONE_NO_2,phoneNumber2);
        editor.commit();
    }

    public String getPhoneNumber2(){
        return preferences.getString(PHONE_NO_2,"123");
    }

    public void setMobileId(String mobileId){
        editor.putString(MOBILE_ID,mobileId);
        editor.commit();
    }

    public String getMobileId(){
        return preferences.getString(MOBILE_ID,"000");
    }

    public void setLocation(String location){
        editor.putString(LOCATION_PASS,location);
        editor.commit();
    }

    public String getLocation(){
        return preferences.getString(LOCATION_PASS,"No Location");
    }

    //todo: location trace

    public void setLocationTrace(String locationTrace){
        editor.putString(LOCATION_TRACE,locationTrace);
        editor.commit();
    }

    public String getLocationTrace(){
        return preferences.getString(LOCATION_PASS,"No Location Trace!");
    }

    public void setIsFirstTimeLoginLaunch(boolean isFirstTimeLogin){
        editor.putBoolean(IS_FIRST_TIME_LOGIN_LAUNCH,isFirstTimeLogin);
        editor.commit();
    }

    public boolean isFirstTimeLoginLaunch(){
        return preferences.getBoolean(IS_FIRST_TIME_LOGIN_LAUNCH,true);
    }

    public void setGeoTrace(String geoTrace){
        editor.putString(GEO_TRACE,geoTrace);
        editor.commit();
    }

    public String getGeoTrace(){
        return preferences.getString(GEO_TRACE,"No Geo found");
    }
}
