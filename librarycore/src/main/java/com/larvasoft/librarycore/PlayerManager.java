package com.larvasoft.librarycore;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.SeekBar;

import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.source.dash.DashMediaSource;
import com.google.android.exoplayer2.source.dash.DefaultDashChunkSource;
import com.google.android.exoplayer2.source.hls.HlsMediaSource;
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;

import static com.larvasoft.librarycore.PlayerManagerFactory.IPlayerManager;
import static com.larvasoft.librarycore.PlayerManagerFactory.IPlayerManagerListener;
import static com.larvasoft.librarycore.ControlLayout.BUTTON_FORWARD;
import static com.larvasoft.librarycore.ControlLayout.BUTTON_FULL_SCREEN;
import static com.larvasoft.librarycore.ControlLayout.BUTTON_PAUSE;
import static com.larvasoft.librarycore.ControlLayout.BUTTON_PLAY;
import static com.larvasoft.librarycore.ControlLayout.BUTTON_REWIND;

class PlayerManager implements IPlayerManager {

    private final Activity mActivity;
    private final PlayerView mPlayerView;
    private SimpleExoPlayer mPlayer;
    private View mCustomControllerLayout;
    private boolean useDefaultController = true;
    private boolean isFullScreened;
    private ControlLayout mDefaultControllerLayout;
    private boolean playWhenReady;

    PlayerManager(Activity activity, PlayerView playerView) {
        this.mActivity = activity;
        this.mPlayerView = playerView;
        this.mPlayerView.setUseController(false);
        this.mActivity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        mPlayer = ExoPlayerFactory.newSimpleInstance(activity);
    }

    @Override
    public void init(String contentURL, boolean playWhenReady, boolean useDefaultController) {
        MediaSource source = buildMediaSource(contentURL);
        this.playWhenReady = playWhenReady;
        if (useDefaultController) {
            this.useDefaultController = true;
            setDefaultControllerLayout();
        }
        this.mPlayerView.setPlayer(mPlayer);
        this.mPlayer.prepare(source);
        setPlayWhenReady(playWhenReady);
    }

    @Override
    public void play() {
        if (mPlayer != null) {
            this.playWhenReady = true;
            setPlayWhenReady(true);
        }
    }

    @Override
    public void pause() {
        if (mPlayer != null) {
            this.playWhenReady = false;
            setPlayWhenReady(false);
        }
    }

    @Override
    public void onResume() {
        if (mPlayer != null) {
            if (useDefaultController) {
                mDefaultControllerLayout.setShowHidePlayAndPause(View.VISIBLE, View.GONE);
                setPlayWhenReady(playWhenReady);
            } else {
                setPlayWhenReady(playWhenReady);
            }
        }
    }

    @Override
    public void onPause() {
        if (mPlayer != null) {
            this.playWhenReady = false;
            setPlayWhenReady(false);
        }
    }

    @Override
    public void onDestroy() {
        if (mPlayer != null) {
            mPlayer.release();
            mPlayer = null;
        }
    }

    @Override
    public void seekTo(long position) {
        if (mPlayer != null) {
            mPlayer.seekTo(position);
        }
    }

    @Override
    public long getDuration() {
        if (mPlayer != null) {
            return mPlayer.getDuration();
        }
        return 0;
    }

    @Override
    public long getCurrentPosition() {
        if (mPlayer != null) {
            return mPlayer.getCurrentPosition();
        }
        return 0;
    }

    @Override
    public boolean getPlayWhenReady() {
        if (mPlayer != null) {
            return mPlayer.getPlayWhenReady();
        }
        return false;
    }

    @Override
    public void setPlayWhenReady(boolean playWhenReady) {
        if (mPlayer != null) {
            this.playWhenReady = playWhenReady;
            mPlayer.setPlayWhenReady(playWhenReady);
        }
    }

