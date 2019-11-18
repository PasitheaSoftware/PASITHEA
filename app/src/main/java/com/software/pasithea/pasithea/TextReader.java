/* Copyright (C) 2019 François Laforgia - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 *
 * @Author: François Laforgia (f.laforgia@logicielpasithea.fr)
 * @Date: April 10th 2019
 *
 */
package com.software.pasithea.pasithea;

import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.util.Log;

class TextReader {
    private static final String TAG = "TextReader";

    private PrepareTextReading mPrepareTextReading = new PrepareTextReading();
    //private static TextToSpeech mTextToSpeech = TtsEngine.getTtsInstance();

    private static onReadingEndListener endListener = null;

    private static int sentenceindex;
    private static int textindex;

    public TextReader(String[] texts, int[] percentages) {
        mPrepareTextReading.prepareReadingList(texts, percentages);
        setTextindex(0);
        setSentenceindex(0);
    }

    /*
    Getter and setter for the variables
    */
    public static void setEndListener(onReadingEndListener endListener) {
        if(TextReader.endListener != null){
            TextReader.endListener = null;
        }
        TextReader.endListener = endListener;
    }

    public static int getSentenceindex() {
        return sentenceindex;
    }

    public void setSentenceindex(int sentenceindex) {
        TextReader.sentenceindex = sentenceindex;
    }

    public static int getTextindex() {
        return textindex;
    }

    public void setTextindex(int textindex) {
        TextReader.textindex = textindex;
    }

    public void startReading(){
        mPrepareTextReading.prepareSentencesList(getTextindex());
        startReadingText();
    }

    private void startReadingText(){
        Log.d(TAG, "startReadingText: number of sentences " + mPrepareTextReading.getNumberofsentences());
        TextToSpeech mTextToSpeech = TtsEngine.getTtsInstance();
        for (int i = getSentenceindex(); i < mPrepareTextReading.getNumberofsentences() ; i++) {
            Log.d(TAG, "startReading: Entering the for loop: "+i);
            int text = getTextindex()+1;
            int sentence = i+1;
            String uid = "Reading text " + text + " - Sentence: " + sentence;
            mTextToSpeech.setOnUtteranceProgressListener(new UtteranceProgressListener() {
                @Override
                public void onStart(String s) {
                    Log.i(TAG, "onStart: " + s);
                }

                @Override
                public void onDone(String s) {
                    Log.i(TAG, "onDone: " + s);
                    Log.d(TAG, "onDone: old index number: " + getSentenceindex());
                    setSentenceindex(getSentenceindex()+1);
                    Log.d(TAG, "onDone: new index number: " + getSentenceindex());
                    if (getSentenceindex() == mPrepareTextReading.getNumberofsentences()){
                        if(endListener != null){
                            endListener.onReadingEnd();
                        }
                    }
                }

                @Override
                public void onError(String s) {
                    Log.d(TAG, "onError: Error occurred");

                }
            });
            mTextToSpeech.speak(mPrepareTextReading.getSentenceslist().get(i),
                    TextToSpeech.QUEUE_ADD, TtsEngine.getParams(),
                    uid);
            Log.d(TAG, "startReadingText: Speak started for sentence: "+i);
        }
    }

    public void readNextText(){
        if(getTextindex()+1 <= mPrepareTextReading.getNumberoftext()){
            setTextindex(getTextindex()+1);
            setSentenceindex(0);
            mPrepareTextReading.clearSentencesList();
            startReading();
        } else {
            Speaker.sayMessage("vous avez atteint la fin de la liste.");
            continueReading();
        }
    }

    public void readPreviousText(){
        if(getTextindex()-1 >= 0){
            setTextindex(getTextindex()-1);
            setSentenceindex(0);
            mPrepareTextReading.clearSentencesList();
            startReading();
        } else {
            Speaker.sayMessage("Vous êtes au début de la liste.");
            continueReading();
        }
    }

    public void readNextSentence(){
        if(getSentenceindex()+1 <= mPrepareTextReading.getNumberofsentences()){
            setSentenceindex(getSentenceindex()+1);
            startReadingText();
        } else {
            Speaker.sayMessage("Vous êtes à la fin du texte.");
            continueReading();
        }
    }

    public void readPreviousSentence(){
        if(getSentenceindex()-1 >= 0){
            setSentenceindex(getSentenceindex()-1);
            startReadingText();
        } else {
            Speaker.sayMessage("Vous êtes au début du texte.");
            continueReading();
        }
    }

    public void continueReading(){
        Log.d(TAG, "continueReading: index "+getSentenceindex());
        startReadingText();
    }
}