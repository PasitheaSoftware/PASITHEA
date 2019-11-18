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
import androidx.lifecycle.Observer;

class WriteRecognitionLiveData implements Observer<String> {
    private static final String TAG = "WriteRecognition";

    private int REQUEST_SPEECH_RECOGNIZER;

    private onWriteListener WriteListener = null;
    private static Activity WriteActivity = null;
    private static Intent WriteStt = null;

    public WriteRecognitionLiveData(int SRID) {
        this.REQUEST_SPEECH_RECOGNIZER = SRID;
    }

    public void setwriteListener(onWriteListener listener) {
        this.WriteListener = listener;
    }

    Observer<String> mObserver = new Observer<String>() {
        @Override
        public void onChanged(String s) {
            WriteListener.textDetected(s);
        }
    };

    public void startWriteRecognition() {
        if(Environment.getAsrSupport() == 1){
            Speaker mSpeaker = new Speaker();
            mSpeaker.sayMessage(Environment.getGlobalContext().getResources().getString(R.string.stt_not_supported));
            return;
        }

        SttEngineLiveData.startRecognition();
         SttEngineLiveData
                .getResultLiveData()
                .observeForever(mObserver);
    }

    public void stopWriteRecognition(){
        SttEngineLiveData.getResultLiveData().removeObserver(mObserver);
    }

    @Override
    public void onChanged(String s) {
        WriteListener.textDetected(s);
    }

    public void restartWriteRecognition(){
        //getWriteActivity().stopService(getWriteStt());
        //getWriteActivity().startService(getWriteStt());
    }
}