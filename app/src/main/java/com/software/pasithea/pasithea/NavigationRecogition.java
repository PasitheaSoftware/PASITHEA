/* Copyright (C) 2019 François Laforgia - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 *
 * @Author: François Laforgia (f.laforgia@logicielpasithea.fr)
 * @Date: April 10th 2019
 *
 */
package com.software.pasithea.pasithea;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

class NavigationRecogition {
    private static final String TAG = "PASITHEA: Navigation";
    private int REQUEST_SPEECH_RECOGNIZER;

    private ArrayList<String> filtervalue = new ArrayList<String>();
    private HashMap<String, String> filterhashmap = new HashMap<String, String>();

    private onNavigateListener NavigationListener = null;
    private Intent NavigatonIntent = new Intent(Environment.getGlobalContext(), SttEngine.class);
    BroadcastReceiver NavigateBroadcastReceiver = null;
    LocalBroadcastManager mLocalBroadcastManager = LocalBroadcastManager.getInstance(Environment.getGlobalContext());


    public NavigationRecogition(int SRID){
        this.REQUEST_SPEECH_RECOGNIZER = SRID;
    }

    public void setNavigationListener(onNavigateListener listener){
        this.NavigationListener = listener;
    }

    public void setKeywords(HashMap<String, String> keywords){
        filterhashmap = keywords;
        Iterator kwIterator = keywords.keySet().iterator();
        while(kwIterator.hasNext()){
            String key = (String)kwIterator.next();
            String value = keywords.get(key);
            if(!value.equals("")){
                filtervalue.add(value);
            }
        }
    }

    public void startNavigationRecognition(){
        if(Environment.getAsrSupport() == 1){
            Speaker mSpeaker = new Speaker();
            mSpeaker.sayMessage("La reconnaissance vocale n'est pas supportée.");
            return;
        }
        IntentFilter mNavigation = new IntentFilter(SttEngine.RESULT_DETECTION);
        NavigateBroadcastReceiver = setBroadcastReceiver();
        mLocalBroadcastManager.registerReceiver(NavigateBroadcastReceiver, mNavigation);
        Environment.getGlobalApplication().startService(NavigatonIntent);
    }

    private static ArrayList<String> ApplyNavFilter(String[] list, ArrayList<String> request) {
        ArrayList<String> topiclist = new ArrayList<String>();
        for (String topic : request) {
            if (Arrays.asList(list).contains(topic)) {
                topiclist.add(topic);
            }
        }
        if (topiclist.size() == 0) {
            topiclist.add("EMPTY");
        }
        return topiclist;
    }

    private BroadcastReceiver setBroadcastReceiver() {
        BroadcastReceiver mNavigationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (SttEngine.RESULT_DETECTION.equals(intent.getAction())) {
                    List<String> results = intent.getStringArrayListExtra(SttEngine.EXTRA_VOICE_RESULTS);
                    int resultstatus = intent.getIntExtra(SttEngine.EXTRA_RESULT, 0);
                    float[] scores = intent.getFloatArrayExtra(SttEngine.EXTRA_CONFIDENCE_SCORES);
                    if (resultstatus == Activity.RESULT_OK) {
                        for (int i = 0; i < results.size(); i++) {
                            Log.d(TAG, "activityResult: Detected words: "
                                    + results.get(i)
                                    + " - "
                                    + "confidence scores: "
                                    + scores[i]);
                        }
                        ArrayList<String> filteredresults = new ArrayList<String>();
                        filteredresults = ApplyNavFilter(results.get(0).split(" "), filtervalue);
                        if (filteredresults.get(0).equals(filterhashmap.get("NEXT"))) {
                            stopNavigationRecognition();
                            NavigationListener.onNavNext();
                            return;
                        } else if (filteredresults.get(0).equals(filterhashmap.get("NEXT_PART"))){
                            stopNavigationRecognition();
                            NavigationListener.onNavNextPart();
                            return;
                        } else if (filteredresults.get(0).equals(filterhashmap.get("PREVIOUS"))) {
                            stopNavigationRecognition();
                            NavigationListener.onNavPrevious();
                            return;
                        } else if (filteredresults.get(0).equals(filterhashmap.get("PREVIOUS_PART"))){
                            stopNavigationRecognition();
                            NavigationListener.onNavPreviousPart();
                            return;
                        } else if (filteredresults.get(0).equals(filterhashmap.get("QUIT"))) {
                            stopNavigationRecognition();
                            NavigationListener.onNavQuit();
                            return;
                        } else if (filteredresults.get(0).equals(filterhashmap.get("RESUME"))) {
                            stopNavigationRecognition();
                            NavigationListener.onNavResume();
                            return;
                        } else if (filteredresults.get(0).equals(filterhashmap.get("STOP"))) {
                            stopNavigationRecognition();
                            NavigationListener.onNavStop();
                            return;
                        } else {
                            NavigationListener.onNavUnk();
                            return;
                        }
                    } else {
                        NavigationListener.onNavUnk();
                    }
                }
            }
        };
        return mNavigationBroadcastReceiver;
    }

    public void stopNavigationRecognition(){
        mLocalBroadcastManager.unregisterReceiver(NavigateBroadcastReceiver);
        Environment.getGlobalApplication().stopService(NavigatonIntent);
    }

    public void restartNavigationRecognition(){
        stopNavigationRecognition();
        startNavigationRecognition();
    }
}