package com.example.cornea.speech.data

data class VoiceToTextParserState(
    val spokenWord :String="",
    val isSpeaking : Boolean=false,
    val bgcolor:String="Black",
    val error:String?=null
)
