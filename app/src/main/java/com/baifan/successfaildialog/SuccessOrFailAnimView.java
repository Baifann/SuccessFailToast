package com.baifan.successfaildialog;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.RectF;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

/**
 * 成功或者失败动画
 * Created by baifan on 16/5/12.
 */
public class SuccessOrFailAnimView extends View implements Runnable {
    private final static String TAG = "SuccessOrFailAnimView";
    /**
     * 画圆的间隔时间
     */
    private final static int TIME_INTERVAL = 10;
    /**
     * 画圆
     */
    private final static int DRAW_STATE_CIRCLE = 100;
    /**
     * 圆圈完成
     */
    private final int MSG_CIRCLE_DONE = 1000;
    /**
     * 画左边
     */
    private final static int DRAW_STATE_LEFT_SUCCESS = 101;
    /**
     * 画右边
     */
    private final static int DRAW_STATE_RIGHT_SUCCESS = 102;
    /**
     * 错误状态左边那一条
     */
    private final static int DRAW_STATE_LEFT_FAIL = 103;
    /**
     * 错误状态右边那一条
     */
    private final static int DRAW_STATE_RIGHT_FAIL = 104;
    /**
     * 成功动画的状态
     */
    private final static int ANIM_SUCCESS = 0;
    /**
     * 失败动画的状态
     */
    private final static int ANIM_FAIL = 1;
    /**
     * rec宽度
     */
    private final static int REC_WITDH = 80;
    /**
     * 画笔的粗
     */
    private final static float PAINT_WIDTH = 5;
    /**
     * 每次增加的角度
     */
    private final static int ANGLE_CHANGE = 5;
    /**
     * 开始角度
     */
    private final static int ANGLE_START = -90;
    /**
     * x的平移量
     */
    private int mTranslateX;
    /**
     * y的平移量
     */
    private int mTranslateY;
    /**
     * 画笔
     */
    private Paint mPaint;
    /**
     * 当前的状态 0成功 1失败
     */
    private int mCurrentState;
    /**
     * 增加角度
     */
    private int mSweepAngle;
    /**
     * 是否开始
     */
    private boolean isStartProgress = true;
    /**
     * 当前的左勾点
     */
    private Point mCurrentLeftSuccessPoint = new Point();
    /**
     * 当前的右勾点
     */
    private Point mCurrentRightSuccessPoint = new Point();
    /**
     * 当前错误的左勾点
     */
    private Point mCurrentLeftFailPoint = new Point();
    /**
     * 当前错误的左勾点
     */
    private Point mCurrentRightFailPoint = new Point();
    /**
     * 当前绘画状态
     */
    private int mCurrentDrawState;

    private OnSuccessOrFailAinmViewListener mListener;

    public interface OnSuccessOrFailAinmViewListener {
        void onShowEnd();
    }

    /**
     * 设置监听
     *
     * @param lisener
     */
    public void setOnSuccessOrFailAinmViewLisener(OnSuccessOrFailAinmViewListener lisener) {
        mListener = lisener;
    }

    public SuccessOrFailAnimView(Context context) {
        this(context, null);
    }

