package com.example.audio

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.sin
import kotlin.random.Random

class AmbientSoundEngine(private val scope: CoroutineScope) {

    data class MixerVolumes(
        val rain: Float = 0.4f,
        val brownNoise: Float = 0.0f,
        val forestWind: Float = 0.25f,
        val chimes: Float = 0.15f
    )

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying.asStateFlow()

    private val _volumes = MutableStateFlow(MixerVolumes())
    val volumes: StateFlow<MixerVolumes> = _volumes.asStateFlow()

    private val _equalizerBars = MutableStateFlow(List(14) { 0.15f })
    val equalizerBars: StateFlow<List<Float>> = _equalizerBars.asStateFlow()

    private var playJob: Job? = null
    private var barJob: Job? = null
    private var audioTrack: AudioTrack? = null

    fun setVolumes(update: (MixerVolumes) -> MixerVolumes) {
        _volumes.value = update(_volumes.value)
    }

    fun togglePlay() {
        if (_isPlaying.value) stop() else start()
    }

    fun start() {
        if (_isPlaying.value) return
        _isPlaying.value = true

        playJob = scope.launch(Dispatchers.Default) {
            val sampleRate = 22050
            val bufferFrames = 2048
            val minBuf = AudioTrack.getMinBufferSize(
                sampleRate,
                AudioFormat.CHANNEL_OUT_MONO,
                AudioFormat.ENCODING_PCM_16BIT
            ).coerceAtLeast(bufferFrames * 2)

            val track = AudioTrack.Builder()
                .setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .build()
                )
                .setAudioFormat(
                    AudioFormat.Builder()
                        .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                        .setSampleRate(sampleRate)
                        .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                        .build()
                )
                .setBufferSizeInBytes(minBuf)
                .setTransferMode(AudioTrack.MODE_STREAM)
                .build()

            audioTrack = track
            track.play()

            val samples = ShortArray(bufferFrames)
            var brownState = 0.0
            var windLp = 0.0
            var chimePhase = 0.0
            var chimeEnv = 0.0
            var sampleCounter = 0L

            while (isActive && _isPlaying.value) {
                val vol = _volumes.value
                for (i in samples.indices) {
                    sampleCounter++
                    val white = Random.nextDouble() * 2.0 - 1.0
                    brownState = (brownState + 0.03 * white).coerceIn(-1.0, 1.0) * 0.995
                    windLp = windLp * 0.985 + white * 0.015

                    if (sampleCounter % (sampleRate * 5L) == 0L && vol.chimes > 0.01f) {
                        chimeEnv = 1.0
                    }
                    chimePhase += 2.0 * PI * 528.0 / sampleRate
                    val chimeSample = sin(chimePhase) * chimeEnv
                    chimeEnv *= 0.9994

                    val rain = (white * 0.35 + (if (Random.nextFloat() < 0.002f) 0.45 else 0.0)) * vol.rain
                    val brown = brownState * 0.75 * vol.brownNoise
                    val wind = windLp * 2.4 * vol.forestWind
                    val chimes = chimeSample * 0.35 * vol.chimes

                    val mixed = (rain + brown + wind + chimes).coerceIn(-0.95, 0.95)
                    samples[i] = (mixed * Short.MAX_VALUE).toInt().toShort()
                }
                track.write(samples, 0, samples.size)
            }
            runCatching {
                track.stop()
                track.release()
            }
            audioTrack = null
        }

        barJob = scope.launch {
            while (isActive && _isPlaying.value) {
                val v = _volumes.value
                val energy = ((v.rain + v.brownNoise + v.forestWind + v.chimes) / 2.5f).coerceIn(0.1f, 1f)
                _equalizerBars.value = List(14) {
                    (0.15f + Random.nextFloat() * 0.85f * energy).coerceIn(0.1f, 1f)
                }
                delay(120L)
            }
        }
    }

    fun stop() {
        _isPlaying.value = false
        playJob?.cancel()
        barJob?.cancel()
        playJob = null
        barJob = null
        _equalizerBars.value = List(14) { 0.12f }
    }
}
