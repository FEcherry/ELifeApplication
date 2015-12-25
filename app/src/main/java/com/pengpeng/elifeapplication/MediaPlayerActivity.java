package com.pengpeng.elifeapplication;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

/*
* 1、前台的服务点击进去之后跳转到activity，但不重新create (fixed)
* 2、重构AudioPlayer，改为继承MediaPlayer (solved)
* 3、进度条两端增加时间 (solved)
* 4、基本完成本地音频播放，构建播放网络音频的类
* 5、尝试连接前后端
* */
public class MediaPlayerActivity extends ActionBarActivity implements View.OnClickListener, MediaPlayer.OnCompletionListener {
    private static final String TAG = "MediaPlayer";
    private LocalAudioPlayer localAudioPlayer = new LocalAudioPlayer(MediaPlayerActivity.this);
    private Cursor cursor;
    private Button playButton;
    private Button preButton;
    private Button nextButton;
    private Button loopButton;
    private Button jumpButton;
    private SeekBar seekBar;
    private TextView startTimeText;
    private TextView endTimeText;
    private static final String PLAY = "Play";
    private static final String PAUSE = "Pause";
    private Intent intent;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    if (localAudioPlayer != null && localAudioPlayer.isPrepared()) {
                        int totalTime = localAudioPlayer.getDuration();
                        int currentTime = localAudioPlayer.getCurrentPosition();
                        int seekBarMax = seekBar.getMax();  //SeekBar继承ProgressBar类，调用ProgressBar的getMax方法取得进度条设置的最大值
                        Log.e("Progress: ", totalTime + " " + currentTime + " " + seekBarMax);
                        if (totalTime > 0 && currentTime > 0 && seekBarMax > 0) {
                            Log.i("Progress: ", String.valueOf(seekBar.getProgress()));
                            startTimeText.setText(getTimeText(currentTime));
                            seekBar.setProgress((int) (seekBarMax * (float) currentTime / totalTime));
                        }
                    }
                    break;
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.local_audio_layout);
        initView();
        cursor = getCursor();
        localAudioPlayer.setCursor(cursor);
        setListener();
        UpdateSeekBarThread thread = new UpdateSeekBarThread();
        thread.start();

    }

    private Cursor getCursor() {
        ContentResolver contentResolver = getContentResolver();
        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        Log.i(TAG + " EXT_CON_URI", uri.toString());
        Cursor cursor = contentResolver.query(uri, null, null, null, null);//调用内容解析器ContentResolver的query方法，提交数据给cursor
        if (cursor == null) {
            Log.i(TAG, "--->Query failed!");
            return null;
        } else if (!cursor.moveToFirst()) {
            Log.i(TAG, "--->No Media!");
            cursor.close();
            return null;
        } else {
            return cursor;
        }
    }

    private void setListener() {
        playButton.setOnClickListener(this);
        nextButton.setOnClickListener(this);
        preButton.setOnClickListener(this);
        loopButton.setOnClickListener(this);
        localAudioPlayer.setOnCompletionListener(this);    //完成监听器
        seekBar.setOnSeekBarChangeListener(new ProgressBarListener());   //进度条改变监听器
        jumpButton.setOnClickListener(this);
    }

    private void initView() {
        playButton = (Button) findViewById(R.id.play_btn);
        nextButton = (Button) findViewById(R.id.next_btn);
        preButton = (Button) findViewById(R.id.previous_btn);
        loopButton = (Button) findViewById(R.id.loop_btn);
        seekBar = (SeekBar) findViewById(R.id.seekBar);
        startTimeText = (TextView) findViewById(R.id.startTime);
        endTimeText = (TextView) findViewById(R.id.endTime);
        jumpButton = (Button)findViewById(R.id.button);
    }

    @Override
    protected void onDestroy() {
        localAudioPlayer.release();
        Log.i(TAG + " onStop", localAudioPlayer.toString());
        localAudioPlayer = null;
        cursor.close();
        if (intent != null) {
            stopService(intent);
        }
        super.onDestroy();
    }

    public String getTimeText(int time) {
        /*
        传入的time参数单位为milliseconds，即毫秒
        这个方法可以将毫秒单位的时间转换为0：00形式的时间
         */
        int totalSeconds = time / 1000;
        int minutes = totalSeconds / 60;
        int seconds = totalSeconds % 60;
        String showTime;
        if (seconds > 9 && seconds < 60) {
            showTime = minutes + ":" + seconds;
        } else {
            showTime = minutes + ":0" + seconds;
        }
        return showTime;
    }

    public class UpdateSeekBarThread extends Thread {
        @Override
        public void run() {
            while (localAudioPlayer != null) {
                try {
                    Thread.sleep(30);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                handler.sendEmptyMessage(0);
            }

        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.play_btn:
                if (PLAY.equals(playButton.getText())) {
                    localAudioPlayer.play();
                    playButton.setText(PAUSE);
                } else if (PAUSE.equals(playButton.getText())) {
                    localAudioPlayer.playPause();
                    playButton.setText(PLAY);
                }
                break;
            case R.id.next_btn:
                localAudioPlayer.playNext();
                if (PLAY.equals(playButton.getText())) {
                    playButton.setText(PAUSE);
                }
                break;
            case R.id.previous_btn:
                localAudioPlayer.playPrevious();
                if (PLAY.equals(playButton.getText())) {
                    playButton.setText(PAUSE);
                }
                break;
            case R.id.loop_btn:
                localAudioPlayer.setLoopin.0. 
        endTimeText.setText(getTimeText(localAudioPlayer.getDuration()));
        intent = new Intent(MediaPlayerActivity.this, MediaPlayerService.class);
        intent.putExtra("songName", localAudioPlayer.getAudioInfo().getCurrentAudioTitle(cursor));//将音频名字传递给”songname“
        startService(intent);
    }


    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {
        playButton.setText(PLAY);
    }


    private class ProgressBarListener implements SeekBar.OnSeekBarChangeListener {

        /*
        * 这里的progress参数指的是相对于seekBar来说的progress，而不是音频的progress
        * seekBar默认是的最大值是100,如果改成音频的大小，那么这里的progress参数就是音频的progress
        * */
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if (fromUser) {
                localAudioPlayer.seekTo((int) (localAudioPlayer.getDuration() * (float) progress / seekBar.getMax()));
                seekBar.setProgress(progress);
            }
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }
    }
}
