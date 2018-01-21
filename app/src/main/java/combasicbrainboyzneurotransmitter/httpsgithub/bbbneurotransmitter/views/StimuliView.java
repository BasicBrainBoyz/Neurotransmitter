package combasicbrainboyzneurotransmitter.httpsgithub.bbbneurotransmitter.views;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by KJH-P on 2018-01-21.
 */

public class StimuliView extends View {

    private static int mCounter;

    // Stimuli objects
    private Rect mStimuliLeft;
    private Rect mStimuliCenter;
    private Rect mStimuliRight;

    private Paint mStimuliLeftPaint;
    private Paint mStimuliCenterPaint;
    private Paint mStimuliRightPaint;

    // Drawing constants for stimuli view
    private static final int STIMULI_WIDTH = 200;
    private static final int STIMULI_HEIGHT = 100;
    private static int mScreenWidthDp;

    // Top edge position for each stimuli
    private static int mStimuliLeftTop;
    private static int mStimuliCenterTop;
    private static int mStimuliRightTop;

    // Left edge position for each stimuli
    private static int mStimuliLeftLeft;
    private static int mStimuliCenterLeft;
    private static int mStimuliRightLeft;

    // Bottom edge position for each stimuli
    private static int mStimuliLeftBottom;
    private static int mStimuliCenterBottom;
    private static int mStimuliRightBottom;

    // Right edge position for each stimuli
    private static int mStimuliLeftRight;
    private static int mStimuliCenterRight;
    private static int mStimuliRightRight;

    public StimuliView(Context context) {
        super(context);

        init(null);
    }

    public StimuliView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        init(attrs);
    }

    public StimuliView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public StimuliView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public void stimuliFlashing() {
        if(mCounter%5 == 0)
        {
            mStimuliLeftPaint.setColor(mStimuliLeftPaint.getColor() == Color.RED ? Color.WHITE : Color.RED);
        }

        if(mCounter%3 == 0)
        {
            mStimuliCenterPaint.setColor(mStimuliCenterPaint.getColor() == Color.RED ? Color.WHITE : Color.RED);
        }

        if(mCounter%2 == 0)
        {
            mStimuliRightPaint.setColor(mStimuliRightPaint.getColor() == Color.RED ? Color.WHITE : Color.RED);
        }

        ++mCounter;

        postInvalidate();
    }

    private void init(@Nullable AttributeSet set) {

        mCounter = 0;

        //Configuration configuration = super.getResources().getConfiguration();
        //mScreenWidthDp = configuration.screenWidthDp; //The current width of the available screen space, in dp units, corresponding to screen width resource qualifier.
        mScreenWidthDp = 1180;
        //int smallestScreenWidthDp = configuration.smallestScreenWidthDp; //The smallest screen size an application will see in normal operation, corresponding to smallest screen width resource qualifier

        int stimuliTop = 100;

        mStimuliLeftTop = stimuliTop;
        mStimuliCenterTop = stimuliTop;
        mStimuliRightTop = stimuliTop;
        mStimuliLeftBottom = stimuliTop + STIMULI_HEIGHT;
        mStimuliCenterBottom = stimuliTop + STIMULI_HEIGHT;
        mStimuliRightBottom = stimuliTop + STIMULI_HEIGHT;

        mStimuliLeftLeft = (((mScreenWidthDp/4) * 1) - (STIMULI_WIDTH/2));
        mStimuliCenterLeft = (((mScreenWidthDp/4) * 2) - (STIMULI_WIDTH/2));
        mStimuliRightLeft = (((mScreenWidthDp/4) * 3) - (STIMULI_WIDTH/2));
        mStimuliLeftRight = mStimuliLeftLeft + STIMULI_WIDTH;
        mStimuliCenterRight = mStimuliCenterLeft + STIMULI_WIDTH;
        mStimuliRightRight = mStimuliRightLeft + STIMULI_WIDTH;

        mStimuliLeft = new Rect();
        mStimuliCenter = new Rect();
        mStimuliRight = new Rect();

        mStimuliLeftPaint = new Paint();
        mStimuliCenterPaint = new Paint();
        mStimuliRightPaint = new Paint();

        mStimuliLeftPaint.setColor(Color.RED);
        mStimuliCenterPaint.setColor(Color.RED);
        mStimuliRightPaint.setColor(Color.RED);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        //canvas.drawColor(Color.RED);
        //canvas.drawRect();

        mStimuliLeft.left = mStimuliLeftLeft;
        mStimuliLeft.top = mStimuliLeftTop;
        mStimuliLeft.right = mStimuliLeftRight;
        mStimuliLeft.bottom = mStimuliLeftBottom;

        mStimuliCenter.left = mStimuliCenterLeft;
        mStimuliCenter.top = mStimuliCenterTop;
        mStimuliCenter.right = mStimuliCenterRight;
        mStimuliCenter.bottom = mStimuliCenterBottom;

        mStimuliRight.left = mStimuliRightLeft;
        mStimuliRight.top = mStimuliRightTop;
        mStimuliRight.right = mStimuliRightRight;
        mStimuliRight.bottom = mStimuliRightBottom;

        canvas.drawRect(mStimuliLeft, mStimuliLeftPaint);
        canvas.drawRect(mStimuliCenter, mStimuliCenterPaint);
        canvas.drawRect(mStimuliRight, mStimuliRightPaint);
    }
}
