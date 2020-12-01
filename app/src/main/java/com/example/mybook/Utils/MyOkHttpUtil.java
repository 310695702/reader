package com.example.mybook.Utils;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;


import com.example.mybook.Interface.INetCallBack;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MyOkHttpUtil {
    private OkHttpClient okHttpClient;
    public static MyOkHttpUtil sInstance = new MyOkHttpUtil();
    private Handler handler = new Handler(Looper.getMainLooper());

    public static MyOkHttpUtil getInstance() {
        return sInstance;
    }

    public MyOkHttpUtil(){
        okHttpClient = new OkHttpClient.Builder()
                .build();
    }

    public void doGet(String url, INetCallBack callBack){
        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();
        Call call = okHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        callBack.onFailure(e);
                    }
                });
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                Log.d("TAG?",response+"");
                String respstr = null;
                try {
                    respstr = response.body().string();
                } catch (Exception e) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            callBack.onFailure(e);
                        }
                    });
                }
                String finalRespstr = respstr;
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        callBack.onSuccess(finalRespstr);
                    }
                });

            }
        });
    }
}
