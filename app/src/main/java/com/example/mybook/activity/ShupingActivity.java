package com.example.mybook.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.mybook.R;
import com.example.mybook.bean.ShuPingBean;
import com.example.mybook.bean.ShupingPostbean;
import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import cz.msebera.android.httpclient.Header;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ShupingActivity extends AppCompatActivity{
    private List<ShuPingBean.Mydata.listdata> mydataList = new ArrayList<>();
    private ListView listView;
    private EditText editText;
    private TextView send,bookname,back;
    private AsyncHttpClient client;
    private String url = "http://49.234.90.62:8080/ssmtest/findShuPingForName/";
    private String thisbook;
    private SharedPreferences sp;
    private MyHandler handler = new MyHandler(this);
    private SwipeRefreshLayout refreshLayout;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shuping);
        sp = getSharedPreferences("login",Context.MODE_PRIVATE);
        client = new AsyncHttpClient();
        client.setConnectTimeout(3000);
        init();
    }

    public static void start(Context context,String bookname){
        Intent intent = new Intent(context, ShupingActivity.class);
        intent.putExtra("bookname",bookname);
        context.startActivity(intent);
    }

    private void inithttp() {
        client.get(url+thisbook, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                refreshLayout.setRefreshing(false);
                if (responseBody==null){
                    handler.sendEmptyMessage(500);
                }else {
                    String str = new String(responseBody);
                    Gson gson = new Gson();
                    ShuPingBean.Mydata bean = gson.fromJson(str,ShuPingBean.Mydata.class);
                    mydataList = bean.getListdata();
                    listView.setAdapter(new ShupingAdapter());
                }

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                    refreshLayout.setRefreshing(false);
                    handler.sendEmptyMessage(0);
            }
        });
    }


    private void init() {
        bookname = findViewById(R.id.tv_bookname);
        thisbook = getIntent().getStringExtra("bookname");
        bookname.setText(getIntent().getStringExtra("bookname"));
        listView = findViewById(R.id.shuping_listview);
        editText = findViewById(R.id.content);
        send = findViewById(R.id.send);
        back = findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        sendMessage();
        inithttp();
        refreshLayout = findViewById(R.id.refreshlayout);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                inithttp();
            }
        });
    }

    private void sendMessage() {
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               dopost("http://49.234.90.62:8080/ssmtest/insertShuping");
            }
        });
    }

    private void dopost(String url) {
        new Thread(){
            @Override
            public void run() {
                super.run();
                if (sp.getString("Name","未登录").equals("未登录")){
                    Intent intent=new Intent(ShupingActivity.this,LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); //关键的一句，将新的activity置为栈顶
                    startActivity(intent);
                }else
                if (editText.getText().toString().length() == 0) {
                    Toast.makeText(ShupingActivity.this, "内容不能为空！", Toast.LENGTH_LONG).show();}
                else {
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");
                    Date date = new Date(System.currentTimeMillis());
                    String nowdate = simpleDateFormat.format(date);
                    ShupingPostbean shupingPostbean = new ShupingPostbean(sp.getString("Name","未登录")
                            ,editText.getText().toString()
                            ,nowdate
                            ,thisbook);
                    Gson gson = new Gson();
                    String json = gson.toJson(shupingPostbean);
                    ShupingPostbean bean = gson.fromJson(json,ShupingPostbean.class);
                    OkHttpClient client = new OkHttpClient();
                    RequestBody body = RequestBody.create(json, MediaType.parse("application/json"));
                    final Request request = new Request.Builder()
                            .url(url)
                            .post(body)
                            .build();
                    Call call = client.newCall(request);
                    call.enqueue(new Callback() {
                        @Override
                        public void onFailure(@NotNull Call call, @NotNull IOException e) {
                            handler.sendEmptyMessage(1);
                        }

                        @Override
                        public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                            if (response==null){
                                handler.sendEmptyMessage(500);
                            }else {
                                final String result=response.body().string();
                                handler.sendEmptyMessage(2);
                            }

                        }
                    });
                }
            }
        }.start();

    }

    public class ShupingAdapter extends BaseAdapter{

        public ShupingAdapter() {

        }

        @Override
        public int getCount() {
            return mydataList.size();
        }

        @Override
        public Object getItem(int position) {
            return mydataList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ShuPingBean.Mydata.listdata mydata = mydataList.get(position);
            ViewHolder viewHolder = new ViewHolder();
            if (convertView==null){
                convertView = getLayoutInflater().inflate(R.layout.item_shuping,parent,false);
                viewHolder.group_username = convertView.findViewById(R.id.group_username);
                viewHolder.group_content = convertView.findViewById(R.id.group_content);
                viewHolder.group_time = convertView.findViewById(R.id.group_time);
                convertView.setTag(viewHolder);
            }else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            viewHolder.group_username.setText(mydata.getUserName());
            viewHolder.group_content.setText(mydata.getContent());
            viewHolder.group_time.setText(mydata.getCreatDate());
            return convertView;
        }

        class ViewHolder{
            private TextView group_username,group_time,group_content;
        }
    }

    public static class MyHandler extends Handler{
        private WeakReference<ShupingActivity> weakReference;

        public MyHandler(ShupingActivity activity){
            weakReference = new WeakReference<>(activity);
        }
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            ShupingActivity shupingActivity = weakReference.get();
            switch (msg.what){
                case 0:
                    Toast.makeText(shupingActivity, "与服务器连接失败", Toast.LENGTH_LONG).show();
                    break;
                case 1:
                    Toast.makeText(shupingActivity, "与服务器连接失败", Toast.LENGTH_LONG).show();
                    break;
                case 2:
                    Toast.makeText(shupingActivity, "评论成功！", Toast.LENGTH_LONG).show();
                    shupingActivity.editText.clearFocus();
                    shupingActivity.editText.setText("");
                    shupingActivity.hideInput();
                    shupingActivity.inithttp();
                    break;
                case 500:
                    Toast.makeText(shupingActivity, "从服务器获取数据失败！", Toast.LENGTH_LONG).show();
            }
        }
    }
//    public void showInput(final EditText et) {
//        et.requestFocus();
//        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
//        imm.showSoftInput(et, InputMethodManager.SHOW_IMPLICIT);
//    }
    protected void hideInput() {
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        View v = getWindow().peekDecorView();
        if (null != v) {
            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
        }
    }
}