package com.akash.movies.room;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;

import java.util.List;

public class MovieViewModel extends AndroidViewModel {

    private MovieRepository movieRepository;
    private LiveData<List<Movie>> allMovies;

    public MovieViewModel(Application application) {
        super(application);
        movieRepository = new MovieRepository(application);
        allMovies = movieRepository.getAllMovies();
    }

    public LiveData<List<Movie>> getAllMovies(){
        return allMovies;
    }

    public void insert(Movie movie){
        movieRepository.insert(movie);
    }

    public void delete(int movieId){
        movieRepository.delete(movieId);
    }
}
