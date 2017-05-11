package com.yx.yxcustomprogress;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.view.animation.LinearInterpolator;

import java.util.ArrayList;

public class ColorfulRingProgressView extends View {
    private float           mPercent        = 0f;//进度值默认为0 最大值为100
    private float           mStrokeWidth    = 0f;//圆环宽度
    private int             mBgColor        = 0xffe1e1e1;//默认背景颜色
    private float           mStartAngle     = 0f;//起点位置
    private float           mSweepAngle     = 0f;//起点位置
    private int             mFgColorStart   = 0xffffe400;//渐变开始颜色
    private int             mFgColorEnd     = 0xffff4800;//渐变结束颜色
    private LinearGradient  mShader         = null;//线性渐变
    private Context         mContext        = null;
    private RectF           mOval           = null;
    private Paint           mPaint          = null;
    private int             distance        = 100;//进度圈离边界的距离
    private String          mTitleText      = null;
    private int             mTitleTextColor = 0;
    private int             mTitleTextSize  = 0;
    private Rect            mTextBound      = null;
    private Paint           mTextPaint      = null;
    private float           startPercent    = 0;//上一次的进度
    private int             flag            = 0;//标记是第一次设置percent的值
    private ObjectAnimator anim = null;

    public ColorfulRingProgressView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext       = context;
        TypedArray a        = context.getTheme().obtainStyledAttributes(attrs, R.styleable.ColorfulRingProgressView, 0, 0);
        try {
            mBgColor        = a.getColor(R.styleable.ColorfulRingProgressView_bgColor, 0xffe1e1e1);
            mFgColorEnd     = a.getColor(R.styleable.ColorfulRingProgressView_fgColorEnd, 0xffff4800);
            mFgColorStart   = a.getColor(R.styleable.ColorfulRingProgressView_fgColorStart, 0xffffe400);
            mPercent        = a.getFloat(R.styleable.ColorfulRingProgressView_percent, 0);
            mStartAngle     = a.getFloat(R.styleable.ColorfulRingProgressView_startAngle, 0);
            mSweepAngle     = a.getFloat(R.styleable.ColorfulRingProgressView_sweepAngle, 360);
            mStrokeWidth    = a.getDimensionPixelSize(R.styleable.ColorfulRingProgressView_strokeWidth, dp2px(21));
            mTitleTextColor = a.getColor(R.styleable.ColorfulRingProgressView_mTitleTextColor, Color.RED);
            mTitleTextSize  = a.getDimensionPixelSize(R.styleable.ColorfulRingProgressView_mTitleTextSize, (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_PX, 20, getResources().getDisplayMetrics()));
            if(TextUtils.isEmpty(a.getString(R.styleable.ColorfulRingProgressView_mTitleText))){
                mTitleText  = "100";
            }else{
                mTitleText  = a.getString(R.styleable.ColorfulRingProgressView_mTitleText);
            }
        } finally {
            a.recycle();
        }
        init();
    }

    private void init() {
        startPercent = mPercent;
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(mStrokeWidth);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mTextBound = new Rect();
        mTextPaint = new Paint();
        mTextPaint.setTextSize(mTitleTextSize);
        mTextPaint.setColor(mTitleTextColor);
        // 计算描绘字体所需要的范围
        mTextPaint.getTextBounds(mTitleText, 0, mTitleText.length(), mTextBound);
    }

    private int dp2px(float dp) {
        return (int) (mContext.getResources().getDisplayMetrics().density * dp + 0.5f);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int centerX = getWidth() / 2;// 获得圆心的x坐标
        int centerY = getHeight() / 2;// 获得圆心的y坐标
        /** step1、画背景圈 */
        mPaint.setShader(null);
        mPaint.setColor(mBgColor);
        // 设定阴影 (柔边, X轴位移, Y轴位移, 阴影颜色)
        mPaint.setShadowLayer(2, 3, 3, 0x8e1b1a);
        canvas.drawArc(mOval, mStartAngle, mSweepAngle, false, mPaint);
        /** step2、画进度圈 */
        mPaint.setShader(mShader);
        canvas.drawArc(mOval, mStartAngle, mPercent * mSweepAngle/100f, false, mPaint);
        /** step3、画百分比 */
        mPaint.setAntiAlias(true);// 消除锯齿
        //计算文字的起始点
        //计算baseline（参考文献） http://blog.csdn.net/sirnuo/article/details/21165665
        Paint.FontMetrics fontMetrics = mTextPaint.getFontMetrics();
        float descentY = centerY + fontMetrics.descent;
        // 设置字符串的左边在屏幕的中间
        mTextPaint.setTextAlign(Paint.Align.CENTER);
        //根据要显示的百分比与圆圈进度的百分比之比来换算要显示的百分比
        canvas.drawText((int) (getPercent() * (Float.parseFloat(getmTitleText()) / startPercent)) + "%", centerX, descentY, mTextPaint);//100
    }
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        updateOval();

        mShader = new LinearGradient(mOval.left, mOval.top,
                mOval.left, mOval.bottom, mFgColorStart, mFgColorEnd, Shader.TileMode.MIRROR);
    }

    public float getPercent() {
        return mPercent;
    }

    public void setPercent(float mPercent,boolean isReset) {
        if(isReset) flag =0;
        this.mTitleText = (int)mPercent +"";
        this.mPercent = mPercent;
        if (flag == 0) {
            flag++;
            startPercent = mPercent;
        }
        refreshTheLayout();
    }


    public void setPercent(float mPercent) {
        this.mTitleText = (int)mPercent +"";
        this.mPercent = mPercent;
        if (flag == 0) {
            flag++;
            startPercent = mPercent;
        }
        refreshTheLayout();
    }

    public String getmTitleText() {
        return mTitleText;
    }

    public float getStrokeWidth() {
        return mStrokeWidth;
    }

    public void setStrokeWidth(float mStrokeWidth) {
        this.mStrokeWidth = mStrokeWidth;
        mPaint.setStrokeWidth(mStrokeWidth);
        updateOval();
        refreshTheLayout();
    }

    private void updateOval() {
        //修改：增加50距离，来画外圈节点
        int xp = getPaddingLeft() + distance + getPaddingRight() + distance;
        int yp = getPaddingBottom() + distance + getPaddingTop() + distance;
        mOval = new RectF(getPaddingLeft() + distance + mStrokeWidth, getPaddingTop() + distance + mStrokeWidth,
                getPaddingLeft() + distance + (getWidth() - xp) - mStrokeWidth,
                getPaddingTop() + distance + (getHeight() - yp) - mStrokeWidth);
    }

    public void setStrokeWidthDp(float dp) {
        this.mStrokeWidth = dp2px(dp);
        mPaint.setStrokeWidth(mStrokeWidth);
        updateOval();
        refreshTheLayout();
    }

    public void refreshTheLayout() {
        invalidate();
        requestLayout();
    }

    public int getFgColorStart() {
        return mFgColorStart;
    }

    public void setFgColorStart(int mFgColorStart) {
        this.mFgColorStart = mFgColorStart;
        mShader = new LinearGradient(mOval.left, mOval.top,
                mOval.left, mOval.bottom, mFgColorStart, mFgColorEnd, Shader.TileMode.MIRROR);
        refreshTheLayout();
    }

    public int getFgColorEnd() {
        return mFgColorEnd;
    }

    public void setFgColorEnd(int mFgColorEnd) {
        this.mFgColorEnd = mFgColorEnd;
        mShader = new LinearGradient(mOval.left, mOval.top,
                mOval.left, mOval.bottom, mFgColorStart, mFgColorEnd, Shader.TileMode.MIRROR);
        refreshTheLayout();
    }


    public float getStartAngle() {
        return mStartAngle;
    }

    public void setStartAngle(float mStartAngle) {
        this.mStartAngle = mStartAngle;
        refreshTheLayout();
    }


    public  void startAnim(){
        anim = ObjectAnimator.ofFloat(this, "percent", 0, getPercent());//设置动画
        anim.setInterpolator(new LinearInterpolator());
        anim.setDuration(500);
        anim.start();
    }
}
