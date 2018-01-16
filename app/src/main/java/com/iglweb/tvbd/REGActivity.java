package com.iglweb.tvbd;

import android.app.ProgressDialog;
import android.content.Intent;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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


public class REGActivity extends AppCompatActivity {

    Button btnSubmit;
    EditText etxName, etxEmail, etxPhone;
    String name = "", email = "", phone = "";
    String androidID;
    ProgressDialog progressDialog;
    String jsonURL = "http://tv-bd.com/submitUserInfo.php";

    public final String NOT_REGISTERED = "0";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reg);
        androidID = Settings.Secure.getString(getBaseContext().getContentResolver(), Settings.Secure.ANDROID_ID);
        progressDialog = new ProgressDialog(REGActivity.this);

        intComponent();


        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getTextFromField();
                Log.e("Value", name + " " + email + " " + phone);
                if (name.equals("") && email.equals("") && phone.equals("")) {
                    Toast.makeText(REGActivity.this, "Please Fill", Toast.LENGTH_SHORT).show();
                } else {
                    if (!isValidEmailId(email)) {
                        Toast.makeText(REGActivity.this, "Email is InValid", Toast.LENGTH_SHORT).show();

                    } else if (!isValidPhone(phone)) {
                        Toast.makeText(REGActivity.this, "Phone is InValid", Toast.LENGTH_SHORT).show();
                    } else {
                        //Do something....
                        StringRequest stringRequest = new StringRequest(Request.Method.POST, jsonURL, new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                Log.e("RE", response.toString());

                                progressDialog.dismiss();
                                Toast.makeText(REGActivity.this, response, Toast.LENGTH_SHORT);
                                if (response.equals("Registration Successfully")) {
                                    startActivity(new Intent(REGActivity.this, MainActivity.class));
                                }
                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Toast.makeText(REGActivity.this, "Something went wrong", Toast.LENGTH_LONG).show();
                                progressDialog.dismiss();
                                error.printStackTrace();
                            }
                        }) {
                            @Override
                            protected Map<String, String> getParams() throws AuthFailureError {
                                Map<String, String> parameters = new HashMap<>();
                                parameters.put("androidID", androidID);
                                parameters.put("name", name);
                                parameters.put("email", email);
                                parameters.put("phone", phone);
                                parameters.put("phonePin", NOT_REGISTERED);
                                return parameters;
                            }
                        };

                        MySingleton.getInstant(REGActivity.this).addToRequestqueue(stringRequest);
                    }
                }

            }
        });
    }

    private void getTextFromField() {
        name = etxName.getText().toString().trim();
        email = etxEmail.getText().toString().trim();
        phone = etxPhone.getText().toString().trim();
    }

    private void intComponent() {
        btnSubmit = findViewById(R.id.btn_submit);
        etxName = findViewById(R.id.etx_name);
        etxEmail = findViewById(R.id.etx_email);
        etxPhone = findViewById(R.id.etx_phone);
    }

    public static boolean isEmailValid(String email) {
        boolean isValid = false;

        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        CharSequence inputStr = email;

        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(inputStr);
        if (matcher.matches()) {
            isValid = true;
        }
        return isValid;
    }

    private boolean isValidEmailId(String email) {

        return Pattern.compile("^(([\\w-]+\\.)+[\\w-]+|([a-zA-Z]{1}|[\\w-]{2,}))@"
                + "((([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\."
                + "([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])){1}|"
                + "([a-zA-Z]+[\\w-]+\\.)+[a-zA-Z]{2,4})$").matcher(email).matches();
    }

    public static boolean isValidPhone(String phone) {
        String expression = "^([0-9\\+]|\\(\\d{1,3}\\))[0-9\\-\\. ]{3,15}$";
        CharSequence inputString = phone;
        Pattern pattern = Pattern.compile(expression);
        Matcher matcher = pattern.matcher(inputString);
        if (matcher.matches()) {
            return true;
        } else {
            return false;
        }
    }
}
