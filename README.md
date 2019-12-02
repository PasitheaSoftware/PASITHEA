# PASITHEA
PASITHEA is a framework to add a vocal assistant in an Android app or service.\
This framework is based on the native Android API for the low-level mechanisms that carry out the speech recongition (SpeechRecongizer) and the text to speech (TextToSpeech).\
On top of these functions we created a set of methods to execute different types of operations. All these methods are customizables which makes the framework fully modular. It can adapt to all the kind of needs.

# Documentation
The framework documentation is available [online](http://logicielpasithea.fr/Pasithea/framework/documentation/)

# Google Developers Challenge
This prokect has been submitted to the Google Developers Challenge.\
The cover letter can be found in the sources or [here](https://github.com/PasitheaSoftware/PASITHEA/blob/1.1.0/Android%20Dev%20Challenge%20-%20Cover%20Letter.pdf)

# Architecture
<p align="center">
  <img width="300" height="450" src="http://logicielpasithea.fr/img/Pasithea_architecture.png">
</p>

## Pasithea
Pasithea is the main entry point for the framework. The internal methods can be accessed only through it.\
It provides simple methods that expose the internal functions to the developer. Pasithea does not provide any GUI elements.

## Internal Functions With Speech-To-Text Engine
All the functions that use the speech-to-text have the same architecture model.
<p align="center">
  <img width="800" height="400" src="http://logicielpasithea.fr/img/STT_architecture.png">
</p>

## Internal functions With Text-To-Speech Engine
These kind of function is used to say a message or to read a text content.
<p align="center">
  <img width="800" height="400" src="http://logicielpasithea.fr/img/TTS_architecture.png">
</p>

For the details of the functions implementation you can refer to the sources or to the [documentation](http://logicielpasithea.fr/Pasithea/framework/documentation/)

## Environment Management
We write a basic environment manager which take care of the audio management.\
The audiofocus is granted when PASITHEA is initialized. PASITHEA will release it when another app requires the audio (a phone call for example).\
PASITHEA manage automatically the sound volume based on the following method:

<b>IF (HEADSET is CONNECTED):</b>\
&nbsp;&nbsp;&nbsp;set sound volume to the half;\
<b>ELSE:</b>\
&nbsp;&nbsp;&nbsp;set sound volume to maximum;

The volume level is displayed when PASITHEA is initialized.  

# Sample Codes
We wrote two samples codes that available on github.

## Question/Answer Sample Code
This [basic sample code](https://github.com/PasitheaSoftware/QuestionAnswer-Demo) demonstrate how to use the Question/Answer function. 

## Full Sample Code
This [sample code](https://github.com/PasitheaSoftware/PASITHEA_FULL_SAMPLE_CODE) is more complex than the one above and it shows how to use all the PASITHEA functions.\
The documentation for this sample code is available [here](http://logicielpasithea.fr/Pasithea/SampleCode/documentation/)




