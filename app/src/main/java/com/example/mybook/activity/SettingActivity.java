package com.example.mybook.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.mybook.R;
import com.example.mybook.bean.User;
import com.google.gson.Gson;

import java.io.IOException;
import java.lang.ref.WeakReference;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class SettingActivity extends AppCompatActivity {
    private EditText name,psw,pswture;
    private Button commit;
    private Context context;
    private SharedPreferences sp;
    private SharedPreferences.Editor editor;
    private MyHandler handler = new MyHandler(this);
    private static Activity mactivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        sp = getSharedPreferences("login",Context.MODE_PRIVATE);
        editor = sp.edit();
        init();
        context = getApplicationContext();
        commit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mypost();
            }
        });
    }

    public static void start(Context context, Activity activity){
        Intent intent = new Intent(context,SettingActivity.class);
        context.startActivity(intent);
        mactivity = activity;
    }

    private void mypost() {
        new Thread(){
            @Override
            public void run() {
                super.run();
                if (name.getText().toString().length() == 0) {
                handler.sendEmptyMessage(10);
                return;
            }else
                    if(name.getText().toString().length()>8||name.getText().toString().length()<2){
                handler.sendEmptyMessage(100);
                return;
            }else if (psw.getText().toString().length() == 0) {
                handler.sendEmptyMessage(9);
                return;
            }else
                    if(psw.getText().toString().length()<6||psw.getText().toString().length()>16){
                handler.sendEmptyMessage(101);
                return;
            }
                else if (pswture.getText().toString().length() == 0){
                handler.sendEmptyMessage(8);
                return;
            }else if (!psw.getText().toString().equals(pswture.getText().toString())){
                handler.sendEmptyMessage(7);
                return;
            }
                else {
                User user = new User(sp.getString("Phone","null"),name.getText().toString(), pswture.getText().toString());
                Gson gson = new Gson();
                String json = gson.toJson(user);
                OkHttpClient client = new OkHttpClient();
                RequestBody body = RequestBody.create(json, MediaType.parse("application/json"));
                final Request request = new Request.Builder()
                        .url("http://49.234.90.62:8080/ssmtest/update")
                        .post(body)
                        .build();
                Call call = client.newCall(request);
                call.enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        handler.sendEmptyMessage(1);
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        final String result = response.body().string();
                        if (result.equals("0")) {
                            handler.sendEmptyMessage(0);
                        }else if (result.equals("1")){
                            editor.putString("Phone",sp.getString("Phone","null").toString());
                            editor.putString("Name",name.getText().toString());
                            editor.putString("Password",pswture.getText().toString());
                            editor.commit();
                            MainActivity.start(SettingActivity.this);
                            finish();
                            mactivity.finish();
                            handler.sendEmptyMessage(2);
                        }else {
                            Log.e("Result:",result+"");
                            handler.sendEmptyMessage(3);
                        }

                    }
                });
            }
        }
        }.start();


    }

    private void init() {
        name = findViewById(R.id.changename);
        psw = findViewById(R.id.changepassword);
        pswture = findViewById(R.id.surechangepassword);
        commit = findViewById(R.id.commit);
    }



    //弱引用
    public static class MyHandler extends Handler {
        private WeakReference<SettingActivity> weakReference;

        public MyHandler(SettingActivity activity){
            weakReference = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            SettingActivity settingActivity = weakReference.get();
            switch (msg.what){
                case 0:
                    Toast.makeText(settingActivity.context, "修改失败,请重试", Toast.LENGTH_LONG).show();
                    break;
                case 1:
                    Toast.makeText(settingActivity.context, "与服务器连接失败", Toast.LENGTH_LONG).show();
                    break;
                case 2:
                    Toast.makeText(settingActivity.context,"修改成功！" , Toast.LENGTH_LONG).show();
                    break;
                case 3:
                    Toast.makeText(settingActivity.context, "发现未知错误", Toast.LENGTH_LONG).show();
                    break;
                case 10:
                    Toast.makeText(settingActivity.context, "请输入修改后的用户名", Toast.LENGTH_LONG).show();
                    break;
                case 9:
                    Toast.makeText(settingActivity.context, "请输入修改后的密码", Toast.LENGTH_LONG).show();
                    break;
                case 8:
                    Toast.makeText(settingActivity.context, "请再次输入修改后密码", Toast.LENGTH_LONG).show();
                    break;
                case 7:
                    Toast.makeText(settingActivity.context, "两次密码输入不一致", Toast.LENGTH_LONG).show();
                    break;
                case 100:
                    Toast.makeText(settingActivity.context,"姓名长度不在范围内", Toast.LENGTH_LONG).show();
                    break;
                case 101:
                    Toast.makeText(settingActivity.context,"请设置6~16位的密码", Toast.LENGTH_LONG).show();
                    break;
            }
        }
    }
}
