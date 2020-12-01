package com.example.mybook.fragment;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.example.mybook.bean.Msg;
import com.example.mybook.helper.MsgLab;
import com.example.mybook.R;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.FileAsyncHttpResponseHandler;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;

public class FaxianFragment extends Fragment {
    private ListView listView;
    private List<Msg> list = new ArrayList<>();
    private MyListAdapter adapter;
    private View rootView;
    private AsyncHttpClient client;
    private com.example.mybook.Interface.callbackValue callbackValue;
    private String name;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
       if (rootView==null){
           rootView = inflater.inflate(R.layout.fragment_faxian,container,false);
           listView = rootView.findViewById(R.id.faxian_listview);
           list.addAll(MsgLab.List());//为list添加数据
           adapter = new MyListAdapter(getContext(),list);//实例化Adapter
           listView.setAdapter(adapter);//为list添加适配器
           client = new AsyncHttpClient();//实例化AsyncHttpClient
           listclick();//自定义list点击方法
           listLongclick();//自定义list长按方法
       }
       ViewGroup parent = (ViewGroup) rootView.getParent();
       if (parent!=null){
           parent.removeView(rootView);
       }

        return rootView;
    }

    private void listLongclick() {
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position){
                    case 0:
                        name = "傲慢与偏见";
                        del(name);
                        break;
                    case 1:
                        name = "百年孤独";
                        del(name);
                        break;
                    case 2:
                        name = "草房子";
                        del(name);
                        break;
                    case 3:
                        name = "行者无疆";
                        del(name);
                        break;
                    case 5:
                        name ="骆驼祥子";
                        del(name);
                        break;
                    case 6:
                        name ="平凡的世界";
                        del(name);
                        break;
                    case 7:
                        name ="偷影子的人";
                        del(name);
                        break;
                    case 8:
                        name ="月亮与六便士";
                        del(name);
                        break;
                    case 4:
                        Toast.makeText(getContext(),"暂无书源！",Toast.LENGTH_SHORT).show();
                        break;
                }
                return true;
            }
        });
    }

    //自定义删除方法
    private void del(String name){
        //获取文件路径
        final File f = new File(getActivity().getExternalFilesDir("txt") + "/KCYD/" + name+".txt");
       //判断文件是否存在
        if (f.exists()){
            android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(getContext());
            builder.setTitle("提示");
            builder.setMessage("您确定要删除"+name+"吗？");
            builder.setNegativeButton("确定", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    f.delete();
                }
            });
            builder.setPositiveButton("取消",null);
            builder.show();
        }else {
            Toast.makeText(getContext(),name,Toast.LENGTH_SHORT).show();
        }
    }


    private void listclick() {
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position){
                    case 0:
                        name = "傲慢与偏见";
                        Open(name);
                        break;
                    case 1:
                        name = "百年孤独";
                        Open(name);
                        break;
                    case 2:
                        name = "草房子";
                        Open(name);
                        break;
                    case 3:
                        name = "行者无疆";
                        Open(name);
                        break;
                    case 5:
                        name ="骆驼祥子";
                        Open(name);
                        break;
                    case 6:
                        name ="平凡的世界";
                        Open(name);
                        break;
                    case 7:
                        name ="偷影子的人";
                        Open(name);
                        break;
                    case 8:
                        name ="月亮与六便士";
                        Open(name);
                        break;
                    case 4:
                        Toast.makeText(getContext(),"暂无书源！",Toast.LENGTH_SHORT).show();
                        break;
                }
            }

            //自定义打开书本方法
            private void Open(final String name) {
                //获取路径
                final File file = new File(getActivity().getExternalFilesDir("txt") + "/KCYD/" + name+".txt");
                if (file.exists()){
                    //todo:打开书籍
                    callbackValue.sendValue(getActivity().getExternalFilesDir("txt") + "/KCYD/" + name+".txt");
                }else {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("提示:");
                builder.setMessage("您要下载"+name+"吗？");
                //设置确定按钮
                builder.setNegativeButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String names =name;
                        Toast.makeText(getContext(),"下载中",Toast.LENGTH_SHORT).show();
                        client.addHeader("Accept-Encoding","identity");
                        client.get("http://49.234.90.62:8080/novel/书评/"+names+"/"+names+".txt", new FileAsyncHttpResponseHandler(file) {


                            @Override
                            public void onFailure(int i, Header[] headers, Throwable throwable, File file) {
                                Toast.makeText(getContext(),"下载失败！请重试",Toast.LENGTH_SHORT).show();
                                file.delete();
                            }

                            @Override
                            public void onSuccess(int i, Header[] headers, File file) {
                                if (file==null){
                                    file.delete();
                                    Toast.makeText(getContext(),"下载失败！请重试",Toast.LENGTH_SHORT).show();
                                }else {
                                    Toast.makeText(getContext(),"下载成功！再次点击打开",Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onProgress(long bytesWritten, long totalSize) {
                                super.onProgress(bytesWritten, totalSize);
                            }
                        }); }
                });
                //设置取消按钮
                builder.setPositiveButton("取消",null);
                builder.show();}
            }
        });
    }



    private class MyListAdapter extends BaseAdapter{

        private Context mcontext;
        private LayoutInflater mInflater;
        private List<Msg> mdatas;

        public MyListAdapter(Context context, List<Msg> list) {
            this.mcontext = context;
            this.mInflater = LayoutInflater.from(mcontext);
            this.mdatas = list;
        }

        @Override
        public int getCount() {
            return mdatas.size();
        }

        @Override
        public Object getItem(int position) {
            return mdatas.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
           ViewHolder viewHolder =null;
            if (convertView==null){
                convertView = mInflater.inflate(R.layout.item_faxian,parent,false);
                viewHolder = new ViewHolder();
                viewHolder.imageView = convertView.findViewById(R.id.faxian_imageview);
                viewHolder.textView_title = convertView.findViewById(R.id.faxian_title);
                viewHolder.textView_content = convertView.findViewById(R.id.faxian_content);
                convertView.setTag(viewHolder);
            }else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            Msg msg = mdatas.get(position);
            viewHolder.imageView.setImageResource(msg.getImgResId());
            viewHolder.textView_title.setText(msg.getTitle());
            viewHolder.textView_content.setText(msg.getContent());

            return convertView;
        }

        public class ViewHolder{
            ImageView imageView;
            TextView textView_title;
            TextView textView_content;
        }
    }
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        callbackValue = (com.example.mybook.Interface.callbackValue) getActivity();//实例化callbackvalue
    }
}
