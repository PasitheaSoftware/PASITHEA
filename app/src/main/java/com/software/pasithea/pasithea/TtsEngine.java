/* Copyright (C) 2019 François Laforgia - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 *
 * @Author: François Laforgia (f.laforgia@logicielpasithea.fr)
 * @Date: April 10th 2019
 *
 */
package com.software.pasithea.pasithea;

import android.content.Context;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.util.Log;

import java.util.Locale;

import static android.speech.tts.TextToSpeech.LANG_MISSING_DATA;
import static android.speech.tts.TextToSpeech.LANG_NOT_SUPPORTED;
import static android.speech.tts.TextToSpeech.QUEUE_ADD;
import static android.speech.tts.TextToSpeech.SUCCESS;


class TtsEngine {
    private static final String TAG = "PASITHEA: TextToSpeech";

    private static TextToSpeech myspeech = null;
    private static onTtsInitDoneListener TtsDonelistener = null;
    private static onChangeLanguageDoneListener TtschangeLocaleListener = null;

    private static Bundle speechparams = new Bundle();
    private static Locale globlang;
    private static Context globcontext;

    private static String initSentence;

    private TtsEngine(){
        this.speechparams.putString(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "");
        this.globlang = Environment.getGlobalLocale();
        this.globcontext = Environment.getGlobalContext();
    }

    public static void setInitDoneListener(onTtsInitDoneListener listener) {
        TtsDonelistener = listener;
    }

    public static void setInitSentence(String initSentence) {
        TtsEngine.initSentence = initSentence;
    }

    public static void setTtsChangeLocaleListener(onChangeLanguageDoneListener ttsChangeLocaleListener) {
        TtschangeLocaleListener = ttsChangeLocaleListener;
    }

    public static synchronized TextToSpeech getTtsInstance() {
        if(myspeech == null) {
            myspeech = new TextToSpeech(Environment.getGlobalContext(), new TextToSpeech.OnInitListener() {
                @Override
                public void onInit(int status) {
                    if (status == SUCCESS) {
                        int result = myspeech.setLanguage(Environment.getGlobalLocale());
                        if (result == LANG_MISSING_DATA
                                || result == LANG_NOT_SUPPORTED) {
                            Log.e(TAG, "onInit: Language error");
                        } else {
                            Log.i(TAG, "onInit: TTS initialization sucessfull");
                            if(Environment.getRestartAll() == 0) {
                                myspeech.speak(initSentence,
                                        QUEUE_ADD,
                                        speechparams,
                                        "TTS initialization");
                            } else {
                                Log.d(TAG, "onInit: " + myspeech.getVoice().getLocale().getLanguage());
                                if(myspeech.getVoice().getLocale().getLanguage().equals("en")){
                                    myspeech.speak(Environment.getGlobalContext().
                                                    getString(R.string.tts_restart_us),
                                            QUEUE_ADD, speechparams,
                                            "TTS change in config");
                                } else if (myspeech.getVoice().getLocale().getLanguage().equals("fr")){
                                    myspeech.speak(Environment.getGlobalContext().
                                                    getString(R.string.tts_restart_fr),
                                            QUEUE_ADD, speechparams,
                                            "TTS change in config");
                                }
                            }
                        }
                    } else {
                        Log.e(TAG, "onInit: TTS initialization failed");
                    }
                }
            });
            myspeech.setOnUtteranceProgressListener(new UtteranceProgressListener() {
                @Override
                public void onStart(String utteranceId) {

                }

                @Override
                public void onDone(String utteranceId) {
                    Log.d(TAG, "onDone: I'm in");
                    TtsDonelistener.TtsInitDone();
                    if(Environment.getRestartAll() == 0) {
                        Log.d(TAG, "onDone: I'm in");
                        TtsDonelistener.TtsInitDone();
                    } else {
                        TtschangeLocaleListener.onReconfigurationDone();
                    }
                }

                @Override
                public void onError(String utteranceId) {
                }
            });
        }
        return myspeech;
    }

    public static TextToSpeech reloadInstance(){
        myspeech = null;
        return getTtsInstance();
    }

    public static Bundle getParams(){
        return speechparams;
    }
}