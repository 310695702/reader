package com.example.mybook.activity;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.Toast;

import com.example.mybook.fragment.MainFragment;
import com.example.mybook.R;
import com.example.mybook.Interface.callbackValue;

public class MainActivity extends AppCompatActivity implements callbackValue {
    private SharedPreferences sp;
    private SharedPreferences.Editor editor;

    public static void start(Context context){
        Intent intent = new Intent(context,MainActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toast.makeText(this,"欢迎来到科成阅读！",Toast.LENGTH_LONG).show();
        sp = getSharedPreferences("login",Context.MODE_PRIVATE);
        editor = sp.edit();
        if (sp.getString("Phone","null").equals("null")){
            editor.putString("Phone","null");
            editor.putString("Password","null");
            editor.putString("Name","未登录");
            editor.putString("Check","false");
            editor.commit();
        }




        int permission = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int permission2 = ContextCompat.checkSelfPermission(this,Manifest.permission.READ_EXTERNAL_STORAGE);
        if (permission!= PackageManager.PERMISSION_GRANTED&&permission2 != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE},1);
        }
        MainFragment mainFragment = new MainFragment();
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().add(R.id.container,mainFragment).commitAllowingStateLoss();
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode==KeyEvent.KEYCODE_BACK){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("提示:");
            builder.setMessage("您确定退出？");
            //设置确定按钮
            builder.setNegativeButton("确定", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    finish();
                }
            });
            //设置取消按钮
            builder.setPositiveButton("容我再想想",null);
            builder.show();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void sendValue(String value) {
        BookActivity.start(this,value);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (sp.getString("Check","false").equals("false")&&!sp.getString("Name","未登录").equals("未登录")){
            editor.putString("Phone","null");
            editor.putString("Password","null");
            editor.putString("Name","未登录");
            editor.putString("Check","false");
            editor.commit();
        }else {
        }
    }
}
