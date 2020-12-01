package com.example.mybook.activity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.mybook.R;

import java.lang.ref.WeakReference;

public class SpashActivity extends AppCompatActivity {
    private TextView mTextView;

    public static final int CODE = 1001;
    public static final int TOTAL_TIME = 3000;
    public static final int INTEVAL_TIME = 1000;
    final MyHandler handler = new MyHandler(this);
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spash);
        mTextView = findViewById(R.id.time_text_view);


        final Message message = Message.obtain();
        message.what = CODE;
        message.arg1 = TOTAL_TIME;
        handler.sendMessage(message);

        mTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //todo:跳到下一页面
                MainActivity.start(SpashActivity.this);
                finish();
                //停止发送消息
                handler.removeMessages(CODE);
            }
        });
    }

    public static class MyHandler extends Handler{
        public final WeakReference<SpashActivity> mWeakReference;

        public MyHandler(SpashActivity spashActivity) {
            mWeakReference = new WeakReference<>(spashActivity);
        }

        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            SpashActivity activity = mWeakReference.get();
            if (msg.what == CODE){


                if (activity!=null){
                    //设置TextView
                    int time = msg.arg1;
                    activity.mTextView.setText(time/INTEVAL_TIME+"秒,点击跳过");
                    //发送倒计时
                    Message message = Message.obtain();
                    message.what = CODE;
                    message.arg1 = time - INTEVAL_TIME;
                    if (time>0) {
                        sendMessageDelayed(message, INTEVAL_TIME);
                    }else {
                        //todo:跳到下一页面
                        MainActivity.start(activity);
                        activity.finish();
                    }
                }
            }
        }
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode== KeyEvent.KEYCODE_BACK){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("提示:");
            builder.setMessage("您确定退出？");
            //设置确定按钮
            builder.setNegativeButton("确定", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    handler.removeCallbacksAndMessages(null);
                    finish();
                }
            });
            //设置取消按钮
            builder.setPositiveButton("容我再想想",null);
            builder.show();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

}
