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
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.adie.moviedb.R;
import com.adie.moviedb.fragment.CastFragment;
import com.adie.moviedb.fragment.CastTVFragment;
import com.adie.moviedb.model.DetailMovie;
import com.adie.moviedb.model.DetailTV;
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


public class DetailTVActivity extends AppCompatActivity {

    private CollapsingToolbarLayout collapsingToolbar;
    private AppBarLayout appBarLayout;
    private TextView title,rating,genre,overview;
    private FloatingActionButton fab;
    String gen="";
    private ImageView header,back;
    private Menu collapsedMenu;
    private boolean appBarExpanded = true;
    int id;
    String key_trailer;
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
        back = (ImageView)findViewById(R.id.bg);

        //get extras value from adapter
        Bundle extras = getIntent().getExtras();

        if(extras!=null){
           id= extras.getInt("id");
            getvid();
            fetch();

            //call fragment with param movie_id
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.content, CastTVFragment.newInstance(id));
            transaction.commit();
        }else{
            startActivity(new Intent(DetailTVActivity.this,MainActivity.class));
            finish();
        }

        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                Log.d(DetailTVActivity.class.getSimpleName(), "onOffsetChanged: verticalOffset: " + verticalOffset);

                //  Vertical offset == 0 indicates appBar is fully expanded.
                if (Math.abs(verticalOffset) > 200) {
                    appBarExpanded = false;
                    invalidateOptionsMenu();
                } else {
                    appBarExpanded = true;
                    invalidateOptionsMenu();
                }
            }
        });

        //fab onclick listener
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //launch youtube app if value youtube key is not null
                if(!key_trailer.isEmpty()){
                    Intent appIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + key_trailer));
                    startActivity(appIntent);
                }else{
                    Snackbar snackbar = Snackbar
                            .make(appBarLayout, "No Video Trailer", Snackbar.LENGTH_LONG);
                    View snackBarView = snackbar.getView();
                    snackBarView.setBackgroundColor(ContextCompat.getColor(getApplicationContext(),R.color.red));
                    snackbar.show();
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

    //request details tv shows
    private void fetch() {
        //init retrofit builder & interface class
        ApiInterface apiService =
                ApiClient.getClient().create(ApiInterface.class);

        Call<DetailTV> call = apiService.getTVDetails(id,API_KEY);

        //request url
        call.enqueue(new Callback<DetailTV>() {
            @Override
            public void onResponse(Call<DetailTV> call, Response<DetailTV> response) {
                int statusCode = response.code();
                //get response value
                DetailTV tv = response.body();
                String year = tv.getReleaseDate().substring(0,4);
                collapsingToolbar.setTitle(tv.getTitle()+" ("+year+")");

                Glide.with(getApplicationContext()).load(BASE_IMAGE_DET+tv.getBackdropPath()+"?api_key="+API_KEY)
                        .thumbnail(0.5f)
                        .crossFade()
                        .fitCenter()
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(header);
                Glide.with(getApplicationContext()).load(BASE_IMAGE_DET+tv.getPosterPath()+"?api_key="+API_KEY)
                        .thumbnail(0.5f)
                        .crossFade()
                        .fitCenter()
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(back);

                //decapsulation serializable genres
                List<Genre> genr = response.body().getGenreIds();
                for(Genre s : genr){
                    gen += "\u2022 "+s.getName()+"\n";
                }

                //fetching value to target view
                title.setText(tv.getTitle());
                rating.setText("TMDB Rating : "+String.valueOf(tv.getVoteAverage()));
                overview.setText(tv.getOverview());
                genre.setText("Genre : \n"+gen);
            }

            @Override
            public void onFailure(Call<DetailTV> call, Throwable t) {
                // Log error here since request failed
                Snackbar snackbar = Snackbar
                        .make(appBarLayout, "Fail to get data", Snackbar.LENGTH_LONG);
                View snackBarView = snackbar.getView();
                snackBarView.setBackgroundColor(ContextCompat.getColor(getApplicationContext(),R.color.red));
                snackbar.show();
            }
        });
    }

    //request youtube key trailer
    private void getvid() {
        //init retrofit builder & interface model class
        ApiInterface apiService =
                ApiClient.getClient().create(ApiInterface.class);

        Call<VideoResponse> call = apiService.getTVTrailer(id,API_KEY);
        //request the api
        call.enqueue(new Callback<VideoResponse>() {
            @Override
            public void onResponse(Call<VideoResponse> call, Response<VideoResponse> response) {
                int statusCode = response.code();
                //fetch response value to video model class
                List<Video> vid = response.body().getResults();
                for(int i = 0;i<vid.size();i++){
                    key_trailer = vid.get(i).getKey();
                }

            }

            @Override
            public void onFailure(Call<VideoResponse> call, Throwable t) {
                // Log error here since request failed

            }
        });
    }
}
