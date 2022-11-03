package com.shyptsolution.musicaddition

import android.Manifest
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.media.MediaPlayer
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.widget.Button
import android.widget.Toast
import androidx.loader.content.CursorLoader
import java.io.IOException
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        var button = findViewById<Button>(R.id.button)
        button.setOnClickListener {
            pickMusic()
        }
       checkPermission(Manifest.permission.READ_EXTERNAL_STORAGE,100)
    }

    private fun checkPermission(permission: String, requestCode: Int) {
        if (ContextCompat.checkSelfPermission(this@MainActivity, permission) == PackageManager.PERMISSION_DENIED) {

            ActivityCompat.requestPermissions(this@MainActivity, arrayOf(permission), requestCode)
        }
    }
    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>,
                                            grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 100) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this@MainActivity, "Camera Permission Granted", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this@MainActivity, "Camera Permission Denied", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun pickMusic() {

        val i = Intent(Intent.ACTION_PICK, MediaStore.Audio.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(i,10)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if(resultCode== RESULT_OK){
            if(requestCode==10){

                val selectedMusicUri: Uri? = data!!.data
                if (selectedMusicUri != null) {
                    Toast.makeText(this@MainActivity,getRealPathFromURI(this, selectedMusicUri).toString(),Toast.LENGTH_LONG).show()
                    val pathFromUri = getRealPathFromURI(this, selectedMusicUri)

                    try {
//                        mp.setDataSource(this, Uri.parse("android.resource://" + this.packageName + "/" + R.raw.music))
                        val mp = MediaPlayer()
                        mp.setDataSource(pathFromUri)
                        mp.prepare()
                        mp.start()
                    } catch (e: IOException) {
                        Toast.makeText(this@MainActivity,e.toString(),Toast.LENGTH_LONG).show()
                        e.printStackTrace()
                    }
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }


    private fun getRealPathFromURI(context: Context, contentUri: Uri): String {

        val projection = arrayOf(MediaStore.Audio.Media.DATA)
//        val projection = arrayOf(MediaStore.Files.getContentUri(contentUri.toString()))
        val loader = CursorLoader(context, contentUri, projection, null, null, null)
        val cursor: Cursor? = loader.loadInBackground()
        val column_index: Int = cursor!!.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)
        cursor.moveToFirst()
        return cursor.getString(column_index)
    }


}