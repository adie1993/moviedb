package com.adie.moviedb.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.adie.moviedb.R;
import com.adie.moviedb.adapter.CastAdapter;
import com.adie.moviedb.model.Cast;
import com.adie.moviedb.model.CreditsResponse;
import com.adie.moviedb.rest.ApiClient;
import com.adie.moviedb.rest.ApiInterface;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.adie.moviedb.rest.ApiClient.API_KEY;


public class CastTVFragment extends Fragment{
    private RecyclerView recyclerView;

    private static int id_movie;
    //commit fragment instance
    public static CastTVFragment newInstance(int id) {
        CastTVFragment fragment = new CastTVFragment();
        id_movie = id;
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
        return inflater.inflate(R.layout.cast_fragment, container, false);
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        InitView(view);
    }

    //init view
    private void InitView(View view) {
        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);


        LinearLayoutManager mlayoutManager
                = new LinearLayoutManager(this.getActivity(), LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(mlayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

    }
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        fetch(id_movie);

    }

    //request credits from api
    private void fetch(int id_moviee) {

            //init retrofit builder & api interface
            ApiInterface apiService =
                    ApiClient.getClient().create(ApiInterface.class);
            //get url
            Call<CreditsResponse> call = apiService.getTVCredits(id_moviee,API_KEY);
            //request url
            call.enqueue(new Callback<CreditsResponse>() {
                @Override
                public void onResponse(Call<CreditsResponse> call, Response<CreditsResponse> response) {
                    int statusCode = response.code();
                    //fetch response value to Cast model class
                      //avoid force close the app while change fragment

                            List<Cast> cast = response.body().getCast();

                            //set list to recyclerview
                            recyclerView.setAdapter(new CastAdapter(getActivity(),cast));





                }

                @Override
                public void onFailure(Call<CreditsResponse> call, Throwable t) {
                    // Log error here since request failed
                    Snackbar snackbar = Snackbar
                            .make(recyclerView, "No Data", Snackbar.LENGTH_LONG);
                    View snackBarView = snackbar.getView();
                    snackBarView.setBackgroundColor(ContextCompat.getColor(getActivity(),R.color.red));
                    snackbar.show();
                }
            });


    }

}
