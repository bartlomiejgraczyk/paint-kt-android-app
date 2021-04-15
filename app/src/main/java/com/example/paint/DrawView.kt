package com.example.paint

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.graphics.Paint.DITHER_FLAG
import android.util.AttributeSet
import android.util.DisplayMetrics
import android.view.MotionEvent
import android.view.View

@Suppress("DEPRECATION")
class DrawView(context: Context, attrs: AttributeSet) : View(context, attrs) {

    private var path: Path = Path()
    private var paint: Paint = Paint()
    private var point: PointF = PointF()
    private var paths: ArrayList<Draw> = ArrayList()
    private var currentColor: Int
    private var strokeWidth: Float
    private var isBlur: Boolean = false
    private var isEmboss: Boolean = false
    private var embossMask: MaskFilter
    private var blurMask: MaskFilter
    private lateinit var bitmap: Bitmap
    private lateinit var canvas: Canvas
    private var bitmapPaint: Paint = Paint(DITHER_FLAG)

    init {
        currentColor = Color.RED
        this.strokeWidth = 30.0f

        setupPaint()

        embossMask = EmbossMaskFilter(floatArrayOf(1f, 1f, 1f), .4f, 6f, 6f)
        blurMask = BlurMaskFilter(20f, BlurMaskFilter.Blur.NORMAL)
    }

    private fun setupPaint() {
        paint.isDither = true
        paint.color = currentColor
        paint.isAntiAlias = true
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = this.strokeWidth
        paint.xfermode = null
        paint.strokeCap = Paint.Cap.ROUND
        paint.strokeJoin = Paint.Join.ROUND
    }

    fun init(displayMetrics: DisplayMetrics) {
        val height: Int = displayMetrics.heightPixels
        val width: Int = displayMetrics.widthPixels
        this.bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        canvas = Canvas(bitmap)
    }

    fun normalMode() {
        isEmboss = false
        isBlur = false
    }

    fun embossMode() {
        isEmboss = true
        isBlur = false
    }

    fun blurMode() {
        isEmboss = false
        isBlur = true
    }

    fun clear() {
        paths.clear()
        path.reset()
        //normalMode()
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        canvas.save()
        canvas.drawColor(Color.WHITE)
        canvas.drawPath(path, paint)

        for (path: Draw in paths) {
            paint.color = path.color
            paint.strokeWidth = path.strokeWidth
            paint.maskFilter = null

            if (path.isEmboss) {
                paint.maskFilter = embossMask
            } else if (path.isBlur) {
                paint.maskFilter = blurMask
            }

            canvas.drawPath(path.path, paint)
        }

        canvas.drawBitmap(bitmap, 0f, 0f, bitmapPaint)
        canvas.restore()
    }

    private fun touchStart(x: Float, y: Float) {
        path = Path()
        val drawing = Draw(currentColor, isEmboss, isBlur, strokeWidth, path)
        paths.add(drawing)

        path.reset()
        path.moveTo(x, y)
        point.x = x
        point.y = y
    }

    private fun touchMove(x: Float, y: Float) {
        path.quadTo(point.x, point.y, (x + point.x) / 2, (y + point.y) / 2)
        point.x = x
        point.y = y
    }

    private fun touchUp() {
        path.lineTo(point.x, point.y)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if (event == null) {
            return false
        }

        val x: Float = event.x
        val y: Float = event.y

        when(event.action) {
            MotionEvent.ACTION_DOWN -> {
                touchStart(x, y)
                invalidate()
            }
            MotionEvent.ACTION_MOVE -> {
                touchMove(x, y)
                invalidate()
            }
            MotionEvent.ACTION_UP -> {
                touchUp()
                invalidate()
            }
        }

        return true
    }

    fun setStrokeSize(size: Int) {
        this.strokeWidth = size.toFloat()
    }

    fun setColor(color: Int) {
        this.currentColor = color
    }
}