package com.passbasedemo.detectme.presenter.ui

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.PointF
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.util.Size
import android.widget.Toast
import androidx.annotation.GuardedBy
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import com.otaliastudios.cameraview.*
import com.otaliastudios.cameraview.controls.Facing
import com.otaliastudios.cameraview.controls.Mode
import com.otaliastudios.cameraview.controls.Preview
import com.passbasedemo.detectme.R
import com.passbasedemo.detectme.facedetection.FaceDetector
import com.passbasedemo.detectme.facedetection.Frame
import com.passbasedemo.detectme.facedetection.LensFacing
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File
import java.lang.Exception
import java.util.concurrent.locks.Lock

class MainActivity : AppCompatActivity() {
    private val PERMISSION_REQUEST= 1
    private val lock = Object()
    @GuardedBy("lock")
    private var isProcessing = false
    companion object {
        private val LOG = CameraLogger.create("DetectMe")
        private val TAG = "DetectMe"

    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        if (!allPermissionGranted()){
            getRunTimePermissions()
        }


    }

    override fun onResume() {
        super.onResume()
        videoPreviewButton.isEnabled = false
        setupCamera()
        videoPreviewButton.setOnClickListener {
            val intent = Intent(this@MainActivity,VideoPreviewActivity::class.java)
            startActivity(intent)
        }
    }



    private fun setupCamera() {
        camera.setLifecycleOwner(this)
        camera.addCameraListener(Listener())
        val faceDetector = FaceDetector(faceBoundsOverlay)
        faceDetector.viewState.observe(this, Observer {
              if (it == null){
                  return@Observer
              }
             if (it){
                 synchronized(lock){
                     if (!isProcessing){
                         Log.e(TAG,"start video...")
                         isProcessing = true
                         startCaptureVideo()
                     }
                 }
             }
        })
        camera.facing = Facing.FRONT
        camera.addFrameProcessor {
            faceDetector.process(
                Frame(
                    data = it.getData<ByteArray>(),
                    rotation = it.rotationToUser,
                    size = Size(it.size.width, it.size.height),
                    format = it.format,
                    lensFacing =  LensFacing.FRONT
                )
            )
        }

    }

    private fun startCaptureVideo(){
        camera.mode = Mode.VIDEO
        captureVideoSnapshot()
    }

    private fun captureVideoSnapshot() {
        if (camera.isTakingVideo) return run {
            message("Already taking video.", false)
        }
        if (camera.preview != Preview.GL_SURFACE) return run {
            message("Video snapshots are only allowed with the GL_SURFACE preview.", true)
        }
        camera.takeVideoSnapshot(File(filesDir, "video.mp4"), 3000)
    }

    private inner class Listener : CameraListener() {

        override fun onCameraError(exception: CameraException) {
            super.onCameraError(exception)
           // message("Got CameraException #" + exception.reason, true)
        }


        override fun onVideoTaken(result: VideoResult) {
            super.onVideoTaken(result)
            isProcessing = false
            VideoPreviewActivity.videoResult = result
            checkView.uncheck()
            LOG.w("onVideoTaken called! .")

        }

        override fun onVideoRecordingStart() {
            super.onVideoRecordingStart()
            checkView.check()
            LOG.w("onVideoRecordingStart!")
        }

        override fun onVideoRecordingEnd() {
            super.onVideoRecordingEnd()
            //message("Video taken. Processing...", false)
            LOG.w("onVideoRecordingEnd!")
        }

        override fun onExposureCorrectionChanged(newValue: Float, bounds: FloatArray, fingers: Array<PointF>?) {
            super.onExposureCorrectionChanged(newValue, bounds, fingers)
           // message("Exposure correction:$newValue", false)
        }

        override fun onZoomChanged(newValue: Float, bounds: FloatArray, fingers: Array<PointF>?) {
            super.onZoomChanged(newValue, bounds, fingers)
           // message("Zoom:$newValue", false)
        }
    }

    private fun message(content: String, important: Boolean) {
        if (important) {
            LOG.w(content)
            Toast.makeText(this, content, Toast.LENGTH_LONG).show()
        } else {
            LOG.i(content)
            Toast.makeText(this, content, Toast.LENGTH_SHORT).show()
        }
    }

    private fun getRunTimePermissions() {
        val allNeededPermission: MutableList<String?> = ArrayList()
        for (permission in getRequiredPermission()!!) {
            if (!permission?.let {
                    isPermissionGranted(
                        this, it
                    )
                }!!) {
                allNeededPermission.add(permission)
            }
        }
        if (!allNeededPermission.isEmpty()) {
            ActivityCompat.requestPermissions(
                this,
                allNeededPermission.toTypedArray(),
                PERMISSION_REQUEST
            )
        }
    }

    private fun allPermissionGranted():Boolean{
        for (permission in this!!.getRequiredPermission()!!){
            if (!permission?.let {
                    isPermissionGranted(
                        this,it
                    )
                }!!){
                return false
            }
        }
        return true
    }

    private fun getRequiredPermission():Array<String?>?{
        return try{
            val info = this.packageManager
                .getPackageInfo(this.packageName, PackageManager.GET_PERMISSIONS)
            val ps = info.requestedPermissions
            if (ps != null && ps.size >0){
                ps
            }else{
                arrayOfNulls(0)
            }
        }catch (e:Exception){
            arrayOfNulls(0)
        }
    }

    private fun isPermissionGranted(
        context: Context,
        permission: String ):Boolean{
        if (ContextCompat.checkSelfPermission(context, permission)
            == PackageManager.PERMISSION_GRANTED
        ){
            Log.i(TAG,"Permission granted: $permission")
            return true
        }
        Log.i(TAG,"Permission not granted: $permission")
        return false
    }

}