package com.passbasedemo.detectme.facedetection

import android.util.Size
import com.passbasedemo.detectme.facedetection.LensFacing

data class Frame(
    @Suppress("ArrayInDataClass") val data: ByteArray?,
    val rotation: Int,
    val size: Size,
    val format: Int,
    val lensFacing: LensFacing
)