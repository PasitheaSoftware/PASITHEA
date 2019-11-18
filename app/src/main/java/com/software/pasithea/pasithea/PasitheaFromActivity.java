/* Copyright (C) 2019 François Laforgia - All Rights Reserved
* Unauthorized copying of this file, via any medium is strictly prohibited
* Proprietary and confidential
*
* @Author: François Laforgia (f.laforgia@logicielpasithea.fr)
* @Date: April 10th 2019
*
*/

package com.software.pasithea.pasithea;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.media.AudioFocusRequest;
import android.os.Build;
import android.speech.tts.TextToSpeech;
import android.util.DisplayMetrics;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import java.util.HashMap;
import java.util.Locale;
import java.util.Random;

import static com.software.pasithea.pasithea.Environment.getGlobalActivty;
import static com.software.pasithea.pasithea.Environment.getGlobalContext;

/**
 * PasitheaFromActivity main class to expose the internal functions to the developpers.
 * This class is a singleton, you need first to build an instance.
 * Once the initialization is done you get an instance by calling the method getInstance().
 * Depending on the build methods you used, the initialization will either retrun the instance of PASITHEA
 * or execute an action once the initialization is done. For more details on the initialization check the PasitheaBuilderFromActivity class.
 * @see PasitheaBuilderFromActivity
 * @author PasitheaFromActivity Software
 * @version 1.0
 *
 */
public class PasitheaFromActivity {
    private static final String TAG = "PASITHEA";

    private Environment EnvManager = new Environment();
    private TextReader PasitheaReader = null;
    private AnswerRecognition mAnswerInstance = null;
    private NavigationRecogition mNavigationInstance = null;
    private WriteRecognition mWriteInstance = null;
    private static onInitListener mInitiListener;

    private static TextToSpeech myspeech = null;

    private static int answerID = 3000;
    private static int navID = 5000;
    private static int writeID = 7000;

    public static PasitheaFromActivity instance = null;

    private PasitheaFromActivity() {
        if (Environment.getCurrentYear() == 2019) {
            initializeTts();
        } else {
            Log.e(TAG, "Testing time is expired. Contact us (contact@logicielpasithea.fr) to get a full account");
            stopPasithea();
        }
    }

    /*
    Protected method used by the builder to set the action to trigger after the initialization is done.
     */

    protected static void setInitiListener(onInitListener InitListener) {
        mInitiListener = InitListener;
    }

    /**
     * Get the PASITHEA instance created during the initialization process. Once PASITHEA is initialized, this method can be called anywhere in the project.
     * This method will return the instance created by the initialization.
     * @return An instance of PASITHEA
     */

    public static synchronized PasitheaFromActivity getInstance(){
        if (instance == null) {
            Log.i(TAG, "getInstance: New instance created");
                instance = new PasitheaFromActivity();
        }
        return instance;
    }

    protected static synchronized PasitheaFromActivity getInstance(onInitListener InitListener){
        if(instance == null){
            mInitiListener = InitListener;
            instance = new PasitheaFromActivity();
        }
        return instance;
    }

    protected static void initializeFramework(@NonNull Context context, @NonNull Activity activity) {
        Environment.setGlobalActivty(activity);
        Environment.setGlobalContext(context);
        Environment.setGlobalLocale(context.getResources().getConfiguration().locale);
        Environment.setAudioManager(activity);
        if (Build.VERSION.SDK_INT >= Environment.AUDIOFOCUS_MIN_BUILD) {
            Environment.requestAudioFocus();
        } else {
            Log.i(TAG, "initializeFramework: Audio focus not supported in the SDK version");
        }
        Environment.setInitialAudioVolume();
    }

