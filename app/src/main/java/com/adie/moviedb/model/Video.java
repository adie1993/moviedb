package com.adie.moviedb.model;

import com.google.gson.annotations.SerializedName;


public class Video {
    @SerializedName("key")
    private String key;


    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }


}