    @Override
    public void hideControllerLayout() {
        if (mCustomControllerLayout != null) {
            mCustomControllerLayout.setVisibility(View.GONE);
        } else if (mDefaultControllerLayout != null && useDefaultController) {
            mDefaultControllerLayout.getView().setVisibility(View.GONE);
        }
    }

    @Override
    public void showControllerLayout() {
        if (mCustomControllerLayout != null) {
            mCustomControllerLayout.setVisibility(View.VISIBLE);
        } else if (mDefaultControllerLayout != null && useDefaultController) {
            mDefaultControllerLayout.getView().setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void setCustomControllerLayout(View view, ViewGroup.LayoutParams layoutParams) {
        if (view != null) {
            this.useDefaultController = false;
            this.mCustomControllerLayout = view;
            this.mPlayerView.addView(view, layoutParams);
        }
    }

    @Override
    public void setCustomControllerLayout(int resource, ViewGroup.LayoutParams layoutParams) {
        LayoutInflater inflater = mActivity.getLayoutInflater();
        View view = inflater.inflate(resource, null, false);
        if (view != null) {
            this.useDefaultController = false;
            this.mCustomControllerLayout = view;
            this.mPlayerView.addView(view, layoutParams);
        }
    }

    @Override
    public View getControllerLayout() {
        if (mDefaultControllerLayout != null) {
            return mDefaultControllerLayout.getView();
        } else if (mCustomControllerLayout != null) {
            return mCustomControllerLayout;
        }
        return null;
    }

    @Override
    public void setPlayerManagerListener(final IPlayerManagerListener listener) {
        if (listener != null) {
            mPlayer.addListener(listener);
            mPlayerView.setId(PLAYER_VIEW_ID);
            mPlayerView.setOnTouchListener(listener);
            mPlayerView.setOnClickListener(listener);
            if (mCustomControllerLayout != null) {
                mCustomControllerLayout.setId(CUSTOM_LAYOUT_ID);
                mCustomControllerLayout.setOnClickListener(listener);
                mCustomControllerLayout.setOnClickListener(listener);
            } else if (useDefaultController && mDefaultControllerLayout != null && mDefaultControllerLayout.getView() != null) {
                mDefaultControllerLayout.getView().setId(DEFAULT_LAYOUT_ID);
                mDefaultControllerLayout.getView().setOnClickListener(listener);
                mDefaultControllerLayout.getView().setOnTouchListener(listener);
            }
        }
    }

    @SuppressLint("StaticFieldLeak")
    private void setDefaultControllerLayout() {
        mDefaultControllerLayout = new ControlLayout(mActivity);
        mDefaultControllerLayout.initDefaultView(playWhenReady);
        initDefaultControllerEvent();
        mPlayerView.addView(mDefaultControllerLayout.getView());
        initDefaultControllerRuntime();
    }

    private void initDefaultControllerRuntime() {
        Runnable runnableSeekBar = new Runnable() {
            @Override
            public void run() {
                mDefaultControllerLayout.getSeekBar().removeCallbacks(this);
                mDefaultControllerLayout.setSeekBarPositionTime(getCurrentPosition());
                mDefaultControllerLayout.setTextPositionTime(getCurrentPosition());
                mDefaultControllerLayout.getSeekBar().postDelayed(this, 1000);
            }
        };
        mDefaultControllerLayout.getSeekBar().postDelayed(runnableSeekBar, 1000);
    }

    private void initDefaultControllerEvent() {
        mDefaultControllerLayout.setListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (view.getId() == BUTTON_PLAY) {
                    mDefaultControllerLayout.setShowHidePlayAndPause(View.GONE, View.VISIBLE);
                    play();
                } else if (view.getId() == BUTTON_PAUSE) {
                    mDefaultControllerLayout.setShowHidePlayAndPause(View.VISIBLE, View.GONE);
                    pause();
                } else if (view.getId() == BUTTON_REWIND) {
                    long position = getCurrentPosition();
                    if (position - 5000 > 0) {
                        mDefaultControllerLayout.setSeekBarPositionTime(position - 5000);
                        mDefaultControllerLayout.setTextPositionTime(position - 5000);
                        seekTo(position - 5000);
                    } else {
                        mDefaultControllerLayout.setSeekBarPositionTime(0);
                        mDefaultControllerLayout.setTextPositionTime(0);
                        seekTo(0);

                    }
                } else if (view.getId() == BUTTON_FORWARD) {
                    long position = getCurrentPosition();
                    if (position + 5000 < getDuration()) {
                        mDefaultControllerLayout.setSeekBarPositionTime(position + 5000);
                        mDefaultControllerLayout.setTextPositionTime(position + 5000);
                        seekTo(position + 5000);

                    } else {
                        mDefaultControllerLayout.setSeekBarPositionTime(getDuration());
                        mDefaultControllerLayout.setTextPositionTime(getDuration());
                        seekTo(getDuration());
                        pause();
                    }
                } else if (view.getId() == BUTTON_FULL_SCREEN) {
                    fullScreen(mDefaultControllerLayout);
                }
            }
        }, new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if (b) {
                    mDefaultControllerLayout.setSeekBarPositionTime(i);
                    mDefaultControllerLayout.setTextPositionTime(i);
                    seekTo(i);
                }

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                seekTo(seekBar.getProgress());
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        mPlayer.addListener(new Player.EventListener() {
            @Override
            public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
                if (playWhenReady && playbackState == Player.STATE_READY) {
                    mDefaultControllerLayout.setMaxTime(mPlayer.getDuration());
                    mDefaultControllerLayout.setDurationText(mPlayer.getDuration());

                }
            }
        });
    }

    private void fullScreen(final ControlLayout defaultControlLayout) {
        boolean check = isFullScreened;
        if (check) {
            mActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            mPlayerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FIT);
//            View decorView = mActivity.getWindow().getDecorView();
//            decorView.setSystemUiVisibility(
//                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
//                            | View.SYSTEM_UI_FLAG_IMMERSIVE);

            mActivity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            defaultControlLayout.setFullScreenButtonBackgroundIsFull(true);
            isFullScreened = false;
        } else {
            mActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            mPlayerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FILL);
