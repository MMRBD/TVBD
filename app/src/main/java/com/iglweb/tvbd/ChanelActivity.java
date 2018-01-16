package com.iglweb.tvbd;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class ChanelActivity extends AppCompatActivity {

    GridView gridViewChanel;
    String[] chanel;
    String[] chanelURL;
    String[] chanelInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_chanel);
        gridViewChanel = findViewById(R.id.gridView_chanel);
        String jURL = "http://tv-bd.com/getUrl.php";


        StringRequest stringRequest = new StringRequest(Request.Method.POST, jURL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jo = new JSONObject(response);
                    JSONArray ja = jo.getJSONArray("urls");
                    chanelURL = new String[ja.length()];
                    chanelInfo = new String[ja.length()];

                    for (int i = 0; i < ja.length(); i++) {
                        JSONObject jOB = ja.getJSONObject(i);
                        chanelURL[i] = jOB.getString("url");
                        chanelInfo[i] = jOB.getString("chanel_info");
                    }
                    viewGrid();

                } catch (JSONException e) {
                    Toast.makeText(ChanelActivity.this, "Something went wrong", Toast.LENGTH_LONG).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(ChanelActivity.this, "Something went wrong", Toast.LENGTH_LONG).show();

            }
        });

        MySingleton.getInstant(ChanelActivity.this).addToRequestqueue(stringRequest);


    }

    private void viewGrid() {
        chanel = new String[chanelURL.length];
        for (int i = 0; i < chanel.length; i++) {
            chanel[i] = (i + 1) + " CH";
        }

        gridViewChanel.setAdapter(new myAdapter());

        gridViewChanel.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                startActivity(new Intent(ChanelActivity.this, PlayerActivity.class).putExtra("position", i).putExtra("chURLS", chanelURL).putExtra("chINFO", chanelInfo));
            }
        });
    }

    private class myAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return chanel.length;
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {


            LayoutInflater inflater = getLayoutInflater();
            view = inflater.inflate(R.layout.item_grid, viewGroup, false);
            TextView tvCH = view.findViewById(R.id.tv_ch);
            tvCH.setText(chanel[i]);

            return view;
        }
    }
}
