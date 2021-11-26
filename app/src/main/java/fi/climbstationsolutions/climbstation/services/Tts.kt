package fi.climbstationsolutions.climbstation.services

import android.content.Context
import android.os.Bundle
import android.speech.tts.TextToSpeech
import java.util.*

class Tts(context: Context) {
    private lateinit var textToSpeech: TextToSpeech
    private val locale = Locale.ENGLISH

    private val ttsInitListener = TextToSpeech.OnInitListener {
        if(it == TextToSpeech.SUCCESS) {
            textToSpeech.language = locale
        }
    }

    init {
        textToSpeech = TextToSpeech(context, ttsInitListener)
    }

    fun speak(text: String) {
        textToSpeech.speak(text, TextToSpeech.QUEUE_ADD, null, null)
    }

    fun destroy() {
        textToSpeech.shutdown()
    }
}