    private void initializeStt() {
        int supported = EnvManager.checkBuildVersion();
        if (supported == 0) {
            onPermissionResultListener permissionlistener = new onPermissionResultListener() {
                @SuppressLint("StringFormatInvalid")
                @Override
                public void onGranted() {
                    Log.i(TAG, "initializeStt: STT supported");
                    if (mInitiListener != null){
                        sayInitSomething(Environment.getGlobalContext().getString(R.string.voice_supported),
                                mInitiListener);
                    } else {
                        saySomething(Environment.getGlobalContext().getString(R.string.voice_supported));
                    }
                }

                @Override
                public void onDenied() {
                    Log.w(TAG, "initializeStt: STT not supported - One or more permissions were denied");

                    if (mInitiListener != null){
                        sayInitSomething(Environment.getGlobalContext().getString(R.string.stt_permissions_not_granted),
                                mInitiListener);
                    } else {
                        saySomething(Environment.getGlobalContext().getString(R.string.stt_permissions_not_granted));
                    }
                }
            };
            EnvManager.setListener(permissionlistener);
            EnvManager.requestAudioPermissions();
            EnvManager.checkAsrSupport();
         } else {
            EnvManager.setAsrSupport(1);
            Log.w(TAG, "initializeStt: STT not supported - min build version not found ");
            if (mInitiListener != null){
                sayInitSomething(Environment.getGlobalContext().getString(R.string.stt_not_supported),
                        mInitiListener);
            } else {
                saySomething(Environment.getGlobalContext().getString(R.string.stt_not_supported));
            }
        }
        Log.i(TAG, "initializeComponents: Components initialization done");
    }

    private  void initializeTts() {
        TtsEngine.setInitSentence("La synthèse vocale est initialisée");
        TtsEngine.setInitDoneListener(new onTtsInitDoneListener() {
            @Override
            public void TtsInitDone() {
                Environment.setRestartAll(1);
                initializeStt();
            }
        });
        myspeech = TtsEngine.getTtsInstance();
    }

    private AnswerRecognition getmAnswerInstance() {
        return mAnswerInstance;
    }

    private void setmAnswerInstance(AnswerRecognition mAnswer) {
        this.mAnswerInstance = mAnswer;
    }

    private NavigationRecogition getmNavigationInstance() {
        return mNavigationInstance;
    }

    private void setmNavigationInstance(NavigationRecogition mNavigationInstance) {
        this.mNavigationInstance = mNavigationInstance;
    }

    private WriteRecognition getmWritInstance() {
        return mWriteInstance;
    }

    private void setmWriteInstance(WriteRecognition mWriteInstance) {
        this.mWriteInstance = mWriteInstance;
    }

    /*
    Audio Focus management
    Used to request and abandon the audio focus
     */

    public static AudioFocusRequest getAudioFocusRequest(){
        return Environment.getFocusRequest();
    }

    public static void abandonAudioFocusRequest(){
        Environment.abandonFocus();
    }

    /*
    Answer management
    Used to create and start a question/answer sequence
    The string array for the answer words is:
    string[0] = yes
    string[1] = no
     */
    private AnswerRecognition createAnswer() {
        AnswerRecognition myAnswer = new AnswerRecognition(answerID);
        answerID += 1;
        return myAnswer;
    }

    /**
     * Start the Question/Answer method from the main activity.
     * This function used closed questions where there is only 2 possibles answers.
     *
     * <p>Example of answer words in the string array:
     * <br>
     * <br>string[0] = "yes"
     * <br>string[1] = "no"</p>
     *
     * But the answer words can be anything.
     *
     * @see onAnswerListener
     *
     * @param question The question to ask (No default)
     * @param answerwords The 2 expected answers in a string list (No default)
     * @param listener The onAnswerListener to trigger the actions depending on the answer detected (No default)
     */
    public void startQuestionAnswer(String question,
                                    String[] answerwords,
                                    onAnswerListener listener){
        stopInstances();
        String uid = "Question_Answer method";
        Speaker PasitheaSpeaker = new Speaker();
        AnswerRecognition mAnswer = createAnswer();
        mAnswer.setAnswerListener(listener);
        setmAnswerInstance(mAnswer);
        mAnswer.setKeywords(answerwords[0], answerwords[1]);
        PasitheaSpeaker.ask_question(question, uid, getGlobalActivty(), mAnswer);
    }

