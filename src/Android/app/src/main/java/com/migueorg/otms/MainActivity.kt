package com.migueorg.otms

import android.Manifest
import android.content.ContentValues.TAG
import android.content.Intent
import android.content.pm.PackageManager
import android.media.AudioRecord
import android.media.projection.MediaProjection
import android.media.projection.MediaProjectionManager
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import java.io.File


class MainActivity : AppCompatActivity() {

    var foregroundService: Intent? = null
    var audio2: Adapters = Adapters()
    var audioRecord: AudioRecord? = null

    private val requestOnePermission = 
        registerForActivityResult(ActivityResultContracts.RequestPermission()){ 
            if (it) { 
                Log.d(TAG, "onActivityResult: PERMISSION GRANTED") 
            } else { 
                Log.e(TAG, "onActivityResult: PERMISSION DENIED") 
            } 
        } 


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        foregroundService = Intent(this, Adapters::class.java)

        startForegroundService(foregroundService) 

        if(!hasAudioPermission()){ 
            requestOnePermission.launch(Manifest.permission.RECORD_AUDIO) 
        }else{ 
            Toast.makeText(this@MainActivity, "Ya están los permisos dados.", Toast.LENGTH_SHORT).show() 
        } 

        requestPantalla() 
    }


    private fun hasAudioPermission(): Boolean { 
        return ActivityCompat.checkSelfPermission(this, 
            Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED 
    } 

    private fun requestPantalla(){ 
 
        val mediaProjectionManager = getSystemService(MediaProjectionManager::class.java) 
        var mediaProjection : MediaProjection 
        var file = File(getExternalFilesDir(null).toString()+"/"+"Capture.pcm") 



        val startMediaProjection = registerForActivityResult( 
            ActivityResultContracts.StartActivityForResult() 
        ) { result -> 
            if (result.resultCode == RESULT_OK) { 
                mediaProjection = mediaProjectionManager 
                    .getMediaProjection(result.resultCode, result.data!!) 
                Toast.makeText(this@MainActivity, "Pantalla aceptada correctamente", Toast.LENGTH_SHORT).show() 

                println("UBICACION: "+getExternalFilesDir(null).toString()+"/"+"Capture.pcm")

                audioRecord = audio2.capturarAudio(mediaProjection)
                audio2.grabarAudio(audioRecord!!,file)

            }
        }

        startMediaProjection.launch(mediaProjectionManager.createScreenCaptureIntent()) 
 
    } 

}