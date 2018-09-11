package rakshaapp.in.yuvakranti.rakshaapp.app;

public class Config {

    public static final String URL_REQUEST_SMS="http://demo.yuvakranti.in/android_sms/request_sms.php";
    public static final String URL_VERIFY_OTP="http://demo.yuvakranti.in/android_sms/verify_otp.php";
    public static final String updateUrl = "http://demo.yuvakranti.in/android_sms/update_raksha_user.php";
    public static final String LOGIN_URL="http://demo.yuvakranti.in/android_sms/raksha_login.php";
    public static final String UPDATE_TRACE_LOCATION="http://demo.yuvakranti.in/android_sms/update_trace_location.php";

    // SMS provider identification
    // It should match with your SMS gateway origin
    // You can use  MSGIND, TESTER and ALERTS as sender ID
    // If you want custom sender Id, approve MSG91 to get one
    public static final String SMS_ORIGIN = "RAKSHA";

    // special character to prefix the otp. Make sure this character appears only once in the sms
    public static final String OTP_DELIMITER = ":";
}
