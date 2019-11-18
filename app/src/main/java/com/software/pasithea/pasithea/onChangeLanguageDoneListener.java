/* Copyright (C) 2019 François Laforgia - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 *
 * @Author: François Laforgia (f.laforgia@logicielpasithea.fr)
 * @Date: April 10th 2019
 *
 */
package com.software.pasithea.pasithea;

/**
 * Luistener interface to trigger the action when the locale is change in the framework.
 *
 * @author Pasithea Software
 * @version 1.0
 *
 * @see Pasithea
 */

public interface onChangeLanguageDoneListener {
    /**
     * Action triggered when the locale reconfiguration is done.
     */
    void onReconfigurationDone();
}
