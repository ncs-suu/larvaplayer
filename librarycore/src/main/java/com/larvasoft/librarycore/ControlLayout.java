package com.larvasoft.librarycore;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

class ControlLayout {
    static int BUTTON_PLAY =1;
    static int BUTTON_PAUSE =2;
    static int BUTTON_REWIND =3;
    static int BUTTON_FORWARD =4;
    static int BUTTON_FULL_SCREEN =5;

    private ImageButton mButtonPlay;
    private ImageButton mButtonPause;
    private ImageButton mButtonRewind;
    private ImageButton mButtonForward;
    private ImageButton mButtonFullScreen;
    private TextView mTextPosition;
    private TextView mTextDuration;
    private SeekBar mSeekTime;
    private Context mContext;
    private View mView;

    ControlLayout(Context context){
        this.mContext = context;
    }

    @SuppressLint("InflateParams")
    void initDefaultView(boolean playWhenReady){
        LayoutInflater inflater = LayoutInflater.from(mContext);
        mView = inflater.inflate(R.layout.controller_layout,null,false);
        mView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        initControl();
        if (playWhenReady) {
            setShowHidePlayAndPause(View.GONE, View.VISIBLE);
        } else {
            setShowHidePlayAndPause(View.VISIBLE, View.GONE);
        }
    }
    void setListener(View.OnClickListener listener, SeekBar.OnSeekBarChangeListener seekBarChangeListener){
        initEvent(listener,seekBarChangeListener);
    }

    private void initControl() {
        mButtonPlay = mView.findViewById(R.id.button_play);
        mButtonPlay.setId(BUTTON_PLAY);
        mButtonPause = mView.findViewById(R.id.button_pause);
        mButtonPause.setId(BUTTON_PAUSE);
        mButtonRewind = mView.findViewById(R.id.button_rewind);
        mButtonRewind.setId(BUTTON_REWIND);
        mButtonForward = mView.findViewById(R.id.button_forward);
        mButtonForward.setId(BUTTON_FORWARD);
        mButtonFullScreen = mView.findViewById(R.id.button_full_screen);
        mButtonFullScreen.setId(BUTTON_FULL_SCREEN);
        mSeekTime = mView.findViewById(R.id.seek_time);
        mTextPosition = mView.findViewById(R.id.text_position);
        mTextDuration = mView.findViewById(R.id.text_duration);
    }
    private void initEvent(View.OnClickListener onClickListener, SeekBar.OnSeekBarChangeListener seekBarChangeListener){
        mView.setOnClickListener(new ControllerListener());
        mButtonPlay.setOnClickListener(onClickListener);
        mButtonPause.setOnClickListener(onClickListener);
        mButtonRewind.setOnClickListener(onClickListener);
        mButtonForward.setOnClickListener(onClickListener);
        mButtonFullScreen.setOnClickListener(onClickListener);
        mSeekTime.setOnSeekBarChangeListener(seekBarChangeListener);
        initRuntime();
    }

    View getView(){
        return mView;
    }

    void setShowHidePlayAndPause(int playVisible, int pauseVisible) {
        mButtonPlay.setVisibility(playVisible);
        mButtonPause.setVisibility(pauseVisible);
    }
    void setSeekBarPositionTime(long position){
        mSeekTime.setProgress((int) position);
    }
    void setTextPositionTime(long position) {
        String text = toTime(position);
        mTextPosition.setText(text);
    }
    void setDurationText(long duration) {
        String text = toTime(duration);
        mTextDuration.setText(text);
    }

    void setMaxTime(long maxTime) {
        mSeekTime.setMax((int) (maxTime));
    }


    SeekBar getSeekBar() {
        return mSeekTime;
    }

    void setFullScreenButtonBackgroundIsFull(boolean choose) {
        if (choose){
            mButtonFullScreen.setBackgroundResource(R.drawable.ic_fullscreen_expand);
        }else {
            mButtonFullScreen.setBackgroundResource(R.drawable.ic_fullscreen_skrink);
        }
    }

    private class ControllerListener implements View.OnClickListener{
        @Override
        public void onClick(View view) {
            mView.setVisibility(View.GONE);
        }
    }
    private String toTime(long value) {
        long h = value / 3600000;
        long m = (value - h * 3600000) / 60000;
        long s = (value - h * 3600000 - m * 60000) / 1000;
        String hh = (h < 10) ? "0" + h : Long.toString(h);
        String mm = (m < 10) ? "0" + m : Long.toString(m);
        String ss = (s < 10) ? "0" + s : Long.toString(s);
        return hh + ":" + mm + ":" + ss;
    }
    private void initRuntime(){
        Runnable runnableController = new Runnable() {
            @Override
            public void run() {
                getView().removeCallbacks(this);
                if (getView().getVisibility() == View.VISIBLE && mButtonPlay.getVisibility()==View.GONE) {
                    getView().setVisibility(View.GONE);
                }
                getView().postDelayed(this, 5000);
            }
        };
        getView().postDelayed(runnableController,5000);
    }
}
