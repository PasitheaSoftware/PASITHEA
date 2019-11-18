/* Copyright (C) 2019 François Laforgia - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 *
 * @Author: François Laforgia (f.laforgia@logicielpasithea.fr)
 * @Date: April 10th 2019
 *
 */
package com.software.pasithea.pasithea;

import android.util.Log;
import android.util.Pair;

import java.text.BreakIterator;
import java.util.ArrayList;

class PrepareTextReading {
    private static final String TAG = "PrepareTextReading";

    private static int numberofsentences;
    private static int numberoftext;
    private static ArrayList<String> sentenceslist = new ArrayList<String>();
    private static ArrayList<Pair> textandpercent = new ArrayList<Pair>();

    public PrepareTextReading(){}

    public  int getNumberofsentences() {
        return numberofsentences;
    }

    public  void setNumberofsentences(int numberofsentences) {
        PrepareTextReading.numberofsentences = numberofsentences;
    }

    public  int getNumberoftext() {
        return numberoftext;
    }

    public  void setNumberoftext(int numberoftext) {
        PrepareTextReading.numberoftext = numberoftext;
    }

    public ArrayList<String> getSentenceslist() {
        return sentenceslist;
    }

    public  void setSentenceslist(ArrayList<String> sentenceslist) {
        PrepareTextReading.sentenceslist = sentenceslist;
    }

    public ArrayList<Pair> getTextandpercent() {
        return textandpercent;
    }

    public void setTextandpercent(ArrayList<Pair> textandpercent) {
        PrepareTextReading.textandpercent = textandpercent;
    }

    public void prepareReadingList(String[] texts, int[] percentages){

        ArrayList<Pair> textandpercent = new ArrayList<Pair>();

        int sizetexts = texts.length;
        int sizepercent = percentages.length;

        if(sizepercent < sizetexts) {
            Log.w(TAG, "PrepareReadingList: Percentage list size below the texts list size.");
            Log.i(TAG, "prepareReadingList: Completing to 100%");
            int delta = sizetexts - sizepercent;
            for (int i = 1; i <= delta; i++) {
                int index = i + sizepercent;
                percentages[index] = 100;
            }
        }

        if(sizepercent > sizetexts){
            Log.w(TAG, "prepareReadingList: Percentages list size above the texts list size.");
            Log.i(TAG, "prepareReadingList: Will use the texts list size.");
        }

        for (int i = 0; i < texts.length; i++) {
            String text = texts[i];
            int percent = percentages[i];
            Pair mPair = Pair.create(text, percent);
            textandpercent.add(mPair);
        }
        setNumberoftext(textandpercent.size());
        setTextandpercent(textandpercent);
    }

    public void prepareSentencesList(int textindex){
        String textToExamine = (String) getTextandpercent().get(textindex).first;
        int percentage = (int) getTextandpercent().get(textindex).second;
        ArrayList<String> mArrayList = new ArrayList<String>();
        ArrayList<String> textlist = new ArrayList<String>();
        if (getSentenceslist().size() != 0){
            sentenceslist.clear();
        }
        BreakIterator mBreakIterator = BreakIterator.getSentenceInstance();
        mBreakIterator.setText(textToExamine);
        int start = mBreakIterator.first();
        for (int end = mBreakIterator.next();
             end != BreakIterator.DONE;
             start = end, end = mBreakIterator.next()) {
            mArrayList.add(textToExamine.substring(start, end));
        }
        if (percentage == 100){
            setNumberofsentences(mArrayList.size());
            setSentenceslist(mArrayList);
            Log.d(TAG, "prepareSentencesList: sentence " + getNumberofsentences());
            Log.i(TAG, "prepareSentencesList: Full text reading");
        } else {
            float percentToInt = (float) percentage / 100;
            int nbsent = Math.round(mArrayList.size() * percentToInt);
            for (int i = 0; i < nbsent; i++) {
                textlist.add(mArrayList.get(i));
                Log.d(TAG, "prepareSentencesList: Sentence "+ i);
                Log.d(TAG, "prepareSentencesList: text: "+mArrayList.get(i));
            }
            setNumberofsentences(textlist.size());
            setSentenceslist(textlist);
            Log.i(TAG, "prepareSentencesList: Reading " + nbsent + " sentences");
        }
        Log.d(TAG, "prepareSentencesList: sentence number " + getNumberofsentences());
    }

    public void clearSentencesList(){
        sentenceslist.clear();
        setNumberofsentences(0);
    }
}