package com.iglweb.tvbd;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.ConnectivityManager;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import android.provider.Settings.Secure;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.iglweb.tvbd.UserSessionManager.PHONE_ID;


public class MainActivity extends AppCompatActivity {
//    TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);


//    final TelephonyManager tm = (TelephonyManager) getBaseContext().getSystemService(Context.TELEPHONY_SERVICE);

//    final String tmDevice, tmSerial, androidId;

    //    String deviceId = deviceUuid.toString();


    //    @SuppressLint("MissingPermission")
//    String androidEMI = telephonyManager.getDeviceId();
//
//    tmDevice = "" + tm.getDeviceId();
//    tmSerial = "" + tm.getSimSerialNumber();
//    androidId = "" + android.provider.Settings.Secure.getString(getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);
//
//    UUID deviceUuid = new UUID(androidId.hashCode(), ((long)tmDevice.hashCode() << 32) | tmSerial.hashCode());


    public static final String INVALID = "-1";
    public static final String EXPIRE = "10";
    public static final String VALID = "9";
    public static final String REJECTED = "8";
    public static final String NO_CREDIT = "7";
    public static final String Y_ERROR = "6";
    public static final String CONNECT_FAIL = "0";
    public static final String CONNECTED = "1";
    public static final String PIN_USED = "-2";


    Button btnContinue;
    EditText etxPinCode, etxMobile;
    TextView tvPin;
    ProgressDialog progressDialog;
    private String androidID;
    String pPin = "";
    String mobile = "";

    UserSessionManager manager;


    private static String URL_CHECK_STATUS = "http://tv-bd.com/tvbdApp/getPhonePin.php";
    private static String URL_NEW_CHECK_STATUS = "http://tv-bd.com/tvbdApp/newGetPhonePin.php";


