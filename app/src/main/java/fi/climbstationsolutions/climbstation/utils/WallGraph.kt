package fi.climbstationsolutions.climbstation.utils

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.Log
import android.view.View
import fi.climbstationsolutions.climbstation.R
import fi.climbstationsolutions.climbstation.network.profile.Profile
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

class WallGraph(context: Context, attrs: AttributeSet) : View(context, attrs) {
    companion object {
        private const val TAG = "WallGraph"
    }

    private var recreateProfile = true

    var profile: Profile? = null
        set(value) {
            if(value == field) return
            field = value
            recreateProfile = true
            invalidate()
        }

    var climbingProgression: Float = 0f
        set(value) {
            if(value == field) return
            field = when {
                value > 100f -> 100f
                value < 0f -> 0f
                else -> value
            }
            invalidate()
        }

    private var wallBounds: RectF? = null

    private val profilePath = Path()

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        canvas?.apply {
            drawColor(Color.GRAY)
            if(recreateProfile) {
                createProfilePath()
                scaleProfilePath(this)
            }
            drawPath(profilePath, gradientPaint())
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        // Try for a width based on our minimum
        val minw = MeasureSpec.getSize(widthMeasureSpec)+
                paddingStart + paddingEnd + suggestedMinimumWidth
        val w = resolveSizeAndState(minw, widthMeasureSpec, 0)

        val minh = MeasureSpec.getSize(heightMeasureSpec) +
                paddingBottom + paddingTop + suggestedMinimumHeight
        val h = resolveSizeAndState(minh, heightMeasureSpec, 0)

        setMeasuredDimension(w, h)

    }

    private fun createProfilePath() {
        val startX = 0f
        val startY = 0f
        val spaceLeft = 0f

        profile?.let {
            recreateProfile = false
            var xDistance = 0f
            var yDistance = 0f
            var minX = Float.MAX_VALUE

            profilePath.apply {
                reset()
                moveTo(startX + spaceLeft, startY)

                it.steps.forEach { s ->
                    val a = (cos(Math.toRadians(-s.angle.toDouble())) * s.distance).toFloat()
                    val b = (sin(Math.toRadians(-s.angle.toDouble())) * s.distance).toFloat()
                    xDistance += b
                    yDistance += a
                    if(xDistance < minX) minX = xDistance
                    rLineTo(b, a)
                }

                when {
                    xDistance >= 0f && minX >= 0f -> rLineTo(-xDistance - spaceLeft, 0f)
                    xDistance >= 0f && minX < 0f -> rLineTo(minX - xDistance - spaceLeft, 0f)
                    else -> rLineTo(-spaceLeft, 0f)
                }

                rLineTo(0f, -yDistance)
            }
        }
    }

    private fun scaleProfilePath(canvas: Canvas) {
        wallBounds = pathBounds(profilePath).also {
            val cBounds = RectF(canvas.clipBounds)

            val ratio = min(
                cBounds.width() / it.width(),
                cBounds.height() / it.height()
            )

            val scaleBiggerMatrix = Matrix()
            scaleBiggerMatrix.setScale(ratio, ratio)
            profilePath.transform(scaleBiggerMatrix)
        }

        wallBounds = pathBounds(profilePath).also {
            val scaleAround = Matrix()
            scaleAround.setScale(1f, -1f, it.centerX(), it.centerY())
            profilePath.transform(scaleAround)

            val transformMatrix = Matrix()
            transformMatrix.setTranslate(if(it.left < 0f) abs(it.left) else 0f, if(it.top < 0f) abs(it.top) else 0f)
            profilePath.transform(transformMatrix)
        }
    }

    private fun pathBounds(path: Path): RectF {
        val rectF = RectF()
        path.computeBounds(rectF, true)
        return rectF
    }

    private val linePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.RED
    }

    private fun gradientPaint(): Paint {
        return Paint(Paint.ANTI_ALIAS_FLAG).apply {
            val mHeight = wallBounds?.height() ?: 0f
            val progress = mHeight * (100f - climbingProgression) / 100f
            val gradient = LinearGradient(0f, progress, 0f, progress + 1f, Color.DKGRAY, Color.GREEN, Shader.TileMode.CLAMP)
            this.isDither = true
            this.shader = gradient
        }
    }

}