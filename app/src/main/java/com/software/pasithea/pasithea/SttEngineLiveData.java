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

public class SttEngineLiveData {
    private static final String TAG = "PASITHEA: SpeechToText";
    public static final String EXTRA_VOICE_RESULTS = "com.software.pasithea.pasithea.EXTRA_VOICE_RESULTS";
    public static final String EXTRA_CONFIDENCE_SCORES = "com.software.pasithea.pasithea.EXTRA_CONFIDENT_SCORES";
    public static final String EXTRA_RESULT = "com.software.pasithea.pasithea.EXTRA_RESULT";
    public static final String RESULT_DETECTION = "com.software.pasithea.pasithea.RESULT_DETECTION";
    private static final int CHANNEL_ID = 1;

    private static MutableLiveData<String> ResultLiveData;

    public SttEngineLiveData(){ }

    public static MutableLiveData<String> getResultLiveData() {
        return ResultLiveData;
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
                ResultLiveData.setValue("ERROR");

            }

            @Override
            public void onResults(Bundle bundle) {
                ResultLiveData.setValue(bundle.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION).get(0));
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
        SpeechRecognizer mSpeechRecognizer  = SpeechRecognizer.createSpeechRecognizer(Environment.getGlobalContext());
        mSpeechRecognizer  = SpeechRecognizer.createSpeechRecognizer(Environment.getGlobalContext());
        mSpeechRecognizer.setRecognitionListener(createListener());
        mSpeechRecognizer.startListening(createIntent());
        NotificationHelper mNotificationHelper = new NotificationHelper(Environment.getGlobalContext());
        NotificationCompat.Builder SttNotificationBuilder = mNotificationHelper.getChannelNotification("SPEECH RECOGNITION", "Voice recognition in progress");
        mNotificationHelper.getManager().notify(CHANNEL_ID, SttNotificationBuilder.build());
    }

    protected void stopRecognition(Observer<String> observer){
    }
}