//            View decorView = mActivity.getWindow().getDecorView();
//            decorView.setSystemUiVisibility(
//                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
//                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
//                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
//                            | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
//                            | View.SYSTEM_UI_FLAG_IMMERSIVE);
            mActivity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            isFullScreened = true;
            defaultControlLayout.setFullScreenButtonBackgroundIsFull(false);
        }
    }

    private MediaSource buildMediaSource(String contentURL) {
        Uri uri = Uri.parse(contentURL);
        if (uri == null) {
            return null;
        }
        String type = uri.getLastPathSegment();
        if (type == null) {
            return null;
        } else if (type.contains("mp3")) {
            return new ProgressiveMediaSource.Factory(new DefaultHttpDataSourceFactory(USER_AGENT)).createMediaSource(uri);
        } else if (type.contains("mp4")) {
            return new ProgressiveMediaSource.Factory(new DefaultHttpDataSourceFactory(USER_AGENT)).createMediaSource(uri);
        } else {
            if (type.contains("m3u8")) {
                return new HlsMediaSource.Factory(new DefaultHttpDataSourceFactory(USER_AGENT)).createMediaSource(uri);
            } else {
                DefaultDashChunkSource.Factory dashChunkSourceFactory = new DefaultDashChunkSource.Factory(new DefaultHttpDataSourceFactory(USER_AGENT));
                DataSource.Factory manifestDataSourceFactory = new DefaultHttpDataSourceFactory(USER_AGENT);
                return new DashMediaSource.Factory(dashChunkSourceFactory, manifestDataSourceFactory).createMediaSource(uri);
            }
        }
    }
}
