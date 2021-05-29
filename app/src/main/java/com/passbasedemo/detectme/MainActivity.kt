package com.passbasedemo.detectme

import android.content.Intent
import android.graphics.PointF
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Size
import android.view.ViewGroup
import android.widget.Toast
import com.otaliastudios.cameraview.*
import com.otaliastudios.cameraview.controls.Facing
import com.otaliastudios.cameraview.controls.Mode
import com.otaliastudios.cameraview.controls.Preview
import com.passbasedemo.detectme.facedetection.FaceDetector
import com.passbasedemo.detectme.facedetection.Frame
import com.passbasedemo.detectme.facedetection.LensFacing
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File

class MainActivity : AppCompatActivity() {

    companion object {
        private val LOG = CameraLogger.create("DetectMe")

    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setupCamera()

    }

    private fun setupCamera() {
        camera.setLifecycleOwner(this)
        camera.addCameraListener(Listener())
        val faceDetector = FaceDetector(faceBoundsOverlay)
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
        startVideoButton.setOnClickListener {
            camera.mode = Mode.VIDEO
            captureVideoSnapshot()
        }
    }

    private fun captureVideo() {
        if (camera.mode == Mode.PICTURE) return run {
            message("Can't record HQ videos while in PICTURE mode.", false)
        }
        if (camera.isTakingPicture || camera.isTakingVideo) return
        message("Recording for 5 seconds...", true)
        camera.takeVideo(File(filesDir, "video.mp4"), 5000)
    }

    private fun captureVideoSnapshot() {
        if (camera.isTakingVideo) return run {
            message("Already taking video.", false)
        }
        if (camera.preview != Preview.GL_SURFACE) return run {
            message("Video snapshots are only allowed with the GL_SURFACE preview.", true)
        }
        message("Recording snapshot for 5 seconds...", true)
        camera.takeVideoSnapshot(File(filesDir, "video.mp4"), 5000)
    }

    private inner class Listener : CameraListener() {

        override fun onCameraError(exception: CameraException) {
            super.onCameraError(exception)
            message("Got CameraException #" + exception.reason, true)
        }


        override fun onVideoTaken(result: VideoResult) {
            super.onVideoTaken(result)
            LOG.w("onVideoTaken called! .")

        }

        override fun onVideoRecordingStart() {
            super.onVideoRecordingStart()
            LOG.w("onVideoRecordingStart!")
        }

        override fun onVideoRecordingEnd() {
            super.onVideoRecordingEnd()
            message("Video taken. Processing...", false)
            LOG.w("onVideoRecordingEnd!")
        }

        override fun onExposureCorrectionChanged(newValue: Float, bounds: FloatArray, fingers: Array<PointF>?) {
            super.onExposureCorrectionChanged(newValue, bounds, fingers)
            message("Exposure correction:$newValue", false)
        }

        override fun onZoomChanged(newValue: Float, bounds: FloatArray, fingers: Array<PointF>?) {
            super.onZoomChanged(newValue, bounds, fingers)
            message("Zoom:$newValue", false)
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

}