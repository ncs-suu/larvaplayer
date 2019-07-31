package com.larvasoft.librarycore;

import android.app.Activity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.ui.PlayerView;

public class PlayerManagerFactory {
    public static IPlayerManager newInstance(final Activity activity, final PlayerView playerView){
        return new PlayerManager(activity,playerView);
    }
    public interface IPlayerManager{
        String USER_AGENT = "SO HA PLAYER";
        int PLAYER_VIEW_ID = 1;
        int DEFAULT_LAYOUT_ID = 2;
        int CUSTOM_LAYOUT_ID = 3;

        void init(final String contentURL, final boolean playWhenReady, boolean useDefaultController);

        void play();
        void pause();

        void onResume();
        void onPause();
        void onDestroy();
        void seekTo(long position);
        long getDuration();
        long getCurrentPosition();
        boolean getPlayWhenReady();
        void setPlayWhenReady(boolean playWhenReady);
        void hideControllerLayout();
        void showControllerLayout();
        void setCustomControllerLayout(View view, ViewGroup.LayoutParams layoutParams);
        void setCustomControllerLayout(int resource, ViewGroup.LayoutParams layoutParams);
        View getControllerLayout();
        void setPlayerManagerListener(IPlayerManagerListener listener);
    }
    public interface IPlayerManagerListener extends View.OnClickListener, View.OnTouchListener, Player.EventListener {
        @Override
        void onClick(View view);
        @Override
        boolean onTouch(View view, MotionEvent motionEvent);
    }
}
