package com.example.cornea

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.example.cornea.speech.utilities.VoiceToTextParser
import com.example.cornea.ui.theme.CorneaTheme
import android.Manifest
import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Mic
import androidx.compose.material.icons.rounded.PlayCircle
import androidx.compose.material.icons.rounded.Stop
import androidx.compose.material3.Button
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerEvent
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.unit.dp

class MainActivity : ComponentActivity() {
    //initialize by lazy, to be implemented after the application is launched
    private val voiceTextParser by lazy { VoiceToTextParser(application) }

    @OptIn(ExperimentalFoundationApi::class)
    @SuppressLint("UnusedContentLambdaTargetStateParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            var canRecordAudio by remember {mutableStateOf(false)}
            val recordAudioLauncher = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.RequestPermission(),
                onResult = {isGranted-> canRecordAudio = isGranted}
            )
            LaunchedEffect(key1 = recordAudioLauncher) {
                recordAudioLauncher.launch(Manifest.permission.RECORD_AUDIO)
            }
            //Collects values from this StateFlow and represents its latest value via State.
            val voice_parser_state = voiceTextParser.state.collectAsState()
            var background=if(voice_parser_state.value.bgcolor=="Black")Color.Black else Color.White
            Scaffold(

            ) {padding->
                Column (
                    modifier= Modifier
                        .combinedClickable (
                            onClick = {Log.i("click","Clicked")},
                            onDoubleClick= {Log.i("click_d","Double Clicked")},
                            onLongClick= {Log.i("click_d","Long Clicked")}
                            )
                        .fillMaxSize()
                        .padding(padding)
                        .background(color=background)
                        .padding(20.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally

                    ) {
                    AnimatedContent(targetState =voice_parser_state.value.isSpeaking) {
                            if(voice_parser_state.value.isSpeaking){
                                Text("Speaking ....")
                            }else{
                                Log.i("speech_spoken",voice_parser_state.value.spokenWord)
                                Text( voice_parser_state.value.spokenWord.ifEmpty { "Click on Mic to record Audio" })
                            }
                    }
                    Button(onClick = {
                        if(voice_parser_state.value.isSpeaking){
                            voiceTextParser.stopListening()
                        }else
                            voiceTextParser.startListening()
                    }) {
                        AnimatedContent(targetState = voice_parser_state.value.isSpeaking) {isSpeaking->
                            if(isSpeaking){
                                Icon(imageVector= Icons.Rounded.Stop, contentDescription = "stop record")
                            }else{
                                Icon(imageVector= Icons.Rounded.PlayCircle, contentDescription = "start record")
                            }

                        }
                    }
                }

            }
        }
    }
}
