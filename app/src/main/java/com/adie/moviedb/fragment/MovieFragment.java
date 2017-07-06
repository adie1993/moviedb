package com.adie.moviedb.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.adie.moviedb.R;
import com.adie.moviedb.adapter.InTheaterAdapter;
import com.adie.moviedb.adapter.MovieAdapter;
import com.adie.moviedb.model.InTheater;
import com.adie.moviedb.model.InTheaterResponse;
import com.adie.moviedb.model.Movie;
import com.adie.moviedb.model.MovieResponse;
import com.adie.moviedb.rest.ApiClient;
import com.adie.moviedb.rest.ApiInterface;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.adie.moviedb.rest.ApiClient.API_KEY;


public class MovieFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener{
    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    //commit fragment instance
    public static MovieFragment newInstance() {
        MovieFragment fragment = new MovieFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //returning our layout file
        return inflater.inflate(R.layout.movie_fragment, container, false);
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        InitView(view);
        getActivity().setTitle("Popular");
    }

    //init view
    private void InitView(View view) {
        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        swipeRefreshLayout = (SwipeRefreshLayout)view.findViewById(R.id.swiperefresh);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorAccent);
        swipeRefreshLayout.setOnRefreshListener(this);

        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this.getContext(), 2);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

    }
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        fetch(true);

    }

    //request detail movies from api
    private void fetch(boolean get) {
        if(get){
            //init retrofit builder & api interface
            ApiInterface apiService =
                    ApiClient.getClient().create(ApiInterface.class);
            //get url
            Call<MovieResponse> call = apiService.getPopular(API_KEY);
            //request url
            call.enqueue(new Callback<MovieResponse>() {
                @Override
                public void onResponse(Call<MovieResponse> call, Response<MovieResponse> response) {
                    int statusCode = response.code();
                    //fetch response value to Movie model class
                    List<Movie> movies = response.body().getResults();
                    //avoid force close the app while change fragment
                    if(isAdded()){
                        //set list to recyclerview
                        swipeRefreshLayout.setRefreshing(false);
                        recyclerView.setAdapter(new MovieAdapter(getActivity(),movies));
                    }else{

                    }

                }

                @Override
                public void onFailure(Call<MovieResponse> call, Throwable t) {
                    // Log error here since request failed
                    Snackbar snackbar = Snackbar
                            .make(recyclerView, "No Data", Snackbar.LENGTH_LONG);
                    View snackBarView = snackbar.getView();
                    snackBarView.setBackgroundColor(ContextCompat.getColor(getActivity(),R.color.red));
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
