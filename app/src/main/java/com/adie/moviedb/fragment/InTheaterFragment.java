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
import com.adie.moviedb.activity.MainActivity;
import com.adie.moviedb.adapter.InTheaterAdapter;
import com.adie.moviedb.model.InTheater;
import com.adie.moviedb.model.InTheaterResponse;
import com.adie.moviedb.rest.ApiClient;
import com.adie.moviedb.rest.ApiInterface;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.adie.moviedb.rest.ApiClient.API_KEY;


public class InTheaterFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener{
    private InTheaterAdapter mAdapter;
    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;

    //commit fragment instance
    public static InTheaterFragment newInstance() {
        InTheaterFragment fragment = new InTheaterFragment();
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
        return inflater.inflate(R.layout.theater_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        InitView(view);
        getActivity().setTitle("In Theater");
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

    //requset list in theater movie
    private void fetch(boolean get) {
        if(get){
            //init retrofit build & interface class
            ApiInterface apiService =
                    ApiClient.getClient().create(ApiInterface.class);

            Call<InTheaterResponse> call = apiService.getTheater(API_KEY);
            //request the api
            call.enqueue(new Callback<InTheaterResponse>() {
                @Override
                public void onResponse(Call<InTheaterResponse> call, Response<InTheaterResponse> response) {
                    int statusCode = response.code();
                    //fetch response value to intheater model class
                    List<InTheater> movies = response.body().getResults();
                    //avoid force close the app while change fragment
                    if(isAdded()){
                        //set list value to recycelview
                        swipeRefreshLayout.setRefreshing(false);
                        recyclerView.setAdapter(new InTheaterAdapter(getActivity(),movies));
                    }else{

                    }

                }

                @Override
                public void onFailure(Call<InTheaterResponse> call, Throwable t) {
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
