package com.gachon.ask;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;


import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    // 5개의 fragment define
    private FragmentManager fragmentManager = getSupportFragmentManager();
    private HomeFragment fragmentHome = new HomeFragment();
    private ExploreFragment fragmentExplore = new ExploreFragment();
    private RankingFragment fragmentRanking = new RankingFragment();
    private CommunityFragment fragmentCommunity = new CommunityFragment();
    private MyPageFragment fragmentMyPage = new MyPageFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // basic fragment setting
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.frameLayout, fragmentHome).commitAllowingStateLoss();

        // define bottomNavigationView and listener
        BottomNavigationView bottomNavigationView = findViewById(R.id.navigationView);
        bottomNavigationView.setOnNavigationItemSelectedListener(new ItemSelectedListener());
        bottomNavigationView.setItemIconTintList(null);
    }

    class ItemSelectedListener implements BottomNavigationView.OnNavigationItemSelectedListener{
        @Override
        public boolean onNavigationItemSelected(MenuItem menuItem) {
            FragmentTransaction transaction = fragmentManager.beginTransaction();

            switch(menuItem.getItemId())
            {
                case R.id.homeItem:
                    transaction.replace(R.id.frameLayout, fragmentHome).commitAllowingStateLoss();
                    break;
                case R.id.exploreItem:
                    transaction.replace(R.id.frameLayout, fragmentExplore).commitAllowingStateLoss();
                    break;
                case R.id.rankItem:
                    transaction.replace(R.id.frameLayout, fragmentRanking).commitAllowingStateLoss();
                    break;
                case R.id.communityItem:
                    transaction.replace(R.id.frameLayout, fragmentCommunity).commitAllowingStateLoss();
                    break;
                case R.id.myPageItem:
                    transaction.replace(R.id.frameLayout, fragmentMyPage).commitAllowingStateLoss();
                    break;
            }
            return true;
        }
    }
}