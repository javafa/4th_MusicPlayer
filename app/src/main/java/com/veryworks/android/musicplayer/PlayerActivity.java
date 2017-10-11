package com.veryworks.android.musicplayer;

import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.veryworks.android.musicplayer.model.Music;

public class PlayerActivity extends AppCompatActivity
implements View.OnClickListener{

    Music music;
    MediaPlayer player = null;
    int current = -1;
    private ViewPager viewPager;
    private RelativeLayout controller;
    private SeekBar seekBar;
    private TextView textCurrentTime;
    private TextView textDuration;
    private ImageButton btnPlay;
    private ImageButton btnFf;
    private ImageButton btnRew;
    private ImageButton btnNext;
    private ImageButton btnPrev;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        load();
        initControl();
        initView();
        initViewPager();
        start();
    }

    private void load() {
        music = Music.getInstance();
        Intent intent = getIntent();
        current = intent.getIntExtra(Const.KEY_POSITION, -1);
    }

    private void initControl() {
        setVolumeControlStream(AudioManager.STREAM_MUSIC);
        setPlayer();
    }

    private void setPlayer(){
        Music.Item item = music.data.get(current);
        Uri musicUri = item.musicUri;
        if(player != null)
            player.release();
        player = MediaPlayer.create(this, musicUri);
        player.setLooping(false);
    }


    private void initView() {
        setContentView(R.layout.activity_player);
        viewPager = (ViewPager) findViewById(R.id.viewPager);
        controller = (RelativeLayout) findViewById(R.id.controller);
        seekBar = (SeekBar) findViewById(R.id.seekBar);
        textCurrentTime = (TextView) findViewById(R.id.textCurrentTime);
        textDuration = (TextView) findViewById(R.id.textDuration);

        btnPlay = (ImageButton) findViewById(R.id.btnPlay);
        btnFf = (ImageButton) findViewById(R.id.btnFf);
        btnRew = (ImageButton) findViewById(R.id.btnRew);
        btnNext = (ImageButton) findViewById(R.id.btnNext);
        btnPrev = (ImageButton) findViewById(R.id.btnPrev);

        btnPlay.setOnClickListener(this);
        btnFf.setOnClickListener(this);
        btnRew.setOnClickListener(this);
        btnNext.setOnClickListener(this);
        btnPrev.setOnClickListener(this);
    }

    private void initViewPager() {
        PlayerPagerAdapter adapter = new PlayerPagerAdapter(this, music.data);
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                current = position;
                setPlayer();
                if(playButtonStat == Const.STAT_PLAY){
                    start();
                }else{

                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        if(current > -1)
            viewPager.setCurrentItem(current);
    }

    private void start() {
        playButtonStat = Const.STAT_PLAY;
        player.start();
        btnPlay.setImageResource(android.R.drawable.ic_media_pause);
    }

    private void pause(){
        playButtonStat = Const.STAT_PAUSE;
        player.pause();
        btnPlay.setImageResource(android.R.drawable.ic_media_play);
    }

    @Override
    protected void onDestroy() {
        if (player != null)
            player.release();

        super.onDestroy();
    }


    int playButtonStat = Const.STAT_PLAY;

    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.btnPlay:
                if(playButtonStat == Const.STAT_PLAY)
                    pause();
                else
                    start();
                break;
            case R.id.btnFf:
                break;
            case R.id.btnRew:
                break;
            case R.id.btnNext:
                break;
            case R.id.btnPrev:
                break;
        }
    }
}
