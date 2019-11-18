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

    private onNavigateListener thislistener = null;
    private static Activity mActivity = null;
    private static Intent mStt = null;

    BroadcastReceiver NavigateBroadcastReceiver = null;
    LocalBroadcastManager mLocalBroadcastManager = LocalBroadcastManager.getInstance(Environment.getGlobalContext());

    public NavigationRecogition(int SRID){
        this.REQUEST_SPEECH_RECOGNIZER = SRID;
    }

    public void setNavigationListener(onNavigateListener listener){
        this.thislistener = listener;
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

    private static Activity getmActivity() {
        return mActivity;
    }

    private static void setmActivity(Activity mActivity) {
        NavigationRecogition.mActivity = mActivity;
    }

    private static Intent getmStt() {
        return mStt;
    }

    private static void setmStt(Intent mStt) {
        NavigationRecogition.mStt = mStt;
    }

    public void startNavigationRecognition(Activity activity){
        if(Environment.getAsrSupport() == 1){
            Speaker mSpeaker = new Speaker();
            mSpeaker.sayMessage("La reconnaissance vocale n'est pas supportée.");
            return;
        }
        setmActivity(activity);
        Intent mStt = new Intent(Environment.getGlobalContext(), SttEngineAsService.class);
        setmStt(mStt);
        IntentFilter mNavigation = new IntentFilter(SttEngineAsService.RESULT_DETECTION);
        NavigateBroadcastReceiver = setBroadcastReceiver();
        mLocalBroadcastManager.registerReceiver(NavigateBroadcastReceiver, mNavigation);
        activity.startService(mStt);
    }

    private BroadcastReceiver setBroadcastReceiver() {
        BroadcastReceiver mNavigationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (SttEngineAsService.RESULT_DETECTION.equals(intent.getAction())) {
                    List<String> results = intent.getStringArrayListExtra(SttEngineAsService.EXTRA_VOICE_RESULTS);
                    int resultstatus = intent.getIntExtra(SttEngineAsService.EXTRA_RESULT, 0);
                    float[] scores = intent.getFloatArrayExtra(SttEngineAsService.EXTRA_CONFIDENCE_SCORES);
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
                            thislistener.onNavNext();
                            return;
                        } else if (filteredresults.get(0).equals(filterhashmap.get("NEXT_PART"))){
                            stopNavigationRecognition();
                            thislistener.onNavNextPart();
                            return;
                        } else if (filteredresults.get(0).equals(filterhashmap.get("PREVIOUS"))) {
                            stopNavigationRecognition();
                            thislistener.onNavPrevious();
                            return;
                        } else if (filteredresults.get(0).equals(filterhashmap.get("PREVIOUS_PART"))){
                            stopNavigationRecognition();
                            thislistener.onNavPreviousPart();
                            return;
                        } else if (filteredresults.get(0).equals(filterhashmap.get("QUIT"))) {
                            stopNavigationRecognition();
                            thislistener.onNavQuit();
                            return;
                        } else if (filteredresults.get(0).equals(filterhashmap.get("RESUME"))) {
                            stopNavigationRecognition();
                            thislistener.onNavResume();
                            return;
                        } else if (filteredresults.get(0).equals(filterhashmap.get("STOP"))) {
                            stopNavigationRecognition();
                            thislistener.onNavStop();
                            return;
                        } else {
                            thislistener.onNavUnk();
                            return;
                        }
                    } else {
                        thislistener.onNavUnk();
                    }
                }
            }
        };
        return mNavigationBroadcastReceiver;
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

    public void stopNavigationRecognition(){
        mLocalBroadcastManager.unregisterReceiver(NavigateBroadcastReceiver);
        //getmActivity().stopService(getmStt());
    }

    public void restartNavigationRecognition(){
        getmActivity().stopService(getmStt());
        getmActivity().startService(getmStt());
    }
}