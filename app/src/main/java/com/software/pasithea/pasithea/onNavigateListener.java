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
 * Listener interface to trigger the actions depending on the navigation keywords detected.
 *
 * @author PasitheaFromActivity Software
 * @version 1.0
 *
 * @see PasitheaFromActivity
 */

public interface onNavigateListener {
    /**
     * Method attached to the "PREVIOUS" keyword
     */
    void onNavPrevious();

    /**
     * Method attached to the "PREVIOUS_PART" keyword
     */
    void onNavPreviousPart();

    /**
     * Method attached to the "NEXT" keyword
     */
    void onNavNext();

    /**
     * Method attached to the "NEXT_PART" keyword
     */
    void onNavNextPart();

    /**
     * Method attached to the "QUIT" keyword
     */
    void onNavQuit();

    /**
     * Method attached to the "RESUME" keyword
     */
    void onNavResume();

    /**
     * Method attached to the "STOP" keyword
     */
    void onNavStop();

    /**
     * This method is not attached to a keyword and it is triggered only when the speech recognition engine didn't succeeded in the spoken word recognition.
     */
    void onNavUnk();
}
