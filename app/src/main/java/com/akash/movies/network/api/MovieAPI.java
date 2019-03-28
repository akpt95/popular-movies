package com.akash.movies.network.api;

import com.akash.movies.network.model.movies.MovieResponse;
import com.akash.movies.network.model.reviews.ReviewResponse;
import com.akash.movies.network.model.videos.VideoResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

/**
 * Created by apati on 11/1/18.
 */

public interface MovieAPI {

    @GET("movie/{filter_type}")
    Call<MovieResponse> getMovies(@Path("filter_type") String filterType);

    @GET("movie/{movie_id}/videos")
    Call<VideoResponse> getVideos(@Path("movie_id") String movie_id);

    @GET("movie/{movie_id}/reviews")
    Call<ReviewResponse> getReviews(@Path("movie_id") String movie_id);

}
