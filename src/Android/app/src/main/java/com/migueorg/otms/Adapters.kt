package com.migueorg.otms

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioPlaybackCaptureConfiguration
import android.media.AudioRecord
import android.media.projection.MediaProjection
import android.media.projection.MediaProjectionManager
import android.os.IBinder
import androidx.core.app.NotificationCompat
import java.io.File
import java.io.FileOutputStream
import kotlin.concurrent.thread

class Adapters: Ports, Service() {


    override val SERVICE_ID: Int = 1318
    override val NOTIFICATION_CHANNEL_ID: String = "OTMS"

    override lateinit var mediaProjectionManager: MediaProjectionManager
    override lateinit var audioCaptureThread: Thread

    override val sampleRate: Int = 16000
    override val channelConfig: Int = AudioFormat.CHANNEL_IN_MONO
    override val audioFormat: Int = AudioFormat.ENCODING_PCM_16BIT

    override val BUFFER_SIZE_IN_BYTES: Int = 2048
    override val BUFFER_SIZE_RECORDING: Int = AudioRecord.getMinBufferSize(sampleRate, channelConfig, audioFormat)

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        startForeground(
            SERVICE_ID,
            NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID).build()
        )

        mediaProjectionManager =
            applicationContext.getSystemService(Context.MEDIA_PROJECTION_SERVICE) as MediaProjectionManager

    }
    override fun capturarAudio(mediaProjection2: MediaProjection): AudioRecord {
        val config = AudioPlaybackCaptureConfiguration.Builder(mediaProjection2!!)
            .addMatchingUsage(AudioAttributes.USAGE_MEDIA)
            .build()

        val audioFormat = AudioFormat.Builder()
            .setEncoding(audioFormat)
            .setSampleRate(sampleRate)
            .setChannelMask(channelConfig)
            .build()

        var audioRecord = AudioRecord.Builder()
            .setAudioFormat(audioFormat)
            .setBufferSizeInBytes(BUFFER_SIZE_IN_BYTES)
            .setAudioPlaybackCaptureConfig(config)
            .build()

        return audioRecord!!

    }

    override fun enviarAudio(audioInterno: AudioRecord, ip: String) {
        TODO("Not yet implemented")
    }

    override fun lecturaNFC(): String {
        TODO("Not yet implemented")
    }

    override fun onBind(p0: Intent?): IBinder? {
        TODO("Not yet implemented")
    }

    override fun grabarAudio(audioInterno: AudioRecord, outputFile: File){
        audioInterno!!.startRecording()
        audioCaptureThread = thread(start = true) {

            val data = ByteArray(BUFFER_SIZE_RECORDING / 2)
            var outputStream = FileOutputStream(outputFile)

            while (!audioCaptureThread.isInterrupted) {
                val read = audioInterno!!.read(data, 0, data.size)
                outputStream!!.write(data, 0, read)

            }
            outputStream!!.flush()
            outputStream!!.close()
        }
    }

    private fun createNotificationChannel() {
        val serviceChannel = NotificationChannel(
            NOTIFICATION_CHANNEL_ID,
            "OTMS",
            NotificationManager.IMPORTANCE_DEFAULT
        )

        val manager = getSystemService(NotificationManager::class.java) as NotificationManager
        manager.createNotificationChannel(serviceChannel)
    }
}