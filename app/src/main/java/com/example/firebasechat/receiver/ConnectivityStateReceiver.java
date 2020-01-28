package com.example.firebasechat.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.example.firebasechat.conectivity.Connection;
import com.example.firebasechat.conectivity.ResponseConectivityListener;
import com.google.firebase.database.FirebaseDatabase;

public class ConnectivityStateReceiver extends BroadcastReceiver {

    private final static String URL = "https://izvadd2020firebaserealtimedata.firebaseio.com";
    private ResponseConectivityListener listener = new ResponseConectivityListener() {
        @Override
        public void onResponse(boolean booleanResult) {
            if(booleanResult){
                FirebaseDatabase database = FirebaseDatabase.getInstance();
            }
            Log.v("---1---","conectivity");
        }
    };

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.v("-------", "on receive");
        Connection connection = new Connection(context, listener);
        if(connection.isActiveConnection()){
            connection.checkConnected(URL);
        }
    }
}
