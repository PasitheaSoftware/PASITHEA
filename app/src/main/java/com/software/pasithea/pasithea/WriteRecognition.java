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

import java.util.List;

class WriteRecognition {
    private static final String TAG = "WriteRecognition";

    private int REQUEST_SPEECH_RECOGNIZER;

    private onWriteListener WriteListener = null;
    private static Activity WriteActivity = null;
    private static Intent WriteStt = null;
    private BroadcastReceiver WriteBroadcastReceiver = null;
    private LocalBroadcastManager mLocalBroadcastManager = LocalBroadcastManager.getInstance(Environment.getGlobalContext());


    public WriteRecognition(int SRID) {
        this.REQUEST_SPEECH_RECOGNIZER = SRID;
    }

    public static Activity getWriteActivity() {
        return WriteActivity;
    }

    public static void setWriteActivity(Activity writeActivity) {
        WriteActivity = writeActivity;
    }

    public static Intent getWriteStt() {
        return WriteStt;
    }

    public static void setWriteStt(Intent writeStt) {
        WriteStt = writeStt;
    }

    public void setwriteListener(onWriteListener listener) {
        this.WriteListener = listener;
    }

    public void startWriteRecognition(Activity activity) {
        if(Environment.getAsrSupport() == 1){
            Speaker mSpeaker = new Speaker();
            mSpeaker.sayMessage(Environment.getGlobalContext().getResources().getString(R.string.stt_not_supported));
            return;
        }
        setWriteActivity(activity);
        Log.d(TAG, "startWriteRecognition: I'm in");
        Intent mWStt = new Intent(Environment.getGlobalContext(), SttEngineAsService.class);
        setWriteStt(mWStt);
        IntentFilter WriteIntent = new IntentFilter(SttEngineAsService.RESULT_DETECTION);
        WriteBroadcastReceiver = setBroadcastReceiver();
        mLocalBroadcastManager.registerReceiver(WriteBroadcastReceiver, WriteIntent);
        activity.startService(getWriteStt());
    }

    private BroadcastReceiver setBroadcastReceiver() {
        BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (SttEngineAsService.RESULT_DETECTION.equals(intent.getAction())) {
                    List<String> results = intent.getStringArrayListExtra(SttEngineAsService.EXTRA_VOICE_RESULTS);
                    int resultstatus = intent.getIntExtra(SttEngineAsService.EXTRA_RESULT, 0);
                    float[] scores = intent.getFloatArrayExtra(SttEngineAsService.EXTRA_CONFIDENCE_SCORES);
                    if (resultstatus == Activity.RESULT_OK) {
                        WriteListener.textDetected(results.get(0));
                    } else {
                        Speaker ErrSpeaker = new Speaker();
                        ErrSpeaker.sayMessage("Je n'ai pas compris ce que vous avez dit");
                        restartWriteRecognition();
                    }

                }
            }
        };
        return mBroadcastReceiver;
    }

    public void stopWriteRecognition(){
        mLocalBroadcastManager.unregisterReceiver(WriteBroadcastReceiver);
        getWriteActivity().stopService(getWriteStt());
    }

    public void restartWriteRecognition(){
        getWriteActivity().stopService(getWriteStt());
        getWriteActivity().startService(getWriteStt());
    }
}