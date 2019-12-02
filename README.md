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
  <img width="460" height="500" src="http://logicielpasithea.fr/img/Pasithea_Arch.png">
</p>

## Pasithea
Pasithea is the main entry point for the framework. The internal methods can be accessed only through it.\
It provides simple methods that expose the internal functions to the developer. Pasithea does not provide any GUI elements.

## Internal Functions With Speech-To-Text Engine
All the functions that use the speech-to-text have the same architecture model.
<p align="center">
  <img width="800" height="400" src="http://logicielpasithea.fr/img/Module_architecture.png">
</p>

## Internal functions With Text-To-Speech Engine
These kind of function is used to say a message or to read a text content.
<p align="center">
  <img width="800" height="400" src="http://logicielpasithea.fr/img/TTS_architecture.png">
</p>

For the details of the functions implementation you can refer to the sources or to the [documentation](http://logicielpasithea.fr/Pasithea/framework/documentation/)



