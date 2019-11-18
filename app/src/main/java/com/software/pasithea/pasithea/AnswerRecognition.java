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
import android.os.Handler;
import android.util.Log;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import java.util.List;

class AnswerRecognition {
    private static final String TAG = "PASITHEA: Answer";
    private static String Yes = "oui";
    private static String No = "non";
    private static Activity mActivity = null;
    private static Intent mStt = null;
    private static Handler mHandler = null;

    private LocalBroadcastManager mLocalBroadcastManager = LocalBroadcastManager.getInstance(Environment.getGlobalContext());
    private BroadcastReceiver AnswerBroadcastReceiver = null;

    private int REQUEST_SPEECH_RECOGNIZER;
    private onAnswerListener thislistener;

    public AnswerRecognition(int SRID) {
        this.REQUEST_SPEECH_RECOGNIZER = SRID;
    }

    public void setAnswerListener(onAnswerListener listener) {
        this.thislistener = listener;
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

    private static Intent getmStt() {
        return mStt;
    }

    private static void setmStt(Intent mStt) {
        AnswerRecognition.mStt = mStt;
    }

    private static Activity getmActivity() {
        return mActivity;
    }

    private static void setmActivity(Activity mActivity) {
        AnswerRecognition.mActivity = mActivity;
    }

    public void startAnswerRecognition(Activity activity) {
        if(Environment.getAsrSupport() == 1){
            Speaker mSpeaker = new Speaker();
            mSpeaker.sayMessage(Environment.getGlobalContext().getResources().getString(R.string.stt_not_supported));
            return;
        }
        setmActivity(activity);
        mStt = new Intent(Environment.getGlobalContext(), SttEngineAsService.class);
        setmStt(mStt);
        IntentFilter mQuestion = new IntentFilter(SttEngineAsService.RESULT_DETECTION);
        AnswerBroadcastReceiver = setBroadcastReceiver();
        mLocalBroadcastManager.registerReceiver(AnswerBroadcastReceiver, mQuestion);
        getmActivity().startService(getmStt());
    }

    private BroadcastReceiver setBroadcastReceiver() {
        BroadcastReceiver mAnswerBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if(SttEngineAsService.RESULT_DETECTION.equals(intent.getAction())){
                    int resultstatus = intent.getIntExtra(SttEngineAsService.EXTRA_RESULT, 0);
                    if (resultstatus == Activity.RESULT_OK) {
                        List<String> results = intent.getStringArrayListExtra(SttEngineAsService.EXTRA_VOICE_RESULTS);
                        float[] scores = intent.getFloatArrayExtra(SttEngineAsService.EXTRA_CONFIDENCE_SCORES);
                        Log.d(TAG, "activityResult: AnswerRecognition:" + resultstatus);
                        for (int i = 0; i < results.size(); i++) {
                            Log.i(TAG, "activityResult: Detected words: "
                                    + results.get(i)
                                    + " - "
                                    + "confidence scores: " + scores[i]);
                        }
                        if (results.get(0).equals(Yes)) {
                            thislistener.onAnswerYes();
                        } else if (results.get(0).equals(No)) {
                            thislistener.onAnswerNo();
                        } else {
                            thislistener.onAnswerUnk();
                        }
                    } else {
                        thislistener.onAnswerUnk();
                    }
                }
            }
        };
        return mAnswerBroadcastReceiver;
    }

    public void stopAnswerRecognition(){
        mLocalBroadcastManager.unregisterReceiver(AnswerBroadcastReceiver);
        getmActivity().stopService(getmStt());
    }

    public void restartAnswerRecognition(){
        getmActivity().stopService(getmStt());
        getmActivity().startService(getmStt());
    }
}