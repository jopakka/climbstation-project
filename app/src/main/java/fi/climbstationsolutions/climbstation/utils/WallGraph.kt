package fi.climbstationsolutions.climbstation.utils

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.Log
import android.view.View
import fi.climbstationsolutions.climbstation.network.profile.Profile
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

class WallGraph(context: Context, attrs: AttributeSet) : View(context, attrs) {

    var profile: Profile? = null
        set(value) {
            field = value
            invalidate()
        }

    private val profilePath = Path()

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        canvas?.apply {
            drawColor(Color.GRAY)
            createProfilePath()
            scaleProfilePath(this)
            drawPath(profilePath, linePaint)
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val minW = suggestedMinimumWidth + paddingLeft + paddingStart
        val w = resolveSizeAndState(minW, widthMeasureSpec, 1)

        val minH = suggestedMinimumHeight + paddingTop + paddingBottom
        val h = resolveSizeAndState(minH, heightMeasureSpec, 0)

        setMeasuredDimension(w, h)
    }

    private fun createProfilePath() {
        val startX = 0f
        val startY = 0f
        val spaceLeft = 2f

        profile?.let {
            var xDistance = 0f
            var yDistance = 0f

            profilePath.apply {
                moveTo(startX + spaceLeft, startY)

                it.steps.forEach { s ->
                    val a = (cos(Math.toRadians(-s.angle.toDouble())) * s.distance).toFloat()
                    val b = (sin(Math.toRadians(-s.angle.toDouble())) * s.distance).toFloat()
                    xDistance += b
                    yDistance += a
                    rLineTo(b, a)
                }

                if(xDistance >= 0f) {
                    rLineTo(-xDistance - spaceLeft, 0f)
                    rLineTo(0f, -yDistance)
                } else {
                    rLineTo(-spaceLeft, 0f)
                    rLineTo(0f, -yDistance)
                }
            }
        }
    }

    private fun scaleProfilePath(canvas: Canvas) {
        var pBounds = pathBounds(profilePath)
        val cBounds = RectF(canvas.clipBounds)

        val ratio = min(
            cBounds.width() / pBounds.width(),
            cBounds.height() / pBounds.height()
        )

        val scaleBiggerMatrix = Matrix()
        scaleBiggerMatrix.setScale(ratio, ratio)
        profilePath.transform(scaleBiggerMatrix)

        pBounds = pathBounds(profilePath)

        val scaleAround = Matrix()
        scaleAround.setScale(1f, -1f, pBounds.centerX(), pBounds.centerY())
        profilePath.transform(scaleAround)

    }

    private fun pathBounds(path: Path): RectF {
        val rectF = RectF()
        path.computeBounds(rectF, true)
        return rectF
    }

    private val linePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.RED
    }

}