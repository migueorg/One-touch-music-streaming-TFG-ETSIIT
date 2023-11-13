package com.migueorg.otms

import android.content.Intent
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.projection.MediaProjection
import android.media.projection.MediaProjectionManager
import android.os.Parcelable
import java.io.File

interface Ports {

    /**
     * Atributos necesarios
     */
    val SERVICE_ID: Int
    val NOTIFICATION_CHANNEL_ID: String
    var mediaProjectionManager: MediaProjectionManager

    var audioCaptureThread: Thread

    val SAMPLE_RATE: Int
    val CHANNEL_CONFIG: Int
    val AUDIO_FORMAT: Int
    val BUFFER_SIZE_IN_BYTES: Int
    val BUFFER_SIZE_RECORDING: Int


    /**
     * Método que devuelve el audio interno del dispositivo en un objeto AudioRecord
     */
    fun capturarAudio(mediaProjection: MediaProjection): AudioRecord

    /**
     * Método que envía un objeto AudioRecord a una IP dada
     */
    fun enviarAudio(audioInterno: AudioRecord, ip: String)

    /**
     * Método que escribe el audio de AudioRecord a un fichero dado
     */
    fun grabarAudio(audioInterno: AudioRecord, outputFile: File)

    /**
     * Método que devuelve lo que lee desde el NFC
     */
    fun lecturaNFC(intent: Intent): String

    /**
     * Método que traduce de estándar NDEF a texto legible
     */
    fun traduceNDEFaTexto(lecturaRaw: Array<Parcelable>?): String
}