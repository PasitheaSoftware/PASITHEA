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
 * Listener interface to trigger the actions on the answer detected.
 *
 * @author PasitheaFromActivity Software
 * @version 1.0
 *
 * @see Pasithea
 */

public interface onAnswerListener {
    /**
     * Method attached to the first keyword in the answerwords array.
     */
    void onAnswerYes();

    /**
     * Method attached to the second keyword in the answerwords array.
     */
    void onAnswerNo();

    /**
     * Methos not attached to a keyword. This method is called when the speech recognition engine didn't succeeded to recognize a keyword in the answerwords array.
     */
    void onAnswerUnk();
}
