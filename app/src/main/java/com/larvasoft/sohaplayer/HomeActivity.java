package com.larvasoft.sohaplayer;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.ui.PlayerView;
import com.larvasoft.librarycore.PlayerManagerFactory;

public class HomeActivity extends AppCompatActivity {
    private PlayerManagerFactory.IPlayerManager mPlayer;
    private PlayerView mPlayerView;
    private final String TAG=this.getClass().getName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        initControl();
        initData();
        initEvent();
    }

    private void initControl() {
        mPlayerView = findViewById(R.id.your_player_view);
    }

    private void initData() {
        String content = "https://hls.mediacdn.vn/kenh14/2018/6/9/8JnfIa84TnU-15285505738661637949249.mp4/master.m3u8";
        mPlayer = PlayerManagerFactory.newInstance(this,mPlayerView);
        mPlayer.init(content,true, true);
        mPlayer.setPlayerManagerListener(new EventChange());
    }

    private void initEvent() {

    }

    @Override
    protected void onPause() {
        super.onPause();
        mPlayer.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mPlayer.onResume();
    }

    @Override
    protected void onDestroy() {
        mPlayer.onDestroy();
        super.onDestroy();
    }

    private class EventChange implements PlayerManagerFactory.IPlayerManagerListener {
        @Override
        public void onClick(View view) {
            if (view.getId() == PlayerManagerFactory.IPlayerManager.PLAYER_VIEW_ID){
                mPlayer.showControllerLayout();
            }else {
                mPlayer.hideControllerLayout();
            }
        }

        @SuppressLint("ClickableViewAccessibility")
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            if (view.getId() == PlayerManagerFactory.IPlayerManager.PLAYER_VIEW_ID){
                mPlayer.showControllerLayout();
            }else {
                mPlayer.hideControllerLayout();
            }
            return false;
        }

        @Override
        public void onTimelineChanged(Timeline timeline, @Nullable Object manifest, int reason) {

        }

        @Override
        public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {

        }

        @Override
        public void onLoadingChanged(boolean isLoading) {
            Log.d(TAG, "onLoadingChanged: "+ isLoading);

        }

        @Override
        public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
            Log.d(TAG, "onPlayerStateChanged: "+ playWhenReady);

        }

        @Override
        public void onRepeatModeChanged(int repeatMode) {
        }

        @Override
        public void onShuffleModeEnabledChanged(boolean shuffleModeEnabled) {

        }

        @Override
        public void onPlayerError(ExoPlaybackException error) {

        }

        @Override
        public void onPositionDiscontinuity(int reason) {

        }

        @Override
        public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {

        }

        @Override
        public void onSeekProcessed() {

        }
    }
}
