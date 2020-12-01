package com.example.mybook.fragment;

import com.example.mybook.R;
import com.example.mybook.activity.SearchActivity;
import com.google.android.material.tabs.TabLayout;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import java.util.ArrayList;
import java.util.List;

public class MainFragment extends Fragment {
    //声明控件
    private ViewPager viewPager;
    private List<Fragment> fragments;
    private TabLayout tabLayout;
    //建立一个成员变量rootView用于保存根View(FrameLayout)
    private ViewGroup rootView;
    private ImageView tosearch;
    private TextView textView;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        this.rootView = (ViewGroup) inflater.inflate(R.layout.fragment_main,container,false);
        init();//初始化数据
        initViewPage();//初始化ViewPage

        return this.rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    private void init(){
        tosearch = this.rootView.findViewById(R.id.iv_search);
        viewPager = this.rootView.findViewById(R.id.Viewpage);
        tabLayout = this.rootView.findViewById(R.id.mainTablayout);
        textView = this.rootView.findViewById(R.id.title);
        tosearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), SearchActivity.class));
            }
        });
    }


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void initViewPage(){
        fragments = new ArrayList<>();
        FaxianFragment faxianFragment=new FaxianFragment();
        WodeFragment wodeFragment=new WodeFragment();
        fragments.add(new ShujiaFragment());
        fragments.add(new FenLeiFragment());
        fragments.add(faxianFragment);
        fragments.add(wodeFragment);

        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getChildFragmentManager());
        viewPagerAdapter.setFragments(fragments);//添加fragment
        viewPager.setAdapter(viewPagerAdapter);//添加适配器


        //添加Tab页
        tabLayout.addTab(tabLayout.newTab());
        tabLayout.addTab(tabLayout.newTab());
        tabLayout.addTab(tabLayout.newTab());
        tabLayout.addTab(tabLayout.newTab());

        tabLayout.setupWithViewPager(viewPager);//实现TabLayout与ViewPager互相变换

        tabLayout.getTabAt(0).setIcon(R.drawable.shujia_selector);
        tabLayout.getTabAt(1).setIcon(R.drawable.fenlei_selector);
        tabLayout.getTabAt(2).setIcon(R.drawable.faxian_selector);
        tabLayout.getTabAt(3).setIcon(R.drawable.wode_selector);

    }



    //为ViewPager派生一个适配器类
   class ViewPagerAdapter extends FragmentPagerAdapter{
        private List<Fragment> fragments;

        public void setFragments(List<Fragment> fragments) {
            this.fragments = fragments;
        }

        public ViewPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }


        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            if (position==0){
                return "书架";
            }else if (position==1){
                return "分类";
            }else if (position==2){
                return "发现";
            }else if (position==3){
                return "我的";
            }
            return null;
        }
    }
}
