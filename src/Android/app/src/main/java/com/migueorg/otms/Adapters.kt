package com.migueorg.otms

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioPlaybackCaptureConfiguration
import android.media.AudioRecord
import android.media.projection.MediaProjection
import android.media.projection.MediaProjectionManager
import android.nfc.NdefMessage
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.os.IBinder
import android.os.Parcelable
import android.util.Log
import androidx.core.app.NotificationCompat
import java.io.File
import java.io.FileOutputStream
import java.io.UnsupportedEncodingException
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress
import java.nio.charset.Charset
import kotlin.concurrent.thread
import kotlin.experimental.and

class Adapters: Ports, Service() {


    override val SERVICE_ID: Int = 1318
    override val NOTIFICATION_CHANNEL_ID: String = "OTMS"

    override lateinit var mediaProjectionManager: MediaProjectionManager
    override lateinit var audioCaptureThread: Thread

    override val SAMPLE_RATE: Int = 16000
    override val CHANNEL_CONFIG: Int = AudioFormat.CHANNEL_IN_MONO
    override val AUDIO_FORMAT: Int = AudioFormat.ENCODING_PCM_16BIT

    override val BUFFER_SIZE_IN_BYTES: Int = 2048
    override val BUFFER_SIZE_RECORDING: Int = AudioRecord.getMinBufferSize(SAMPLE_RATE, CHANNEL_CONFIG, AUDIO_FORMAT)
    private val PORT: Int = 49200


    private var bufferSize: Int = 8192
    private var buffer: ByteArray = ByteArray(bufferSize)

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
    override fun capturarAudio(mediaProjection: MediaProjection): AudioRecord {
        val config = AudioPlaybackCaptureConfiguration.Builder(mediaProjection!!)
            .addMatchingUsage(AudioAttributes.USAGE_MEDIA)
            .build()

        val audioFormat = AudioFormat.Builder()
            .setEncoding(AUDIO_FORMAT)
            .setSampleRate(SAMPLE_RATE)
            .setChannelMask(CHANNEL_CONFIG)
            .build()

        var audioRecord = AudioRecord.Builder()
            .setAudioFormat(audioFormat)
            .setBufferSizeInBytes(BUFFER_SIZE_IN_BYTES)
            .setAudioPlaybackCaptureConfig(config)
            .build()

        return audioRecord!!

    }

    override fun enviarAudio(audioInterno: AudioRecord, ip: String) {
        audioInterno!!.startRecording()

        val socket = DatagramSocket()
        var packet: DatagramPacket
        val receptor = InetAddress.getByName(ip)

        audioCaptureThread = thread(start = true) {

            while (!audioCaptureThread.isInterrupted) {

                audioInterno.read(buffer, 0, buffer.size)

                packet = DatagramPacket(buffer, buffer.size, receptor, PORT)

                socket.send(packet)
            }
        }
    }

    override fun lecturaNFC(intent: Intent): String {
        var ip = ""
        if (intent.action == NfcAdapter.ACTION_TECH_DISCOVERED) {
            ip = traduceNDEFaTexto(intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES))
        }
        return ip
    }

    override fun traduceNDEFaTexto(lecturaRaw: Array<Parcelable>?): String {

        var msgs = mutableListOf<NdefMessage>()
        var traduccion = ""

        if (lecturaRaw != null) {

            for (i in lecturaRaw.indices) {
                msgs.add(i, lecturaRaw[i] as NdefMessage)
            }

            val payload = msgs[0].records[0].payload
            val textEncoding: Charset = if ((payload[0] and 128.toByte()).toInt() == 0) Charsets.UTF_8 else Charsets.UTF_16 // Get the Text Encoding
            val languageCodeLength: Int = (payload[0] and 51).toInt() // Get the Language Code, e.g. "en"

            try {
                traduccion = String(
                    payload,
                    languageCodeLength + 1,
                    payload.size - languageCodeLength - 1,
                    textEncoding
                )
            } catch (e: UnsupportedEncodingException) {
                Log.e("UnsupportedEncoding", e.toString())
            }

        }

        return traduccion
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

    override fun onBind(p0: Intent?): IBinder? {
        TODO("Not yet implemented")
    }
}