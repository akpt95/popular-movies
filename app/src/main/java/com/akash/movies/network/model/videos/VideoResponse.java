package com.akash.movies.network.model.videos;

import java.util.List;

public class VideoResponse{

    private int id;
    private List<Result> results;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public List<Result> getResults() {
        return results;
    }

    public void setResults(List<Result> results) {
        this.results = results;
    }
}
