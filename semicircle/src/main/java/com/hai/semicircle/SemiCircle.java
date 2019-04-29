package com.hai.semicircle;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class SemiCircle extends View {
    private static final int MAX = 180;
    private final float DENSITY = getContext().getResources().getDisplayMetrics().density;

    private RectF mArcRect = new RectF();

    //Paints required for drawing
    private Paint mArcPaint;
    private Paint mArcProgressPaint;
    private Paint mRisePaint;
    private Paint mSetPaint;
    private Paint mHorizontalPaint;
    private Paint mDotPaint;

    //Arc related dimens
    private int mArcRadius = 0;
    private int mArcWidth = 2;
    private int mArcProgressWidth = 2;

    //Thumb Drawable
    private Drawable mThumb;

    //Thumb position related coordinates
    private int mTranslateX;
    private int mTranslateY;
    private int mThumbXPos;
    private int mThumbYPos;

    private String mSunriseTime;
    private String mSunsetTime;


    private int mAngle = 0;
    private boolean mEnabled = true;
    private int mTextTopMargin = 0;

    public SemiCircle(Context context) {
        super(context);
        init(context, null, 0);
    }

    public SemiCircle(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, R.attr.semicircleViewStyle);
    }

    public SemiCircle(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }

    private void init(Context context, AttributeSet attrs, int defStyle) {

        final Resources res = getResources();

        /*
          Defaults, may need to link this into theme settings
         */
        int arcColor = 0;
        int arcProgressColor = 0;
        int thumbHalfHeight = 0;
        int thumbHalfWidth = 0;
        int textSize = 0;
        int textColor = 0;
        int horizontalColor = 0;

//        mThumb = res.getDrawable(R.drawable.thumb_selector);

        /*
          Convert all default dimens to pixels for current density
         */
        mArcWidth = (int) (mArcWidth * DENSITY);
        mArcProgressWidth = (int) (mArcProgressWidth * DENSITY);

        if (attrs != null) {
            final TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.SemiCircle, defStyle, 0);

            Drawable thumb = array.getDrawable(R.styleable.SemiCircle_thumb);
            if (thumb != null) {
                mThumb = thumb;
            }

            thumbHalfHeight = mThumb.getIntrinsicHeight() / 2;
            thumbHalfWidth = mThumb.getIntrinsicWidth() / 2;
            mThumb.setBounds(-thumbHalfWidth, -thumbHalfHeight, thumbHalfWidth, thumbHalfHeight);

            //Dimensions
            mArcProgressWidth = (int) array.getDimension(R.styleable.SemiCircle_progressWidth, mArcProgressWidth);
            mArcWidth = (int) array.getDimension(R.styleable.SemiCircle_arcWidth, mArcWidth);

            //Integers
            mAngle = array.getInteger(R.styleable.SemiCircle_angle, mAngle);

            //Colors
            arcProgressColor = array.getColor(R.styleable.SemiCircle_arcColor, arcColor);
            arcColor = array.getColor(R.styleable.SemiCircle_arcProgressColor, arcProgressColor);

            //Boolean
            mEnabled = array.getBoolean(R.styleable.SemiCircle_enabled, mEnabled);

            textSize = (int) array.getDimension(R.styleable.SemiCircle_timeTextSize, textSize);
            textColor = array.getColor(R.styleable.SemiCircle_timeTextColor, textColor);

            mSunriseTime = array.getString(R.styleable.SemiCircle_sunriseTime);
            mSunsetTime = array.getString(R.styleable.SemiCircle_sunsetTime);
            horizontalColor = array.getColor(R.styleable.SemiCircle_horizontalColor, horizontalColor);
            mTextTopMargin = (int) array.getDimension(R.styleable.SemiCircle_timeTextTopMargin, mTextTopMargin);

            array.recycle();
        }
        /*
          Creating and configuring the paints as  required.
         */
        mAngle = (mAngle > MAX) ? MAX : ((mAngle < 0) ? 0 : mAngle);

        mArcPaint = new Paint();
        mArcPaint.setColor(arcColor);
        mArcPaint.setAntiAlias(true);
        mArcPaint.setStyle(Paint.Style.STROKE);
        mArcPaint.setStrokeWidth(mArcWidth);
        mArcPaint.setPathEffect(new DashPathEffect(new float[]{5, 7}, 0));

        mArcProgressPaint = new Paint();
        mArcProgressPaint.setColor(arcProgressColor);
        mArcProgressPaint.setAntiAlias(true);
        mArcProgressPaint.setStyle(Paint.Style.STROKE);
        mArcProgressPaint.setStrokeWidth(mArcProgressWidth);
        mArcProgressPaint.setPathEffect(new DashPathEffect(new float[]{5, 7}, 0));

        mRisePaint = new Paint();
        mSetPaint = new Paint();
        mRisePaint.setAntiAlias(true);
        mSetPaint.setAntiAlias(true);
        mRisePaint.setStyle(Paint.Style.STROKE);
        mSetPaint.setStyle(Paint.Style.STROKE);
        mSetPaint.setColor(textColor);
        mRisePaint.setColor(textColor);
        mRisePaint.setTextSize(textSize);
        mSetPaint.setTextSize(textSize);

        mHorizontalPaint = new Paint();
        mHorizontalPaint.setAntiAlias(true);
        mHorizontalPaint.setStyle(Paint.Style.STROKE);
        mHorizontalPaint.setStrokeWidth(1);
        mHorizontalPaint.setColor(horizontalColor);

        mDotPaint = new Paint();
        mDotPaint.setAntiAlias(true);
        mDotPaint.setStyle(Paint.Style.FILL);
        mDotPaint.setColor(arcColor);

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        int height = getDefaultSize(getSuggestedMinimumHeight(),
                heightMeasureSpec);
        int width = getDefaultSize(getSuggestedMinimumWidth(),
                widthMeasureSpec);
        int min = Math.min(width, height);
        //width = min;
        height = min / 2;


        float top;
        float left;
        int arcDiameter = min;


        arcDiameter = (int) (arcDiameter - 2 * 20 * DENSITY);
        mArcRadius = arcDiameter / 2;


        top = height - (mArcRadius);
        left = width / 2 - mArcRadius;

        mArcRect.set(left, top, left + arcDiameter, top + arcDiameter);

        mTranslateX = (int) mArcRect.centerX();
        mTranslateY = (int) mArcRect.centerY();


        int thumbAngle = mAngle;
        mThumbXPos = (int) (mArcRadius * Math.cos(Math.toRadians(thumbAngle)));
        mThumbYPos = (int) (mArcRadius * Math.sin(Math.toRadians(thumbAngle)));

        setMeasuredDimension(width, (int) (height*1.3));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.save();
        canvas.scale(1, -1, mArcRect.centerX(), mArcRect.centerY());
        canvas.drawArc(mArcRect, mAngle, MAX - mAngle, false, mArcPaint);
        canvas.drawArc(mArcRect, 0, mAngle, false, mArcProgressPaint);

        canvas.restore();

        canvas.save();
        canvas.translate(mArcRect.centerX(), mArcRect.centerY());
        canvas.drawText(mSunsetTime, mArcRadius - mRisePaint.measureText(mSunriseTime)/2, mTextTopMargin, mRisePaint);
        canvas.drawText(mSunriseTime, -mArcRadius - mSetPaint.measureText(mSunsetTime)/2, mTextTopMargin, mSetPaint);
        canvas.drawLine(-mArcRadius*1.5f, 0, mArcRadius*1.5f, 0, mHorizontalPaint);
        canvas.drawCircle(-mArcRadius, 0 , 7, mDotPaint);
        canvas.drawCircle(mArcRadius, 0 , 7, mDotPaint);
        canvas.restore();

        if (mEnabled) {
            // Draw the thumb nail
            canvas.save();
            canvas.scale(-1, 1, mArcRect.centerX(), mArcRect.centerY());
            canvas.translate(mTranslateX - mThumbXPos, mTranslateY - mThumbYPos);
            mThumb.draw(canvas);
            canvas.restore();
        }

    }

    @Override
    protected void drawableStateChanged() {
        super.drawableStateChanged();
        if (mThumb != null && mThumb.isStateful()) {
            int[] state = getDrawableState();
            mThumb.setState(state);
        }
        invalidate();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return false;
    }

    private void updateThumbPosition() {
        int thumbAngle = mAngle; //(int) (mStartAngle + mProgressSweep + mRotation + 90);
        mThumbXPos = (int) (mArcRadius * Math.cos(Math.toRadians(thumbAngle)));
        mThumbYPos = (int) (mArcRadius * Math.sin(Math.toRadians(thumbAngle)));
    }

    private void onProgressRefresh(int angle) {
        updateAngle(angle, false);
    }

    private void updateAngle(int angle, boolean fromUser) {
        mAngle = (angle > MAX) ? MAX : (angle < 0) ? 0 : angle;
        updateThumbPosition();
        invalidate();
    }


    public void setAngle(int angle) {
        this.mAngle = 180 - angle;
        onProgressRefresh(mAngle);
    }

    public void setEnabled(boolean enabled) {
        this.mEnabled = enabled;
        invalidate();
    }

    public void setSunriseTime(String mSunriseTime) {
        this.mSunriseTime = mSunriseTime;
    }

    public void setSunsetTime(String mSunsetTime) {
        this.mSunsetTime = mSunsetTime;
    }
}