    /**
     * Start the question/answer method from another activty than the main activity.
     * This function used closed questions where there is only 2 possibles answers.
     *
     * <p>Example of answer words in the string array:
     * <br>string[0] = "yes"
     * <br>string[1] = "no"</p>
     *
     * But the answer words can be anything.
     *
     * @see onAnswerListener
     *
     * @param question The question to ask (No default)
     * @param answerwords The 2 expected answers in a string list (No default)
     * @param activity The activity to start the method in
     * @param listener The onAnswerListener to trigger the actions depending on the answer detected (No default)
     */
    public void startQuestionAnswer(String question,
                                    String[] answerwords,
                                    AppCompatActivity activity,
                                    onAnswerListener listener){
        stopInstances();
        String uid = "Question_Answer method";
        Speaker PasitheaSpeaker = new Speaker();
        AnswerRecognition mAnswer = createAnswer();
        mAnswer.setAnswerListener(listener);
        setmAnswerInstance(mAnswer);
        mAnswer.setKeywords(answerwords[0], answerwords[1]);
        PasitheaSpeaker.ask_question(question, uid, activity, mAnswer);
    }

    /**
     * Restart the answer recognition
     * This method keeps the same parameters than the first call to startQuestionAnswer() and it will restart it in the same activity, if a new activty has been passed as argument.
     *
     */
    public void restartQuestionAnswer(){
        getmAnswerInstance().restartAnswerRecognition();
    }

    /**
     * Stop the answer recognition
     */
    public void stopQuestionAnswer(){
        getmAnswerInstance().stopAnswerRecognition();
        setmAnswerInstance(null);
    }

    /*
    Convert voice to text management
     */
    private WriteRecognition createWrite(){
        WriteRecognition mWriteText = new WriteRecognition(writeID);
        writeID += 1;
        return mWriteText;
    }

    /**
     * Start a speech-to-text session. This method returns the text transcription of the speech detected and passes it to a onWriteListener defines by the developper.
     *
     * @see onWriteListener
     *
     * @param listener The onWriteListener to trigger the action once the text transcript is available (No default)
     */
    public void startWriteText(onWriteListener listener){
        stopInstances();
        WriteRecognition mWriteRecognition = createWrite();
        mWriteRecognition.setwriteListener(listener);
        setmWriteInstance(mWriteRecognition);
        mWriteRecognition.startWriteRecognition(getGlobalActivty());
    }

    /**
     * Start a speech-to-text session in a new activity other than the main activity. The activity supported by must be an AppCompatActivity.
     * This method returns the text transcription of the speech detected and passes it to a onWriteListener defines by the developper.
     *
     * @see onWriteListener
     *
     * @param activity The new activity to start the session in
     * @param listener The onWriteListener to trigger the action once the text transcript is available (No default)
     */
    public void startWriteText(AppCompatActivity activity, onWriteListener listener){
        stopInstances();
        WriteRecognition mWriteRecognition = createWrite();
        mWriteRecognition.setwriteListener(listener);
        setmWriteInstance(mWriteRecognition);
        mWriteRecognition.startWriteRecognition(activity);
    }

    /**
     * Restart the speech-to-text session.
     */

    public void restartWriteText(){
        getmWritInstance().restartWriteRecognition();
    }

    /**
     * Stop the speech-to-text session.
     */

    public void stopWriteText(){
        getmWritInstance().stopWriteRecognition();
        setmWriteInstance(null);
    }

    /*
    Navigation Management
    Used to create and start a Navigation sequence
     */
    private NavigationRecogition createNavigation() {
        NavigationRecogition myNavigation = new NavigationRecogition(navID);
        navID += 1;
        return myNavigation;
    }

    /**
     * Start a navigation session.
     * The keywords are defines by the developper and they are stored in a hashmap.
     * The hashmap keys are fixed but the values can be anything.
     * <p>Keys of the hashmap:
     * <br>"NEXT"
     * <br>"PREVIOUS"
     * <br>"QUIT"
     * <br>"RESUME"
     * <br>"STOP"
     * <br>"NEXT_PART"
     * <br>"PREVIOUS_PART"</p>
     *
     * Each keywords is attached to a action defines by the developper in a onNavigateListener and the action is triggered when the keywords is detected.
     *
     * @see onNavigateListener
     *
     * @param keywords The hashmap for the keywords
     * @param listener The onNavigateListener to triger the actions
     */
    public void startNavigation(HashMap<String, String> keywords, onNavigateListener listener){
        pauseReading();
        Speaker PasitheaSpeaker = new Speaker();
        NavigationRecogition mNavigation = createNavigation();
        mNavigation.setNavigationListener(listener);
        mNavigation.setKeywords(keywords);
        setmNavigationInstance(mNavigation);
        PasitheaSpeaker.ask_navigation(mNavigation);
    }

