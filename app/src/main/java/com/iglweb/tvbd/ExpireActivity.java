package com.iglweb.tvbd;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static com.iglweb.tvbd.MainActivity.EXPIRE;

public class ExpireActivity extends AppCompatActivity {

    TextView tvName, tvMobile, tvpin;
    Button btnCall;

    static String URL_RESELLER_INFO = "URL";
    String name, mobile, userPin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
        }
        setContentView(R.layout.activity_expire);

        tvName = findViewById(R.id.tv_name);
        tvMobile = findViewById(R.id.tv_mobile);
        tvpin = findViewById(R.id.tv_pin);
        btnCall = findViewById(R.id.btn_call);

        btnCall.setVisibility(View.INVISIBLE);

        Intent intent = getIntent();
        String androidID = intent.getStringExtra(EXPIRE);
        getreselerInfo(androidID);

        btnCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                call();
            }
        });
    }

    @SuppressLint("MissingPermission")
    public void call() {
        Intent callIntent = new Intent(Intent.ACTION_DIAL);
        callIntent.setData(Uri.parse("tel:" + mobile));
        startActivity(callIntent);
    }

    private void getreselerInfo(final String androidID) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_RESELLER_INFO, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject mainObject = new JSONObject(response);
                    JSONArray mainArray = mainObject.getJSONArray("reseller");
                    JSONObject childObject = mainArray.getJSONObject(0);
                    name = childObject.getString("res_name");
                    mobile = childObject.getString("res_cell");
                    userPin = childObject.getString("user_pin");

                    tvName.setText(name);
                    tvMobile.setText(mobile);
                    tvpin.setText("YOUR PIN: "+userPin);

                    btnCall.setVisibility(View.VISIBLE);


                } catch (JSONException e) {
                    Toast.makeText(ExpireActivity.this, "Something Wrong in your Internet Connection", Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(ExpireActivity.this, "Something went wrong", Toast.LENGTH_LONG).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> parameters = new HashMap<>();
                parameters.put("androidID", androidID);
                return parameters;
            }
        };

        MySingleton.getInstant(ExpireActivity.this).addToRequestqueue(stringRequest);
    }
}
