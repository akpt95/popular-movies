package com.akash.movies.network;

import android.support.annotation.NonNull;
import android.util.Log;

import com.akash.movies.network.api.MovieAPI;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Dispatcher;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;


public final class RetrofitController {

    private final static String API_KEY = "bf4fddd4d2881c4b485a78393ba81fbc";
    private final static String BASE_URL = "https://api.themoviedb.org/3/";
    private static final String TAG = RetrofitController.class.getSimpleName();
    private static final int OKHTTP_CLIENT_TIMEOUT = 30;
    private static RetrofitController retrofitInstance;
    private JacksonConverterFactory jacksonConverterFactory;

    private MovieAPI movieAPI;

    private RetrofitController() {
        Log.v(TAG, " in RetrofitController()");

        //Make JSON converter
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.NONE);
        objectMapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        jacksonConverterFactory = JacksonConverterFactory.create(objectMapper);
    }

    public static RetrofitController getInstance() {
        Log.v(TAG, RetrofitController.class.getName() + " in getInstance()");

        if (retrofitInstance == null) {
            synchronized (RetrofitController.class) {
                if (retrofitInstance == null) {
                    retrofitInstance = new RetrofitController();
                }
            }
        }
        return retrofitInstance;
    }

    @NonNull
    private retrofit2.Retrofit getRetrofitBuilder(String url){
        OkHttpClient okHttpClient;
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        KeyInterceptor keyInterceptor = new KeyInterceptor();

        Dispatcher dispatcher = new Dispatcher();
        dispatcher.setMaxRequests(1);

        //Make http client
        okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(OKHTTP_CLIENT_TIMEOUT, TimeUnit.SECONDS)
                .readTimeout(OKHTTP_CLIENT_TIMEOUT, TimeUnit.SECONDS)
                .addInterceptor(loggingInterceptor)
                .addInterceptor(keyInterceptor)
                .dispatcher(dispatcher)
                .followRedirects(false)
                .build();

        return new Retrofit.Builder()
                .client(okHttpClient)
                .baseUrl(url)
                .addConverterFactory(jacksonConverterFactory)
                .build();
    }

    public class KeyInterceptor implements Interceptor{

        @Override
        public Response intercept(@NonNull Chain chain) throws IOException {
            Request original = chain.request();
            HttpUrl originalHttpUrl = original.url();

            HttpUrl url = originalHttpUrl.newBuilder()
                    .addQueryParameter("api_key", API_KEY)
                    .build();

            // Request customization: add request headers
            Request.Builder requestBuilder = original.newBuilder()
                    .url(url);

            Request request = requestBuilder.build();
            return chain.proceed(request);
        }
    }

    public MovieAPI getMovieAPI(){

        if(movieAPI!= null){
            return movieAPI;
        }

        Retrofit retrofit = getRetrofitBuilder(BASE_URL);
        movieAPI = retrofit.create(MovieAPI.class);

        return movieAPI;
    }

}
