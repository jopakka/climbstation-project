package fi.climbstationsolutions.climbstation.utils

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import fi.climbstationsolutions.climbstation.R
import fi.climbstationsolutions.climbstation.database.ClimbProfileWithSteps
import fi.climbstationsolutions.climbstation.database.ClimbStep
import fi.climbstationsolutions.climbstation.network.profile.Profile
import fi.climbstationsolutions.climbstation.network.profile.Step
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

/**
 * Custom view which draws [Profile]s [Step]s to [Canvas]
 *
 * @author Joonas Niemi
 */
class WallProfile(context: Context, attrs: AttributeSet) : View(context, attrs) {

    private var recreateProfile = true
    private var wallStartX = 0f
    private var wallBounds: RectF? = null
    private val profilePath = Path()

    var profile: ClimbProfileWithSteps? = null
        set(value) {
            if (value == field) return
            field = value
            recreateProfile = true
            postInvalidate()
        }

    var climbingProgression: Float = 0f
        set(value) {
            if (value == field) return
            field = when {
                value > 100f -> 100f
                value < 0f -> 0f
                else -> value
            }
            invalidate()
        }

    private var wallColor: Int
    private var wallFillColor: Int

    init {
        val typedArray = context.theme.obtainStyledAttributes(attrs, R.styleable.WallProfile, 0, 0)
        try {
            wallColor = typedArray.getColor(R.styleable.WallProfile_wallColor, Color.DKGRAY)
            wallFillColor = typedArray.getColor(R.styleable.WallProfile_wallFillColor, Color.GREEN)
        } finally {
            typedArray.recycle()
        }
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        canvas?.apply {
            if (recreateProfile) {
                createProfilePath(this)
            }
            val paint = gradientPaint()
            drawPath(profilePath, paint)
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        // Try for a width based on our minimum
        val minw = MeasureSpec.getSize(widthMeasureSpec) +
                paddingStart + paddingEnd + suggestedMinimumWidth
        val w = resolveSizeAndState(minw, widthMeasureSpec, 0)

        val minh = MeasureSpec.getSize(heightMeasureSpec) +
                paddingBottom + paddingTop + suggestedMinimumHeight
        val h = resolveSizeAndState(minh, heightMeasureSpec, 0)

        setMeasuredDimension(w, h)

    }

    /**
     * Creates new [profilePath]
     */
    private fun createProfilePath(canvas: Canvas) {
        val startX = 0f
        val startY = 0f

        profile?.let {
            recreateProfile = false
            var xDistance = 0f
            var yDistance = 0f
            var minX = Float.MAX_VALUE

            // Clears profilePath, then draw new one
            profilePath.apply {
                reset()
                moveTo(startX, startY)

                // Draws line for each step
                it.steps.forEach { s ->
                    val (a, b) = calculateAndDrawLine(this, s)
                    xDistance += b
                    yDistance += a
                    if (xDistance < minX) minX = xDistance
                }

                when {
                    // Negative wall
                    xDistance >= 0f && minX >= 0f -> rLineTo(-xDistance, 0f)
                    // Neutral wall
                    xDistance >= 0f && minX < 0f -> rLineTo(minX - xDistance, 0f)
                }

                scalePathToFitScreen(canvas, this)
                flipPathAround(this)
                movePathToVisible(this)
                movePathMiddleOfView(this)
                fillPathToScreenEdge(this)
            }
        }
    }

    /**
     * Calculates xy coordinates for next step in path and then draws it and
     * returns those coordinates
     */
    private fun calculateAndDrawLine(p: Path, s: ClimbStep): Pair<Float, Float> {
        val a = (cos(Math.toRadians(-s.angle.toDouble())) * s.distance).toFloat()
        val b = (sin(Math.toRadians(-s.angle.toDouble())) * s.distance).toFloat()
        p.rLineTo(b, a)
        return Pair(a, b)
    }

    /**
     * Scales [path] to fit view
     */
    private fun scalePathToFitScreen(canvas: Canvas, path: Path) {
        wallBounds = pathBounds(path).also {
            val cBounds = RectF(canvas.clipBounds)

            val ratio = min(
                cBounds.width() / it.width(),
                cBounds.height() / it.height()
            )

            val scaleBiggerMatrix = Matrix()
            scaleBiggerMatrix.setScale(ratio, ratio)
            path.transform(scaleBiggerMatrix)
        }
    }

    /**
     * Flips [path] around its X-axis
     */
    private fun flipPathAround(path: Path) {
        wallBounds = pathBounds(path).also {
            // Scale profilePath around
            val scaleAround = Matrix()
            scaleAround.setScale(1f, -1f, it.centerX(), it.centerY())
            path.transform(scaleAround)
        }
    }

    /**
     * Move [path] to fully visible
     */
    private fun movePathToVisible(path: Path) {
        wallBounds = pathBounds(path).also {
            val transformMatrix = Matrix()
            transformMatrix.setTranslate(
                if (it.left < 0f) abs(it.left) else 0f,
                if (it.top < 0f) abs(it.top) else 0f
            )
            path.transform(transformMatrix)
        }
    }

    /**
     * Moves [path] to middle of view
     */
    private fun movePathMiddleOfView(path: Path) {
        wallBounds = pathBounds(path).also {
            // Move profilePath to middle of view
            val moveMatrix = Matrix()
            val newX = (width.toFloat() - it.width()) / 2f
            moveMatrix.setTranslate(newX, 0f)
            path.transform(moveMatrix)

            wallStartX = newX
        }
    }

    /**
     * Fills start of view
     */
    private fun fillPathToScreenEdge(path: Path) {
        path.apply {
            lineTo(0f, 0f)
            rLineTo(0f, wallBounds?.height() ?: 0f)
        }
    }

    /**
     * Returns [path]s bounds
     */
    private fun pathBounds(path: Path): RectF {
        val rectF = RectF()
        path.computeBounds(rectF, true)
        return rectF
    }

    /**
     * Return gradient [Paint].
     * [climbingProgression] controls gradient fill amount
     */
    private fun gradientPaint(): Paint {
        return Paint(Paint.ANTI_ALIAS_FLAG).apply {
            val mHeight = wallBounds?.height() ?: 0f
            val progress = mHeight * (100f - climbingProgression) / 100f
            val gradient = LinearGradient(
                0f,
                progress,
                0f,
                progress + 1f,
                wallColor,
                wallFillColor,
                Shader.TileMode.CLAMP
            )
            this.isDither = true
            this.shader = gradient
        }
    }

}