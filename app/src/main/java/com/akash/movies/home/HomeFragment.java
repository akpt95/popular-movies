package com.akash.movies.home;


import android.app.Fragment;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.akash.movies.MainActivity;
import com.akash.movies.R;
import com.akash.movies.details.MovieDetailsFragment;
import com.akash.movies.network.RetrofitController;
import com.akash.movies.network.model.movies.MovieResponse;
import com.akash.movies.room.Movie;
import com.squareup.picasso.Picasso;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment implements AdapterView.OnItemSelectedListener {

    public static final String TAG = HomeFragment.class.getName();
    private MainActivity mainActivity;

    private String filterType = "popular";

    private RecyclerView recyclerViewMovies;
    private LayoutInflater inflater;
    private Spinner spinnerFilter;
    private TextView textToolbarTitle;

    private MovieResponse movieResponse;
    private List<Movie> favouriteMovies;
    private TextView textNoMoviesAvailable;
    GridLayoutManager gridLayoutManager;
    private static final String SAVED_LAYOUT_MANAGER = "layout-manager-state";
    private Parcelable layoutManagerSavedState;
    private Parcelable savedInstanceState;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home, container, false);

        mainActivity = (MainActivity) getActivity();
        this.inflater = inflater;
        spinnerFilter = view.findViewById(R.id.spinner_filter);
        textToolbarTitle = view.findViewById(R.id.text_toolbar_title);
        textNoMoviesAvailable = view.findViewById(R.id.text_no_movies_available);

        setupToolbar();

        recyclerViewMovies = view.findViewById(R.id.recycler_view_movies);
        gridLayoutManager = new GridLayoutManager(mainActivity, 2);
        recyclerViewMovies.setLayoutManager(gridLayoutManager);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private void setupToolbar() {

        spinnerFilter.setVisibility(View.VISIBLE);
        textToolbarTitle.setVisibility(View.VISIBLE);
        textToolbarTitle.setText(R.string.movies);

        ArrayAdapter<CharSequence> arrayAdapter = ArrayAdapter.createFromResource(mainActivity, R.array.filter_array, R.layout.custom_spinner_item);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerFilter.setAdapter(arrayAdapter);
        spinnerFilter.setOnItemSelectedListener(this);
    }

    private void getMovieData(String filterType) {

        mainActivity.showProgressDialog("Loading..");

        final Call<MovieResponse> movieResponseCall = RetrofitController.getInstance().getMovieAPI().getMovies(filterType);
        movieResponseCall.enqueue(new Callback<MovieResponse>() {
            @Override
            public void onResponse(@NonNull Call<MovieResponse> call, @NonNull Response<MovieResponse> response) {

                mainActivity.dismissProgressDialog();

                if(response.isSuccessful()){

                    if (response.body() != null){
                        movieResponse = response.body();

                        MovieAdapter movieAdapter = new MovieAdapter(movieResponse, favouriteMovies);
                        recyclerViewMovies.setAdapter(movieAdapter);
//                        recyclerViewMovies.getLayoutManager().onRestoreInstanceState(layoutManagerSavedState);
                    }

                }
            }

            @Override
            public void onFailure(@NonNull Call<MovieResponse> call, @NonNull Throwable t) {
                mainActivity.dismissProgressDialog();
                Log.e(TAG, "getMovieData() failed with error: "+t.getMessage());
            }
        });


    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

        String filterItemString = parent.getItemAtPosition(position).toString();

        if(filterItemString.equalsIgnoreCase("Top Rated")){
            filterType = "top_rated";
            getMovieData(filterType);
        }else if(filterItemString.equalsIgnoreCase("Most Popular")){
            filterType = "popular";
            getMovieData(filterType);
        } else {
            getFavouriteMovies();
        }

    }



    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    private void getFavouriteMovies() {

        favouriteMovies = mainActivity.getFavouriteMovies();
        MovieAdapter movieAdapter = new MovieAdapter(null, favouriteMovies);
        recyclerViewMovies.setAdapter(movieAdapter);
    }

    class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.ViewHolder>{

        MovieResponse movieResponse;
        List<Movie> favouriteMovies;

        MovieAdapter(MovieResponse movieResponse, List<Movie> favouriteMovies) {
            this.movieResponse = movieResponse;
            this.favouriteMovies = favouriteMovies;
        }

        @NonNull
        @Override
        public MovieAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

            View view = inflater.inflate(R.layout.movie_recycler_view_item, parent,false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull final MovieAdapter.ViewHolder holder, int position) {

            if(movieResponse!=null){
                if (movieResponse.getResults().get(position).getPosterPath()== null){
                    holder.textNoPreviewAvailable.setVisibility(View.VISIBLE);
                } else {
                    holder.textNoPreviewAvailable.setVisibility(View.GONE);
                    String imageUrl = "http://image.tmdb.org/t/p/w185/" + movieResponse.getResults().get(position).getPosterPath();
                    Picasso.with(mainActivity).load(imageUrl).into(holder.imageViewMoviePoster);
                }

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        MovieDetailsFragment movieDetailsFragment = new MovieDetailsFragment();
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("movie_result", movieResponse.getResults().get(holder.getAdapterPosition()));
                        movieDetailsFragment.setArguments(bundle);
                        mainActivity.launchFragment(movieDetailsFragment);
                    }
                });

            } else if(favouriteMovies!=null){
                if (favouriteMovies.get(position).getPosterPath() == null){
                    holder.textNoPreviewAvailable.setVisibility(View.VISIBLE);
                } else {
                    holder.textNoPreviewAvailable.setVisibility(View.GONE);
                    String imageUrl = "http://image.tmdb.org/t/p/w185/" + favouriteMovies.get(position).getPosterPath();
                    Picasso.with(mainActivity).load(imageUrl).into(holder.imageViewMoviePoster);
                }

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        MovieDetailsFragment movieDetailsFragment = new MovieDetailsFragment();
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("favourite_movie", favouriteMovies.get(holder.getAdapterPosition()));
                        movieDetailsFragment.setArguments(bundle);
                        mainActivity.launchFragment(movieDetailsFragment);
                    }
                });
            }



        }

        @Override
        public int getItemCount() {

            if(movieResponse != null){
                if (!movieResponse.getResults().isEmpty()){
                    textNoMoviesAvailable.setVisibility(View.GONE);
                    return movieResponse.getResults().size();
                }
                else {
                    textNoMoviesAvailable.setVisibility(View.VISIBLE);
                    return 0;
                }
            } else if(favouriteMovies!=null){

                if (favouriteMovies.isEmpty())
                    textNoMoviesAvailable.setVisibility(View.VISIBLE);
                else
                    textNoMoviesAvailable.setVisibility(View.GONE);

                return favouriteMovies.size();
            } else {
                textNoMoviesAvailable.setVisibility(View.VISIBLE);
                return 0;
            }

        }

        class ViewHolder extends RecyclerView.ViewHolder{

            private ImageView imageViewMoviePoster;
            private TextView textNoPreviewAvailable;

            ViewHolder(View itemView) {
                super(itemView);

                imageViewMoviePoster = itemView.findViewById(R.id.image_view_movie_poster);
                textNoPreviewAvailable = itemView.findViewById(R.id.text_no_preview_available);
            }
        }
    }
}
