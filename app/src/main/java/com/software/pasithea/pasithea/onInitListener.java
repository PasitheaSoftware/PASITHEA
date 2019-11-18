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
 * Listener interface to trigger an action once the initialization is done.
 *
 * @author Pasithea Software
 * @version 1.0
 *
 * @see PasitheaBuilder
 */
public interface onInitListener {
    /**
     * Set the action to start after the iniitialization.
     * Because this action occurs after the initialization, it can call an instance of PASITHEA.
     */
    void InitDone();
}