package com.startup.driveschoolclient.util;

import android.content.Context;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

/**
 * Created by chamath on 2/26/18.
 */

public class ServerConnection {

    private static RequestQueue queue;

    public static void init(Context context){
        queue = Volley.newRequestQueue(context);
    }

    public static boolean sendMessage(StringRequest stringRequest){
        if(queue==null)
            return false;
        queue.add(stringRequest);
        return true;
    }


}
