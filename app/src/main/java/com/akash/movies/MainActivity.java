package com.akash.movies;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.akash.movies.home.HomeFragment;
import com.akash.movies.room.Movie;
import com.akash.movies.room.MovieViewModel;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = MainActivity.class.getName();

    private ProgressDialog progressDialog;
    private MovieViewModel movieViewModel;
    private List<Movie> favouriteMovies;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        progressDialog = new ProgressDialog(this);
        movieViewModel = ViewModelProviders.of(this).get(MovieViewModel.class);

        movieViewModel.getAllMovies().observe(this, new Observer<List<Movie>>() {
            @Override
            public void onChanged(@Nullable List<Movie> movies) {
                favouriteMovies = movies;
            }
        });

        HomeFragment homeFragment = new HomeFragment();
        launchFragment(homeFragment);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        FragmentManager fragmentManager = getFragmentManager();

        if (fragmentManager.getBackStackEntryCount() <= 0) {
            finish();
        }

    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }

    public void addMovieAsFavourite(Movie movie){
        movieViewModel.insert(movie);
    }

    public List<Movie> getFavouriteMovies(){
        return favouriteMovies;
    }

    public boolean checkIfMovieIsFavourite(int movieId){

        for(Movie movie : favouriteMovies){
            if(movie.getMovieId() == movieId)
                return true;
        }

        return false;
    }

    public void deleteFromFavouriteMovies(int movieId){
        movieViewModel.delete(movieId);
    }

    public void launchFragment(Fragment fragment) {

        Log.d(TAG, " launching fragment : " + fragment.getClass().getSimpleName());

        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, fragment, fragment.getClass()
                .getSimpleName());
        fragmentTransaction.addToBackStack(fragment.getClass().getSimpleName());
        fragmentTransaction.commitAllowingStateLoss();
    }

    public void showProgressDialog(String message) {
        if (!this.isFinishing()) {

            if (progressDialog == null) {
                return;
            }
            progressDialog.setMessage(message);
            progressDialog.setCancelable(false);
            progressDialog.show();
        }
    }

    public void dismissProgressDialog() {
        if (!this.isFinishing()) {

            if (progressDialog == null) {
                return;
            }
            progressDialog.dismiss();
        }
    }
}
