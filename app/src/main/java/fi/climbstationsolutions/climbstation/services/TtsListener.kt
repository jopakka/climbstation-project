package fi.climbstationsolutions.climbstation.services

interface TtsListener {
    fun speak(text: String)
}