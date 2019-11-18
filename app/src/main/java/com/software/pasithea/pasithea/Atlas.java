package com.software.pasithea.pasithea;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkRequest;
import android.speech.tts.TextToSpeech;

import androidx.annotation.NonNull;

import static android.content.Context.CONNECTIVITY_SERVICE;

class Atlas {

    private static WifiChangeListener mWifiChangeListener = null;

    public Atlas(){}

    protected static void setWifiChangeListener(WifiChangeListener wifiChangeListener) {
        mWifiChangeListener = wifiChangeListener;
    }

    protected void StartWifiListener(){
        Context mContext = Environment.getGlobalContext();
        NetworkRequest mNetworkRequest = new NetworkRequest.Builder().addTransportType(NetworkCapabilities.TRANSPORT_WIFI).build();
        ConnectivityManager.NetworkCallback mNetCallback = new ConnectivityManager.NetworkCallback() {

            @Override
            public void onAvailable(@NonNull Network network) {
                mWifiChangeListener.onWifiAvailable();
            }

            @Override
            public void onLost(@NonNull Network network) {
                mWifiChangeListener.onWifiLost();
            }

            @Override
            public void onUnavailable() {
                mWifiChangeListener.onWifiUnavailable();
            }
        };

        ConnectivityManager mConnectivityManager = (ConnectivityManager) mContext.getSystemService(CONNECTIVITY_SERVICE);
        mConnectivityManager.registerNetworkCallback(mNetworkRequest, mNetCallback);
    }
}
