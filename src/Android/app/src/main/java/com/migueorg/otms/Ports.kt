package com.migueorg.otms

import android.media.AudioFormat
import android.media.AudioRecord
import android.media.projection.MediaProjection
import android.media.projection.MediaProjectionManager
import java.io.File

interface Ports {

    /**
     * Atributos necesarios
     */
    val SERVICE_ID: Int
    val NOTIFICATION_CHANNEL_ID: String
    var mediaProjectionManager: MediaProjectionManager

    var audioCaptureThread: Thread

    val sampleRate: Int
    val channelConfig: Int
    val audioFormat: Int
    val BUFFER_SIZE_IN_BYTES: Int

    val BUFFER_SIZE_RECORDING: Int


    /**
     * Método que devuelve el audio interno del dispositivo en un objeto AudioRecord
     */
    fun capturarAudio(mediaProjection2: MediaProjection): AudioRecord

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
    fun lecturaNFC(): String
}