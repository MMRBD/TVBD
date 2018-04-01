package com.iglweb.tvbd;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class CHActivity extends AppCompatActivity {

    GridView gridView;

    private ImageAdapter mAdapter;


    private static String URL_CHANNEL_TYPE = "URL";
    private static String URL_GET_CHANNEL_URL = "URL";

    String[] chURL;
    String[] chLogoURL;
    String[] chType;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_ch);
        gridView = findViewById(R.id.gridView_ch);
        getChannelURL();

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                startIntent(chURL[position]);
            }
        });
        gridView.setFocusable(true);

//        gridView.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                Animation a = AnimationUtils.loadAnimation(CHActivity.this, R.anim.zoom);
//                if (view != null){
//                    view.startAnimation(a);
//                }
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> parent) {
//            }
//        });

    }


    private void getTVType() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_CHANNEL_TYPE, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject mainObject = new JSONObject(response);
                    JSONArray mainArray = mainObject.getJSONArray("chTypes");
                    chType = new String[mainArray.length()];
                    for (int i = 0; i < mainArray.length(); i++) {
                        JSONObject childOBJ = mainArray.getJSONObject(i);
                        chType[i] = childOBJ.getString("ch_type");
                    }

                } catch (JSONException e) {
                    Toast.makeText(CHActivity.this, "SOMETHING WRONG IN YOUR INTERNET CONNECTION", Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(CHActivity.this, "SOMETHING WENT WRONG", Toast.LENGTH_LONG).show();
            }
        });

        MySingleton.getInstant(CHActivity.this).addToRequestqueue(stringRequest);
    }

    private void getChannelURL() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_GET_CHANNEL_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject mainObject = new JSONObject(response);
                    JSONArray mainArray = mainObject.getJSONArray("tvInfo");
                    chURL = new String[mainArray.length()];
                    chLogoURL = new String[mainArray.length()];
                    for (int i = 0; i < mainArray.length(); i++) {
                        JSONObject childOBJ = mainArray.getJSONObject(i);
                        chURL[i] = childOBJ.getString("ch_url");
                        chLogoURL[i] = childOBJ.getString("ch_logo");
                    }
                    mAdapter = new ImageAdapter(CHActivity.this);
                    gridView.setAdapter(mAdapter);

                } catch (JSONException e) {
                    Toast.makeText(CHActivity.this, "SOMETHING WRONG IN YOUR INTERNET CONNECTION", Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(CHActivity.this, "SOMETHING WENT WRONG", Toast.LENGTH_LONG).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> parameters = new HashMap<>();
                parameters.put("ch_type", String.valueOf(chType));
                return parameters;
            }
        };

        MySingleton.getInstant(CHActivity.this).addToRequestqueue(stringRequest);
    }

    private class ImageAdapter extends BaseAdapter {


        private Context context;
        private LayoutInflater layoutInflator;

        public ImageAdapter(Context c) {
            context = c;
            layoutInflator = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        }


        @Override
        public int getCount() {
            return chLogoURL.length;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ImageView image;

            if (convertView == null) {
                convertView = layoutInflator.inflate(R.layout.item_img_grid, parent, false);
            }
            image = convertView.findViewById(R.id.img_grid);
            setImagesUsingPicasso(image, chLogoURL[position]);

            return convertView;
        }

    }

    private void setImagesUsingPicasso(ImageView networkImage, String url) {
        Picasso.with(CHActivity.this).load(url).placeholder(CHActivity.this.getResources().getDrawable(R.drawable.empty_photo)).error(CHActivity.this.getResources().getDrawable(R.drawable.empty_photo)).into(networkImage);

//        Picasso.with(ChanelActivity.this).load(url).fit().into(networkImage);
    }

    private void startIntent(String url) {
        try {
//            Intent intent = new Intent(Intent.ACTION_VIEW);
//            intent.setDataAndType(Uri.parse(url), "video/*");
//            startActivity(Intent.createChooser(intent, "Choose Video Player"));
//            startActivity(intent);


            Intent intent = new Intent(Intent.ACTION_VIEW);
            Uri videoUri = Uri.parse(url);
            intent.setDataAndType(videoUri, "application/x-mpegURL");
//            intent.setPackage("org.videolan.vlc");
            intent.setComponent(new ComponentName("org.videolan.vlc", "org.videolan.vlc.gui.video.VideoPlayerActivity"));
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        } catch (Exception e) {

            showIptvCoreNotFoundDialog();

        }
    }

    public void showIptvCoreNotFoundDialog() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.dialog_core_not_installed_title);
        builder.setMessage(R.string.dialog_core_not_installed_message);
        builder.setPositiveButton(R.string.dialog_button_install,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogInterface, int id) {
                        try {
                            // try to open Google Play app first
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + _IPTV_CORE_PACKAGE_NAME)));
                        } catch (ActivityNotFoundException e) {
                            // if Google Play is not found for some reason, let's open browser
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=" + _IPTV_CORE_PACKAGE_NAME)));
                        }
                    }
                });
        builder.setNegativeButton(R.string.dialog_button_cancel,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogInterface, int id) {
                        // if cancelled - just close the app
                        //finish();
                    }
                });
        builder.setCancelable(true);
        builder.create().show();
    }

    private static final String _IPTV_CORE_PACKAGE_NAME = "org.videolan.vlc";
    private static final String _IPTV_CORE_CLASS_NAME = _IPTV_CORE_PACKAGE_NAME + ".ChanelActivity";


}
