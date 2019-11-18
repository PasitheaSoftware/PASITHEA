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
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import java.util.ArrayList;

public class SttEngineAsService extends RecognitionService implements RecognitionListener {
    private static final String TAG = "PASITHEA: SpeechToText";
    public static final String EXTRA_VOICE_RESULTS = "pro.bizance.pasithea.EXTRA_VOICE_RESULTS";
    public static final String EXTRA_CONFIDENCE_SCORES = "pro.byzance.pasithea.EXTRA_CONFIDENT_SCORES";
    public static final String EXTRA_RESULT = "pro.byzance.pasithea.EXTRA_RESULT";
    private static final int CHANNEL_ID = 1;

    private NotificationHelper mNotificationHelper;

    // New version w/ Livedata
    private static MutableLiveData<String> ResultLiveData;

    private static Intent mSrIntent;
    public static final String RESULT_DETECTION = "pro.byzance.pasithea.RESULT_DETECTION";

    private static SpeechRecognizer mSpeechRecognizer = null;
    private LocalBroadcastManager mLocalBroadcastManager = LocalBroadcastManager.getInstance(Environment.getGlobalContext());

    public SttEngineAsService(){}

    @Override
    public void onCreate() {
        super.onCreate();
        if (mLocalBroadcastManager == null){
            Log.d(TAG, "onCreate: NO BROADCAST MANAGER");
        } else {
            Log.d(TAG, "onCreate: BROADCAST MANAGER EXISTS");
        }
        mSrIntent = createIntent();
        mSpeechRecognizer  = SpeechRecognizer.createSpeechRecognizer(Environment.getGlobalContext());
        mSpeechRecognizer.setRecognitionListener(this);
        mNotificationHelper = new NotificationHelper(Environment.getGlobalContext());
        Log.i(TAG, "onCreate: Service creation");
    }

    public static LiveData<String> getResultLiveData() {
        return ResultLiveData;
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mSpeechRecognizer.startListening(mSrIntent);
        NotificationCompat.Builder SttNotificationBuilder = mNotificationHelper.getChannelNotification("SPEECH RECOGNITION", "Voice recognition in progress");
        mNotificationHelper.getManager().notify(CHANNEL_ID, SttNotificationBuilder.build());
        return super.onStartCommand(intent, flags, startId);
    }

    private Intent createIntent(){
        Intent myIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        myIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        myIntent.putExtra(RecognizerIntent.EXTRA_PREFER_OFFLINE, "true");
        myIntent.putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_COMPLETE_SILENCE_LENGTH_MILLIS, 500);
        myIntent.putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_MINIMUM_LENGTH_MILLIS, 5000);
        return myIntent;
    }

    @Override
    public void onReadyForSpeech(Bundle params) {
        Log.i(TAG, "onReadyForSpeech");
    }

    @Override
    public void onBeginningOfSpeech() {
        Log.i(TAG, "onBeginningOfSpeech");
    }

    @Override
    public void onRmsChanged(float rmsdB) {
    }

    @Override
    public void onBufferReceived(byte[] buffer) {
        Log.i(TAG, "onBufferReceived");
    }

    @Override
    public void onEndOfSpeech() {
        Log.i(TAG, "onEndOfSpeech");

    }

    @Override
    public void onError(int error) {
        Intent mBroadcastIntent = new Intent(RESULT_DETECTION);
        Log.e(TAG, "onError");
        if(error == SpeechRecognizer.ERROR_NO_MATCH){
            Log.d(TAG, "onError: No match found");
            mBroadcastIntent.putExtra(EXTRA_RESULT, SpeechRecognizer.ERROR_NO_MATCH);
        }
        if (error == SpeechRecognizer.ERROR_SPEECH_TIMEOUT) {
            Log.d(TAG, "onError: speech timeout");
            mBroadcastIntent.putExtra(EXTRA_RESULT, SpeechRecognizer.ERROR_SPEECH_TIMEOUT);
        }
        if (error == SpeechRecognizer.ERROR_AUDIO){
            Log.d(TAG, "onError: audio error");
            mBroadcastIntent.putExtra(EXTRA_RESULT, SpeechRecognizer.ERROR_AUDIO);
        }
        if (error == SpeechRecognizer.ERROR_CLIENT){
            Log.d(TAG, "onError: client error");
            mBroadcastIntent.putExtra(EXTRA_RESULT, SpeechRecognizer.ERROR_CLIENT);
        }
        mNotificationHelper.getManager().cancel(CHANNEL_ID);
        mLocalBroadcastManager.sendBroadcast(mBroadcastIntent);
    }

    @Override
    public void onResults(Bundle results) {
        Log.i(TAG, "onResults");

        //ResultLiveData.setValue(results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION));

        Intent mBroadcastIntent = new Intent(RESULT_DETECTION);
        mBroadcastIntent.putStringArrayListExtra(EXTRA_VOICE_RESULTS,
                results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION));

        mBroadcastIntent.putExtra(EXTRA_CONFIDENCE_SCORES,
                results.getFloatArray(SpeechRecognizer.CONFIDENCE_SCORES));

        mBroadcastIntent.putExtra(EXTRA_RESULT, Activity.RESULT_OK);
        mNotificationHelper.getManager().cancel(CHANNEL_ID);
        mLocalBroadcastManager.sendBroadcast(mBroadcastIntent);
    }

    @Override
    public void onPartialResults(Bundle partialResults) {
        Log.i(TAG, "onPartialResults");
    }

    @Override
    public void onEvent(int eventType, Bundle params) {
        Log.i(TAG, "onEvent");

    }

    @Override
    protected void onStartListening(Intent recognizerIntent, Callback listener) {
        Log.i(TAG, "onStartListening");
    }

    @Override
    protected void onCancel(Callback listener) {
        Log.i(TAG, "onCancel");
    }

    @Override
    protected void onStopListening(Callback listener) {
        Log.i(TAG, "onStopListening");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "onDestroy: Destroy");
    }
}
