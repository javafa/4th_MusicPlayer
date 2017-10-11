package com.veryworks.android.musicplayer;

import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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
        initView();
        initViewPager();
        initControl();
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

    SeekBarThread seekBarThread = null;
    private void setPlayer(){
        Music.Item item = music.data.get(current);
        Uri musicUri = item.musicUri;
        if(seekBarThread != null)
            seekBarThread.setStop();
        if(player != null) {
            player.release();
            player = null;
        }

        player = MediaPlayer.create(this, musicUri);
        player.setLooping(false);

        // 화면세팅
        String duration = miliToSec(player.getDuration()); // 16754265 => 03:15
        textDuration.setText(duration);
        textCurrentTime.setText("00:00");

        seekBar.setMax(player.getDuration());

        seekBarThread = new SeekBarThread(handler);
        seekBarThread.start();
    }

    private String miliToSec(int mili){
        int sec = mili / 1000;
        int min = sec / 60;
        sec = sec % 60;

        return String.format("%02d", min) + ":" + String.format("%02d",sec);
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
        if(seekBarThread != null)
            seekBarThread.setStop();

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

    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch(msg.what){
                case Const.WHAT_SET:
                    if(player != null) {
                        int cp = player.getCurrentPosition();
                        seekBar.setProgress(cp);
                        textCurrentTime.setText(miliToSec(cp));
                    }
                    break;
            }
        }
    };
}

class SeekBarThread extends Thread {
    private boolean runFlag = true;
    private Handler handler;
    public SeekBarThread(Handler handler){
        this.handler = handler;
    }
    public void setStop(){
        runFlag = false;
    }
    public void run(){
        while(runFlag) {
            handler.sendEmptyMessage(Const.WHAT_SET);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}