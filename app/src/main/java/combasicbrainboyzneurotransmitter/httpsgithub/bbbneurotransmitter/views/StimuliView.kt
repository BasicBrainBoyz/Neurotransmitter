package combasicbrainboyzneurotransmitter.httpsgithub.bbbneurotransmitter.views

import android.content.Context
import android.content.res.Configuration
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.os.HandlerThread
import android.util.AttributeSet
import android.view.View

/**
 * Created by KJH-P on 2018-01-21.
 */

class StimuliView : View {

    private val BBB_BLUE: Int = Color.argb(255, 105,255,255)

    // Stimuli objects
    private var mStimuliLeft: Rect? = null
    private var mStimuliCenter: Rect? = null
    private var mStimuliRight: Rect? = null

    private var mStimuliLeftPaint: Paint? = null
    private var mStimuliCenterPaint: Paint? = null
    private var mStimuliRightPaint: Paint? = null

    constructor(context: Context) : super(context) {

        init(null)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {

        init(attrs)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {}

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes) {}

    fun stimuliFlashing() {
        if (mCounter % 5 == 0) {
            mStimuliLeftPaint!!.color = if (mStimuliLeftPaint!!.color == BBB_BLUE) Color.BLACK else BBB_BLUE
        }

        if (mCounter % 3 == 0) {
            mStimuliCenterPaint!!.color = if (mStimuliCenterPaint!!.color == BBB_BLUE) Color.BLACK else BBB_BLUE
        }

        if (mCounter % 2 == 0) {
            mStimuliRightPaint!!.color = if (mStimuliRightPaint!!.color == BBB_BLUE) Color.BLACK else BBB_BLUE
        }

        ++mCounter

        postInvalidate()
    }

    private fun init(set: AttributeSet?) {

        mCounter = 0

        //Configuration configuration = super.getResources().getConfiguration();
        //mScreenWidthDp = configuration.screenWidthDp; //The current width of the available screen space, in dp units, corresponding to screen width resource qualifier.
        mScreenWidthDp = 1180
        //int smallestScreenWidthDp = configuration.smallestScreenWidthDp; //The smallest screen size an application will see in normal operation, corresponding to smallest screen width resource qualifier

        val stimuliTop = 100

        mStimuliLeftTop = stimuliTop
        mStimuliCenterTop = stimuliTop
        mStimuliRightTop = stimuliTop
        mStimuliLeftBottom = stimuliTop + STIMULI_HEIGHT
        mStimuliCenterBottom = stimuliTop + STIMULI_HEIGHT
        mStimuliRightBottom = stimuliTop + STIMULI_HEIGHT

        mStimuliLeftLeft = mScreenWidthDp / 4 * 1 - STIMULI_WIDTH / 2
        mStimuliCenterLeft = mScreenWidthDp / 4 * 2 - STIMULI_WIDTH / 2
        mStimuliRightLeft = mScreenWidthDp / 4 * 3 - STIMULI_WIDTH / 2
        mStimuliLeftRight = mStimuliLeftLeft + STIMULI_WIDTH
        mStimuliCenterRight = mStimuliCenterLeft + STIMULI_WIDTH
        mStimuliRightRight = mStimuliRightLeft + STIMULI_WIDTH

        mStimuliLeft = Rect()
        mStimuliCenter = Rect()
        mStimuliRight = Rect()

        mStimuliLeftPaint = Paint()
        mStimuliCenterPaint = Paint()
        mStimuliRightPaint = Paint()

        mStimuliLeftPaint!!.color = BBB_BLUE
        mStimuliCenterPaint!!.color = BBB_BLUE
        mStimuliRightPaint!!.color = BBB_BLUE
    }

    override fun onDraw(canvas: Canvas) {
        //canvas.drawColor(Color.RED);
        //canvas.drawRect();

        mStimuliLeft!!.left = mStimuliLeftLeft
        mStimuliLeft!!.top = mStimuliLeftTop
        mStimuliLeft!!.right = mStimuliLeftRight
        mStimuliLeft!!.bottom = mStimuliLeftBottom

        mStimuliCenter!!.left = mStimuliCenterLeft
        mStimuliCenter!!.top = mStimuliCenterTop
        mStimuliCenter!!.right = mStimuliCenterRight
        mStimuliCenter!!.bottom = mStimuliCenterBottom

        mStimuliRight!!.left = mStimuliRightLeft
        mStimuliRight!!.top = mStimuliRightTop
        mStimuliRight!!.right = mStimuliRightRight
        mStimuliRight!!.bottom = mStimuliRightBottom

        canvas.drawRect(mStimuliLeft!!, mStimuliLeftPaint!!)
        canvas.drawRect(mStimuliCenter!!, mStimuliCenterPaint!!)
        canvas.drawRect(mStimuliRight!!, mStimuliRightPaint!!)
    }

    companion object {

        private var mCounter: Int = 0

        // Drawing constants for stimuli view
        private val STIMULI_WIDTH = 200
        private val STIMULI_HEIGHT = 100
        private var mScreenWidthDp: Int = 0

        // Top edge position for each stimuli
        private var mStimuliLeftTop: Int = 0
        private var mStimuliCenterTop: Int = 0
        private var mStimuliRightTop: Int = 0

        // Left edge position for each stimuli
        private var mStimuliLeftLeft: Int = 0
        private var mStimuliCenterLeft: Int = 0
        private var mStimuliRightLeft: Int = 0

        // Bottom edge position for each stimuli
        private var mStimuliLeftBottom: Int = 0
        private var mStimuliCenterBottom: Int = 0
        private var mStimuliRightBottom: Int = 0

        // Right edge position for each stimuli
        private var mStimuliLeftRight: Int = 0
        private var mStimuliCenterRight: Int = 0
        private var mStimuliRightRight: Int = 0

    }
}