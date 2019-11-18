/* Copyright (C) 2019 François Laforgia - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 *
 * @Author: François Laforgia (f.laforgia@logicielpasithea.fr)
 * @Date: April 10th 2019
 *
 */
package com.software.pasithea.pasithea;

import android.Manifest;
import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.pm.PackageManager;
import android.media.AudioAttributes;
import android.media.AudioDeviceInfo;
import android.media.AudioFocusRequest;
import android.media.AudioManager;
import android.media.AudioManager.OnAudioFocusChangeListener;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;

import static java.lang.Math.round;

class Environment extends AppCompatActivity {
    private static final String TAG = "PASITHEA: Environment";
    private static final int MIN_BUILD = Build.VERSION_CODES.M;
    public static final int AUDIOFOCUS_MIN_BUILD = Build.VERSION_CODES.O;
    public static final String WIFI_KEY = "WIFI";
    public static final String MOBILE_KEY = "MOBILE";

    private static int AsrSupport = 0;
    private static int RestartAll = 0;
    private static int currentAudioVolume;
    private static int maxAudioVolume;
    private static int[] audioDevicesList = {3,4,7,8,22};
    private static boolean runAsService = false;
    private static AudioFocusRequest mFocusRequest = null;
    private static AudioManager mAudioManager;
    private static Locale oldLocale = null;
    private static Activity globalActivty = null;
    private static Context globalContext = null;
    private static Locale globalLocale = null;
    private static Application globalApplication = null;

    private onPermissionResultListener PermissionListener = null;
    private static onPermissionRequestEndListener RequestEndListener = null;

    public Environment() { }

    /**
     * Set the permission listener to handle the permission result actions
     * @param listener
     */

    protected void setListener(onPermissionResultListener listener) {
        PermissionListener = listener;
    }

    public static void setRequestEndListener(onPermissionRequestEndListener listener) {
        Log.d(TAG, "setRequestEndListener: entering the function");
        RequestEndListener = listener;
    }

    /*
        Audio focus management.
        This group of methods will request the audio focus for this app. The audiofocus is hardcoded to
        AUDIOFOCUS_GAIN_TRANSIENT, this means that the app can duck when another audio notification is
        coming.
        The methods are:
        setaudiomanager(activity): Set the audioManager for the specified activity.
        checkAudioFocus(): Ask for the audio focus.
         */
    public static void setAudioManager(Activity activity) {
        Environment.mAudioManager = (AudioManager) activity.getSystemService(Context.AUDIO_SERVICE);
        Environment.mAudioManager.setParameters("noise_suppression=on");
    }

    public static void requestAudioFocus(){
        mFocusRequest = createFocusRequest();
        requestFocus(mAudioManager);
    }

    private static OnAudioFocusChangeListener getAudioFocusListener(){
        OnAudioFocusChangeListener mAudioFocusChangeListener = null;
        mAudioFocusChangeListener = new OnAudioFocusChangeListener() {
            @Override
            public void onAudioFocusChange(int focusChange) {
                if (focusChange == AudioManager.AUDIOFOCUS_GAIN_TRANSIENT) {
                }
            }
        };
        return mAudioFocusChangeListener;
    }

