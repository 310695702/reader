package com.example.mybook.fragment;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.example.mybook.R;
import com.example.mybook.bean.User;
import com.example.mybook.activity.LoginActivity;
import com.example.mybook.activity.MainActivity;
import com.example.mybook.activity.SettingActivity;
import com.example.mybook.activity.ShoucangActivity;

import java.util.ArrayList;
import java.util.List;

public class WodeFragment extends Fragment {
    private View rootview;
    private Context context;
    private SharedPreferences sp;
    private SharedPreferences.Editor editor;
    private List<User> list = new ArrayList<>();
    private TextView gotologin;
    private TextView tv;
    private LinearLayout setting,shoucang;
    private MainActivity activity;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (rootview==null){
            rootview = inflater.inflate(R.layout.fragment_wode,container,false);
            shoucang = rootview.findViewById(R.id.shoucang);
            gotologin = rootview.findViewById(R.id.btn_gotoload);
            setting = rootview.findViewById(R.id.shezhi);
            activity = (MainActivity) getActivity();
            sp = getActivity().getSharedPreferences("login",Context.MODE_PRIVATE);
            editor = sp.edit();
            tv = rootview.findViewById(R.id.user_name);
            shoucang.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(getContext(), ShoucangActivity.class));
                }
            });
        }
        ViewGroup parent = (ViewGroup) rootview.getParent();
        if (parent!=null){
            parent.removeView(rootview);
        }
        context=inflater.getContext();
        return rootview;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if(sp.getString("Name","未登录").equals("未登录")){
            gotologin();
            setting.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent=new Intent(context, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); //关键的一句，将新的activity置为栈顶
                    startActivity(intent);
                    activity.finish();
                }
            });
        }else {
            setting.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                   SettingActivity.start(getContext(),activity);
                }
            });
            gotologin.setText("退出登录");
            gotologin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog alertDialog = new AlertDialog.Builder(getActivity())
                            .setTitle("退出登录")
                            .setMessage("是否退出登录")
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    gotologin.setText("登录");
                                    editor.putString("Phone","null");
                                    editor.putString("Password","null");
                                    editor.putString("Name","未登录");
                                    editor.putString("Check","false");
                                    editor.commit();
                                    tv.setText("未登录");
                                    Intent intentr=new Intent(context,LoginActivity.class);
                                    intentr.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); //关键的一句，将新的activity置为栈顶
                                    startActivity(intentr);
                                    getActivity().finish();
                                }
                            }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    return;
                                }
                            }).create();
                    alertDialog.show();
                }
            });
        }

        tv.setText("未登录");
        tv.setText(sp.getString("Name","null"));
    }

    @Override
    public void onResume() {
        super.onResume();
        tv.setText(sp.getString("Name","未登录"));

    }

    private void gotologin() {
        gotologin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(context,LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); //关键的一句，将新的activity置为栈顶
                startActivity(intent);
                getActivity().finish();
            }
        });
    }


    @Override
    public void onDestroy() {
        super.onDestroy();

    }
}
