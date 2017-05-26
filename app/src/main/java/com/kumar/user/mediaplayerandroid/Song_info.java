package com.kumar.user.mediaplayerandroid;

/**
 * Created by User on 5/26/2017.
 */

public class Song_info {
    public String Path;
    public String song_name;
    public String album_name;
    public String artist_name;

    public Song_info(String path, String song_name, String album_name, String artist_name) {
        this.Path = path;
        this.song_name = song_name;
        this.album_name = album_name;
        this.artist_name = artist_name;
    }
}
