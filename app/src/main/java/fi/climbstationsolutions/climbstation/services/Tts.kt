package fi.climbstationsolutions.climbstation.services

import android.content.Context
import android.speech.tts.TextToSpeech
import java.util.*

/**
 * Class which can be used to handle all [TextToSpeech] actions
 */
class Tts(context: Context) {
    private lateinit var textToSpeech: TextToSpeech
    private val locale = Locale.ENGLISH

    private val ttsInitListener = TextToSpeech.OnInitListener {
        if (it == TextToSpeech.SUCCESS) {
            textToSpeech.language = locale
        }
    }

    init {
        textToSpeech = TextToSpeech(context, ttsInitListener)
    }

    /**
     * [textToSpeech] will say [text]
     */
    fun speak(text: String) {
        textToSpeech.speak(text, TextToSpeech.QUEUE_ADD, null, null)
    }

    /**
     * Shutdown [textToSpeech]
     */
    fun destroy() {
        textToSpeech.shutdown()
    }
}