package com.software.pasithea.pasithea;

interface WifiChangeListener {

    void onWifiAvailable();
    void onWifiLost();
    Void onWifiUnavailable();
}
