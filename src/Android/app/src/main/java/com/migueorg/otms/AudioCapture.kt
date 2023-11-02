package com.migueorg.otms

import android.Manifest
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
import android.os.Environment
import android.os.IBinder
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import java.io.File
import java.io.FileOutputStream
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress
import kotlin.concurrent.thread
import kotlin.experimental.and

class AudioCapture : Service() {

    val SERVICE_ID = 1318
    val NOTIFICATION_CHANNEL_ID = "OTMS"
    private lateinit var mediaProjectionManager: MediaProjectionManager
    private var mediaProjection: MediaProjection? = null

    private lateinit var audioCaptureThread: Thread
    private var audioRecord: AudioRecord? = null

    private val sampleRate = 16000
    private val channelConfig = AudioFormat.CHANNEL_IN_MONO
    private val audioFormat = AudioFormat.ENCODING_PCM_16BIT
    private val BUFFER_SIZE_IN_BYTES = 2048
    private val NUM_SAMPLES_PER_READ = 1024

    private val BUFFER_SIZE_RECORDING = AudioRecord.getMinBufferSize(sampleRate, channelConfig, audioFormat)



    override fun onBind(p0: Intent?): IBinder? {
        TODO("Not yet implemented")
    }

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


    private fun createNotificationChannel() {
        val serviceChannel = NotificationChannel(
            NOTIFICATION_CHANNEL_ID,
            "OTMS",
            NotificationManager.IMPORTANCE_DEFAULT
        )

        val manager = getSystemService(NotificationManager::class.java) as NotificationManager
        manager.createNotificationChannel(serviceChannel)
    }

    public fun startAudioCapture(mediaProjection2: MediaProjection, file: File) {

        val config = AudioPlaybackCaptureConfiguration.Builder(mediaProjection2!!)
            .addMatchingUsage(AudioAttributes.USAGE_MEDIA)
            .build()

        val audioFormat = AudioFormat.Builder()
            .setEncoding(audioFormat)
            .setSampleRate(sampleRate)
            .setChannelMask(channelConfig)
            .build()

        audioRecord = AudioRecord.Builder()
            .setAudioFormat(audioFormat)
            .setBufferSizeInBytes(BUFFER_SIZE_IN_BYTES)
            .setAudioPlaybackCaptureConfig(config)
            .build()


        audioRecord!!.startRecording()
        audioCaptureThread = thread(start = true) {

            writeAudioToFile(file)
        }
    }

    private fun writeAudioToFile(outputFile: File) {
        val data = ByteArray(BUFFER_SIZE_RECORDING / 2)
        var outputStream = FileOutputStream(outputFile)

        while (!audioCaptureThread.isInterrupted) {
            val read = audioRecord!!.read(data, 0, data.size)
            outputStream!!.write(data, 0, read)

        }
        outputStream!!.flush()
        outputStream!!.close()
    }


}