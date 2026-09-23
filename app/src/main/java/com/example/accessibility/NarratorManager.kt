package com.example.accessibility

import android.content.Context
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Locale

class NarratorManager(context: Context) : TextToSpeech.OnInitListener {

    private val appContext = context.applicationContext
    private var tts: TextToSpeech? = null
    private var isInitialized = false

    private val _isSpeaking = MutableStateFlow(false)
    val isSpeaking: StateFlow<Boolean> = _isSpeaking.asStateFlow()

    private val _isMuted = MutableStateFlow(false)
    val isMuted: StateFlow<Boolean> = _isMuted.asStateFlow()

    private val _speechRate = MutableStateFlow(0.85f) // 0.85x for optimal semi-literate comprehension
    val speechRate: StateFlow<Float> = _speechRate.asStateFlow()

    private val _autoNarrateScreens = MutableStateFlow(true)
    val autoNarrateScreens: StateFlow<Boolean> = _autoNarrateScreens.asStateFlow()

    private val _lastText = MutableStateFlow("Olá! Eu sou a Rê, sua narradora inteligente.")
    val lastText: StateFlow<String> = _lastText.asStateFlow()

    private var lastSpokenTime = 0L

    init {
        tts = TextToSpeech(appContext, this)
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            val result = tts?.setLanguage(Locale("pt", "BR"))
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                tts?.setLanguage(Locale.getDefault())
            }
            tts?.setSpeechRate(_speechRate.value)
            tts?.setPitch(1.05f) // Warm, pleasant, friendly voice

            tts?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                override fun onStart(utteranceId: String?) {
                    _isSpeaking.value = true
                }

                override fun onDone(utteranceId: String?) {
                    _isSpeaking.value = false
                }

                @Deprecated("Deprecated in Java")
                override fun onError(utteranceId: String?) {
                    _isSpeaking.value = false
                }

                override fun onError(utteranceId: String?, errorCode: Int) {
                    _isSpeaking.value = false
                    Log.e("NarratorManager", "TTS error code: $errorCode")
                }
            })
            isInitialized = true
        } else {
            Log.e("NarratorManager", "Failed to initialize TextToSpeech.")
        }
    }

    /**
     * Speaks text immediately, flushing any pending audio queue to prevent concurrent overlapping voices.
     */
    fun speak(text: String, isScreenChange: Boolean = false) {
        if (_isMuted.value) return
        if (isScreenChange && !_autoNarrateScreens.value) return

        val now = System.currentTimeMillis()
        // Debounce repeated clicks within 300ms of identical text
        if (text == _lastText.value && now - lastSpokenTime < 300L) {
            return
        }
        lastSpokenTime = now
        _lastText.value = text

        if (!isInitialized) return

        tts?.stop()
        tts?.setSpeechRate(_speechRate.value)
        val utteranceId = "re_voice_${System.currentTimeMillis()}"
        tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, utteranceId)
        _isSpeaking.value = true
    }

    fun stop() {
        tts?.stop()
        _isSpeaking.value = false
    }

    fun toggleMute(): Boolean {
        val newMute = !_isMuted.value
        _isMuted.value = newMute
        if (newMute) {
            stop()
        } else {
            speak("Voz da Rê ativada. Estou pronta para te ajudar!")
        }
        return newMute
    }

    fun setSpeechRate(rate: Float) {
        _speechRate.value = rate
        tts?.setSpeechRate(rate)
        speak("Velocidade ajustada.")
    }

    fun toggleAutoNarrate(): Boolean {
        val next = !_autoNarrateScreens.value
        _autoNarrateScreens.value = next
        if (next) {
            speak("Leitura automática de telas ativada.")
        } else {
            speak("Leitura automática pausada. Toque no botão de som sempre que quiser ouvir.")
        }
        return next
    }

    fun replayLast() {
        speak(_lastText.value)
    }

    fun shutdown() {
        tts?.stop()
        tts?.shutdown()
    }
}
