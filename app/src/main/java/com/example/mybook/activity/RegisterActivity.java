package com.example.mybook.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.mybook.helper.EditTextClearTools;
import com.example.mybook.R;
import com.example.mybook.bean.User;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class RegisterActivity extends AppCompatActivity {
    private EditText et_userName;
    private EditText et_phone;
    private EditText et_password1;
    private EditText et_password2;
    private Context  context;
    private Intent intent;
    private String reg="1[3458][0-9]{9}";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);
        context=getApplicationContext();
        findViewById(R.id.btn_re).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        findViewById(R.id.btn_toregister).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                okhttpRegister();
            }
        });
        init();
    }
    private void init(){
        et_userName = findViewById(R.id.et_userName);
        et_phone = findViewById(R.id.et_phone);
        et_password1 = findViewById(R.id.et_password1);
        et_password2 = findViewById(R.id.et_password2);
        ImageView unameClear = findViewById(R.id.iv_unameClear);
        ImageView phoneClear = findViewById(R.id.iv_phoneClear);
        ImageView pwd1Clear = findViewById(R.id.iv_pwdClear1);
        ImageView pwd2Clear = findViewById(R.id.iv_pwdClear2);

        EditTextClearTools.addClearListener(et_userName,unameClear);
        EditTextClearTools.addClearListener(et_phone,phoneClear);
        EditTextClearTools.addClearListener(et_password1,pwd1Clear);
        EditTextClearTools.addClearListener(et_password2,pwd2Clear);
    }

    public void okhttpRegister(){

        if(et_userName.getText().toString().length()==0){
            Toast.makeText(context,"姓名不能为空", Toast.LENGTH_LONG).show();
            return;
        }
        if(et_userName.getText().toString().length()>8||et_userName.getText().toString().length()<2){
            Toast.makeText(context,"姓名长度不在范围内", Toast.LENGTH_LONG).show();
            return;
        }
        if(et_phone.getText().toString().length()==0){
            Toast.makeText(context,"手机号不能为空", Toast.LENGTH_LONG).show();
            return;
        }
        Pattern pattern = Pattern.compile(reg);
        Matcher matcher = pattern.matcher(et_phone.getText().toString());
        boolean b = matcher.matches();
        if(!b){
            Toast.makeText(context,"手机号不正确", Toast.LENGTH_LONG).show();
            return;
        }
        if(et_password1.getText().toString().length()==0){
            Toast.makeText(context,"密码不能为空", Toast.LENGTH_LONG).show();
            return;
        }
        if(et_password1.getText().toString().length()<6||et_password1.getText().toString().length()>16){
            Toast.makeText(context,"请设置6~16位的密码", Toast.LENGTH_LONG).show();
            return;
        }
        if(et_password2.getText().toString().length()==0){
            Toast.makeText(context,"请确认密码", Toast.LENGTH_LONG).show();
            return;
        }
        if(!et_password1.getText().toString().equals(et_password2.getText().toString())){
            Toast.makeText(context,"密码不一致请重新输入！", Toast.LENGTH_LONG).show();
            return;
        }

        //Toast.makeText(context,"注册成功！", Toast.LENGTH_LONG).show();

        new Thread(){
            @Override
            public void run() {
                super.run();
                User user = new User(et_phone.getText().toString(),et_userName.getText().toString(),et_password1.getText().toString());
                Gson gson = new Gson();
                String json = gson.toJson(user);
                OkHttpClient client=new OkHttpClient();
                RequestBody body = RequestBody.create(MediaType.parse("application/json;charset=UTF-8"),json);
                Request request = new Request.Builder()
                        .url("http://49.234.90.62:8080/ssmtest/register")
                        .post(body)
                        .build();
                Call call = client.newCall(request);
                Log.i("this",request.toString());
                call.enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(context,"服务器请求失败", Toast.LENGTH_LONG).show();
                            }
                        });
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        if (response==null){
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(context,"获取数据失败！", Toast.LENGTH_LONG).show();
                                }
                            });
                        }else {
                            final String result = response.body().string();
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if(result.equals("注册成功!")){
                                        Toast.makeText(context,result, Toast.LENGTH_LONG).show();
                                        intent=getIntent();
                                        intent.putExtra("Phone",et_phone.getText().toString());
                                        intent.putExtra("Password",et_password1.getText().toString());
                                        finish();
                                    }
                                    else{
                                        Toast.makeText(context,result, Toast.LENGTH_LONG).show();
                                    }
                                }
                            });
                        }

                    }
                });
            }
        }.start();

    }
}