    /**
     * Start a navigation session in another activity than the main activity.
     * The keywords are defines by the developper and they are stored in a hashmap.
     * The hashmap keys are fixed but the values can be anything.
     * <p>Keys of the hashmap:
     * <br>"NEXT"
     * <br>"PREVIOUS"
     * <br>"QUIT"
     * <br>"RESUME"
     * <br>"STOP"
     * <br>"NEXT_PART"
     * <br>"PREVIOUS_PART"</p>
     *
     * Each keywords is attached to a action defines by the developper in a onNavigateListener and the action is triggered when the keywords is detected.
     *
     * @see onNavigateListener
     *
     * @param keywords The hashmap for the keywords
     * @param listener The onNavigateListener to triger the actions
     */
    public void startNavigation(HashMap<String, String> keywords,
                                onNavigateListener listener,
                                AppCompatActivity activity){
        pauseReading();
        Speaker PasitheaSpeaker = new Speaker();
        NavigationRecogition mNavigation = createNavigation();
        mNavigation.setNavigationListener(listener);
        mNavigation.setKeywords(keywords);
        setmNavigationInstance(mNavigation);
        PasitheaSpeaker.ask_navigation(mNavigation, activity);
    }

    /**
     * Restart the navigation session with the same arguments than the ones used in the first call.
     */
    public void restartNavigation(){
        getmNavigationInstance().restartNavigationRecognition();
    }

    /**
     * Stop the navigation session.
     */
    public void stopNavigation(){
        getmNavigationInstance().stopNavigationRecognition();
        setmNavigationInstance(null);
    }

    /*
     ###########################
     # Text reading management #
     ###########################
      */

    /**
     * Start a partial document reading session with an action at the end of the reading.
     * The documents are passed as a string array even if there is only one text to read.
     * This method start with the first document and read the first percentage of  the list.
     * Once the first reading is over, it jumps to the next in the list and read the next percentage in the list.
     *
     * <p>Examples:
     * <p>
     * <b>1) text == percentage</b>
     * <br>texts == [text1, text2] && percentage == [25, 45]
     * <br>
     * startReadingText() reads 25% of text1 and 45% of text2.</p>
     * <p><b>2) text != percentage</b>
     * <br>texts == [text1, text2] && percentage == [25]
     *<br>
     * startReadingText reads 25% of text1 and 100% of text2</p></p>
     *
     * The percentage of the text is converted in a number of sentences rounded up.
     * The listener is triggered once the last text of the list is reached.
     *
     * @see onReadingEndListener
     *
     * @param text Text(s) to read as a String array (No default)
     * @param percentage Integer array to define the percentages. Must be between 1 and 99 (No default)
     * @param listener The onNavigateListener to trigger the action once the reading is over (No default)
     */
    public void startReadingText(String[] text, int[] percentage, onReadingEndListener listener){
        TextReader.setEndListener(listener);
        PasitheaReader = new TextReader(text, percentage);
        PasitheaReader.startReading();
    }

    /**
     * Start a partial document reading session.
     * The documents are passed as a string array even if there is only one text to read.
     * This method start with the first document and read the first percentage of  the list.
     * Once the first reading is over, it jumps to the next in the list and read the next percentage in the list.
     *
     * <p>Examples:
     * <p>
     * <b>1) text == percentage</b>
     * <br>texts == [text1, text2] && percentage == [25, 45]
     * <br>
     * startReadingText() reads 25% of text1 and 45% of text2.</p>
     * <p><b>2) text != percentage</b>
     * <br>texts == [text1, text2] && percentage == [25]
     * <br>
     * startReadingText reads 25% of text1 and 100% of text2</p></p>
     *
     * The percentage of the text is converted in a number of sentences rounded up.
     * The method returns once the last text of the list is read or when the reading is stopped.
     *
     * @param text Text(s) to read as a String array (No default)
     * @param percentage Integer array to define the percentage. Must be between 1 and 99 (No default)
     */
    public void startReadingText(String[] text, int[] percentage){
        PasitheaReader = new TextReader(text, percentage);
        PasitheaReader.startReading();
    }

