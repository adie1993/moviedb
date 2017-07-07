package com.adie.moviedb.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.adie.moviedb.R;
import com.adie.moviedb.adapter.MovieAdapter;
import com.adie.moviedb.fragment.InTheaterFragment;
import com.adie.moviedb.fragment.MovieFragment;
import com.adie.moviedb.fragment.TVFragment;
import com.adie.moviedb.model.Movie;
import com.adie.moviedb.model.MovieResponse;
import com.adie.moviedb.model.SearchResponse;
import com.adie.moviedb.model.SearchResult;
import com.adie.moviedb.rest.ApiClient;
import com.adie.moviedb.rest.ApiInterface;
import com.miguelcatalan.materialsearchview.MaterialSearchView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.adie.moviedb.rest.ApiClient.API_KEY;

public class MainActivity extends AppCompatActivity {
    MaterialSearchView searchView;
    ListView lstView;
    private Toolbar toolbar;
    List<SearchResult> lstSource;
    String[] title;
    BottomNavigationView navigation;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");

        toolbar.setTitleTextColor(Color.parseColor("#FFFFFF"));
        toolbar.setLogo(R.mipmap.ic_launcher);
        searchView = (MaterialSearchView)findViewById(R.id.search_view);
        navigation = (BottomNavigationView) findViewById(R.id.navigation);
        lstView = (ListView)findViewById(R.id.lstView);
        lstView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Object ls = lstView.getItemAtPosition(position);

                searchView.setQuery(ls.toString(),false);
                lstView.setAdapter(null);
                searchView.closeSearch();
                Intent i = new Intent(getApplicationContext(),DetailSearchActivity.class);
                i.putExtra("query",ls.toString());
                startActivity(i);
            }
        });
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
        searchView.setOnSearchViewListener(new MaterialSearchView.SearchViewListener() {
            @Override
            public void onSearchViewShown() {

            }

            @Override
            public void onSearchViewClosed() {
               lstView.setAdapter(null);
               lstView.animate();


            }
        });
        searchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                Intent i = new Intent(getApplicationContext(),DetailSearchActivity.class);
                i.putExtra("query",query);
                lstView.setAdapter(null);

                startActivity(i);
                return false;
            }

            @Override
            public boolean onQueryTextChange(final String newText) {
                if(newText != null && !newText.isEmpty()){

                    ApiInterface apiService =
                            ApiClient.getClient().create(ApiInterface.class);
                    //get url
                    Call<SearchResponse> call = apiService.getSearch(API_KEY,newText);
                    //request url
                    call.enqueue(new Callback<SearchResponse>() {
                        @Override
                        public void onResponse(Call<SearchResponse> call, Response<SearchResponse> response) {
                            int statusCode = response.code();
                            //fetch response value to Movie model class
                            lstSource = response.body().getResults();
                            //avoid force close the app while change fragment
                            List<String> lstFound = new ArrayList<String>();
                            for(SearchResult item:lstSource){
                                if(item.getTitle().toLowerCase(Locale.getDefault()).contains(newText))
                                    lstFound.add(item.getTitle());
                            }

                            ArrayAdapter adapter = new ArrayAdapter(MainActivity.this,android.R.layout.simple_list_item_1,lstFound);
                            lstView.setAdapter(adapter);
                        }

                        @Override
                        public void onFailure(Call<SearchResponse> call, Throwable t) {
                            // Log error here since request failed

                        }
                    });

                }
                else{
                    lstView.setAdapter(null);
                }
                return true;
            }

        });

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_item,menu);
        MenuItem item = menu.findItem(R.id.action_search);
        searchView.setMenuItem(item);
        return true;
    }
}
