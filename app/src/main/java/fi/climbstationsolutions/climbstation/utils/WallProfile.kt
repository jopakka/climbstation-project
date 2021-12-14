package fi.climbstationsolutions.climbstation.utils

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import fi.climbstationsolutions.climbstation.R
import fi.climbstationsolutions.climbstation.database.ClimbProfileWithSteps
import fi.climbstationsolutions.climbstation.database.ClimbStep
import fi.climbstationsolutions.climbstation.database.SessionWithData
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
    private var outlineBounds: RectF? = null
    private val profilePath = Path()

    var profile: ClimbProfileWithSteps? = null
        set(value) {
            if (value == field) return
            field = value
            recreateProfile = true
            invalidate()
        }

    private var climbingProgression: Float = 0f
        set(value) {
            if (value == field) return
            field = when {
                value > 100f -> 100f
                value < 0f -> 0f
                else -> value
            }
            invalidate()
        }

    var sessionWithData: SessionWithData? = null
        set(value) {
            if (field == value) return
            field = value
            invalidate()
        }

    private var wallColor: Int
    private var wallFillColor: Int
    private var wallOutlineColor: Int
    private var wallOutlineThickness: Float = 0f
        set(value) {
            if (value < 0f) return
            field = value
        }

    init {
        val typedArray = context.theme.obtainStyledAttributes(attrs, R.styleable.WallProfile, 0, 0)
        try {
            wallColor = typedArray.getColor(R.styleable.WallProfile_wallColor, Color.DKGRAY)
            wallOutlineColor =
                typedArray.getColor(R.styleable.WallProfile_wallOutlineColor, Color.WHITE)
            wallOutlineThickness =
                typedArray.getFloat(R.styleable.WallProfile_wallOutlineThickness, 00f)
            wallFillColor = typedArray.getColor(
                R.styleable.WallProfile_wallFillColor,
                ContextCompat.getColor(context, R.color.climbstation_red)
            )
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
            climbingProgression = calculateProgression()
            drawPath(profilePath, outlinePaint())
            drawPath(profilePath, gradientPaint())
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

                scalePathToFitScreen(this)
                flipPathAround(this)
                movePathToVisible(this)
                movePathMiddleOfView(canvas, this)
                fillPathToScreenEdge(this)
            }
        }
    }

    /**
     * Calculates xy coordinates for next step in path and then draws it and
     * returns those coordinates
     */
    private fun calculateAndDrawLine(p: Path, s: ClimbStep): Pair<Float, Float> {
        val dist = if (s.distance == 0) 1 else s.distance
        val a = (cos(Math.toRadians(-s.angle.toDouble())) * dist).toFloat()
        val b = (sin(Math.toRadians(-s.angle.toDouble())) * dist).toFloat()
        p.rLineTo(b, a)
        return Pair(a, b)
    }

    /**
     * Scales [path] to fit view
     */
    private fun scalePathToFitScreen(path: Path) {
        outlineBounds = pathBounds(path).also {
            val ratio = min(
                width / (it.width()),
                height / (it.height())
            )

            val scaleBiggerMatrix = Matrix()
            scaleBiggerMatrix.setScale(ratio, ratio)
            path.transform(scaleBiggerMatrix)
        }

        outlineBounds = pathBounds(path).also {
            val ratio = (it.height() - wallOutlineThickness) / it.height()

            val scaleBiggerMatrix = Matrix()
            scaleBiggerMatrix.setScale(ratio, ratio)
            path.transform(scaleBiggerMatrix)
        }
        outlineBounds = pathBounds(path)
    }

    /**
     * Flips [path] around its X-axis
     */
    private fun flipPathAround(path: Path) {
        outlineBounds = pathBounds(path).also {
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
        outlineBounds = pathBounds(path).also {
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
    private fun movePathMiddleOfView(canvas: Canvas, path: Path) {
        outlineBounds = pathBounds(path).also {
            val cBound = RectF(canvas.clipBounds)

            // Move profilePath to middle of view
            val moveMatrix = Matrix()
            val newX = (cBound.width() - it.width()) / 2f
            val newY = (cBound.height() - it.height()) / 2f
            moveMatrix.setTranslate(newX, newY)
            path.transform(moveMatrix)

            wallStartX = newX
        }
    }

    /**
     * Fills start of view
     */
    private fun fillPathToScreenEdge(path: Path) {
        path.apply {
            val x = wallOutlineThickness / 2f
            lineTo(x, x)
            rLineTo(x, outlineBounds?.height() ?: 0f)
            close()
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
            val mHeight = outlineBounds?.height()?.plus(outlineBounds?.top ?: 0f) ?: 0f
            val progress =
                mHeight * (100f - climbingProgression) / 100f + (wallOutlineThickness / 2)
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

    private fun calculateProgression(): Float {
        val max = profile?.steps?.sumOf { it.distance }?.toFloat() ?: return climbingProgression
        val total = sessionWithData?.data?.lastOrNull()?.totalDistance?.div(1000f)
            ?: return climbingProgression
        return total / max
    }

    private fun outlinePaint(): Paint {
        return Paint(Paint.ANTI_ALIAS_FLAG).apply {
            this.color = wallOutlineColor
            this.strokeWidth = wallOutlineThickness
            this.style = Paint.Style.FILL_AND_STROKE
        }
    }
}