    /**
     * Start a full document reading session with an action at the end of the reading.
     * This method will read all the document in the list and once it's done it will trigger the action defines in the listener.
     *
     * @see onReadingEndListener
     *
     * @param text Text(s) to read as a String array (No default)
     * @param listener The onNavigateListener to trigger the action once the reading is over (No default)
     */
    public void startReadingText(String[] text, onReadingEndListener listener){
        TextReader.setEndListener(listener);
        int[] percentage = new int[1];
        percentage[0] = 100;
        PasitheaReader = new TextReader(text, percentage);
        PasitheaReader.startReading();
    }

    /**
     * Start a full document reading session.
     * This method will read all the documents passed in the list.
     * The method returns once the reading is done or when the reading is stopped.
     *
     * @param text Text(s) to read as a String array (No default)
     */
    public void startReadingText(String[] text){
        int[] percentage = new int[1];
        percentage[0] = 100;
        PasitheaReader = new TextReader(text, percentage);
        PasitheaReader.startReading();
    }

    /**
     * Resume the reading after it has been paused by a call to pauseReading() method.
     * The reading is resumed from the beginning of the last sentence being read.
     *
     */
    public void continueReading(){
        if(PasitheaReader != null){
            PasitheaReader.continueReading();
        } else {
            Log.e(TAG, "continueReading: Reader not initialized");
        }
    }

    /**
     * Jump to the next text and start reading it.
     * If the current text is the last in the list the method will say a vocal message to inform the user.
     */
    public void readNextText(){
        if(PasitheaReader != null){
            PasitheaReader.readNextText();
        } else {
            Log.e(TAG, "continueReading: Reader not initialized");
        }
    }

    /**
     * Jump to the previous text in the list and start to read it.
     * If the current text is the first in the list, the method will say a vocal message to inform the user.
     */
    public void readPreviousText(){
        if(PasitheaReader != null){
            PasitheaReader.readPreviousText();
        } else {
            Log.e(TAG, "continueReading: Reader not initialized");
        }
    }

    /**
     * Read the next sentence in the current text.
     * If the current sentence being read is the last of the text, the method will say a vocal message to inform the user.
     */
    public void readNextSentence(){
        if(PasitheaReader != null){
            PasitheaReader.readNextSentence();
        } else {
            Log.e(TAG, "continueReading: Reader not initialized");
        }
    }

    /**
     * Read the previous sentence in the current text.
     * If the current sentence being read is the firs of the text, the method will say a vocal message to inform the user.
     */
    public void readPreviousSentence(){
        if(PasitheaReader != null){
            PasitheaReader.readPreviousSentence();
        } else {
            Log.e(TAG, "continueReading: Reader not initialized");
        }
    }

    /**
     * Pause the reading
     */
    public void pauseReading(){
        myspeech.stop();
    }

    /**
     * Shutdown the reader instance.
     * after the call of this method the reading is no more available and needs to be restarted from scratch.
     */

    public void shutdownReadingText(){
        myspeech.stop();
        myspeech.shutdown();
        TextToSpeech mTtsEngine = TtsEngine.getTtsInstance();
        if (mTtsEngine.isSpeaking()){
            mTtsEngine.shutdown();
        }
    }

    // Generic methods
    private void stopInstances(){
        if(getmAnswerInstance() != null){
            Log.d(TAG, "stopInstances: Stop Answer");
            stopQuestionAnswer();
        }
        if(getmWritInstance() != null){
            Log.d(TAG, "stopInstances: Stop Write");
            stopWriteText();
        }
        if (getmNavigationInstance() != null){
            Log.d(TAG, "stopInstances: Stop Navigation");
            stopNavigation();
        }
        Log.d(TAG, "stopInstances: Nothing to stop");
    }

    /**
     * Say a <b>short message</b> randomly chosen from a sentences array provided as argument.
     *
     * @param sentencelist A String array with the possible messages to say. there is no size limit of this array (No default)
     */
    public void saySomethingRandom(String[] sentencelist){
        String message = generateRandomSentence(sentencelist);
        Speaker.sayMessage(message);
    }

