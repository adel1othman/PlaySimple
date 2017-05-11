package com.example.android.playsimple;

/**
 * Created by Adel on 5/10/2017.
 */

public class Song {
    private int mSongName;
    private int mArtistName;
    private int mAudioResourceId;
    private int mImageResourceId;

    Song(int songName, int artistName, int audioResourceId, int imageResourceId) {
        mSongName = songName;
        mArtistName = artistName;
        mAudioResourceId = audioResourceId;
        mImageResourceId = imageResourceId;
    }

    int getmSongName() {
        return mSongName;
    }

    int getmArtistName() {
        return mArtistName;
    }

    int getmAudioResourceId() {
        return mAudioResourceId;
    }

    int getmImageResourceId() {
        return mImageResourceId;
    }
}
