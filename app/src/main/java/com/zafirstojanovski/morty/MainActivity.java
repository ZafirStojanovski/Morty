package com.zafirstojanovski.morty;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;

import com.zafirstojanovski.morty.Fragments.ChatFragment;
import com.zafirstojanovski.morty.Fragments.FAQFragment;
import com.zafirstojanovski.morty.Fragments.SettingsFragment;

public class MainActivity extends AppCompatActivity implements FAQFragment.OnQuestionSelectedListener{

    private SectionsPagerAdapter sectionsPagerAdapter;
    private TabLayout tabLayout;
    private ViewPager viewPager;

    public ChatFragment chatFragment;

    @Override
    public void onQuestionSelected(final String question) {
        selectPage(1);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                chatFragment.writeStatement(question);
            }
        }, 150);
    }

    private void selectPage(int pageIndex) {
        tabLayout.setScrollPosition(pageIndex,0f,true);
        viewPager.setCurrentItem(pageIndex);
    }

    private class SectionsPagerAdapter extends FragmentPagerAdapter{
        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }


        @Override
        public Fragment getItem(int position) {
            if (position == 0) return FAQFragment.newInstance();
            else if (position == 1) {
                chatFragment = ChatFragment.newInstance();
                return chatFragment;
            }
            else if (position == 2) return SettingsFragment.newInstance();
            return null;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0: return "FAQ";
                case 1: return "Morty";
                case 2: return "Settings";
                default: return null;
            }
        }

        @Override
        public int getCount() {
            return 3;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setupTabLayout();
    }

    private void setupTabLayout() {
        // adapter
        sectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // viewpager
        viewPager = findViewById(R.id.container);
        viewPager.setAdapter(sectionsPagerAdapter);

        // tablayout
        tabLayout = findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

        // load chat initially
        viewPager.setCurrentItem(1);

        // listeners
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(viewPager));
    }

}