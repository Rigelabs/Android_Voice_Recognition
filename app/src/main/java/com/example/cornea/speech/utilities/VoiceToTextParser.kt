package com.example.cornea.speech.utilities

import android.app.Application
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.util.Log
import com.example.cornea.speech.data.VoiceToTextParserState
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.util.Timer
import kotlin.concurrent.schedule

class VoiceToTextParser(private val app: Application):RecognitionListener {
    //create the local state and initialize using the data class
    private val _state = MutableStateFlow(VoiceToTextParserState())
    val state = _state.asStateFlow()

    //initialize the SpeechRecognizer and pass the application context
    private val speechRecognizer: SpeechRecognizer =SpeechRecognizer.createSpeechRecognizer(app)
    private fun initializeSpeechRecognizer(languageCode:String="en"): Intent {
        //check is SpeechRecognizer is not available and update the error to the state
        if(!SpeechRecognizer.isRecognitionAvailable(app)){
            _state.update { it.copy(error = "Speech Recognition is not available") }
        }
        //Start an activity that will prompt the user for speech and send it through a speech recognizer
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            //pass intent and pass Extra language model, in free form and provide the language code
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE,languageCode)
        }
        //sets the recognizer to the current instance
        speechRecognizer.setRecognitionListener(this)
        return intent
    }
    fun startListening(){
            //clear the state
        _state.update { VoiceToTextParserState() }

        //start listening and pass the intent
        speechRecognizer.startListening(initializeSpeechRecognizer())
        //update the state
        _state.update { it.copy(isSpeaking = true) }

    }
    fun stopListening(){
            _state.update { it.copy(isSpeaking = false) }
        speechRecognizer.stopListening()
    }
    override fun onReadyForSpeech(params: Bundle?) {
        _state.update { it.copy(error = null) }
    }

    override fun onBeginningOfSpeech() =Unit

    override fun onRmsChanged(rmsdB: Float)=Unit

    override fun onBufferReceived(buffer: ByteArray?) =Unit

    override fun onEndOfSpeech() {
            _state.update { it.copy(isSpeaking = false)}

            Log.i("speech_","end_speech")
            //start listening and pass the intent
                speechRecognizer.startListening(initializeSpeechRecognizer())
                //update the state
                _state.update { it.copy(isSpeaking = true) }

    }

    override fun onError(error: Int) {
        if(error==SpeechRecognizer.ERROR_CLIENT){
            return
        }
        _state.update { it.copy(error="Error $error") }
    }

    override fun onResults(results: Bundle?) {
        //Returns the value associated with the given key
        results
            //Key used to retrieve an ArrayList<String> from the Bundle
            ?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
            ?.getOrNull(0)
            ?.let { result ->
                _state.update { it.copy(spokenWord = result) }
                if(_state.value.spokenWord=="next"){
                    _state.update { it.copy(bgcolor = "White") }
                }else{
                    _state.update { it.copy(bgcolor = "Black") }
                }
            }


    }

    override fun onPartialResults(partialResults: Bundle?) =Unit
    override fun onEvent(eventType: Int, params: Bundle?) =Unit

}