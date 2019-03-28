package com.akash.movies.room;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import java.util.List;

@Dao
public interface MovieDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Movie movie);

    @Query("DELETE from movie_table where movie_id = :movieId")
    void deleteMovie(int movieId);

    @Query("SELECT * from movie_table")
    LiveData<List<Movie>> getAllMovies();


}
