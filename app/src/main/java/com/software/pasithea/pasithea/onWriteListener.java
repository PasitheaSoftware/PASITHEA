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
 * Listener interface to trigger an action when the text transcript is return by the speech recognition engine.
 *
 * @author PasitheaFromActivity Software
 * @version 1.0
 *
 * @see Pasithea
 */

public interface onWriteListener {
    /**
     * Action to perform on the text returned by the speech recognition engine.
     * @param text Text transcript from the speech detected
     */
    void textDetected(String text);
}
