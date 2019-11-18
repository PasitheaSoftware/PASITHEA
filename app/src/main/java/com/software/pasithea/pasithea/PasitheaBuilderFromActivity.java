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
import android.app.Application;
import android.content.Context;

/**
 * Builder class for PASITHEA initialization.
 * @author PasitheaFromActivity Software
 * @version 1.0
 */

public class PasitheaBuilderFromActivity {

    private Activity mActivity = null;
    private Application mApplication = null;
    private Context mContext;
    private onInitListener InitListener = null;

    public PasitheaBuilderFromActivity(){}

    /**
     * Set the initial values for PASITHEA.
     * These values are mandatory and have no default.
     * @param activity The activity in which the builder is called
     * @param context The context in which the builder is called
     */
    public PasitheaBuilderFromActivity setValues(Activity activity, Context context){
        this.mActivity = activity;
        this.mContext = context;
        return this;
    }

    public PasitheaBuilderFromActivity setValues(Application application){
        this.mApplication = application;
        this.mContext = application.getApplicationContext();
        return this;
    }

    /**
     * Set the listener to trigger an anction once the initialisation is done.
     * This methos is optional. If it is not set, the initialization will just return an instance of PASITHEA.
     *
     * @see onInitListener
     *
     * @param listener The onInitListener to trigger an action after the initialization
     */
    public PasitheaBuilderFromActivity setInitListener(onInitListener listener){
        this.InitListener = listener;
        return this;
    }

    /**
     * Initialize the instance of PASITHEA and returns it.
     * @return An initialized instance of PasitheaFromActivity
     */

    public PasitheaFromActivity build(){
        if (InitListener !=null){
            PasitheaFromActivity.setInitiListener(InitListener);
        }
        PasitheaFromActivity.initializeFramework(mContext, mActivity);
        return PasitheaFromActivity.getInstance();
    }
}
