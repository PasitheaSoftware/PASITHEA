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
import android.os.SystemClock;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import static android.speech.tts.TextToSpeech.QUEUE_ADD;

class Speaker {
    private static final String TAG = "SpeakerInstance";

    private static onReadingEndListener mListener = null;
    private static TextToSpeech SpeakerTts = TtsEngine.getTtsInstance();

    public Speaker() { }

    public void ask_question(String question, String utteranceID, final Activity activity, final AnswerRecognition answerdetector) {
        TextToSpeech AnswerTTS = TtsEngine.getTtsInstance();
        final String myUID = utteranceID;
        final String myQuestion = question;
        AnswerTTS.setOnUtteranceProgressListener(new UtteranceProgressListener() {

            @Override
            public void onStart(String utteranceId) {
                Log.d(TAG, "onStart: " + myUID);
            }

            @Override
            public void onDone(String utteranceId) {
                AnswerRecognition myAnswer = answerdetector;
                myAnswer.startAnswerRecognition(activity);
                Log.d(TAG, "onDone: " + myUID);
            }

            @Override
            public void onError(String utteranceId) {
                //TODO: Catch Answer error
                Log.d(TAG, "onError: ANSWER ERROR" + myUID);
            }
        });
        AnswerTTS.speak(myQuestion, TextToSpeech.QUEUE_ADD, TtsEngine.getParams(), "ask_question");
    }

    public void ask_navigation(NavigationRecogition navigation) {
        navigation.startNavigationRecognition(Environment.getGlobalActivty());
    }

    public void ask_navigation(NavigationRecogition navigation, AppCompatActivity activity) {
        navigation.startNavigationRecognition(activity);
    }

    public static void sayMessage(String message){
        SpeakerTts.setOnUtteranceProgressListener(new UtteranceProgressListener() {
            @Override
            public void onStart(String utteranceId) {
                // TODO: Check simpole message start
            }

            @Override
            public void onDone(String utteranceId) {
                SpeakerTts.stop();
            }

            @Override
            public void onError(String utteranceId) {
                Log.d(TAG, "onError: MESSAGE: ERROR");
                // TODO: Catch simple message error

            }
        });
        SpeakerTts.speak(message, QUEUE_ADD, TtsEngine.getParams(), "message reading");
        while (SpeakerTts.isSpeaking()){
            SystemClock.sleep(10);
        }
    }

    public static void sayMessage(String message, final onInitListener listener){
        SpeakerTts.setOnUtteranceProgressListener(new UtteranceProgressListener() {
            @Override
            public void onStart(String utteranceId) {
                Log.d(TAG, "onStart: ");
                // TODO: check message + listener start
            }

            @Override
            public void onDone(String utteranceId) {
                SpeakerTts.stop();
                listener.InitDone();
            }
            @Override
            public void onError(String utteranceId) {
                Log.d(TAG, "onError: MESSAGE + LISTENER: ERROR");
                // TODO: Catch message + listener error
            }
        });
        SpeakerTts.speak(message, QUEUE_ADD, TtsEngine.getParams(), "message reading");
    }

    public static void sayMessage(String message, onReadingEndListener listener){
        mListener = listener;
        SpeakerTts.setOnUtteranceProgressListener(new UtteranceProgressListener() {
            @Override
            public void onStart(String utteranceId) {
                Log.d(TAG, "onStart: ");
                // TODO: check message + listener start
            }

            @Override
            public void onDone(String utteranceId) {
                SpeakerTts.stop();
                mListener.onReadingEnd();
            }

            @Override
            public void onError(String utteranceId) {
                Log.d(TAG, "onError: MESSAGE + LISTENER: ERROR");
                // TODO: Catch message + listener error
            }
        });
        SpeakerTts.speak(message, QUEUE_ADD, TtsEngine.getParams(), "message reading");
    }
}