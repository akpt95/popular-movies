package com.akash.movies.details;


import android.app.Fragment;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.akash.movies.MainActivity;
import com.akash.movies.R;
import com.akash.movies.network.RetrofitController;
import com.akash.movies.network.model.movies.Result;
import com.akash.movies.network.model.reviews.ReviewResponse;
import com.akash.movies.network.model.videos.VideoResponse;
import com.akash.movies.room.Movie;
import com.squareup.picasso.Picasso;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class MovieDetailsFragment extends Fragment implements DetailsRecyclerViewAdapter.OnItemClickListner{

    public static final String TAG = MovieDetailsFragment.class.getName();
    private MainActivity mainActivity;

    private TextView textToolbarTitle;
    private Spinner spinnerFilter;

    private TextView textNoPreviewAvailable;
    private ImageView imageViewMoviePoster;
    private TextView textUserRating;
    private TextView textReleaseDate;
    private TextView textOriginalTitle;
    private TextView textSynopsis;
    private RecyclerView recyclerViewVideos;
    private RecyclerView recyclerViewReviews;
    private TextView textNoVideosAvailable;
    private TextView textNoReviewsAvailable;
    private ImageView imageFavourite;

    private Result movieResult;
    private VideoResponse videoResponse;
    private ReviewResponse reviewResponse;
    private Movie favouriteMovie;

    private int movieId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_movie_details, container, false);

        mainActivity = (MainActivity) getActivity();

        Bundle bundle = getArguments();
        movieResult = (Result) bundle.getSerializable("movie_result");
        favouriteMovie = (Movie) bundle.getSerializable("favourite_movie");

        if(movieResult != null){
            movieId = movieResult.getId();
        } else if(favouriteMovie !=null){
            movieId = favouriteMovie.getMovieId();
        }

        spinnerFilter = view.findViewById(R.id.spinner_filter);
        textToolbarTitle = view.findViewById(R.id.text_toolbar_title);

        setupToolbar();

        textNoPreviewAvailable = view.findViewById(R.id.text_no_preview_available);
        imageViewMoviePoster = view.findViewById(R.id.image_view_movie_poster);
        textUserRating = view.findViewById(R.id.text_user_rating);
        textReleaseDate = view.findViewById(R.id.text_release_date);
        textOriginalTitle = view.findViewById(R.id.text_original_title);
        textSynopsis = view.findViewById(R.id.text_synopsis);
        textNoVideosAvailable = view.findViewById(R.id.text_no_videos_available);
        textNoReviewsAvailable = view.findViewById(R.id.text_no_reviews_available);
        recyclerViewVideos = view.findViewById(R.id.recycler_view_videos);
        recyclerViewReviews = view.findViewById(R.id.recycler_view_reviews);
        imageFavourite = view.findViewById(R.id.image_favourite);

        if(mainActivity.checkIfMovieIsFavourite(movieId)){
            imageFavourite.setImageDrawable(mainActivity.getDrawable(android.R.drawable.btn_star_big_on));
        } else {
            imageFavourite.setImageDrawable(mainActivity.getDrawable(android.R.drawable.btn_star_big_off));
        }

        imageFavourite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(mainActivity.checkIfMovieIsFavourite(movieId)){
                    mainActivity.deleteFromFavouriteMovies(movieId);
                    imageFavourite.setImageDrawable(mainActivity.getDrawable(android.R.drawable.btn_star_big_off));
                } else {
                    insertMovie();
                    imageFavourite.setImageDrawable(mainActivity.getDrawable(android.R.drawable.btn_star_big_on));
                }

            }
        });

        LinearLayoutManager layoutManager = new LinearLayoutManager(mainActivity);
        recyclerViewVideos.setLayoutManager(layoutManager);
        recyclerViewVideos.setNestedScrollingEnabled(false);

        LinearLayoutManager layoutManager2 = new LinearLayoutManager(mainActivity);
        recyclerViewReviews.setLayoutManager(layoutManager2);
        recyclerViewReviews.setNestedScrollingEnabled(false);


        setupUI();
        getVideoData();
        getReviewData();

        return view;
    }

    private void insertMovie() {
        Movie movie = new Movie(movieResult.getId(), movieResult.getPosterPath(),
                movieResult.getVoteAverage(), movieResult.getReleaseDate(),
                movieResult.getOriginalTitle(), movieResult.getOverview());
        mainActivity.addMovieAsFavourite(movie);
    }


    private void setupToolbar() {
        spinnerFilter.setVisibility(View.GONE);
        textToolbarTitle.setVisibility(View.VISIBLE);
        textToolbarTitle.setText(R.string.movie_details);

    }

    private void setupUI() {

        if(movieResult !=null){
            if (movieResult.getPosterPath()== null){
                textNoPreviewAvailable.setVisibility(View.VISIBLE);
            } else {
                textNoPreviewAvailable.setVisibility(View.GONE);
                String imageUrl = "http://image.tmdb.org/t/p/w185/" + movieResult.getPosterPath();
                Picasso.with(mainActivity).load(imageUrl).into(imageViewMoviePoster);
            }

            textUserRating.setText(String.valueOf(movieResult.getVoteAverage()));
            textReleaseDate.setText(movieResult.getReleaseDate());
            textOriginalTitle.setText(movieResult.getOriginalTitle());
            textSynopsis.setText(movieResult.getOverview());

        } else if(favouriteMovie!=null){

            if (favouriteMovie.getPosterPath()== null){
                textNoPreviewAvailable.setVisibility(View.VISIBLE);
            } else {
                textNoPreviewAvailable.setVisibility(View.GONE);
                String imageUrl = "http://image.tmdb.org/t/p/w185/" + favouriteMovie.getPosterPath();
                Picasso.with(mainActivity).load(imageUrl).into(imageViewMoviePoster);
            }

            textUserRating.setText(String.valueOf(favouriteMovie.getVoteAverage()));
            textReleaseDate.setText(favouriteMovie.getReleaseDate());
            textOriginalTitle.setText(favouriteMovie.getOriginalTitle());
            textSynopsis.setText(favouriteMovie.getOverview());
        }



    }

    private void getVideoData() {

        mainActivity.showProgressDialog("Loading..");

        final Call<VideoResponse> videoResponseCall = RetrofitController.getInstance().getMovieAPI().getVideos(String.valueOf(movieId));
        videoResponseCall.enqueue(new Callback<VideoResponse>() {
            @Override
            public void onResponse(@NonNull Call<VideoResponse> call, @NonNull Response<VideoResponse> response) {

                mainActivity.dismissProgressDialog();

                if(response.isSuccessful()){

                    if (response.body() != null){
                         videoResponse = response.body();

                         DetailsRecyclerViewAdapter detailsRecyclerViewAdapter = new DetailsRecyclerViewAdapter("video", videoResponse, null);
                         recyclerViewVideos.setAdapter(detailsRecyclerViewAdapter);
                         detailsRecyclerViewAdapter.setItemClickListner(MovieDetailsFragment.this);

                         if (videoResponse.getResults().isEmpty()){
                             textNoVideosAvailable.setVisibility(View.VISIBLE);
                         } else {
                             textNoVideosAvailable.setVisibility(View.GONE);
                         }


                    }

                }
            }

            @Override
            public void onFailure(@NonNull Call<VideoResponse> call, @NonNull Throwable t) {
                mainActivity.dismissProgressDialog();
                Log.e(TAG, "getVideoData() failed with error: "+t.getMessage());
            }
        });
    }

    private void getReviewData() {

        mainActivity.showProgressDialog("Loading..");

        final Call<ReviewResponse> videoResponseCall = RetrofitController.getInstance().getMovieAPI().getReviews(String.valueOf(movieId));
        videoResponseCall.enqueue(new Callback<ReviewResponse>() {
            @Override
            public void onResponse(@NonNull Call<ReviewResponse> call, @NonNull Response<ReviewResponse> response) {

                mainActivity.dismissProgressDialog();

                if(response.isSuccessful()){

                    if (response.body() != null){
                        reviewResponse = response.body();

                        DetailsRecyclerViewAdapter detailsRecyclerViewAdapter = new DetailsRecyclerViewAdapter("reviews", null, reviewResponse);
                        recyclerViewReviews.setAdapter(detailsRecyclerViewAdapter);
                        detailsRecyclerViewAdapter.setItemClickListner(MovieDetailsFragment.this);

                        if (reviewResponse.getResults().isEmpty()){
                            textNoReviewsAvailable.setVisibility(View.VISIBLE);
                        } else {
                            textNoReviewsAvailable.setVisibility(View.GONE);
                        }

                    }

                }
            }

            @Override
            public void onFailure(@NonNull Call<ReviewResponse> call, @NonNull Throwable t) {
                mainActivity.dismissProgressDialog();
                Log.e(TAG, "getReviewData() failed with error: "+t.getMessage());
            }
        });
    }

    public static void watchYoutubeVideo(Context context, String key){
        Intent appIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + key));
        Intent webIntent = new Intent(Intent.ACTION_VIEW,
                Uri.parse("http://www.youtube.com/watch?v=" + key));
        try {
            context.startActivity(appIntent);
        } catch (ActivityNotFoundException ex) {
            context.startActivity(webIntent);
        }
    }

    @Override
    public void onItemCLick(View view, int position) {
        if(videoResponse != null){
            String key = videoResponse.getResults().get(position).getKey();
            watchYoutubeVideo(mainActivity, key);
        }
    }
}
