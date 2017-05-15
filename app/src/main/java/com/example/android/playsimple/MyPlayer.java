package com.example.android.playsimple;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

/**
 * Created by Adel on 5/10/2017.
 */

public class MyPlayer extends AppCompatActivity {

    static MediaPlayer mMediaPlayer;
    AudioManager mAudioManager;

    private AudioManager.OnAudioFocusChangeListener mOnAudioFocusChangeListener = new AudioManager.OnAudioFocusChangeListener() {
        @Override
        public void onAudioFocusChange(int focusChange) {
            if (focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT ||
                    focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK) {

                mMediaPlayer.pause();
                mMediaPlayer.seekTo(0);
            } else if (focusChange == AudioManager.AUDIOFOCUS_GAIN) {
                mMediaPlayer.start();
            } else if (focusChange == AudioManager.AUDIOFOCUS_LOSS) {
                releaseMediaPlayer();
            }
        }
    };

    private MediaPlayer.OnCompletionListener mCompletionListener = new MediaPlayer.OnCompletionListener() {
        @Override
        public void onCompletion(MediaPlayer mediaPlayer) {
            songIndex++;
            if (songIndex > songs.size() - 1){
                songIndex = 0;
            }
            onSongChanged();
        }
    };

    Button prev, play, pause, next;
    TextView song, artist, progressTime, fullTime;
    ImageView album;
    SeekBar seekBar;
    int songId, songIndex, currentTime, skBarProgress;
    ArrayList<Song> songs;
    Handler myHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.player);

        songId  = getIntent().getIntExtra("songID", 0);

        mAudioManager = (AudioManager) getBaseContext().getSystemService(Context.AUDIO_SERVICE);

        song = (TextView)findViewById(R.id.songName);
        artist = (TextView)findViewById(R.id.artistName);
        album = (ImageView)findViewById(R.id.albumImage);
        progressTime = (TextView)findViewById(R.id.startTime);
        fullTime = (TextView)findViewById(R.id.fullTime);
        seekBar = (SeekBar)findViewById(R.id.seekbar);
        prev = (Button)findViewById(R.id.btnPrev);
        play = (Button)findViewById(R.id.btnPlay);
        pause = (Button)findViewById(R.id.btnPause);
        next = (Button)findViewById(R.id.btnNext);

        songs = new ArrayList<Song>();
        songs.add(new Song(R.string.aho, R.string.amr, R.raw.aho_lel_we_adda, R.drawable.shoft));
        songs.add(new Song(R.string.atr, R.string.ahmed, R.raw.atr_el_hayah, R.drawable.mekky));
        songs.add(new Song(R.string.dawar, R.string.ahmed, R.raw.dawar_benafsak, R.drawable.mekky));
        songs.add(new Song(R.string.elhassah, R.string.ahmed, R.raw.el_hassah_el_sabaa, R.drawable.mekky));
        songs.add(new Song(R.string.elhelm, R.string.ahmed, R.raw.el_helm, R.drawable.mekky));
        songs.add(new Song(R.string.elleila, R.string.amr, R.raw.el_leila, R.drawable.wayah));
        songs.add(new Song(R.string.shoft, R.string.amr, R.raw.shoft_el_ayam, R.drawable.shoft));
        songs.add(new Song(R.string.srce, R.string.nzb, R.raw.srce_vatreno, R.drawable.srce));
        songs.add(new Song(R.string.svad, R.string.alex, R.raw.svadiba, R.drawable.svadiba));
        songs.add(new Song(R.string.terca, R.string.silente, R.raw.terca_na_tisinu, R.drawable.silente));
        songs.add(new Song(R.string.pirate, R.string.dk, R.raw.the_pirate_bay_song, R.drawable.dubioza));
        songs.add(new Song(R.string.treble, R.string.svadbas, R.raw.treblebass, R.drawable.treblebass));
        songs.add(new Song(R.string.wayah, R.string.amr, R.raw.wayah, R.drawable.wayah));
        songs.add(new Song(R.string.zorica, R.string.mejasi, R.raw.zorica, R.drawable.zorica));

        play.setEnabled(false);

        //releaseMediaPlayer();

        int result = mAudioManager.requestAudioFocus(mOnAudioFocusChangeListener,
                AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN_TRANSIENT);

        if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
            for (int i = 0; i < songs.size(); i++){
                if (songs.get(i).getmAudioResourceId() == songId){
                    songIndex = i;
                    break;
                }
            }

            onSongChanged();
        }

        prev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pause.setEnabled(true);
                play.setEnabled(false);
                songIndex--;
                if (songIndex < 0){
                    songIndex = songs.size() - 1;
                }
                onSongChanged();
            }
        });

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pause.setEnabled(true);
                play.setEnabled(false);
                songIndex++;
                if (songIndex > songs.size() - 1){
                    songIndex = 0;
                }
                onSongChanged();
            }
        });

        pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMediaPlayer.pause();
                play.setEnabled(true);
                pause.setEnabled(false);
            }
        });

        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMediaPlayer.start();
                play.setEnabled(false);
                pause.setEnabled(true);
            }
        });

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                skBarProgress = progress;
                progressTime.setText(String.format("%dm:%ds", TimeUnit.MILLISECONDS.toMinutes((long) progress),
                        TimeUnit.MILLISECONDS.toSeconds((long) progress) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes((long) progress))));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                myHandler.removeCallbacks(UpdateSongTime);
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mMediaPlayer.seekTo(skBarProgress);
                myHandler.postDelayed(UpdateSongTime,100);
            }
        });
    }

    @Override
    public void onStop() {
        super.onStop();

        //releaseMediaPlayer();
    }

    private void releaseMediaPlayer() {
        if (mMediaPlayer != null) {
            mMediaPlayer.release();

            mMediaPlayer = null;

            mAudioManager.abandonAudioFocus(mOnAudioFocusChangeListener);
        }
    }

    private void onSongChanged(){
        releaseMediaPlayer();
        song.setText(songs.get(songIndex).getmSongName());
        artist.setText(songs.get(songIndex).getmArtistName());
        album.setImageResource(songs.get(songIndex).getmImageResourceId());
        mMediaPlayer = MediaPlayer.create(getBaseContext(), songs.get(songIndex).getmAudioResourceId());
        mMediaPlayer.start();
        mMediaPlayer.setOnCompletionListener(mCompletionListener);

        fullTime.setText(String.format("%dm:%ds", TimeUnit.MILLISECONDS.toMinutes((long) mMediaPlayer.getDuration()),
                TimeUnit.MILLISECONDS.toSeconds((long) mMediaPlayer.getDuration()) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes((long) mMediaPlayer.getDuration()))));

        myHandler.postDelayed(UpdateSongTime,100);
    }

    private Runnable UpdateSongTime = new Runnable() {
        public void run() {
            if (mMediaPlayer != null){
                currentTime = mMediaPlayer.getCurrentPosition();
                progressTime.setText(String.format("%dm:%ds", TimeUnit.MILLISECONDS.toMinutes((long) currentTime),
                        TimeUnit.MILLISECONDS.toSeconds((long) currentTime) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes((long) currentTime))));
                seekBar.setMax(mMediaPlayer.getDuration());
                seekBar.setProgress(currentTime);
                myHandler.postDelayed(this, 100);
            }
        }
    };

    @Override
    public void onBackPressed() {
    }
}
