package com.passbasedemo.detectme.presenter.ui

import android.content.ContentValues
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.MediaController
import android.widget.Toast
import android.widget.VideoView
import androidx.core.content.FileProvider
import com.otaliastudios.cameraview.VideoResult
import com.otaliastudios.cameraview.size.AspectRatio
import com.passbasedemo.detectme.R
import kotlinx.android.synthetic.main.activity_video_preview.*
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.OutputStream

class VideoPreviewActivity : AppCompatActivity() {
    companion object {
        var videoResult: VideoResult? = null
    }

    private val videoView: VideoView by lazy { findViewById<VideoView>(R.id.video) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video_preview)
        val result = videoResult ?: run {
            finish()
            return
        }
        videoView.setOnClickListener { playVideo() }
        val controller = MediaController(this)
        controller.setAnchorView(videoView)
        controller.setMediaPlayer(videoView)
        videoView.setMediaController(controller)
        videoView.setVideoURI(Uri.fromFile(result.file))
        videoView.setOnPreparedListener { mp ->
            val lp = videoView.layoutParams
            val videoWidth = mp.videoWidth.toFloat()
            val videoHeight = mp.videoHeight.toFloat()
            val viewWidth = videoView.width.toFloat()
            lp.height = (viewWidth * (videoHeight / videoWidth)).toInt()
            videoView.layoutParams = lp
            playVideo()
            if (result.isSnapshot) {
                // Log the real size for debugging reason.
                Log.e("VideoPreview", "The video full size is " + videoWidth + "x" + videoHeight)
            }
        }
    }

    fun playVideo() {
        if (!videoView.isPlaying) {
            videoView.start()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (!isChangingConfigurations) {
            videoResult = null
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.save, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.share) {
           // Toast.makeText(this, "Sharing...", Toast.LENGTH_SHORT).show()
            val intent = Intent(Intent.ACTION_SEND)
            intent.type = "video/*"
            val uri = FileProvider.getUriForFile(this,
                this.packageName + ".provider",
                videoResult!!.file)
            intent.putExtra(Intent.EXTRA_STREAM, uri)
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            startActivity(intent)
            return true
        }
        if(item.itemId == R.id.save){
            saveImagetoStorage()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    private  fun saveImagetoStorage(
        fileName:String = "screenshot.mp4",
        mimeType: String = "video/mp4",
        directory:String = Environment.DIRECTORY_DOWNLOADS,
        mediaContentUri:Uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
    ){
        val videoOutStream:OutputStream
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q){
            val values = ContentValues().apply {
                put(MediaStore.Video.Media.DISPLAY_NAME,fileName)
                put(MediaStore.Video.Media.MIME_TYPE,mimeType)
                put(MediaStore.Video.Media.RELATIVE_PATH,directory)
            }

            contentResolver.run {
                val uri = contentResolver.insert(
                    mediaContentUri,
                    values
                )?:return
                videoOutStream = openOutputStream(uri) ?:return
            }
        }else{
            val videoPath = Environment.getExternalStoragePublicDirectory(directory).absolutePath
            val videoFile = File(videoPath,fileName)
            videoOutStream = FileOutputStream(videoFile)
        }
        val bufferSize = 4 * 1024
        val buffer = ByteArray(bufferSize)
        var byteRead = 0;
        val fileInput = FileInputStream( videoResult!!.file)
        while (fileInput.available() !== 0){
            byteRead = fileInput.read(buffer)
            videoOutStream.write(buffer,0,byteRead)
        }
    }
}