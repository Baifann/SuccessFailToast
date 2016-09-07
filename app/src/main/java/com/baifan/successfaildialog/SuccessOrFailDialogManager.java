package com.baifan.successfaildialog;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

/**
 * Created by baifan on 16/5/12.
 */
public class SuccessOrFailDialogManager {
    /**
     * 成功的状态
     */
    public final static int STATE_SUCCESS = 0;
    /**
     * 失败的状态
     */
    public final static int STATE_FAIL = 1;
    /**
     * 上下文
     */
    private Context mContext;
    /**
     * 提示类
     */
    private Dialog mDialog;
    /**
     * 动画提示框
     */
    private SuccessOrFailAnimView mSofView;
    /**
     * 描述
     */
    private TextView mTvDes;
    /**
     * 当前的状态 成功还是失败
     */
    private int mCurrentDialogState;

    public SuccessOrFailDialogManager(Context context, int state) {
        mContext = context;
        mCurrentDialogState = state;
        initDialog();
    }

    /**
     * 初始化dialog
     */
    private void initDialog() {
        mDialog = new Dialog(mContext, R.style.Theme_progress_dialog);
        View v = LayoutInflater.from(mContext).inflate(R.layout.dialog_success_fail, null);
        mDialog.setContentView(v);
        initViews(v);
    }

    /**
     * 初始化控件
     *
     * @param v 传入的布局
     */
    private void initViews(View v) {
        mTvDes = (TextView) v.findViewById(R.id.tv_dialog_des);
        mSofView = (SuccessOrFailAnimView) v.findViewById(R.id.sofView_dialog);
    }

    /**
     * 设置text
     *
     * @param text
     */
    public void setText(String text) {
        mTvDes.setText(text);
    }

    public void setText(int resId) {
        mTvDes.setText(resId);
    }

    /**
     * 显示
     */
    public void show() {
        showByState();
    }

    /**
     * 消失
     */
    public void dismiss() {
        if (mDialog != null && mDialog.isShowing()) {
            mDialog.dismiss();
        }
    }

    /**
     * 根据状态来显示不同的
     */
    private void showByState() {
        switch (mCurrentDialogState) {
            case STATE_FAIL:
                mDialog.show();
                mSofView.fail();
                mSofView.setOnSuccessOrFailAinmViewLisener(new SuccessOrFailAnimView.OnSuccessOrFailAinmViewListener() {
                    @Override
                    public void onShowEnd() {
                        SuccessOrFailDialogManager.this.dismiss();
                    }
                });
                break;
            case STATE_SUCCESS:
                mDialog.show();
                mSofView.success();
                mSofView.setOnSuccessOrFailAinmViewLisener(new SuccessOrFailAnimView.OnSuccessOrFailAinmViewListener() {
                    @Override
                    public void onShowEnd() {
                        SuccessOrFailDialogManager.this.dismiss();
                    }
                });
                break;
        }
    }
}
