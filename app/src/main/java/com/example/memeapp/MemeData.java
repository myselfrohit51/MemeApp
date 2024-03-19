package com.example.memeapp;

import com.google.gson.annotations.SerializedName;

public class MemeData {
    @SerializedName("url")
    private String imageUrl;

    public String getImageUrl() {
        return imageUrl;
    }

    @SerializedName("subreddit")
    private String subreddit;

    public String getSubreddit(){
        return subreddit;
    }

    @SerializedName("nsfw")
    private boolean nsfw;

    public boolean isNsfw() {
        return nsfw;
    }

    @SerializedName("author")
    private String author;

    public String getAuthor() {
        return author;
    }
}

