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
 * Created by baifan on 16/5/17.
 */
public class SuccessOrFailToast implements Runnable {
    public final static int STATE_SUCCESS = 1;
    public final static int STATE_FAIL = 2;
    private final static long DELAYED_TIME_HIDE = 150;
    private final static int MSG_MAX_NUM = 1;

    private SuccessOrFailAnimView mSofView;

    private TextView mTvDes;

    private WindowManager mWindowManager;

    private View mWindowView;

    private BlockingQueue<String> mMsgQueue = new ArrayBlockingQueue<String>(MSG_MAX_NUM);

    public static SuccessOrFailToast instance;

    private int mCurrentState;

    private final static int MSG_SHOW = 0;
    private final static int MSG_HIDE = 1;

    private static final String DEFAULT_ID = "DEFAULT";

    private Context mContext;

    private SuccessOrFailToast(Context context) {
        mContext = context;
        initViews(context);
    }

    public static synchronized SuccessOrFailToast getInstance(Context context) {
        if (instance == null) {
            synchronized (SuccessOrFailToast.class) {
                if (instance == null) {
                    instance = new SuccessOrFailToast(context);
                }
            }
        }
        return instance;
    }

    public void show(int messageId, int state){
        setCurrentState(state);
        setText(messageId);
        mWindowManager = (WindowManager) mContext.getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
        show();
    }

    /**
     * 显示
     *
     * @param message 内容
     */
    public void show(String message, int state) {
        setCurrentState(state);
        setText(message);
        mWindowManager = (WindowManager) mContext.getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
        show();
    }

    private void setCurrentState(int currentState) {
        mCurrentState = currentState;
    }

    private void setText(String message) {
        mTvDes.setText(message);
    }

    private void setText(int resId) {
        mTvDes.setText(resId);
    }

    private void show() {
        Thread thread = new Thread(this);
        thread.start();
    }

    /**
     * 初始化windowmanager参数
     */
    private void initWindowManagerParms(View v) {
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.type = WindowManager.LayoutParams.TYPE_TOAST;
        lp.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE; //设置这个 window 不可点击，不会获取焦点，这样可以不干扰背后的 Activity 的交互。
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.format = PixelFormat.TRANSLUCENT; //这样可以保证 Window 的背景是透明的，不然背景可能是黑色或者白色。
        lp.windowAnimations = android.R.style.Animation_Toast; //使用官方原生的 Toast 动画效果
        mWindowManager.addView(v, lp);
    }

    /**
     * 初始化控件
     */
    private void initViews(Context context) {
        mWindowView = LayoutInflater.from(context).inflate(R.layout.dialog_success_fail, null);
        mSofView = (SuccessOrFailAnimView) mWindowView.findViewById(R.id.sofView_dialog);
        mTvDes = (TextView) mWindowView.findViewById(R.id.tv_dialog_des);
    }

    /**
     * 选择当前的成功或者失败
     */
    private void chooseSuccessOrFail(int currentState) {
        switch (currentState) {
            case STATE_SUCCESS:
                showSuccess();
                break;
            case STATE_FAIL:
                showFail();
                break;
        }
    }

    private void showSuccess() {
        mSofView.success();
        mSofView.setOnSuccessOrFailAinmViewLisener(new SuccessOrFailAnimView.OnSuccessOrFailAinmViewListener() {
            @Override
            public void onShowEnd() {
                mHandler.sendEmptyMessageDelayed(MSG_HIDE, DELAYED_TIME_HIDE);
            }
        });
    }

    private void showFail() {
        mSofView.fail();
        mSofView.setOnSuccessOrFailAinmViewLisener(new SuccessOrFailAnimView.OnSuccessOrFailAinmViewListener() {
            @Override
            public void onShowEnd() {
                mHandler.sendEmptyMessageDelayed(MSG_HIDE, DELAYED_TIME_HIDE);
            }
        });
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_SHOW:
                    initWindowManagerParms(mWindowView);
                    chooseSuccessOrFail(mCurrentState);
                    break;
                case MSG_HIDE:
                    mWindowManager.removeView(mWindowView);
                    try {
                        mMsgQueue.take();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    break;
            }
        }
    };

    @Override
    public void run() {
        try {
            mMsgQueue.put(DEFAULT_ID);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        mHandler.sendEmptyMessage(MSG_SHOW);
    }
}
