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
import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.util.Log;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import java.util.List;

class AnswerRecognition {
    private static final String TAG = "PASITHEA: Answer";
    private static String Yes = "oui";
    private static String No = "non";

    private int REQUEST_SPEECH_RECOGNIZER;
    private onAnswerListener Answerlistener;
    private Intent AnswerIntent = new Intent(Environment.getGlobalContext(), SttEngine.class);
    private LocalBroadcastManager mLocalBroadcastManager = LocalBroadcastManager.getInstance(Environment.getGlobalContext());
    private BroadcastReceiver AnswerBroadcastReceiver = null;

    public AnswerRecognition(int SRID) {
        this.REQUEST_SPEECH_RECOGNIZER = SRID;
    }

    public void setAnswerListener(onAnswerListener listener) {
        this.Answerlistener = listener;
    }

    public void setKeywords(String yes, String no){
        Yes = yes;
        No = no;
    }

    public static String[] getKeywords(){
        String[] returnStringList = new String[2];
        returnStringList[0] = Yes;
        returnStringList[1] = No;
        return returnStringList;
    }

    public void startAnswerRecognition() {
        if(Environment.getAsrSupport() == 1){
            Speaker mSpeaker = new Speaker();
            mSpeaker.sayMessage(Environment.getGlobalContext().getResources().getString(R.string.stt_not_supported));
            return;
        }
        IntentFilter mQuestion = new IntentFilter(SttEngine.RESULT_DETECTION);
        AnswerBroadcastReceiver = setBroadcastReceiver();
        mLocalBroadcastManager.registerReceiver(AnswerBroadcastReceiver, mQuestion);
        Environment.getGlobalApplication().startService(AnswerIntent);
    }

    private BroadcastReceiver setBroadcastReceiver() {
        BroadcastReceiver mAnswerBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if(SttEngine.RESULT_DETECTION.equals(intent.getAction())){
                    int resultstatus = intent.getIntExtra(SttEngine.EXTRA_RESULT, 0);
                    if (resultstatus == Activity.RESULT_OK) {
                        List<String> results = intent.getStringArrayListExtra(SttEngine.EXTRA_VOICE_RESULTS);
                        float[] scores = intent.getFloatArrayExtra(SttEngine.EXTRA_CONFIDENCE_SCORES);
                        Log.d(TAG, "activityResult: AnswerRecognition:" + resultstatus);
                        for (int i = 0; i < results.size(); i++) {
                            Log.i(TAG, "activityResult: Detected words: "
                                    + results.get(i)
                                    + " - "
                                    + "confidence scores: " + scores[i]);
                        }
                        if (results.get(0).equals(Yes)) {
                            Answerlistener.onAnswerYes();
                        } else if (results.get(0).equals(No)) {
                            Answerlistener.onAnswerNo();
                        } else {
                            Answerlistener.onAnswerUnk();
                        }
                    } else {
                        Answerlistener.onAnswerUnk();
                    }
                }
            }
        };
        return mAnswerBroadcastReceiver;
    }


    public void stopAnswerRecognition(){
        mLocalBroadcastManager.unregisterReceiver(AnswerBroadcastReceiver);
        Environment.getGlobalApplication().stopService(AnswerIntent);
    }

    public void restartAnswerRecognition(){
        stopAnswerRecognition();
        startAnswerRecognition();
    }
}