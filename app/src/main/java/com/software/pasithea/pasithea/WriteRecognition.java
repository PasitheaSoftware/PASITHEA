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

import androidx.lifecycle.Observer;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import java.util.List;

class WriteRecognition {
    private static final String TAG = "WriteRecognition";

    private int REQUEST_SPEECH_RECOGNIZER;

    private onWriteListener WriteListener = null;
    private Intent WriteIntent = new Intent(Environment.getGlobalContext(), SttEngine.class);
    private BroadcastReceiver WriteBroadcastReceiver = null;
    private LocalBroadcastManager mLocalBroadcastManager = LocalBroadcastManager.getInstance(Environment.getGlobalContext());

    public WriteRecognition(int SRID) {
        this.REQUEST_SPEECH_RECOGNIZER = SRID;
    }

    public void setwriteListener(onWriteListener listener) {
        this.WriteListener = listener;
    }

    public void startWriteRecognition() {
        if(Environment.getAsrSupport() == 1){
            Speaker mSpeaker = new Speaker();
            mSpeaker.sayMessage(Environment.getGlobalContext().getResources().getString(R.string.stt_not_supported));
            return;
        }
        IntentFilter mWrite = new IntentFilter(SttEngine.RESULT_DETECTION);
        WriteBroadcastReceiver = setBroadcastReceiver();
        mLocalBroadcastManager.registerReceiver(WriteBroadcastReceiver, mWrite);
        Environment.getGlobalApplication().startService(WriteIntent);
    }

    private BroadcastReceiver setBroadcastReceiver() {
        BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (SttEngine.RESULT_DETECTION.equals(intent.getAction())) {
                    List<String> results = intent.getStringArrayListExtra(SttEngine.EXTRA_VOICE_RESULTS);
                    int resultstatus = intent.getIntExtra(SttEngine.EXTRA_RESULT, 0);
                    float[] scores = intent.getFloatArrayExtra(SttEngine.EXTRA_CONFIDENCE_SCORES);
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
        Environment.getGlobalApplication().stopService(WriteIntent);
    }

    public void restartWriteRecognition(){
        stopWriteRecognition();
        startWriteRecognition();
    }
}