    public SuccessOrFailAnimView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initPaint();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        //将canvas 平移至中心
        canvas.translate(mTranslateX, mTranslateY);
        switch (mCurrentDrawState) {
            case DRAW_STATE_CIRCLE:
                drawArc(canvas);
                break;

            case DRAW_STATE_LEFT_SUCCESS:
                drawArc(canvas);
                drawSuccessLeft(canvas);
                break;

            case DRAW_STATE_RIGHT_SUCCESS:
                drawArc(canvas);
                drawSuccessLeft(canvas);
                drawSuccessRight(canvas);
                break;
            case DRAW_STATE_LEFT_FAIL:
                drawArc(canvas);
                drawFailLeft(canvas);
                break;
            case DRAW_STATE_RIGHT_FAIL:
                drawArc(canvas);
                drawFailLeft(canvas);
                drawFailRight(canvas);
                break;
        }
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            Log.i(TAG, "MSG.WHAT:" + msg.what);
            switch (msg.what) {
                case MSG_CIRCLE_DONE:
                    //选择画成功还是失败
                    chooseDrawSuccessOrFail();
                    break;
            }
        }
    };

    /**
     * 画圆环
     */
    private void drawArc(Canvas canvas) {
        RectF rectf = new RectF();
        rectf.left = -REC_WITDH;
        rectf.top = -REC_WITDH;
        rectf.right = REC_WITDH;
        rectf.bottom = REC_WITDH;
        canvas.drawArc(rectf, ANGLE_START, mSweepAngle, false, mPaint);
    }

    /**
     * 画圆圈
     */
    private void drawArc() {
        int startAngle = 0;
        int endAngle = 360;

        final ValueAnimator anim = ValueAnimator.ofInt(startAngle, endAngle);
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mSweepAngle = (int) animation.getAnimatedValue();
                mCurrentDrawState = DRAW_STATE_CIRCLE;
                invalidate();
            }
        });
        anim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                chooseDrawSuccessOrFail();
            }
        });
        anim.setDuration(600);
        anim.start();
    }

    /**
     * 画打钩的左边
     */
    private void drawSuccessLeft() {
        Point startPoint = new Point();
        Point endPoint = new Point();
        startPoint.x = -(REC_WITDH / 2);
        startPoint.y = 0;
        endPoint.x = 0;
        endPoint.y = REC_WITDH / 2;

        ValueAnimator anim = ValueAnimator.ofObject(new PointEvaluator(), startPoint, endPoint);
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mCurrentLeftSuccessPoint = (Point) animation.getAnimatedValue();
                mCurrentDrawState = DRAW_STATE_LEFT_SUCCESS;
                invalidate();
            }
        });
        anim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                drawSuccessRignt();
            }
        });
        anim.setDuration(200);
        anim.start();
    }

    /**
     * 画右边打钩
     */
    private void drawSuccessRignt() {
        Point startPoint = new Point();
        Point endPoint = new Point();
        startPoint.x = 0;
        startPoint.y = REC_WITDH / 2;
        endPoint.x = REC_WITDH / 2;
        endPoint.y = -(REC_WITDH) / 2;
        ValueAnimator anim = ValueAnimator.ofObject(new PointEvaluator(), startPoint, endPoint);
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mCurrentRightSuccessPoint = (Point) animation.getAnimatedValue();
                mCurrentDrawState = DRAW_STATE_RIGHT_SUCCESS;
                invalidate();
            }
        });
        anim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                if (mListener != null) {
                    mListener.onShowEnd();
                }
            }
        });
        anim.setDuration(250);
        anim.start();
    }

    /**
     * 画失败的左边
     */
    private void drawFailLeft() {
        Point startPoint = new Point();
        Point endPoint = new Point();
        startPoint.x = -REC_WITDH / 2;
        startPoint.y = -REC_WITDH / 2;
        endPoint.x = REC_WITDH / 2;
        endPoint.y = REC_WITDH / 2;
        ValueAnimator anim = ValueAnimator.ofObject(new PointEvaluator(), startPoint, endPoint);
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mCurrentLeftFailPoint = (Point) animation.getAnimatedValue();
                mCurrentDrawState = DRAW_STATE_LEFT_FAIL;
                invalidate();
            }
        });
        anim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                drawFailRight();
            }
        });
        anim.setDuration(250);
        anim.start();
    }

    /**
     * 画失败的右边
     */
    private void drawFailRight() {
        Point startPoint = new Point();
        Point endPoint = new Point();
        startPoint.x = REC_WITDH / 2;
        startPoint.y = -REC_WITDH / 2;
        endPoint.x = -REC_WITDH / 2;
        endPoint.y = REC_WITDH / 2;
        ValueAnimator anim = ValueAnimator.ofObject(new PointEvaluator(), startPoint, endPoint);
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mCurrentRightFailPoint = (Point) animation.getAnimatedValue();
                mCurrentDrawState = DRAW_STATE_RIGHT_FAIL;
                invalidate();
            }
        });
        anim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                if (mListener != null) {
                    mListener.onShowEnd();
                }
            }
        });
        anim.setDuration(250);
        anim.start();
    }

    /**
     * 在画布中画圆
     *
     * @param canvas
     */
    private void drawSuccessLeft(Canvas canvas) {
        Point startPoint = new Point();
        startPoint.x = -REC_WITDH / 2;
        startPoint.y = 0;
        canvas.drawLine(startPoint.x, startPoint.y, mCurrentLeftSuccessPoint.x, mCurrentLeftSuccessPoint.y, mPaint);
    }

    /**
     * 在画布中画有半部分的打钩
     *
     * @param canvas
     */
    private void drawSuccessRight(Canvas canvas) {
        Point startPoint = new Point();
        startPoint.x = 0;
        startPoint.y = REC_WITDH / 2;
        canvas.drawLine(startPoint.x, startPoint.y, mCurrentRightSuccessPoint.x, mCurrentRightSuccessPoint.y, mPaint);
    }

    /**
     * 在画布中画失败的左边
     */
    private void drawFailLeft(Canvas canvas) {
        Point startPoint = new Point();
        startPoint.x = -REC_WITDH / 2;
        startPoint.y = -REC_WITDH / 2;
        canvas.drawLine(startPoint.x, startPoint.y, mCurrentLeftFailPoint.x, mCurrentLeftFailPoint.y, mPaint);
    }

    /**
     * 在画布中画失败的右边边
     */
    private void drawFailRight(Canvas canvas) {
        Point startPoint = new Point();
        startPoint.x = REC_WITDH / 2;
        startPoint.y = -REC_WITDH / 2;
        canvas.drawLine(startPoint.x, startPoint.y, mCurrentRightFailPoint.x, mCurrentRightFailPoint.y, mPaint);
    }


    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        //TODO 中点输出
        mTranslateX = w / 2;
        mTranslateY = h / 2;
        Log.i(TAG, "mTranslateX:" + mTranslateX + ", mTranslateY:" + mTranslateY);
        super.onSizeChanged(w, h, oldw, oldh);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        //不能用父类继承方法
        setMeasuredDimension(measureWidth(widthMeasureSpec),
                measureHeight(heightMeasureSpec));
    }

    /**
     * 测量宽度
     *
     * @param measureSpec
     * @return
     */
    private int measureWidth(int measureSpec) {
        int result = 0;
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);

        if (specMode == MeasureSpec.EXACTLY) {
            result = specSize;
        } else {
            result = 200;
            if (specMode == MeasureSpec.AT_MOST) {
                result = Math.min(result, specSize);
            }
        }

        return result;
    }

    private int measureHeight(int measureSpec) {
        int result = 0;
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);

        if (specMode == MeasureSpec.EXACTLY) {
            result = specSize;
        } else {
            result = 200;
            if (specMode == MeasureSpec.AT_MOST) {
                result = Math.min(result, specSize);
            }
        }

        return result;
    }

    /**
     * 初始化画笔
     */
    private void initPaint() {
        mPaint = new Paint();
        mPaint.setColor(getContext().getResources().getColor(R.color.green));
        mPaint.setStrokeWidth(PAINT_WIDTH);
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.STROKE);
    }

    /**
     * 成功
     */
    public void success() {
        mCurrentState = ANIM_SUCCESS;
        setSuccessPaintColor();

//        Thread thread = new Thread(this);
//        thread.start();
        drawArc();
    }

    /**
     * 失败
     */
    public void fail() {
        mCurrentState = ANIM_FAIL;
        setFailPaintColor();

//        Thread thread = new Thread(this);
//        thread.start();
        drawArc();
    }

    /**
     * 设置成功状态画笔的颜色
     */
    private void setSuccessPaintColor() {
        mPaint.setColor(getContext().getResources().getColor(R.color.green));
    }

    /**
     * 设置
     */
    private void setFailPaintColor() {
        mPaint.setColor(getContext().getResources().getColor(R.color.red));
    }

    /**
     * 增加角度
     */
    private void addSweepAngle() {
        mSweepAngle = mSweepAngle + ANGLE_CHANGE;
        postInvalidate();
        if (mSweepAngle == 360) {
            //当旋转角度等于360° 停止转动
            isStartProgress = false;
            mHandler.sendEmptyMessage(MSG_CIRCLE_DONE);
        }
    }

    @Override
    public void run() {
        while (isStartProgress) {
            long start = System.currentTimeMillis();
            mCurrentDrawState = DRAW_STATE_CIRCLE;
            addSweepAngle();
            long end = System.currentTimeMillis();
            if (end - start < TIME_INTERVAL) {
                try {
                    Thread.sleep(TIME_INTERVAL - (end - start));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 选择画成功还是失败
     */
    private void chooseDrawSuccessOrFail() {
        switch (mCurrentState) {
            case ANIM_SUCCESS:
                drawSuccessLeft();
                break;
            case ANIM_FAIL:
                drawFailLeft();
                break;
        }
    }
}