    /**
     * Say a <b>short message</b> randomly chosen from a sentences array provided as argument and trigger an action after the speech is done.
     *
     * @see onReadingEndListener
     * @param sentencelist A String array with the possible messages to say. there is no size limit of this array (No default)
     * @param readingfinished The onReadinEndListener to trigger the action (No default)
     */
    public void saySomethingRandom(String[] sentencelist, onReadingEndListener readingfinished){
        String message =generateRandomSentence(sentencelist);
        Speaker.sayMessage(message, readingfinished);
    }

    /**
     * Say a <b>short message</b> and return.
     *
     * @param message The message to read (No default)
     */
    public void saySomething(String message){
        Speaker.sayMessage(message);
    }

    private void sayInitSomething(String message, onInitListener initDoneListener){
        Speaker.sayMessage(message, initDoneListener);
    }

    /**
     * Say a <b>short message</b> and trigger an action when the speech is done.
     *
     * @see onReadingEndListener
     *
     * @param message The message to read (No default)
     * @param readingfinished The onReadinEndListener to trigger the action (No default)
     */
     public void saySomething(String message, onReadingEndListener readingfinished){
        Speaker.sayMessage(message, readingfinished);
    }

    /**
     * Default vocal message says when the speech recognition failed.
     * This method can used inside the unknown functions of the onAnswerListener and the onµNavigateListener.
     * This method say the message only. the restart of the session must be done through the restart methods available for each fonctionnality (restartAnswer(), restartNavigation(), ...).
     * The use of this method is not mandatory and the developper may prefer to create a custom method to manage this kind of error.
     * <br>
     * The recognition failure can occur when:
     * <p><b>- The speech recognition didn't recognized the spoken words as a keyword:</b>
     * <br>In this case this is the duty of the developper to manage this error by using this method or a custom method.
     * <br>For the question/answer this error is manager through the onAnswerUnk() method of the interface.
     * <br>For the navigation this is managed through the onNavUnk() of the interface</p>
     * <p><b>- An internal error occurred:</b>
     * <br>In this case the speech recognition engine will call this method and restart the recognition automatically.</p>
     *
     * @see onAnswerListener
     * @see onNavigateListener
     */
    public void unkAnswer(){
        saySomething(getGlobalContext().getResources().getString(R.string.unknown_result));
    }

    private String generateRandomSentence(String[] sentencelist){
        int indexRandom = new Random().nextInt(sentencelist.length);
        return sentencelist[indexRandom];
    }

    /**
     * Change the Locale of the framework only.
     * This method does not touch to the locale of the apps or of the language of the system.
     * It can be used to change the language of the voice engines like for instance switching dynamically from French to English.
     * Once the change is done, the method call the onChangeLocaleListener to trigger the action configred.
     * <br>The version 1.0 supports <b>French</b> and <b>English</b> natively.
     *
     * @see onChangeLanguageDoneListener
     *
     * @param locale The new locale to set (Default is the system locale)
     * @param listener A listener to trigger an action once the locale is changed (No default)
     */
    public void changeLanguage(Locale locale, onChangeLanguageDoneListener listener){
        if(!locale.equals(Environment.getGlobalLocale())){
            TtsEngine.setTtsChangeLocaleListener(listener);
            Environment.setOldLocale(Environment.getOldLocale());
            Resources res = getGlobalContext().getResources();
            DisplayMetrics dm = res.getDisplayMetrics();
            Configuration conf = res.getConfiguration();
            conf.setLocale(locale);
            res.updateConfiguration(conf, dm);
            Environment.setGlobalLocale(locale);
            TtsEngine.reloadInstance();
        } else {
            saySomething(getGlobalContext().getResources().getString(R.string.locale_no_change));
        }
    }

    public void stopPasithea(){
        EnvManager.restoreVolume();
        myspeech.shutdown();
        if(Build.VERSION.SDK_INT >= Environment.AUDIOFOCUS_MIN_BUILD){
            abandonAudioFocusRequest();
        }
        instance = null;
    }

    /*public int getAsrSupport(){
        return Environment.getAsrSupport();
    }

    public void restoreVolume(){
        EnvManager.restoreVolume();
    }

    public void setVolume(int volumeLevel){
        EnvManager.setAudioVolume(volumeLevel);
    }*/
}