    @SuppressLint("HardwareIds")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
        }
        setContentView(R.layout.activity_main);
        manager = new UserSessionManager(MainActivity.this);
        intComponent();
        androidID = Secure.getString(getBaseContext().getContentResolver(), Secure.ANDROID_ID);
        // manager.logoutUser();
        progressDialog = new ProgressDialog(MainActivity.this);
        progressDialog.setMessage("Please Wait...\nWe are Connecting to Server");
        progressDialog.setCancelable(false);

        HashMap<String, String> spin = manager.getUserPhoneID();
        String a = spin.get(PHONE_ID);
        if (a.equals("tv")) {
            Toast.makeText(MainActivity.this, "YOU ARE NEW USER HERE!! PLEASE ENTER YOUR PIN", Toast.LENGTH_SHORT).show();
        } else {
            progressDialog.show();
            checkVolley(a);
        }

        btnContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                pPin = etxPinCode.getText().toString().trim();
                mobile = etxMobile.getText().toString().trim();
                if (pPin.equals("") || mobile.equals("")) {
                    Toast.makeText(MainActivity.this, "PLEASE ENTER YOUR PIN AND MOBILE NUMBER", Toast.LENGTH_SHORT).show();
                } else if (!(etxPinCode.getText().length() >= 6)) {
                    Toast.makeText(MainActivity.this, "PIN MUST BE 6 CHARACTER", Toast.LENGTH_SHORT).show();
                } else {
                    if (isValidPhone(mobile)) {
                        progressDialog.show();
                        newCheckVolley(pPin, mobile);
                    } else {
                        Toast.makeText(MainActivity.this, "PLEASE ENTER VALID MOBILE NUMBER", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

    }

    public static boolean isValidPhone(String phone) {
        String expression = "^(?:\\+?88|88)?01[15-9]\\d{8}$";
        Pattern pattern = Pattern.compile(expression);
        Matcher matcher = pattern.matcher(phone);
        return matcher.matches();
    }

    private void checkVolley(final String pin) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_CHECK_STATUS, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    if (response.equals(INVALID)) {
                        progressDialog.dismiss();
                        Toast.makeText(MainActivity.this, "INVALID PIN ENTER AGAIN", Toast.LENGTH_LONG).show();
                    } else if (response.equals(VALID)) {
                        startActivity(new Intent(MainActivity.this, CHActivity.class));
                        finish();
                        progressDialog.dismiss();

                    } else if (response.equals(EXPIRE)) {
                        progressDialog.dismiss();
                        startActivity(new Intent(MainActivity.this, ExpireActivity.class).putExtra(EXPIRE, androidID));
                        finish();
                    } else if (response.equals(REJECTED)) {
                        progressDialog.dismiss();
                        Toast.makeText(MainActivity.this, "REJECTED", Toast.LENGTH_LONG).show();

                    } else if (response.equals(PIN_USED)) {
                        progressDialog.dismiss();
                        Toast.makeText(MainActivity.this, "PIN ALREADY USED", Toast.LENGTH_LONG).show();
                    } else if (response.equals(NO_CREDIT)) {
                        progressDialog.dismiss();
                        startActivity(new Intent(MainActivity.this, ExpireActivity.class).putExtra(EXPIRE, androidID));
                        finish();
                    } else if (response.equals(Y_ERROR)) {
                        progressDialog.dismiss();
                        Toast.makeText(MainActivity.this, "SOMETHING WRONG", Toast.LENGTH_LONG).show();
                    } else if (response.equals(CONNECT_FAIL)) {
                        progressDialog.dismiss();
                        Toast.makeText(MainActivity.this, "FAIL TO CONNECT", Toast.LENGTH_LONG).show();
                    } else if (response.equals(CONNECTED)) {
                        progressDialog.dismiss();
                        manager.createUserLoginSession(pin);
                        Toast.makeText(MainActivity.this, "SUCCESSFULLY CONNECTED", Toast.LENGTH_LONG).show();
                        startActivity(new Intent(MainActivity.this, CHActivity.class));
                        finish();
                    } else {
                        progressDialog.dismiss();
                        Toast.makeText(MainActivity.this, "SOMETHING WRONG" + response, Toast.LENGTH_LONG).show();
                        progressDialog.dismiss();
                    }

                } catch (Exception e) {
                    Toast.makeText(MainActivity.this, "SOMETHING WRONG" + e.toString(), Toast.LENGTH_LONG).show();
                    showDialog();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MainActivity.this, "SOMETHING WRONG IN YOUR INTERNET CONNECTION TRY AGAIN", Toast.LENGTH_LONG).show();
                showDialog();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> parameters = new HashMap<>();
                parameters.put("androidID", androidID);
                parameters.put("pin", pin);
                return parameters;
            }
        };

        MySingleton.getInstant(MainActivity.this).addToRequestqueue(stringRequest);
    }

    private void newCheckVolley(final String pin, final String mobile) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_NEW_CHECK_STATUS, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    if (response.equals(INVALID)) {
                        progressDialog.dismiss();
                        Toast.makeText(MainActivity.this, "INVALID PIN ENTER AGAIN", Toast.LENGTH_LONG).show();
                    } else if (response.equals(VALID)) {
                        startActivity(new Intent(MainActivity.this, CHActivity.class));
                        finish();
                        progressDialog.dismiss();

                    } else if (response.equals(EXPIRE)) {
                        progressDialog.dismiss();
                        startActivity(new Intent(MainActivity.this, ExpireActivity.class).putExtra(EXPIRE, androidID));
                        finish();

                    } else if (response.equals(REJECTED)) {
                        progressDialog.dismiss();
                        Toast.makeText(MainActivity.this, "REJECTED", Toast.LENGTH_LONG).show();

                    } else if (response.equals(PIN_USED)) {
                        progressDialog.dismiss();
                        Toast.makeText(MainActivity.this, "PIN ALREADY USED", Toast.LENGTH_LONG).show();

                    } else if (response.equals(NO_CREDIT)) {
                        progressDialog.dismiss();
                        startActivity(new Intent(MainActivity.this, ExpireActivity.class).putExtra(EXPIRE, androidID));
                        finish();

                    } else if (response.equals(Y_ERROR)) {
                        progressDialog.dismiss();
                        Toast.makeText(MainActivity.this, "SOMETHING WRONG", Toast.LENGTH_LONG).show();

                    } else if (response.equals(CONNECT_FAIL)) {
                        progressDialog.dismiss();
                        Toast.makeText(MainActivity.this, "FAIL TO CONNECT", Toast.LENGTH_LONG).show();

                    } else if (response.equals(CONNECTED)) {
                        progressDialog.dismiss();
                        manager.createUserLoginSession(pin);
                        Toast.makeText(MainActivity.this, "SUCCESSFULLY CONNECTED", Toast.LENGTH_LONG).show();
                        startActivity(new Intent(MainActivity.this, CHActivity.class));
                        finish();
                    } else {
                        progressDialog.dismiss();
                        Toast.makeText(MainActivity.this, "SOMETHING WRONG" + response, Toast.LENGTH_LONG).show();
                        progressDialog.dismiss();
                    }

                } catch (Exception e) {
                    Toast.makeText(MainActivity.this, "SOMETHING WRONG" + e.toString(), Toast.LENGTH_LONG).show();
                    showDialog();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MainActivity.this, "SOMETHING WRONG IN YOUR INTERNET CONNECTION TRY AGAIN", Toast.LENGTH_LONG).show();
                showDialog();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> parameters = new HashMap<>();
                parameters.put("androidID", androidID);
                parameters.put("pin", pin);
                parameters.put("mobile", mobile);
                return parameters;
            }
        };

        MySingleton.getInstant(MainActivity.this).addToRequestqueue(stringRequest);
    }

    public void showDialog() {
        progressDialog.dismiss();
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("ALERT...");
        builder.setMessage("SOMETHING WRONG IN YOU INTERNET CONNECTION TRY AGAIN");
        builder.setPositiveButton("OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogInterface, int id) {
                        startActivity(new Intent(MainActivity.this, MainActivity.class));
                        finish();
                    }
                });
        builder.setNegativeButton("EXIT", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        builder.setCancelable(false);
        builder.setIcon(R.drawable.v_warning);
        builder.create().show();
    }

    private void intComponent() {
        btnContinue = findViewById(R.id.btn_continue);
        etxPinCode = findViewById(R.id.etx_pin_code);
        etxMobile = findViewById(R.id.etx_pin_mobile);
        tvPin = findViewById(R.id.tv_pin_code);
    }

    public final boolean isInternetOn() {

        // get Connectivity Manager object to check connection
        ConnectivityManager connectivityManager =
                (ConnectivityManager) getSystemService(getBaseContext().CONNECTIVITY_SERVICE);

        // Check for network connections
        if (connectivityManager.getNetworkInfo(0).getState() == android.net.NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(0).getState() == android.net.NetworkInfo.State.CONNECTING ||
                connectivityManager.getNetworkInfo(1).getState() == android.net.NetworkInfo.State.CONNECTING ||
                connectivityManager.getNetworkInfo(1).getState() == android.net.NetworkInfo.State.CONNECTED) {

            // if connected with internet

            Toast.makeText(this, "Connected", Toast.LENGTH_LONG).show();
            return true;

        } else if (
                connectivityManager.getNetworkInfo(0).getState() == android.net.NetworkInfo.State.DISCONNECTED ||
                        connectivityManager.getNetworkInfo(1).getState() == android.net.NetworkInfo.State.DISCONNECTED) {

            Toast.makeText(this, "Not Connected", Toast.LENGTH_LONG).show();
            return false;
        }
        return false;

    }
}