    private static void requestFocus(AudioManager manager){
        if(manager.isMusicActive()){
            Log.i(TAG, "RequestAudioFocus: AudioManager is playing music");
        } else {
            Log.i(TAG, "RequestAudioFocus: AudioManager is not playing music");
        }

        int res = 0;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            res = manager.requestAudioFocus(getFocusRequest());
        } else {
            res = manager.requestAudioFocus(getAudioFocusListener(),
                    AudioManager.STREAM_ACCESSIBILITY,
                    AudioManager.AUDIOFOCUS_GAIN_TRANSIENT);
        }
        if(res == AudioManager.AUDIOFOCUS_REQUEST_GRANTED){
            Log.i(TAG, "onCreate: Focus granted");
        }
    }

    public static AudioFocusRequest getFocusRequest() {
        return mFocusRequest;
    }

    private static AudioFocusRequest createFocusRequest() {
        AudioAttributes playbackAttributes = new AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_MEDIA)
                .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                .build();
        if (Build.VERSION.SDK_INT >= AUDIOFOCUS_MIN_BUILD) {
            mFocusRequest = new AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN_TRANSIENT)
                    .setAudioAttributes(playbackAttributes)
                    .setAcceptsDelayedFocusGain(true)
                    .setOnAudioFocusChangeListener(getAudioFocusListener())
                    .build();
        }
        return mFocusRequest;
    }

    protected static void abandonFocus(){
        if(Build.VERSION.SDK_INT >= AUDIOFOCUS_MIN_BUILD){
            try {
                mAudioManager.abandonAudioFocusRequest(getFocusRequest());
            } catch (IllegalArgumentException e){
                e.printStackTrace();
                Log.e(TAG, "abandonFocus: ", e);
            }
        }
    }

    /*
    Permissions management
     */

    protected void requestAudioPermissions() {
        Log.i(TAG, "checkRecordAudioPermissions: start");
        int recordCheck = ContextCompat.checkSelfPermission(getGlobalContext(), Manifest.permission.RECORD_AUDIO);
        Log.d(TAG, "requestAudioPermissions: "+ recordCheck);
        if (recordCheck != PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "requestAudioPermissions: "+ getAsrSupport());
            setAsrSupport(1);
        } else {
            setAsrSupport(0);
            requestStoragePermissions();
        }
    }

    protected void requestStoragePermissions() {
        Log.i(TAG, "checkStoragePermissions: start");
        int recordCheck = ContextCompat.checkSelfPermission(getGlobalContext(), Manifest.permission.READ_EXTERNAL_STORAGE);
        if (recordCheck != PackageManager.PERMISSION_GRANTED) {
            setAsrSupport(1);
        } else {
            setAsrSupport(0);
        }
    }

    protected void checkAsrSupport(){
        if(getAsrSupport() == 0){
            PermissionListener.onGranted();
        } else {
            PermissionListener.onDenied();
        }
    }

    /*
    Network management
    */
    protected HashMap<String, NetworkInfo> checkNetwork() {
        Log.i(TAG, "checkNetwork: start");
        ConnectivityManager EnvConn = (ConnectivityManager) getGlobalContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        assert EnvConn != null;
        NetworkInfo EnvActiveNet = EnvConn.getActiveNetworkInfo();
        NetworkInfo EnvWIFIInfo = EnvConn.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        NetworkInfo EnvMobileInfo = EnvConn.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        HashMap<String, NetworkInfo> EnvHashResult = new HashMap<String, NetworkInfo>();
        EnvHashResult.put(WIFI_KEY, EnvWIFIInfo);
        EnvHashResult.put(MOBILE_KEY, EnvMobileInfo);
        Log.i(TAG, "checkNetwork: end");
        return EnvHashResult;
    }

    /*
    Audio management
     */

    public static int getCurrentAudioVolume() {
        return currentAudioVolume;
    }

    public static void setCurrentAudioVolume(int currentAudioVolume) {
        Environment.currentAudioVolume = currentAudioVolume;
    }

    public static int getMaxAudioVolume() {
        return maxAudioVolume;
    }

    public static void setMaxAudioVolume(int maxAudioVolume) {
        Environment.maxAudioVolume = maxAudioVolume;
    }

    private static void manageVolume(){
        mAudioManager = (AudioManager)getGlobalContext().getSystemService(Context.AUDIO_SERVICE);
        setCurrentAudioVolume(mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC));
        setMaxAudioVolume(mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC));
    }

    protected static void setInitialAudioVolume() {
        manageVolume();
        AudioDeviceInfo[] mDeviceInfo = mAudioManager.getDevices(AudioManager.GET_DEVICES_OUTPUTS);
        ArrayList<Integer> connectedDevices = new ArrayList<Integer>();
        for (int i = 0; i < mDeviceInfo.length; i++) {
            for (int sub : audioDevicesList) {
                if (mDeviceInfo[i].getType() == sub) {
                    connectedDevices.add(sub);
                }
            }
        }

        if (connectedDevices.size() > 0){
            int volume = (int)round(getMaxAudioVolume()/2);
            mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, volume, AudioManager.FLAG_SHOW_UI);
        } else {
            mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC), AudioManager.FLAG_SHOW_UI );
        }
    }

    protected void setAudioVolume(int volume){
        mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, volume, AudioManager.FLAG_SHOW_UI);
    }

    protected void restoreVolume(){
        mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, getCurrentAudioVolume(), AudioManager.FLAG_SHOW_UI);
    }

    protected int checkBuildVersion(){
        if(Build.VERSION.SDK_INT >= MIN_BUILD){
            return 0;
        } else { return 1;}
    }

    public static int getAsrSupport() {
        return AsrSupport;
    }

    public static void setAsrSupport(int asrSupport) {
        AsrSupport += asrSupport;
    }

    public static Activity getGlobalActivty() {
        return globalActivty;
    }

    public static Context getGlobalContext() {
        return globalContext;
    }

    public static Locale getGlobalLocale() {
        return globalLocale;
    }

    public static Application getGlobalApplication() {
        return globalApplication;
    }

    public static int getRestartAll() {
        return RestartAll;
    }

    public static void setGlobalActivty(Activity globalActivty) {
        Environment.globalActivty = globalActivty;
    }

    public static void setGlobalContext(Context globalContext) {
        Environment.globalContext = globalContext;
    }

    public static void setGlobalLocale(Locale globalLocale) {
        Environment.globalLocale = globalLocale;
    }

    public static void setGlobalApplication(Application application) {
        Environment.globalApplication = application;
    }

    public static void setRestartAll(int restartAll) {
        RestartAll = restartAll;
    }

    public static void setOldLocale(Locale oldLocale) {
        Environment.oldLocale = oldLocale;
    }

    public static Locale getOldLocale() {
        return oldLocale;
    }

    public static int getCurrentYear(){
        Calendar currentDate = Calendar.getInstance();
        return currentDate.get(Calendar.YEAR);
    }

    public static boolean getRunAsService() {
        return runAsService;
    }

    public static void setRunAsService(boolean runAsService) {
        Environment.runAsService = runAsService;
    }
}