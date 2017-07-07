package com.adie.moviedb.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.adie.moviedb.R;
import com.adie.moviedb.adapter.MovieAdapter;
import com.adie.moviedb.adapter.SearchAdapter;
import com.adie.moviedb.fragment.CastFragment;
import com.adie.moviedb.model.DetailMovie;
import com.adie.moviedb.model.Genre;
import com.adie.moviedb.model.SearchResponse;
import com.adie.moviedb.model.SearchResult;
import com.adie.moviedb.model.Video;
import com.adie.moviedb.model.VideoResponse;
import com.adie.moviedb.rest.ApiClient;
import com.adie.moviedb.rest.ApiInterface;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.adie.moviedb.rest.ApiClient.API_KEY;
import static com.adie.moviedb.rest.ApiClient.BASE_IMAGE_DET;


public class DetailSearchActivity extends AppCompatActivity  implements SwipeRefreshLayout.OnRefreshListener{



    String query;
    RecyclerView recyclerView;
    SwipeRefreshLayout swipeRefreshLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_search);

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        recyclerView = (RecyclerView)findViewById(R.id.recycler_view);
        swipeRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.swiperefresh);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorAccent);
        swipeRefreshLayout.setOnRefreshListener(this);

        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this.getApplicationContext(), 2);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Bundle extras = getIntent().getExtras();
        //get extras value id from adapter
        if(extras!=null){
           query= extras.getString("query");

            getSupportActionBar().setTitle("Search result of "+query);
            fetch(true);


        }else{
            //if value id = null back to mainactivity
            startActivity(new Intent(DetailSearchActivity.this,MainActivity.class));
            finish();
        }




    }





    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }


        return super.onOptionsItemSelected(item);
    }


    private void fetch(boolean get) {
        if(get) {


            //init retrofit builder & interface model gson class
            ApiInterface apiService =
                    ApiClient.getClient().create(ApiInterface.class);

            Call<SearchResponse> call = apiService.getSearch(API_KEY, query);

            //request api
            call.enqueue(new Callback<SearchResponse>() {
                @Override
                public void onResponse(Call<SearchResponse> call, Response<SearchResponse> response) {
                    //if response is success do fetch value response to target view
                    swipeRefreshLayout.setRefreshing(false);
                    int statusCode = response.code();
                    List<SearchResult> movies = response.body().getResults();

                    if(!movies.isEmpty()){
                        recyclerView.setAdapter(new SearchAdapter(getApplicationContext(),movies));
                    }else{
                        Snackbar snackbar = Snackbar
                                .make(recyclerView, "No Data", Snackbar.LENGTH_LONG);
                        View snackBarView = snackbar.getView();
                        snackBarView.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.red));
                        snackbar.show();
                    }
                }

                @Override
                public void onFailure(Call<SearchResponse> call, Throwable t) {
                    // Log error here since request failed
                    Snackbar snackbar = Snackbar
                            .make(recyclerView, "No Data", Snackbar.LENGTH_LONG);
                    View snackBarView = snackbar.getView();
                    snackBarView.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.red));
                    snackbar.show();
                }
            });
        }
        if(!get){
            swipeRefreshLayout.setRefreshing(false);
        }
    }



    @Override
    public void onRefresh() {
    fetch(true);
    }
}
