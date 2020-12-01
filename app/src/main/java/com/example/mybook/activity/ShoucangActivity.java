package com.example.mybook.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.mybook.bean.BookListResult;
import com.example.mybook.R;
import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.FileAsyncHttpResponseHandler;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;

public class ShoucangActivity extends AppCompatActivity implements com.example.mybook.Interface.callbackValue {
    private ListView listView;
    private List<BookListResult.BookData> books = new ArrayList<>();
    private AsyncHttpClient client;
    private SharedPreferences sharedPreferences;
    private com.example.mybook.Interface.callbackValue callbackValue;
    private int p;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shoucang);
        sharedPreferences = getSharedPreferences("shoucang",Context.MODE_PRIVATE);
        init();//初始化控件
        inithttp();//初始化http通信
    }


    private void init() {
        listView = findViewById(R.id.shoucang_listview);
    }

    private void inithttp() {
        String url = "http://49.234.90.62:8080/ssmtest/BookListResult";
        client = new AsyncHttpClient();
        client.setConnectTimeout(3000);
        client.get(url, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {
                if (bytes==null){
                    Toast.makeText(ShoucangActivity.this,"数据获取失败！",Toast.LENGTH_SHORT).show();
                }else {
                    final String result = new String(bytes);
                    Gson gson = new Gson();
                    BookListResult bookListResult = gson.fromJson(result, BookListResult.class);
                    books = bookListResult.getData();
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    for(int j = books.size()-1;j>=0;j--){
                        BookListResult.BookData info = books.get(j);
                        String str = sharedPreferences.getString(info.getBookname(),null);
                        if (info!=null&&!info.getBookname().equals(str)){
                            Log.e("BookName1",str+"");
                            Log.e("BookName2",info.getBookname());
                            books.remove(j);
                            Log.e("remove",j+"");
                        }

                    }
                    BooKListAdapter adapter = new BooKListAdapter(ShoucangActivity.this, books);
                    listView.setAdapter(adapter);
                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            ShupingActivity.start(ShoucangActivity.this,books.get(position).getBookname());
                        }
                    });
                    listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                        @Override
                        public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                            final File f = new File(ShoucangActivity.this.getExternalFilesDir("txt") + "/KCYD/" + books.get(position).getBookname()+".txt");
                            if (f.exists()){
                                AlertDialog.Builder builder = new AlertDialog.Builder(ShoucangActivity.this);
                                builder.setTitle("提示");
                                builder.setMessage("您确定要删除"+books.get(position).getBookname()+"吗？");
                                builder.setNegativeButton("确定", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        f.delete();
                                        adapter.notifyDataSetChanged();
                                    }
                                });
                                builder.setPositiveButton("取消",null);
                                builder.show();
                            }else {
                                Toast.makeText(ShoucangActivity.this,books.get(position).getBookname(),Toast.LENGTH_SHORT).show();
                            }
                            return true;
                        }
                    });
                }

            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                Toast.makeText(ShoucangActivity.this,"与服务器连接失败！",Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void sendValue(String value) {
        BookActivity.start(this,value);
    }

    private class BooKListAdapter extends BaseAdapter {
        private List<BookListResult.BookData> mdata;
        private Context mcontext;

        public BooKListAdapter(Context context,List<BookListResult.BookData> list) {
            this.mcontext = context;
            this.mdata = list;
        }

        @Override
        public int getCount() {
            return mdata.size();
        }

        @Override
        public Object getItem(int position) {
            return mdata.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }


        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            final BookListResult.BookData book = mdata.get(position);
            notifyDataSetChanged();
            final SharedPreferences.Editor editor = sharedPreferences.edit();
            ShoucangActivity.BooKListAdapter.ViewHolder viewHolder = new ShoucangActivity.BooKListAdapter.ViewHolder();
            if (convertView == null){
                convertView = getLayoutInflater().inflate(R.layout.item_shujia_shoucang_search,null);
                viewHolder.mNameTextView = convertView.findViewById(R.id.tv_title2);
                viewHolder.mButton = convertView.findViewById(R.id.btn_download);
                viewHolder.shoucang = convertView.findViewById(R.id.btn_shoucang);
                viewHolder.imageView = convertView.findViewById(R.id.shujia_imageview);
                convertView.setTag(viewHolder);
            }else {
                viewHolder = (ShoucangActivity.BooKListAdapter.ViewHolder)convertView.getTag();
            }
            if (book.getBookname()!=null&&viewHolder.imageView!=null){
                String imgurl = "http://49.234.90.62:8080/bookimg/"+book.getBookname()+".jpg";
                Glide.with(mcontext)
                        .load(imgurl)
                        .into(viewHolder.imageView);
            }
            viewHolder.mNameTextView.setText(book.getBookname());
            viewHolder.mButton.setText("点击下载");
            viewHolder.shoucang.setText(sharedPreferences.getString(book.getBookname(),null)==null?"收藏":"取消收藏");
            final ShoucangActivity.BooKListAdapter.ViewHolder finalViewHolder1 = viewHolder;
            viewHolder.shoucang.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (sharedPreferences.getString(book.getBookname(),null)==null){
                        editor.putString(book.getBookname(),book.getBookname());
                        editor.commit();
                        Toast.makeText(ShoucangActivity.this,"收藏成功!",Toast.LENGTH_SHORT).show();
                        finalViewHolder1.shoucang.setText("已收藏");
                    }else {
                        editor.remove(book.getBookname());
                        editor.commit();
                        Toast.makeText(ShoucangActivity.this,"已取消收藏!",Toast.LENGTH_SHORT).show();
                        finalViewHolder1.shoucang.setText("收藏");
                        inithttp();
                    }
                }
            });
            final ShoucangActivity.BooKListAdapter.ViewHolder finalViewHolder = viewHolder;
            final String path = mcontext.getExternalFilesDir("txt") + "/KCYD/" + book.getBookname()+".txt";
            final File file = new File(path);
            viewHolder.mButton.setText(file.exists()?"点击打开":"点击下载");
            viewHolder.mButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //下载书籍   or  打开书籍
                    if (file.exists()){
                        //todo:打开书籍
                        sendValue(path);
                    }else {
                        client.addHeader("Accept-Encoding","identity");
                        client.get("http://49.234.90.62:8080"+book.getBookfile(), new FileAsyncHttpResponseHandler(file) {
                            @Override
                            public void onFailure(int i, Header[] headers, Throwable throwable, File file) {
                                finalViewHolder.mButton.setText("下载失败");
                            }

                            @Override
                            public void onSuccess(int i, Header[] headers, File file) {
                                finalViewHolder.mButton.setText("点击打开");
                            }

                            @Override
                            public void onProgress(long bytesWritten, long totalSize) {
                                super.onProgress(bytesWritten, totalSize);
                                finalViewHolder.mButton.setText(String.valueOf(bytesWritten*100/totalSize)+"%");
                            }
                        });
                    }

                }
            });

            return convertView;
        }
        class ViewHolder{
            public TextView mNameTextView;
            public Button mButton,shoucang;
            public ImageView imageView;
        }
    }
}
