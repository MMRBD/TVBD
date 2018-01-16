package com.iglweb.tvbd;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
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


    public final String INVALID = "-1";
    public final String VALID = "9";
    public final String REJECTED = "8";
    public final String NO_CREDIT = "7";
    public final String Y_ERROR = "6";
    public final String CONNECT_FAIL = "0";
    public final String CONNECTED = "1";
    public final String PIN_USED = "-2";


    Button btnContinue;
    EditText etxPinCode;
    TextView tvPin;
    ProgressDialog progressDialog;
    private String androidID;
    String pPin = "";

    UserSessionManager manager;


    String url = "http://tv-bd.com/getPhonePin.php";


    @SuppressLint("HardwareIds")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);
        manager = new UserSessionManager(MainActivity.this);
        intComponent();
        androidID = Secure.getString(getBaseContext().getContentResolver(), Secure.ANDROID_ID);
        // manager.logoutUser();
        progressDialog = new ProgressDialog(MainActivity.this);
        progressDialog.setMessage("Please Wait, We are Connecting to Server");

        HashMap<String, String> spin = manager.getUserPhoneID();
        String a = spin.get(PHONE_ID);
        if (!(a.equals(""))) {
            tvPin.setVisibility(View.INVISIBLE);
            etxPinCode.setVisibility(View.INVISIBLE);
            btnContinue.setVisibility(View.INVISIBLE);
            progressDialog.show();
            checkVolley(a);
        }


        //Toast.makeText(MainActivity.this, "Android id: " + androidID, Toast.LENGTH_SHORT).show();
        Log.e("Android ID", androidID);


        btnContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                pPin = etxPinCode.getText().toString().trim();
                if (pPin.equals("")) {
                    Toast.makeText(MainActivity.this, "Please Enter Your Pin", Toast.LENGTH_SHORT).show();
                } else if (!(etxPinCode.getText().length() >= 6)) {
                    Toast.makeText(MainActivity.this, "Pin Must Be 6 Character", Toast.LENGTH_SHORT).show();

                } else {

                    progressDialog.show();
                    checkVolley(pPin);
                }
            }
        });

    }

    private void checkVolley(final String pin) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {


                Log.e("Phone", response);
                try {
                    if (response.equals(INVALID)) {
                        progressDialog.dismiss();
                        Toast.makeText(MainActivity.this, "Invalid Pin", Toast.LENGTH_LONG).show();
                    } else if (response.equals(VALID)) {
                        progressDialog.dismiss();
                        startActivity(new Intent(MainActivity.this, ChanelActivity.class));
                        finish();

                    } else if (response.equals(REJECTED)) {
                        progressDialog.dismiss();
                        Toast.makeText(MainActivity.this, "REJECTED", Toast.LENGTH_LONG).show();

                    } else if (response.equals(PIN_USED)) {
                        progressDialog.dismiss();
                        Toast.makeText(MainActivity.this, "PIN ALREADY USED", Toast.LENGTH_LONG).show();

                    } else if (response.equals(NO_CREDIT)) {
                        progressDialog.dismiss();
                        Toast.makeText(MainActivity.this, "PLEASE CREDIT", Toast.LENGTH_LONG).show();

                    } else if (response.equals(Y_ERROR)) {
                        progressDialog.dismiss();
                        Toast.makeText(MainActivity.this, "Something Wrong", Toast.LENGTH_LONG).show();

                    } else if (response.equals(CONNECT_FAIL)) {
                        progressDialog.dismiss();
                        Toast.makeText(MainActivity.this, "FAIL TO CONNECT", Toast.LENGTH_LONG).show();

                    } else if (response.equals(CONNECTED)) {
                        progressDialog.dismiss();
                        manager.createUserLoginSession(pin);
                        Toast.makeText(MainActivity.this, "SUCCESSFULLY CONNECTED", Toast.LENGTH_LONG).show();
                        startActivity(new Intent(MainActivity.this, ChanelActivity.class));
                        finish();
                    } else {
                        progressDialog.dismiss();
                        Toast.makeText(MainActivity.this, "Something Wrong", Toast.LENGTH_LONG).show();
                    }

                } catch (Exception e) {
                    Toast.makeText(MainActivity.this, "Something wrong " + e.toString(), Toast.LENGTH_LONG).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MainActivity.this, "Something Wrong In Your INTERNET CONNECTION", Toast.LENGTH_LONG).show();
                progressDialog.dismiss();
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

//    @Override
//    protected void onStart() {
//        super.onStart();
//        if (new UserSessionManager(MainActivity.this).isUserLoggedIn()) {
//            startActivity(new Intent(MainActivity.this, ChanelActivity.class));
//
//            finish();
//        }
//    }

    private void intComponent() {
        btnContinue = findViewById(R.id.btn_continue);
        etxPinCode = findViewById(R.id.etx_pin_code);
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
