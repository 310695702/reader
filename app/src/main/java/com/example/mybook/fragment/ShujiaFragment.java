package com.example.mybook.fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.mybook.bean.BookListResult;
import com.example.mybook.R;
import com.example.mybook.activity.ShupingActivity;
import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.FileAsyncHttpResponseHandler;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;


public class ShujiaFragment extends Fragment{

    private ListView listView;
    private List<BookListResult.BookData> books = new ArrayList<>();
    private AsyncHttpClient client;
    private View rootView;
    private SharedPreferences sharedPreferences;

    //回调接口，用来传值 给目标activity，经过中介activity再向目标传值
    private com.example.mybook.Interface.callbackValue callbackValue;
    private String url = "http://49.234.90.62:8080/ssmtest/BookListResult";
    private boolean isCreate = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (rootView==null){
            Log.e("TAG","onCreateView");
            isCreate =true;
            rootView = inflater.inflate(R.layout.fragment_shujia,container,false);
            listView = rootView.findViewById(R.id.shujia_listview);
            client = new AsyncHttpClient();
            client.setConnectTimeout(3000);
            client.get(url, new AsyncHttpResponseHandler() {
                        @Override
                        public void onSuccess(int i, Header[] headers, byte[] bytes) {
                            if(bytes==null){
                                Toast.makeText(getContext(),"数据获取失败!",Toast.LENGTH_SHORT).show();
                                Log.e("TAG","onSuccess");
                            }else {
                                Log.e("TAG","onSuccess and result!=null");
                                final String result = new String(bytes);
                                Gson gson = new Gson();
                                BookListResult bookListResult = gson.fromJson(result, BookListResult.class);
                                books = bookListResult.getData();
                                final BooKListAdapter adapter = new BooKListAdapter(getContext());
                                listView.setAdapter(adapter);
                                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                        ShupingActivity.start(getContext(),books.get(position).getBookname());
                                    }
                                });
                                listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                                    @Override
                                    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                                        final File f = new File(getContext().getExternalFilesDir("txt") + "/KCYD/" + books.get(position).getBookname()+".txt");
                                        if (f.exists()){
                                            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
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
                                            Toast.makeText(getContext(),books.get(position).getBookname(),Toast.LENGTH_SHORT).show();
                                        }
                                        return true;
                                    }
                                });
                            }

                        }

                        @Override
                        public void onRetry(int retryNo) {
                            super.onRetry(retryNo);
                            Toast.makeText(getContext(),"正在重新连接服务器第"+retryNo+"次...",Toast.LENGTH_SHORT).show();

                        }

                        @Override
                        public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                            Toast.makeText(getContext(),"与服务器连接失败!",Toast.LENGTH_SHORT).show();
                            Log.e("TAG","onFailure");
                        }
                    });
                }

        ViewGroup parent = (ViewGroup) rootView.getParent();
        if (parent!=null){
            parent.removeView(rootView);
            isCreate = false;
        }
        return rootView;
    }


    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser&&isCreate){
                    client.get(url, new AsyncHttpResponseHandler() {
                        @Override
                        public void onSuccess(int i, Header[] headers, byte[] bytes) {
                            if(bytes==null){
                                Toast.makeText(getContext(),"数据获取失败!",Toast.LENGTH_SHORT).show();
                            }else {
                                final String result = new String(bytes);
                                Gson gson = new Gson();
                                BookListResult bookListResult = gson.fromJson(result, BookListResult.class);
                                books = bookListResult.getData();
                                final BooKListAdapter adapter = new BooKListAdapter(getContext());
                                listView.setAdapter(adapter);
                                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                        ShupingActivity.start(getContext(),books.get(position).getBookname());
                                    }
                                });
                                listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                                    @Override
                                    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                                        final File f = new File(getContext().getExternalFilesDir("txt") + "/KCYD/" + books.get(position).getBookname()+".txt");
                                        if (f.exists()){
                                            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
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
                                            Toast.makeText(getContext(),books.get(position).getBookname(),Toast.LENGTH_SHORT).show();
                                        }
                                        return true;
                                    }
                                });
                            }

                        }

                        @Override
                        public void onRetry(int retryNo) {
                            super.onRetry(retryNo);
                            Toast.makeText(getContext(),"正在重新连接服务器第"+retryNo+"次...",Toast.LENGTH_SHORT).show();

                        }

                        @Override
                        public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                            Toast.makeText(getContext(),"与服务器连接失败!",Toast.LENGTH_SHORT).show();
                        }
                    });
        }else {
        }
    }

    private class BooKListAdapter extends BaseAdapter {
        private Context mcontext;

        public BooKListAdapter(Context context) {
            this.mcontext = context;
        }

        @Override
        public int getCount() {
            return books.size();
        }

        @Override
        public Object getItem(int position) {
            return books.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            final BookListResult.BookData book = books.get(position);
            Log.e("TAG",book.getBookfile());
            ViewHolder viewHolder = new ViewHolder();
            if (convertView == null){
                convertView = getLayoutInflater().inflate(R.layout.item_shujia_shoucang_search,null);
                viewHolder.mNameTextView = convertView.findViewById(R.id.tv_title2);
                viewHolder.mButton = convertView.findViewById(R.id.btn_download);
                viewHolder.shoucang = convertView.findViewById(R.id.btn_shoucang);
                viewHolder.imageView = convertView.findViewById(R.id.shujia_imageview);
                convertView.setTag(viewHolder);
            }else {
                viewHolder = (ViewHolder)convertView.getTag();
            }

            if (book.getBookname()!=null&&viewHolder.imageView!=null){
                String imgurl = "http://49.234.90.62:8080/bookimg/"+book.getBookname()+".jpg";
                Glide.with(getContext())
                        .load(imgurl)
                        .into(viewHolder.imageView);
            }
            viewHolder.mNameTextView.setText(book.getBookname());
            viewHolder.mButton.setText("点击下载");
           sharedPreferences = mcontext.getSharedPreferences("shoucang",Context.MODE_PRIVATE);
           final SharedPreferences.Editor editor = sharedPreferences.edit();
            viewHolder.shoucang.setText(sharedPreferences.getString(book.getBookname(),null)==null?"收藏":"取消收藏");
            final ViewHolder finalViewHolder1 = viewHolder;
            viewHolder.shoucang.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (sharedPreferences.getString(book.getBookname(),null)==null){
                        editor.putString(book.getBookname(),book.getBookname());
                        editor.commit();
                        Toast.makeText(getContext(),"收藏成功!",Toast.LENGTH_SHORT).show();
                        finalViewHolder1.shoucang.setText("已收藏");
                    }else {
                        editor.remove(book.getBookname());
                        editor.commit();
                        Toast.makeText(getContext(),"已取消收藏!",Toast.LENGTH_SHORT).show();
                        finalViewHolder1.shoucang.setText("收藏");
                    }
                }
            });
            final ViewHolder finalViewHolder = viewHolder;
            final String path = mcontext.getExternalFilesDir("txt") + "/KCYD/" + book.getBookname()+".txt";
            final File file = new File(path);
            Log.e("TAG",book.getBookfile()+"");
            viewHolder.mButton.setText(file.exists()?"点击打开":"点击下载");
            viewHolder.mButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //下载书籍   or  打开书籍
                    if (file.exists()){
                        //todo:打开书籍
                        callbackValue.sendValue(path);
                    }else {
                        client.addHeader("Accept-Encoding","identity");
                        client.get("http://49.234.90.62:8080"+book.getBookfile(), new FileAsyncHttpResponseHandler(file) {
                            @Override
                            public void onFailure(int i, Header[] headers, Throwable throwable, File file) {
                                finalViewHolder.mButton.setText("下载失败");
                                return;
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
    public void onAttach(Context context) {
        super.onAttach(context);
        callbackValue = (com.example.mybook.Interface.callbackValue) getActivity();
    }
}