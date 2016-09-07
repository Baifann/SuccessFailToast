package com.baifan.successfaildialog;

import android.content.Context;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * Created by baifan on 16/5/13.
 */
public class SuccessOrFailWindowManager implements Runnable {
    private final static int MSG_MAX_NUM = 1;

    private static SuccessOrFailAnimView mSofView;

    private static TextView mTvDes;

    private static WindowManager mWindowManager;

    private static View mWindowView;

    private static BlockingQueue<String> mMsgQueue = new ArrayBlockingQueue<String>(MSG_MAX_NUM);

    private final static int MSG_SHOW = 0;
    private final static int MSG_HIDE = 1;

    public static void successShow(Context context, String text) {
        mWindowManager = (WindowManager) context.getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
        initViews(context);
        setText(text);
        showSuccess();
    }

    public static void successShow(Context context, int resId) {
        mWindowManager = (WindowManager) context.getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
        initViews(context);
        setText(resId);
        showSuccess();
    }

    public static void failShow(Context context, String text) {
        mWindowManager = (WindowManager) context.getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
        initViews(context);
        setText(text);
        showFail();
    }

    public static void failShow(Context context, int resId) {
        mWindowManager = (WindowManager) context.getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
        initViews(context);
        setText(resId);
        showFail();
    }

    private static void initViews(Context context) {
        mWindowView = LayoutInflater.from(context).inflate(R.layout.dialog_success_fail, null);
        mSofView = (SuccessOrFailAnimView) mWindowView.findViewById(R.id.sofView_dialog);
        mTvDes = (TextView) mWindowView.findViewById(R.id.tv_dialog_des);

        initWindowManagerParms(mWindowView);
    }

    /**
     * 初始化windowmanager参数
     */
    private static void initWindowManagerParms(View v) {
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.type = WindowManager.LayoutParams.TYPE_TOAST;
        lp.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE; //设置这个 window 不可点击，不会获取焦点，这样可以不干扰背后的 Activity 的交互。
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.format = PixelFormat.TRANSLUCENT; //这样可以保证 Window 的背景是透明的，不然背景可能是黑色或者白色。
        lp.windowAnimations = android.R.style.Animation_Toast; //使用官方原生的 Toast 动画效果
        mWindowManager.addView(v, lp);
    }

    private static void setText(String text) {
        mTvDes.setText(text);
    }

    private static void setText(int resId) {
        mTvDes.setText(resId);
    }

    private synchronized static void showSuccess() {
        synchronized (SuccessOrFailWindowManager.class) {
            mSofView.success();
            mSofView.setOnSuccessOrFailAinmViewLisener(new SuccessOrFailAnimView.OnSuccessOrFailAinmViewListener() {
                @Override
                public void onShowEnd() {
                    mWindowManager.removeView(mWindowView);
                }
            });
        }
    }

    private synchronized static void showFail() {
        synchronized (SuccessOrFailWindowManager.class) {
            mSofView.fail();
            mSofView.setOnSuccessOrFailAinmViewLisener(new SuccessOrFailAnimView.OnSuccessOrFailAinmViewListener() {
                @Override
                public void onShowEnd() {
                    mWindowManager.removeView(mWindowView);
                }
            });
        }
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_SHOW:
                    break;
                case MSG_HIDE:

                    break;
            }
        }
    };

    @Override
    public void run() {
        try {
            String text = mMsgQueue.take();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
