package com.pengpeng.elifeapplication;

import android.content.Context;
import android.database.Cursor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.widget.Toast;

import java.io.IOException;

/**
 * Created by pengpeng on 15-12-6.
 建立一个本地音乐播放器，继承MediaPlayer类,实现IAudioPlayer接口
 */
public class LocalAudioPlayer extends MediaPlayer implements IAudioPlayer{
    private Context context;
    private AudioInfo audioInfo;//本地音频特有
    private Cursor cursor;//本地音频特有
    private boolean prepared = false;
    private boolean paused = false;

    public LocalAudioPlayer(Context context) {
        this.context = context;
        this.audioInfo = new AudioInfo();
    }

    public void setCursor(Cursor cursor){
        this.cursor = cursor;
    }

    public boolean isPrepared() {
        return this.prepared;
    }

    public boolean isPaused() {
        return this.paused;
    }

    public AudioInfo getAudioInfo() {
        return this.audioInfo;
    }

    public void playPause() {   //暂停，调用MediaPlayer的isPlaying方法判断是否正在播放，否即报错
        if (isPlaying()) {
            pause();
            this.paused = true;
        } else {
            Toast.makeText(context, "The mediaPlayer is not playing!", Toast.LENGTH_SHORT).show();
        }
    }

    public void playPrevious() {    //播放上一首，调用cursor的moveToPrevious方法，再调用playPrepared和MediaPlayer的start方法；若空报错；调用Cursor的isFirst方法判断是否为第一首
        if (cursor == null) {
            Toast.makeText(context, "The cursor is null", Toast.LENGTH_SHORT).show();
        } else if (cursor.isFirst()) {
            Toast.makeText(context, "This is the first audio.", Toast.LENGTH_SHORT).show();
        } else {
            cursor.moveToPrevious();
            playPrepared();
            start();
        }
    }

    public void playNext() {    //播放下一首，调用cursor的moveToNext方法，再调用playPrepared和MediaPlayer的start方法；若空报错，调用Cursor的isLast方法判断是否为最后一首
        if (cursor == null) {
            Toast.makeText(context, "The cursor is null", Toast.LENGTH_SHORT).show();
        } else if (cursor.isLast()) {
            Toast.makeText(context, "This is the last audio.", Toast.LENGTH_SHORT).show();
        } else {
            cursor.moveToNext();
            playPrepared();
            start();
        }
    }

    public void playPrepared() {    //准备，传入音频URL，调用MediaPlayer的prepare方法且设prepared为true；若出错，打印异常
//        String urlString = "http://music.baidutt.com/up/kwcackwa/cmypus.mp3";
//        mediaPlayer.setDataSource(urlString);
        Uri contentUri = audioInfo.getCurrentAudioUri(cursor);
        try {
            reset();
            setDataSource(context.getApplicationContext(), contentUri);
            setAudioStreamType(AudioManager.STREAM_MUSIC);
            prepare();
            prepared = true;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void play() {     //播放，判断是否暂停，若是，开始播放且设pause为false；若不是，调用playPrepared方法，再开始播放且设pause为false
        if (isPaused()) {
            start();
            paused = false;
        } else {
            playPrepared();
            start();
            paused = false;
        }
    }
}
