package com.akash.movies.room;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

import java.io.Serializable;

@Entity(tableName = "movie_table")
public class Movie implements Serializable {

    @PrimaryKey
    @ColumnInfo(name = "movie_id")
    private int movieId;

    @ColumnInfo(name = "poster_path")
    private String posterPath;

    @ColumnInfo(name = "vote_average")
    private float voteAverage;

    @ColumnInfo(name = "release_date")
    private String releaseDate;

    @ColumnInfo(name = "original_title")
    private String originalTitle;

    @ColumnInfo(name = "overview")
    private String overview;

    public Movie(int movieId, String posterPath, float voteAverage, String releaseDate, String originalTitle, String overview) {
        this.movieId = movieId;
        this.posterPath = posterPath;
        this.voteAverage = voteAverage;
        this.releaseDate = releaseDate;
        this.originalTitle = originalTitle;
        this.overview = overview;
    }

    public int getMovieId() {
        return movieId;
    }

    public String getPosterPath() {
        return posterPath;
    }

    public float getVoteAverage() {
        return voteAverage;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public String getOriginalTitle() {
        return originalTitle;
    }

    public String getOverview() {
        return overview;
    }
}
