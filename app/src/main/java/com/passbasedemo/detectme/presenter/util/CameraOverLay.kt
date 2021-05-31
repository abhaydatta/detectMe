package com.passbasedemo.detectme.presenter.util

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import com.passbasedemo.detectme.presenter.util.SharedPrefManager

class CameraOverLay@JvmOverloads constructor(ctx: Context, attrs: AttributeSet? = null) :
    View(ctx, attrs) {
    private val paint:Paint = Paint()
    private var cameraOverLayRect = RectF()

    init {
        paint.style = Paint.Style.STROKE
        paint.color = ContextCompat.getColor(context, android.R.color.holo_green_light)
        paint.strokeWidth = 10f
        SharedPrefManager.init(ctx)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        var x0:Float = canvas.width/2.0F
        var y0:Float = canvas.width/2.0F
        var dx:Float = canvas.width/5.0F
        var dy:Float = canvas.width/5.0F

        cameraOverLayRect = RectF(x0-dx, y0-dy,x0+dx,y0+dy)
        canvas.drawRect(cameraOverLayRect,paint)
        SharedPrefManager.write("x0", x0)
        SharedPrefManager.write("y0", y0)
        SharedPrefManager.write("dx", dx)
        SharedPrefManager.write("dy", dy)
    }

}