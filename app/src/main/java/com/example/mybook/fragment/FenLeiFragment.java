package com.example.mybook.fragment;


import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

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

public class FenLeiFragment extends Fragment {
    private ListView listView;
    private GridView gridView;
    private List<BookListResult.BookData> books = new ArrayList<>();
    private List<BookListResult.BookData> mlist = new ArrayList<>();
    private AsyncHttpClient client;
    private com.example.mybook.Interface.callbackValue callbackValue;
    private View rootView;
    private Gson gson = new Gson();
    String listurl = "http://49.234.90.62:8080/ssmtest/BookClassResult",
    urlforresult = "http://49.234.90.62:8080/ssmtest/FindBookForClass?bookclass=";
    private boolean isCreate = false;
    private FenleiAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (rootView==null){
            isCreate=true;
            rootView = inflater.inflate(R.layout.fragment_fenlei,container,false);
            listView = rootView.findViewById(R.id.fenlei_listview);
            gridView = rootView.findViewById(R.id.fenlei_GridView);
            client = new AsyncHttpClient();
            client.setConnectTimeout(3000);
                    client.get(listurl, new AsyncHttpResponseHandler() {
                        @Override
                        public void onSuccess(int i, Header[] headers, byte[] bytes) {
                            if (bytes==null){
                                Toast.makeText(getContext(),"数据获取失败!",Toast.LENGTH_SHORT).show();
                            }else {
                                final String result = new String(bytes);
                                BookListResult bookListResult = gson.fromJson(result, BookListResult.class);
                                mlist = bookListResult.getData();
                                adapter = new FenleiAdapter(getContext(),mlist);
                                listView.setAdapter(adapter);
                                get(urlforresult+mlist.get(0).getBookclass());
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
                    listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                        @Override
                        public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                            String str = mlist.get(position).bookclass;
                            Toast.makeText(getContext(),str,Toast.LENGTH_SHORT).show();
                            return true;
                        }
                    });
                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            String str = mlist.get(position).bookclass;
                            get(urlforresult+str);
                            adapter.selectedItemPosition(position);
                            adapter.notifyDataSetChanged();
                        }
                    });

        }
        ViewGroup parent = (ViewGroup) rootView.getParent();
        if (parent!=null){
            parent.removeView(rootView);
            isCreate=false;
        }

        return rootView;
    }


    public void get(String url){
        final String a = url;
                client.get(a, new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(int i, Header[] headers, byte[] bytes) {
                        if (bytes==null){
                            Toast.makeText(getContext(),"数据获取失败!",Toast.LENGTH_SHORT).show();
                        }else {
                            final String result = new String(bytes);
                            BookListResult bookListResult = gson.fromJson(result, BookListResult.class);
                            books = bookListResult.getData();
                            gridView.setAdapter(new GridViewAdapter(getContext()));
                            gridView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                                @Override
                                public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                                    final File f = new File(getContext().getExternalFilesDir("txt") + "/KCYD/" + books.get(position).getBookname()+".txt");
                                    if (f.exists()){
                                        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(getContext());
                                        builder.setTitle("提示");
                                        builder.setMessage("您确定要删除"+books.get(position).getBookname()+"吗？");
                                        builder.setNegativeButton("确定", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                f.delete();
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
            }


    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser&&isCreate){
                    client.get(listurl, new AsyncHttpResponseHandler() {
                        @Override
                        public void onSuccess(int i, Header[] headers, byte[] bytes) {
                            if (bytes==null){
                                Toast.makeText(getContext(),"数据获取失败!",Toast.LENGTH_SHORT).show();
                            }else {
                                final String result = new String(bytes);
                                BookListResult bookListResult = gson.fromJson(result, BookListResult.class);
                                mlist = bookListResult.getData();
                                final FenleiAdapter adapter = new FenleiAdapter(getContext(),mlist);
                                listView.setAdapter(adapter);
                                get(urlforresult+mlist.get(0).getBookclass());
                                listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                                    @Override
                                    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                                        String str = mlist.get(position).bookclass;
                                        Toast.makeText(getContext(),str,Toast.LENGTH_SHORT).show();
                                        return true;
                                    }
                                });
                                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                        String str = mlist.get(position).bookclass;
                                        get(urlforresult+str);
                                        adapter.selectedItemPosition(position);
                                        adapter.notifyDataSetChanged();
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

    private class GridViewAdapter extends BaseAdapter{
        private Context mcontext;

        public GridViewAdapter(Context mcontext) {
            this.mcontext = mcontext;
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
        public View getView(int position, View convertView, ViewGroup parent) {
            final BookListResult.BookData book = books.get(position);
            ViewHolder viewHolder = new ViewHolder();
            if (convertView==null){
                convertView = getLayoutInflater().inflate(R.layout.item_fenlei_gridview,null);
                viewHolder.imageView = convertView.findViewById(R.id.gridview_image);
                viewHolder.textView = convertView.findViewById(R.id.gridview_text);
                convertView.setTag(viewHolder);
            }else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            if (book.getBookname()!=null&&viewHolder.imageView!=null){
                String imgurl = "http://49.234.90.62:8080/bookimg/"+book.getBookname()+".jpg";
                Glide.with(getContext())
                        .load(imgurl)
                        .into(viewHolder.imageView);
            }
            viewHolder.textView.setText(book.getBookname());
            final FenLeiFragment.GridViewAdapter.ViewHolder finalViewHolder = viewHolder;
            final String path = mcontext.getExternalFilesDir("txt") + "/KCYD/" + book.getBookname() +".txt";
            final File file = new File(path);
            viewHolder.imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (file.exists()){
                        //todo:打开书籍
                        callbackValue.sendValue(path);
                    }else {
                        AlertDialog.Builder builder = new AlertDialog.Builder(mcontext);
                        builder.setTitle("提示:");
                        builder.setMessage("您要下载"+book.getBookname()+"吗？");
                        //设置确定按钮
                        builder.setNegativeButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //下载书籍   or  打开书籍
                                client.addHeader("Accept-Encoding", "identity");
                                client.get("http://49.234.90.62:8080" + book.getBookfile(), new FileAsyncHttpResponseHandler(file) {
                                    @Override
                                    public void onFailure(int i, Header[] headers, Throwable throwable, File file) {
                                        Toast.makeText(getContext(), "下载失败", Toast.LENGTH_SHORT).show();
                                        file.delete();
                                    }

                                    @Override
                                    public void onSuccess(int i, Header[] headers, File file) {
                                        Toast.makeText(getContext(), "下载成功！", Toast.LENGTH_SHORT).show();
                                        finalViewHolder.textView.setText(book.getBookname());
                                    }

                                    @Override
                                    public void onProgress(long bytesWritten, long totalSize) {
                                        super.onProgress(bytesWritten, totalSize);
                                        finalViewHolder.textView.setText(String.valueOf(bytesWritten * 100 / totalSize) + "%");
                                    }
                                });
                            }
                        });
                        //设置取消按钮
                        builder.setPositiveButton("取消",null);
                        builder.show();
                    }
                }
            });
            return convertView;
        }

        class ViewHolder{
            private ImageView imageView;
            private TextView textView;
        }

    }


    private class FenleiAdapter extends BaseAdapter{
        private List<BookListResult.BookData> mdata;
        private Context mContext;
        private int selectedItemPosition = 0;


        public FenleiAdapter(Context context, List<BookListResult.BookData> list) {
            this.mContext = context;
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
            BookListResult.BookData list = mlist.get(position);
           ViewHolder viewHolder = new ViewHolder();
           if (convertView==null){
               convertView = getLayoutInflater().inflate(R.layout.item_fenlei,null);
               viewHolder.textView = convertView.findViewById(R.id.fenzu_textview);
               viewHolder.cardView = convertView.findViewById(R.id.fenlei_cardview);
               convertView.setTag(viewHolder);
           }else{
               viewHolder = (ViewHolder) convertView.getTag();
           }
           viewHolder.textView.setText(mlist.get(position).bookclass);
           if (position==selectedItemPosition){
               convertView.setBackgroundColor(Color.GRAY);
           }else {
               convertView.setBackgroundColor(Color.WHITE);
           }

           return convertView;
        }

        public void selectedItemPosition(int position) {
            this.selectedItemPosition = position;
        }

        class ViewHolder{
           private TextView textView;
           private CardView cardView;
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        callbackValue = (com.example.mybook.Interface.callbackValue) getActivity();
    }

}
