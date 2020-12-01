package com.example.mybook.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mybook.R;
import com.example.mybook.view.BookPageBezierHelper;
import com.example.mybook.view.BookPageView;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class BookActivity extends AppCompatActivity {


    public static final String FILE_PATH = "file_path";
    private BookPageView mbookPageView;
    private TextView mprogress;
    private View msettingView;
    private RecyclerView mrecyclerView;
    private int mcurrentLength;
    private BookPageBezierHelper mhelper;
    private Bitmap mnowbitmap;
    private Bitmap mnextbitmap;
    private String mfilePath;
    private int width;
    private int height;
    private int mlastlenth;
    private SharedPreferences sharedPreferences1;
    private SharedPreferences.Editor editor1;
    private TextToSpeech tts;
    private SeekBar mseekBar;
    private int totallength;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book);

        //沉浸式状态栏处理 安卓版本大于4.4可以使用
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }


        //获取书籍地址
        if (getIntent()!=null){
            mfilePath = getIntent().getStringExtra(FILE_PATH);
            if (!TextUtils.isEmpty(mfilePath)){
                totallength = (int) new File(mfilePath).length();
            }
            Log.e("TAG", mfilePath);
        }else {
            //todo:不能找到这本书
        }
        //初始化
        mbookPageView = findViewById(R.id.book_page_view);
        mprogress = findViewById(R.id.tv_progress);
        msettingView = findViewById(R.id.setting_view);
        mrecyclerView = findViewById(R.id.setting_recyclerview);
        mseekBar = findViewById(R.id.seekBar);

        //添加recyclerview数据
        List<String> settingList = new ArrayList<>();
        settingList.add("添加书签");
        settingList.add("读取书签");
        settingList.add("设置背景");
        settingList.add("语言朗读");
        settingList.add("跳转进度");
        LinearLayoutManager layoutManager = new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false);
        mrecyclerView.setLayoutManager(layoutManager);
        mrecyclerView.setAdapter(new HorizontalAdapter(this,settingList));

        mseekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                openBookByProgress(sharedPreferences1.getInt("bg", R.drawable.bg1),seekBar.getProgress()*totallength/100);
                mprogress.setText(String.format("%s%%",progress));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });





        //get Size
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        width = displayMetrics.widthPixels;
        height = displayMetrics.heightPixels;


        sharedPreferences1 = this.getSharedPreferences("background",MODE_PRIVATE);
        editor1 = sharedPreferences1.edit();
        //通过progress打开书籍
        openBookByProgress(sharedPreferences1.getInt("bg", R.drawable.bg1),0);


        //设置progress、
        mhelper.setOnProgressChangedListener(new BookPageBezierHelper.OnProgressChangedListener() {
            @Override
            public void setProgress(int currentLength, int totalLength) {
                mcurrentLength = currentLength;
                float progress = (float) (mcurrentLength*100.0 /totalLength);
                mprogress.setText(String.format("%.2f",progress)+"%");
            }
        });




        //设置usersetting view listener
        mbookPageView.setOnUserNeedSettingListener(new BookPageView.OnUserNeedSettingListener() {
            @Override
            public void onUserNeedSetting() {
                msettingView.setVisibility(msettingView.getVisibility() == View.VISIBLE?View.GONE:View.VISIBLE);
            }
        });



    }

    private void openBookByProgress(int backgroundResourceID,int progress) {
        //set book helper需要传入宽度高度
        mhelper = new BookPageBezierHelper(width, height,progress);
        mbookPageView.setBookPageBezierHelper(mhelper);

        //需要制作2张Bitmap 当前页和下一页
        mnowbitmap = Bitmap.createBitmap(width, height,Bitmap.Config.ARGB_8888);
        mnextbitmap = Bitmap.createBitmap(width, height,Bitmap.Config.ARGB_8888);
        mbookPageView.setBitmaps(mnowbitmap, mnextbitmap);

        //设置背景图片
        mhelper.setBackground(this,backgroundResourceID);

        //OpenBook
        //如果不为空 打开它 如果为空 不能找到
        if (!TextUtils.isEmpty(mfilePath)){
            try {
                Log.e("TAG", mfilePath +"");
                mhelper.openBook(mfilePath);
                //画这本书第一页
                mhelper.draw(new Canvas(mnowbitmap));

            } catch (IOException e) {
                e.printStackTrace();
                //todo:不能找到这本书
            }
        }else {
            //todo:不能找到这本书
        }
        mbookPageView.invalidate();
    }

    public static void start(Context context,String filePath){
        Intent intent = new Intent(context, BookActivity.class);
        intent.putExtra(FILE_PATH,filePath);
        context.startActivity(intent);
    }


    private class HorizontalAdapter extends RecyclerView.Adapter {
        private Context mcontext;
        private List<String> mdata;
        public HorizontalAdapter(Context context, List<String> list) {
            this.mcontext = context;
            this.mdata = list;
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            TextView itemView = new TextView(mcontext);
            return new ViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {
            TextView textView = (TextView) holder.itemView;
            textView.setWidth(240);
            textView.setHeight(180);
            textView.setTextSize(16);
            textView.setTextColor(Color.WHITE);
            textView.setGravity(Gravity.CENTER);
            textView.setText(mdata.get(position));
            final SharedPreferences sharedPreferences = mcontext.getSharedPreferences("book_preference",Context.MODE_PRIVATE);
            final SharedPreferences.Editor editor = sharedPreferences.edit();

            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    switch (position){
                        case 0:
                            //添加书签
                            //1.获取progress
                            //2.save progress to preference
                            if (mcurrentLength!=0){
                                editor.putInt("bookmark",mcurrentLength);
                                editor.commit();
                                Toast.makeText(mcontext,"添加书签成功！",Toast.LENGTH_SHORT).show();
                            }
                            break;
                        case 1:
                            //读取书签
                            //get 书签 from preference
                            //重载书籍to progress
                            mlastlenth = sharedPreferences.getInt("bookmark",0);
                            if (mlastlenth!=0){
                                openBookByProgress(sharedPreferences1.getInt("bg", R.drawable.bg1), mlastlenth);
                            }

                            break;
                        case 2:
                            int background = sharedPreferences1.getInt("bg", R.drawable.bg1);
                            if ((background == R.drawable.bg1)) {
                                background = R.drawable.bg2;
                            } else if (background == R.drawable.bg2){
                                background = R.drawable.bg3;
                            }
                            else if (background == R.drawable.bg3){
                                background = R.drawable.bg4;
                            }
                            else if (background == R.drawable.bg4){
                                background = R.drawable.bg5;
                            }
                            else if (background == R.drawable.bg5){
                                background = R.drawable.bg6;
                            }
                            else if (background == R.drawable.bg6){
                                background = R.drawable.bg7;
                            }
                            else if (background == R.drawable.bg7){
                                background = R.drawable.bg8;
                            }
                            else {
                                background = R.drawable.bg1;
                            }
                            editor1.putInt("bg",background);
                            editor1.apply();
                            openBookByProgress(background, mlastlenth);
                            break;
                        case 3:
                            if (tts==null){
                                tts = new TextToSpeech(mcontext, new TextToSpeech.OnInitListener() {
                                    @Override
                                    public void onInit(int status) {
                                        if (status == TextToSpeech.SUCCESS){
                                            int result = tts.setLanguage(Locale.CHINA);
                                            if (result == TextToSpeech.LANG_MISSING_DATA
                                                    || result == TextToSpeech.LANG_NOT_SUPPORTED){
                                                Log.e("TAG","语言不支持");
                                                Uri uri = Uri.parse("https://mip.onlinedown.net/download/back?http://mobile.appchina.com/market/download/redirect?package=com.iflytek.tts&channel=ext.cop.huajun&p=delivery");
                                                Intent intent = new Intent(Intent.ACTION_VIEW,uri);
                                                Toast.makeText(mcontext,"请在设置-语言和输入法-文字转语音输出中，将下载的插件设置为默认",Toast.LENGTH_LONG).show();
                                                startActivity(intent);
                                            }else {
                                                Log.e("TAG","成功");
                                                tts.speak(mhelper.getCurrentPageContent(),TextToSpeech.QUEUE_ADD,null);

                                            }
                                        }else {
                                            Toast.makeText(mcontext,"该机型不支持该功能",Toast.LENGTH_LONG).show();
                                            Log.e("TAG","onInit:error");
                                        }
                                    }
                                });
                            }else {
                                if (tts.isSpeaking()){
                                    tts.stop();
                                }else {
                                    tts.speak(mhelper.getCurrentPageContent(),TextToSpeech.QUEUE_FLUSH,null);
                                }
                            }
                            break;
                        case 4:
                            //显示出来
                            mseekBar.setVisibility(mseekBar.getVisibility()==View.VISIBLE?View.GONE:View.VISIBLE);

                            break;
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return mdata.size();
        }
    }
    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView mTextView;
        public ViewHolder(@NonNull TextView itemView) {
            super(itemView);
            mTextView = itemView;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (tts!=null){
            tts.shutdown();
        }
    }
}
