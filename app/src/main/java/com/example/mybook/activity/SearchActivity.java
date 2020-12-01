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
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.mybook.bean.BookListResult;
import com.example.mybook.R;
import com.example.mybook.Interface.callbackValue;
import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.FileAsyncHttpResponseHandler;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;

public class SearchActivity extends AppCompatActivity implements callbackValue {
    private SearchView searchView;
    private TextView cancel;
    private List<BookListResult.BookData> list = new ArrayList<>();
    private AsyncHttpClient client;
    private SharedPreferences sharedPreferences;
    private ListView resultlist;
    private String url = "http://49.234.90.62:8080/ssmtest/findBookForName/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        client = new AsyncHttpClient();
        client.setConnectTimeout(3000);
        init();
    }

    private void init() {
        resultlist = findViewById(R.id.result_list);
        searchView = findViewById(R.id.searchview);
        searchView.setIconifiedByDefault(false);
        cancel = findViewById(R.id.tv_cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SearchActivity.this.finish();
            }
        });
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Log.e("TAG2",query);
                if (query.equals(" ")){
                    Toast.makeText(SearchActivity.this,"请输入您要搜索的内容！",Toast.LENGTH_SHORT).show();
                }else {
                    client.get(url+query, new AsyncHttpResponseHandler() {
                        @Override
                        public void onSuccess(int i, Header[] headers, byte[] bytes) {
                                final String result = new String(bytes);
                                Gson gson = new Gson();
                                BookListResult bookListResult = gson.fromJson(result, BookListResult.class);
                                list = bookListResult.getData();
                                if (list.size()>0){
                                    final SearchActivity.BooKListAdapter adapter = new SearchActivity.BooKListAdapter(SearchActivity.this);
                                    resultlist.setAdapter(adapter);
                                    resultlist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                        @Override
                                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                            ShupingActivity.start(SearchActivity.this,list.get(position).getBookname());
                                        }
                                    });
                                    resultlist.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                                        @Override
                                        public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                                            final File f = new File(SearchActivity.this.getExternalFilesDir("txt") + "/KCYD/" + list.get(position).getBookname()+".txt");
                                            if (f.exists()){
                                                AlertDialog.Builder builder = new AlertDialog.Builder(SearchActivity.this);
                                                builder.setTitle("提示");
                                                builder.setMessage("您确定要删除"+list.get(position).getBookname()+"吗？");
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
                                                Toast.makeText(SearchActivity.this,list.get(position).getBookname(),Toast.LENGTH_SHORT).show();
                                            }
                                            return true;
                                        }
                                    });
                                }else {
                                    Toast.makeText(SearchActivity.this,"搜索内容为空！",Toast.LENGTH_SHORT).show();
                                    client.get("http://49.234.90.62:8080/ssmtest/BookListResult", new AsyncHttpResponseHandler() {
                                        @Override
                                        public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                                            Toast.makeText(SearchActivity.this,"为您推荐以下几本书籍！",Toast.LENGTH_SHORT).show();
                                            final String result = new String(responseBody);
                                            Gson gson = new Gson();
                                            BookListResult bookListResult = gson.fromJson(result, BookListResult.class);
                                            list = bookListResult.getData();
                                            final SearchActivity.BooKListAdapter adapter = new SearchActivity.BooKListAdapter(SearchActivity.this);
                                            resultlist.setAdapter(adapter);
                                            resultlist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                                @Override
                                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                                    ShupingActivity.start(SearchActivity.this,list.get(position).getBookname());
                                                }
                                            });
                                            resultlist.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                                                @Override
                                                public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                                                    final File f = new File(SearchActivity.this.getExternalFilesDir("txt") + "/KCYD/" + list.get(position).getBookname()+".txt");
                                                    if (f.exists()){
                                                        AlertDialog.Builder builder = new AlertDialog.Builder(SearchActivity.this);
                                                        builder.setTitle("提示");
                                                        builder.setMessage("您确定要删除"+list.get(position).getBookname()+"吗？");
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
                                                        Toast.makeText(SearchActivity.this,list.get(position).getBookname(),Toast.LENGTH_SHORT).show();
                                                    }
                                                    return true;
                                                }
                                            });
                                        }

                                        @Override
                                        public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

                                        }
                                    });

                                }

                        }

                        @Override
                        public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                            Toast.makeText(SearchActivity.this,"与服务器连接失败！",Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                Log.e("TAG",newText);
                if (newText.length()>0){
                    if (newText.substring(0,1).equals(" ")){

                    }else {
                        client.get(url+newText, new AsyncHttpResponseHandler() {
                            @Override
                            public void onSuccess(int i, Header[] headers, byte[] bytes) {
                                onSeccess(bytes);
                            }

                            @Override
                            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                                Toast.makeText(SearchActivity.this,"与服务器连接失败！",Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }


                return false;
            }
        });
    }

    private void onSeccess(byte[] bytes) {
        if (bytes==null){
            Toast.makeText(SearchActivity.this,"获取数据失败！",Toast.LENGTH_SHORT).show();
        }else {
            final String result = new String(bytes);
            Gson gson = new Gson();
            BookListResult bookListResult = gson.fromJson(result, BookListResult.class);
            list = bookListResult.getData();
            final BooKListAdapter adapter = new BooKListAdapter(SearchActivity.this);
            resultlist.setAdapter(adapter);
            resultlist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    ShupingActivity.start(SearchActivity.this,list.get(position).getBookname());
                }
            });
            resultlist.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                    final File f = new File(SearchActivity.this.getExternalFilesDir("txt") + "/KCYD/" + list.get(position).getBookname()+".txt");
                    if (f.exists()){
                        AlertDialog.Builder builder = new AlertDialog.Builder(SearchActivity.this);
                        builder.setTitle("提示");
                        builder.setMessage("您确定要删除"+list.get(position).getBookname()+"吗？");
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
                        Toast.makeText(SearchActivity.this,list.get(position).getBookname(),Toast.LENGTH_SHORT).show();
                    }
                    return true;
                }
            });
        }
    }


    private class BooKListAdapter extends BaseAdapter {
        private Context mcontext;

        public BooKListAdapter(Context context) {
            this.mcontext = context;
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int position) {
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }


        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            final BookListResult.BookData book = list.get(position);
            sharedPreferences = getSharedPreferences("shoucang",MODE_PRIVATE);
            final SharedPreferences.Editor editor = sharedPreferences.edit();
            SearchActivity.BooKListAdapter.ViewHolder viewHolder = new SearchActivity.BooKListAdapter.ViewHolder();
            if (convertView == null){
                convertView = getLayoutInflater().inflate(R.layout.item_shujia_shoucang_search,null);
                viewHolder.mNameTextView = convertView.findViewById(R.id.tv_title2);
                viewHolder.mButton = convertView.findViewById(R.id.btn_download);
                viewHolder.shoucang = convertView.findViewById(R.id.btn_shoucang);
                viewHolder.imageView = convertView.findViewById(R.id.shujia_imageview);
                convertView.setTag(viewHolder);
            }else {
                viewHolder = (SearchActivity.BooKListAdapter.ViewHolder)convertView.getTag();
            }
            Log.e("TAG",book.getBookname());
            if (book.getBookname()!=null&&viewHolder.imageView!=null){
                String imgurl = "http://49.234.90.62:8080/bookimg/"+book.getBookname()+".jpg";
                Glide.with(SearchActivity.this)
                        .load(imgurl)
                        .into(viewHolder.imageView);
            }
//            viewHolder.mNameTextView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    ShupingActivity.start(SearchActivity.this,book.getBookname());
//                    Log.e("TAG","点击了"+book.getBookname());
//                }
//            });
            viewHolder.mNameTextView.setText(book.getBookname());
            viewHolder.mButton.setText("点击下载");
            viewHolder.shoucang.setText(sharedPreferences.getString(book.getBookname(),null)==null?"收藏":"取消收藏");
            final SearchActivity.BooKListAdapter.ViewHolder finalViewHolder1 = viewHolder;
            viewHolder.shoucang.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (sharedPreferences.getString(book.getBookname(),null)==null){
                        editor.putString(book.getBookname(),book.getBookname());
                        editor.commit();
                        Toast.makeText(SearchActivity.this,"收藏成功!",Toast.LENGTH_SHORT).show();
                        finalViewHolder1.shoucang.setText("已收藏");
                    }else {
                        editor.remove(book.getBookname());
                        editor.commit();
                        Toast.makeText(SearchActivity.this,"已取消收藏!",Toast.LENGTH_SHORT).show();
                        finalViewHolder1.shoucang.setText("收藏");
                    }
                }
            });
            final SearchActivity.BooKListAdapter.ViewHolder finalViewHolder = viewHolder;
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

    @Override
    public void sendValue(String value) {
        BookActivity.start(this,value);
    }
}
