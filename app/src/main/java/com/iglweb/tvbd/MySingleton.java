package com.iglweb.tvbd;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

/**
 * Created by MMR on 1/4/2018.
 */

public class MySingleton {
    private static MySingleton mInstant;
    private RequestQueue requestQueue;
    private static Context mContext;

    private MySingleton(Context mContext){
        this.mContext = mContext;
        requestQueue = getRequestQueue();
    }

    public RequestQueue getRequestQueue(){
        if(requestQueue == null){
            requestQueue = Volley.newRequestQueue(mContext.getApplicationContext());
        }

        return requestQueue;
    }

    public static synchronized MySingleton getInstant( Context  context){
        if (mInstant == null){
            mInstant = new MySingleton(context);
        }
        return mInstant;
    }
    public<T> void addToRequestqueue(Request<T> request){
        requestQueue.add(request);
    }
}