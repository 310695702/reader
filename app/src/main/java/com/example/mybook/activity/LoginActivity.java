package com.example.mybook.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.mybook.helper.EditTextClearTools;
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

public class LoginActivity extends AppCompatActivity {
    private EditText etUserPhone;
    private EditText etUserPassword;
    private Context context;
    private SharedPreferences sp;
    private SharedPreferences.Editor editor;
    private CheckBox checkBox;
    private MyHandler handler = new MyHandler(this);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.load);
        init();//初始化控件
        checkBox = findViewById(R.id.cb_checkbox);
        sp = getSharedPreferences("login", Context.MODE_PRIVATE);//获取login，方式Context.MODE_PRIVATE
        editor = sp.edit();
        if (sp.getString("Check","false").equals("true")){
            etUserPhone.setText(sp.getString("Phone","null"));
            etUserPassword.setText(sp.getString("Password","null"));
        }


        context=getApplicationContext();
        findViewById(R.id.btn_login).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                okhttpLogin();//发起okhttplogin请求
            }
        });
        findViewById(R.id.btn_register).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getApplicationContext(),RegisterActivity.class);
                startActivity(intent);
            }
        });
    }

    private void init(){
        etUserPhone = findViewById(R.id.et_userName);
        etUserPassword = findViewById(R.id.et_password);
        ImageView unameClear =  findViewById(R.id.iv_unameClear);
        ImageView pwdClear =  findViewById(R.id.iv_pwdClear);
        EditTextClearTools.addClearListener(etUserPhone,unameClear);
        EditTextClearTools.addClearListener(etUserPassword,pwdClear);
    }

    public void okhttpLogin() {
        new Thread(){
            @Override
            public void run() {
                super.run();
                if (etUserPhone.getText().toString().length() == 0) {
                    handler.sendEmptyMessage(10);
                    return;
                } else if (etUserPassword.getText().toString().length() == 0) {
                    handler.sendEmptyMessage(9);
                    return;
                } else {
                    User user = new User(etUserPhone.getText().toString(), etUserPassword.getText().toString());

                    Gson gson = new Gson();
                    String json = gson.toJson(user);
                    OkHttpClient client = new OkHttpClient();
                    RequestBody body = RequestBody.create(json,MediaType.parse("application/json"));
                    final Request request = new Request.Builder()
                            .url("http://49.234.90.62:8080/ssmtest/load")
                            .post(body)
                            .build();
                    Call call = client.newCall(request);
                    call.enqueue(new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {
                            handler.sendEmptyMessage(2);
                        }

                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            if (response==null){
                                handler.sendEmptyMessage(500);
                            }else {
                                final String result = response.body().string();
                                if (result.equals("ADNE")) {
                                    handler.sendEmptyMessage(1);
                                } else if (result.equals("passNotCorrect")) {
                                    handler.sendEmptyMessage(3);
                                }
                                else{
                                    if (checkBox.isChecked()){
                                        editor.putString("Check","true").commit();

                                    }else {
                                        editor.putString("Check","false").commit();

                                    }
                                    editor.putString("Name",result+"");
                                    Log.e("TAG",etUserPhone.getText().toString());
                                    editor.putString("Phone",etUserPhone.getText().toString());
                                    editor.putString("Password",etUserPassword.getText().toString());
                                    editor.commit();
                                    MainActivity.start(LoginActivity.this);
                                    finish();
                                    Message message = new Message();
                                    message.obj = result;
                                    message.what=4;
                                    handler.sendMessage(message);
                                }
                            }
                            }

                    });
                }
            }
        }.start();

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode==KeyEvent.KEYCODE_BACK){
            MainActivity.start(this);
            LoginActivity.this.finish();
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onResume() {
        super.onResume();
        etUserPhone.setText("");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data.getStringExtra("Phone").toString()=="null"){
            return;
        }
        etUserPhone.setText(data.getStringExtra("Phone").toString());
        etUserPassword.setText(data.getStringExtra("Password").toString());
    }

    //弱引用
    public static class MyHandler extends Handler{
        private WeakReference<LoginActivity> weakReference;

        public MyHandler(LoginActivity activity){
            weakReference = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            LoginActivity loginActivity = weakReference.get();
            switch (msg.what){
                case 1:
                    Toast.makeText(loginActivity.context, "手机号不存在，请确认", Toast.LENGTH_LONG).show();
                    loginActivity.etUserPassword.setText("");
                    break;
                case 2:
                    Toast.makeText(loginActivity.context, "与服务器连接失败", Toast.LENGTH_LONG).show();
                    break;
                case 3:
                    Toast.makeText(loginActivity.context, "密码不正确，请重新输入", Toast.LENGTH_LONG).show();
                    loginActivity.etUserPassword.setText("");
                    break;
                case 4:
                    String result = (String) msg.obj;
                    Toast.makeText(loginActivity.context,"欢迎您！"+result , Toast.LENGTH_LONG).show();
                    break;
                case 10:
                    Toast.makeText(loginActivity.context, "请输入手机号", Toast.LENGTH_LONG).show();
                    break;
                case 9:
                    Toast.makeText(loginActivity.context, "请输入密码", Toast.LENGTH_LONG).show();
                    break;
                case 500:
                    Toast.makeText(loginActivity.context, "数据获取失败！", Toast.LENGTH_LONG).show();
                    break;
            }
        }
    }
}
