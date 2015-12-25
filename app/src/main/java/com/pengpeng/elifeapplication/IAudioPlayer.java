package com.pengpeng.elifeapplication;

/**
 * Created by pengpeng on 15-12-8.
 建立IAudioPlayer接口
 */
public interface IAudioPlayer {

    public boolean isPrepared();//是否准备好

    public boolean isPaused();//是否已暂停

    public void playPause();//暂停

    public void playPrevious();//上一首

    public void playNext();//下一首

    public void playPrepared();//准备

    public void play();//播放
}
