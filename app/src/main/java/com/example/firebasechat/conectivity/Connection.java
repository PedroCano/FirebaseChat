package com.example.firebasechat.conectivity;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import java.io.IOException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class Connection {

    private final static String TAG = "ConnectionFirebase";
    private Context context;
    private ResponseConectivityListener listener;

    public Connection(Context context, ResponseConectivityListener responseConectivityListener) {
        this.context = context;
        this.listener = responseConectivityListener;
    }

    public boolean isActiveConnection(){
        return this.isMobileData() || this.isWifi();
    }

    public boolean isWifi() {
        boolean response = false;
        ConnectivityManager gesCon = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (gesCon != null){
            NetworkInfo redwifi = gesCon.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            if(redwifi != null){
                //response = redwifi.isAvailable();
                response = redwifi.getState() == NetworkInfo.State.CONNECTED;
            }
        }
        return response;
    }

    public boolean isMobileData() {
        boolean response = false;
        ConnectivityManager gesCon = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (gesCon != null){
            NetworkInfo mobileData = gesCon.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
            if(mobileData != null){
                if(mobileData.isAvailable()){
                    response = mobileData.getState() == NetworkInfo.State.CONNECTED;
                }
            }
        }
        return response;
    }

    public void checkConnected(){
        checkConnected(null);
    }

    public void checkConnected(final String url){
        Thread thread = new Thread(){
            @Override
            public void run() {
                String newUrl = url;
                if(url == null || url.isEmpty()){
                    newUrl = "https://www.google.es";
                }

                final int TIEMPO_CONEXION = 2000;
                boolean conectado = false;

                try {
                    HttpsURLConnection conexionHttps = (HttpsURLConnection) (new URL(newUrl).openConnection());
                    conexionHttps.setRequestProperty("User-Agent", "ConnectionTest");
                    conexionHttps.setRequestProperty("Connection", "close");
                    conexionHttps.setConnectTimeout(TIEMPO_CONEXION);
                    conexionHttps.setReadTimeout(TIEMPO_CONEXION);
                    conexionHttps.connect();
                    conectado = (conexionHttps.getResponseCode() == 200);
                } catch (IOException e) {
                    Log.v(TAG, e.getMessage());
                }
                listener.onResponse(conectado);
            }
        };
        thread.start();
    }
}
