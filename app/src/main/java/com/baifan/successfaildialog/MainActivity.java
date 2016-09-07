package com.baifan.successfaildialog;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {
    SuccessOrFailAnimView mSFViewSuccess;
    SuccessOrFailAnimView msfViewFail;
    Button mBtnStartSuccess;
    Button mBtnStartFail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews();
        initEvents();
    }

    private void initViews() {
        mSFViewSuccess = (SuccessOrFailAnimView) findViewById(R.id.sofView_success);
        msfViewFail = (SuccessOrFailAnimView) findViewById(R.id.sofView_fail);
        mBtnStartSuccess = (Button) findViewById(R.id.btn_start_success);
        mBtnStartFail = (Button) findViewById(R.id.btn_start_fail);
    }

    private void initEvents() {
        mBtnStartSuccess.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                mSFViewSuccess.success();
//                SuccessOrFailDialogManager mDialog = new SuccessOrFailDialogManager(MainActivity.this, SuccessOrFailDialogManager.STATE_SUCCESS);
//                mDialog.setText("成功");
//                mDialog.show();
//                SuccessOrFailWindowManager.successShow(MainActivity.this, "成功");
                SuccessOrFailToast.getInstance(MainActivity.this).show("成功", SuccessOrFailToast.STATE_SUCCESS);
            }
        });
        mBtnStartFail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                SuccessOrFailDialogManager mDialog = new SuccessOrFailDialogManager(MainActivity.this, SuccessOrFailDialogManager.STATE_FAIL);
//                mDialog.setText("失败");
//                mDialog.show();
//                SuccessOrFailWindowManager.failShow(MainActivity.this, "失败");
                SuccessOrFailToast.getInstance(MainActivity.this).show("失败", SuccessOrFailToast.STATE_FAIL);
            }
        });
    }
}
