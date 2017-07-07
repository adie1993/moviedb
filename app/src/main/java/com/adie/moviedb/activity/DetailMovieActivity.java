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
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.adie.moviedb.R;
import com.adie.moviedb.fragment.CastFragment;
import com.adie.moviedb.fragment.InTheaterFragment;
import com.adie.moviedb.model.DetailMovie;
import com.adie.moviedb.model.Genre;
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


public class DetailMovieActivity extends AppCompatActivity {

    private CollapsingToolbarLayout collapsingToolbar;
    private AppBarLayout appBarLayout;
    private TextView title,rating,genre,overview;
    private FloatingActionButton fab;
    String gen ="";
    private ImageView header,back;
    private Menu collapsedMenu;
    private boolean appBarExpanded = true;
    int id;
    String key_trailer="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_detail);

        final Toolbar toolbar = (Toolbar) findViewById(R.id.anim_toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setTitle("");
        fab = (FloatingActionButton)findViewById(R.id.yutup);
        appBarLayout = (AppBarLayout) findViewById(R.id.appbar);
        header = (ImageView) findViewById(R.id.header);
        title = (TextView)findViewById(R.id.title);
        genre = (TextView)findViewById(R.id.genre);
        rating = (TextView)findViewById(R.id.rating);
        overview = (TextView)findViewById(R.id.overview);
        collapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        collapsingToolbar.setExpandedTitleColor(getResources().getColor(android.R.color.transparent));
        back = (ImageView)findViewById(R.id.bg);
        Bundle extras = getIntent().getExtras();
        //get extras value id from adapter
        if(extras!=null){
           id= extras.getInt("id");
            getvid();
            fetch();

            //call fragment with param movie_id
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.content, CastFragment.newInstance(id));
            transaction.commit();
        }else{
            //if value id = null back to mainactivity
            startActivity(new Intent(DetailMovieActivity.this,MainActivity.class));
            finish();
        }

        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                Log.d(DetailMovieActivity.class.getSimpleName(), "onOffsetChanged: verticalOffset: " + verticalOffset);

                //Vertical offset == 0 indicates appBar is fully expanded.
                if (Math.abs(verticalOffset) > 200) {
                    appBarExpanded = false;
                    invalidateOptionsMenu();
                } else {
                    appBarExpanded = true;
                    invalidateOptionsMenu();
                }
            }
        });

        //floating action bar onclick listener
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //get youtube key from getvid() method

                if(!key_trailer.isEmpty()){
                    Intent appIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + key_trailer));
                    startActivity(appIntent);
                }

            }
        });

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


    private void fetch() {
        //init retrofit builder & interface model gson class
        ApiInterface apiService =
                ApiClient.getClient().create(ApiInterface.class);

        Call<DetailMovie> call = apiService.getMovieDetails(id,API_KEY);

        //request api
        call.enqueue(new Callback<DetailMovie>() {
            @Override
            public void onResponse(Call<DetailMovie> call, Response<DetailMovie> response) {
                //if response is success do fetch value response to target view
                int statusCode = response.code();
                DetailMovie movies = response.body();
                if(movies.getReleaseDate()!=""){
                    String year = movies.getReleaseDate().substring(0,4);
                    collapsingToolbar.setTitle(movies.getTitle()+" ("+year+")");
                }else if(movies.getReleaseDate()==""){
                    collapsingToolbar.setTitle(movies.getTitle());
                }


                Glide.with(getApplicationContext()).load(BASE_IMAGE_DET+movies.getBackdropPath()+"?api_key="+API_KEY)
                        .thumbnail(0.5f)
                        .crossFade()
                        .fitCenter()
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(header);
                Glide.with(getApplicationContext()).load(BASE_IMAGE_DET+movies.getPosterPath()+"?api_key="+API_KEY)
                        .thumbnail(0.5f)
                        .crossFade()
                        .fitCenter()
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(back);

                //decapsulation of genres serializable
                List<Genre> genr = response.body().getGenreIds();
                for(Genre s : genr){
                    gen += "\u2022 "+s.getName()+"\n";
                }
                //settext to target view
                title.setText(movies.getTitle());
                rating.setText("TMDB Rating : "+String.valueOf(movies.getVoteAverage()));
                overview.setText(movies.getOverview());
                genre.setText("Genre : \n"+gen);
            }

            @Override
            public void onFailure(Call<DetailMovie> call, Throwable t) {
                // Log error here since request failed
                Snackbar snackbar = Snackbar
                        .make(appBarLayout, "No Data", Snackbar.LENGTH_LONG);
                View snackBarView = snackbar.getView();
                snackBarView.setBackgroundColor(ContextCompat.getColor(getApplicationContext(),R.color.red));
                snackbar.show();
            }
        });
    }

    //request youtube key to api
    private void getvid() {
        //init retrofit builder & interface class
        ApiInterface apiService =
                ApiClient.getClient().create(ApiInterface.class);

        Call<VideoResponse> call = apiService.getTrailer(id,API_KEY);

        //request url
        call.enqueue(new Callback<VideoResponse>() {
            @Override
            public void onResponse(Call<VideoResponse> call, Response<VideoResponse> response) {
                int statusCode = response.code();
                //get response value fetch to interface class
                List<Video> vid = response.body().getResults();
                for(int i = 0;i<vid.size();i++){

                        key_trailer = vid.get(i).getKey();

                }
                //set visibility fab
                if(key_trailer.isEmpty()){
                    fab.setVisibility(View.GONE);
                }else{
                    fab.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(Call<VideoResponse> call, Throwable t) {
                // Log error here since request failed

            }
        });
    }
}
