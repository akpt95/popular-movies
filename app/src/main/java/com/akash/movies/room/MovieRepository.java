package com.akash.movies.room;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.os.AsyncTask;

import java.util.List;

public class MovieRepository {

    private MovieDao movieDao;
    private LiveData<List<Movie>> allMovies;

    public MovieRepository(Application application){
        MovieDatabase movieDatabase = MovieDatabase.getDatabase(application);
        movieDao = movieDatabase.movieDao();
        allMovies = movieDao.getAllMovies();
    }

    LiveData<List<Movie>> getAllMovies(){
        return  allMovies;
    }



    public void insert(Movie movie){

        new insertAsyncTask(movieDao).execute(movie);
    }

    public void delete(int movieId){
        new deleteAsyncTask(movieDao).execute(movieId);
    }


    private static class insertAsyncTask extends AsyncTask<Movie, Void, Void> {

        private MovieDao mAsyncTaskDao;

        insertAsyncTask(MovieDao movieDao) {
            mAsyncTaskDao = movieDao;
        }

        @Override
        protected Void doInBackground(Movie... movies) {
            mAsyncTaskDao.insert(movies[0]);
            return null;
        }
    }

    private static class deleteAsyncTask extends AsyncTask<Integer, Void, Void> {

        private MovieDao mAsyncTaskDao;

        deleteAsyncTask(MovieDao movieDao) {
            mAsyncTaskDao = movieDao;
        }

        @Override
        protected Void doInBackground(Integer... movieIds) {
            mAsyncTaskDao.deleteMovie(movieIds[0]);
            return null;
        }
    }
}
