package com.migueorg.otms

import android.media.AudioRecord

interface Ports {

    /**
     * Método que devuelve el audio interno del dispositivo en un objeto AudioRecord
     */
    fun capturarAudio(): AudioRecord

    /**
     * Método que envía un objeto AudioRecord a una IP dada
     */
    fun enviarAudio(audioInterno: AudioRecord, ip: String)

    /**
     * Método que devuelve lo que lee desde el NFC
     */
    fun lecturaNFC(): String
}