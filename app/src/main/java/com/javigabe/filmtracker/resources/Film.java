package com.javigabe.filmtracker.resources;

import android.graphics.Bitmap;

public class Film {
    private String id, name, genre, sinopsis;
    private Bitmap poster;

    public Film(String id, String name, String genre, Bitmap poster, String sinopsis) {
        this.id = id;
        this.name = name;
        this.genre = genre;
        this.poster = poster;
        this.sinopsis = sinopsis;
    }

    public Film(String id, String name, String genre, Bitmap poster) {
        this(id, name, genre, poster, "");
    }

    public String getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public String getGenre() {
        return this.genre;
    }

    public Bitmap getPoster() {
        return poster;
    }

    public String getSinopsis() { return sinopsis; }
}
