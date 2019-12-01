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
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognitionService;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import java.util.ArrayList;

public class SttEngine extends RecognitionService {
    private static final String TAG = "PASITHEA: SpeechToText";
    public static final String EXTRA_VOICE_RESULTS = "com.software.pasithea.pasithea.EXTRA_VOICE_RESULTS";
    public static final String EXTRA_CONFIDENCE_SCORES = "com.software.pasithea.pasithea.EXTRA_CONFIDENT_SCORES";
    public static final String EXTRA_RESULT = "com.software.pasithea.pasithea.EXTRA_RESULT";
    public static final String RESULT_DETECTION = "com.software.pasithea.pasithea.RESULT_DETECTION";
    private static final int CHANNEL_ID = 1;

    private static SpeechRecognizer mSpeechRecognizer = null;
    private static NotificationHelper mNotificationHelper;

    private static LocalBroadcastManager mLocalBroadcastManager = LocalBroadcastManager.getInstance(Environment.getGlobalContext());

    public SttEngine(){ }

    @Override
    public void onCreate() {
        super.onCreate();
        if (mLocalBroadcastManager == null){
            Log.d(TAG, "onCreate: NO BROADCAST MANAGER");
        } else {
            Log.d(TAG, "onCreate: BROADCAST MANAGER EXISTS");
        }
        mSpeechRecognizer  = SpeechRecognizer.createSpeechRecognizer(Environment.getGlobalContext());
        mSpeechRecognizer  = SpeechRecognizer.createSpeechRecognizer(Environment.getGlobalContext());
        mSpeechRecognizer.setRecognitionListener(createListener());
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mSpeechRecognizer.startListening(createIntent());
        mNotificationHelper = new NotificationHelper(Environment.getGlobalContext());
        NotificationCompat.Builder SttNotificationBuilder = mNotificationHelper.getChannelNotification("SPEECH RECOGNITION", "Voice recognition in progress");
        mNotificationHelper.getManager().notify(CHANNEL_ID, SttNotificationBuilder.build());
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    protected void onStartListening(Intent intent, Callback callback) {

    }

    @Override
    protected void onCancel(Callback callback) {

    }

    @Override
    protected void onStopListening(Callback callback) {

    }

    private static Intent createIntent(){
        Intent myIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        myIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        myIntent.putExtra(RecognizerIntent.EXTRA_PREFER_OFFLINE, "true");
        myIntent.putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_COMPLETE_SILENCE_LENGTH_MILLIS, 500);
        myIntent.putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_MINIMUM_LENGTH_MILLIS, 5000);
        return myIntent;
    }

    private static RecognitionListener createListener(){
        return new RecognitionListener() {
            @Override
            public void onReadyForSpeech(Bundle bundle) {
            }

            @Override
            public void onBeginningOfSpeech() {
            }

            @Override
            public void onRmsChanged(float v) {
            }

            @Override
            public void onBufferReceived(byte[] bytes) {
            }

            @Override
            public void onEndOfSpeech() {
            }

            @Override
            public void onError(int i) {
                Intent mBroadcastIntent = new Intent(RESULT_DETECTION);
                Log.e(TAG, "onError");
                if(i == SpeechRecognizer.ERROR_NO_MATCH){
                    Log.d(TAG, "onError: No match found");
                    mBroadcastIntent.putExtra(EXTRA_RESULT, SpeechRecognizer.ERROR_NO_MATCH);
                }
                if (i == SpeechRecognizer.ERROR_SPEECH_TIMEOUT) {
                    Log.d(TAG, "onError: speech timeout");
                    mBroadcastIntent.putExtra(EXTRA_RESULT, SpeechRecognizer.ERROR_SPEECH_TIMEOUT);
                }
                if (i == SpeechRecognizer.ERROR_AUDIO){
                    Log.d(TAG, "onError: audio error");
                    mBroadcastIntent.putExtra(EXTRA_RESULT, SpeechRecognizer.ERROR_AUDIO);
                }
                if (i == SpeechRecognizer.ERROR_CLIENT){
                    Log.d(TAG, "onError: client error");
                    mBroadcastIntent.putExtra(EXTRA_RESULT, SpeechRecognizer.ERROR_CLIENT);
                }
                mNotificationHelper.getManager().cancel(CHANNEL_ID);
                mLocalBroadcastManager.sendBroadcast(mBroadcastIntent);
                mNotificationHelper.getManager().cancel(CHANNEL_ID);
            }

            @Override
            public void onResults(Bundle bundle) {
                Intent mBroadcastIntent = new Intent(RESULT_DETECTION);
                mBroadcastIntent.putStringArrayListExtra(EXTRA_VOICE_RESULTS,
                        bundle.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION));

                mBroadcastIntent.putExtra(EXTRA_CONFIDENCE_SCORES,
                        bundle.getFloatArray(SpeechRecognizer.CONFIDENCE_SCORES));

                mBroadcastIntent.putExtra(EXTRA_RESULT, Activity.RESULT_OK);
                mNotificationHelper.getManager().cancel(CHANNEL_ID);
                mLocalBroadcastManager.sendBroadcast(mBroadcastIntent);
                mNotificationHelper.getManager().cancel(CHANNEL_ID);
            }

            @Override
            public void onPartialResults(Bundle bundle) {
            }

            @Override
            public void onEvent(int i, Bundle bundle) {
            }
        };
    }

    protected static void startRecognition(){
        mSpeechRecognizer  = SpeechRecognizer.createSpeechRecognizer(Environment.getGlobalContext());
        mSpeechRecognizer  = SpeechRecognizer.createSpeechRecognizer(Environment.getGlobalContext());
        mSpeechRecognizer.setRecognitionListener(createListener());
        mSpeechRecognizer.startListening(createIntent());
        mNotificationHelper = new NotificationHelper(Environment.getGlobalContext());
        NotificationCompat.Builder SttNotificationBuilder = mNotificationHelper.getChannelNotification("SPEECH RECOGNITION", "Voice recognition in progress");
        mNotificationHelper.getManager().notify(CHANNEL_ID, SttNotificationBuilder.build());
    }

    protected static void stopRecognition(){
        mSpeechRecognizer.stopListening();
        mSpeechRecognizer = null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

}
