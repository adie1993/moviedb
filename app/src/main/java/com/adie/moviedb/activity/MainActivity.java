package com.adie.moviedb.activity;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.adie.moviedb.R;
import com.adie.moviedb.fragment.InTheaterFragment;
import com.adie.moviedb.fragment.MovieFragment;
import com.adie.moviedb.fragment.TVFragment;

public class MainActivity extends AppCompatActivity {

    private Toolbar toolbar;
    BottomNavigationView navigation;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("");
        toolbar.setLogo(R.mipmap.ic_launcher);

        navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener
                (new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        Fragment selectedFragment = null;
                        switch (item.getItemId()) {
                            case R.id.navigation_now:
                                //get selected bottom navigation item to avoid looping commit fragment
                                if(navigation.getMenu().findItem(R.id.navigation_now).isChecked()){
                                    return true;
                                }else{
                                    selectedFragment = InTheaterFragment.newInstance();
                                }
                                break;
                            case R.id.navigation_movie:
                                if(navigation.getMenu().findItem(R.id.navigation_movie).isChecked()){
                                    return true;
                                }else{
                                    selectedFragment = MovieFragment.newInstance();
                                }
                                break;
                            case R.id.navigation_tv:
                                if(navigation.getMenu().findItem(R.id.navigation_tv).isChecked()){
                                    return true;
                                }else{
                                    selectedFragment = TVFragment.newInstance();
                                }
                                break;
                        }
                        //displaying fragment on selected navigation menu item
                        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                        transaction.replace(R.id.content, selectedFragment);
                        transaction.commit();
                        return true;
                    }
                });

        //Manually displaying the first fragment - one time only
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.content, InTheaterFragment.newInstance());
        transaction.commit();
       
    }

}
