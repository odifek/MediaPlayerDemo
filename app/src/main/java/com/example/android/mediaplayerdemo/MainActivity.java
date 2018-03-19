package com.example.android.mediaplayerdemo;

import android.media.MediaPlayer;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.DragEvent;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import java.sql.Time;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    private Button pauseButton, playButton;
    private MediaPlayer mediaPlayer;

    private TextView currTime, duration;
    private SeekBar seekBar;
    private double startTime = 0;
    private double finalTime = 0;

    private Handler myHandler = new Handler();;
    private int forwardTime = 5000;
    private int backwardTime = 5000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        playButton = findViewById(R.id.play_btn);

        pauseButton = findViewById(R.id.pause_btn);

        currTime = findViewById(R.id.curr_time_tv);
        duration = findViewById(R.id.duration_tv);
        seekBar = findViewById(R.id.seek_bar);
        seekBar.setClickable(false);

        prepareMediaPlayer();

//        mediaPlayer.on
    }

    /**
     * is called whenever the Activity is stopped, either by pressing back button
     */
    @Override
    protected void onStop() {
        super.onStop();
        mediaPlayer.release();
        mediaPlayer = null;
        myHandler.removeCallbacks(UpdateSongTime);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        prepareMediaPlayer();
    }

    /**
     * Prepares the media for playback
     * Loads the audio resource
     * Sets up all the buttons and their listeners
     */
    private void prepareMediaPlayer(){
        // Disable pauseButton until music starts playing
        pauseButton.setEnabled(false);
        // Enable play button so that we can start playing music
        playButton.setEnabled(true);

        // New media object is created using the raw audio resource
        mediaPlayer = MediaPlayer.create(this, R.raw.i_will_not_forget_thee);

        // PlayButton is set up to listen to button clicks
        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Start to play audio
                mediaPlayer.start();

                finalTime = mediaPlayer.getDuration();
                startTime = mediaPlayer.getCurrentPosition();

                // The max in milliseconds
                seekBar.setMax((int)finalTime);

                // Determine the current time in minutes and seconds and update textview
                currTime.setText(String.format("%02d:%02d",
                        TimeUnit.MILLISECONDS.toMinutes((long)startTime),
                        TimeUnit.MILLISECONDS.toSeconds((long)startTime) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes((long)startTime))));

                duration.setText(String.format("%02d:%02d",
                        TimeUnit.MILLISECONDS.toMinutes((long)finalTime),
                        TimeUnit.MILLISECONDS.toSeconds((long)finalTime) -
                                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes((long)finalTime))));

                seekBar.setProgress((int)startTime);
                myHandler.postDelayed(UpdateSongTime, 100);
                // Disable play button and enable pause button once music starts playing
                pauseButton.setEnabled(true);
                playButton.setEnabled(false);
            }
        });

        pauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mediaPlayer.pause();
                pauseButton.setEnabled(false);
                playButton.setEnabled(true);
            }
        });

        // Configures the seekBar to listen listen to manual seeking and adjust MediaPlayer
        // Accordingly
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (mediaPlayer != null && fromUser){
                    mediaPlayer.seekTo(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                pauseButton.setEnabled(false);
                playButton.setEnabled(true);
            }
        });



    }

    private Runnable UpdateSongTime = new Runnable() {
        @Override
        public void run() {
            startTime = mediaPlayer.getCurrentPosition();
            currTime.setText(String.format("%02d:%02d",
                    TimeUnit.MILLISECONDS.toMinutes((long)startTime),
                    TimeUnit.MILLISECONDS.toSeconds((long)startTime) -
                            TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes((long)startTime)))
            );
            seekBar.setProgress((int)startTime);
            myHandler.postDelayed(this, 100);
        }
    };


}
