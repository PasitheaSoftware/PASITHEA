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

public class PasitheaBuilder {

    private Activity mActivity = null;
    private Application mApplication = null;
    private Context mContext;
    private onInitListener InitListener = null;

    public PasitheaBuilder(){}

    /**
     * Set the initial value for PASITHEA.
     * This value is mandatory and have no default.
     * @param application The Application calling the initialization.
     */
    public PasitheaBuilder setValues(Application application){
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
    public PasitheaBuilder setInitListener(onInitListener listener){
        this.InitListener = listener;
        return this;
    }

    /**
     * Initialize the instance of PASITHEA and returns it.
     * @return An initialized instance of PasitheaFromActivity
     */

    public Pasithea build(){
        if (InitListener !=null){
            Pasithea.setInitiListener(InitListener);
            Pasithea.initializeFramework(mApplication);
            return Pasithea.getInstance(InitListener);
        } else {
            Pasithea.initializeFramework(mApplication);
            return Pasithea.getInstance();
        }
    }
}
