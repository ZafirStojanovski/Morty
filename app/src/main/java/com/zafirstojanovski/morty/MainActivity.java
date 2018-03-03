package com.zafirstojanovski.morty;

import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;

import com.zafirstojanovski.morty.Fragments.ChatFragment;
import com.zafirstojanovski.morty.Fragments.FavoritesFragment;
import com.zafirstojanovski.morty.Fragments.SettingsFragment;

public class MainActivity extends AppCompatActivity {

    private SectionsPagerAdapter sectionsPagerAdapter;
    private TabLayout tabLayout;
    private ViewPager viewPager;


    public class SectionsPagerAdapter extends FragmentPagerAdapter {
        public SectionsPagerAdapter(FragmentManager fm) { super(fm); }

        @Override
        public Fragment getItem(int position) {
            switch(position){
                case 0: return SettingsFragment.newInstance();
                case 1: return ChatFragment.newInstance();
                case 2: return FavoritesFragment.newInstance();
                default: return null;
            }
        }
        @Override
        public int getCount() {
            // Show 3 total pages.
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
        viewPager = (ViewPager) findViewById(R.id.container);
        viewPager.setAdapter(sectionsPagerAdapter);

        // tablayout
        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

        setupTabIcons();

        // load chat firstly
        viewPager.setCurrentItem(1);

        // listeners
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(viewPager));
    }

    private void setupTabIcons() {
        tabLayout.getTabAt(0).setIcon(R.drawable.ic_settings_grey_24dp);
        tabLayout.getTabAt(1).setIcon(R.drawable.realistic_evil_morty);
        tabLayout.getTabAt(2).setIcon(R.drawable.ic_star_grey_24dp);
    }
}