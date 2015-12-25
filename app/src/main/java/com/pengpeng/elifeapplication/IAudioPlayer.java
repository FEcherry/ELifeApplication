package com.pengpeng.elifeapplication;

/**
 * Created by pengpeng on 15-12-8.
 */
public interface IAudioPlayer {

    public boolean isPrepared();大家是否

    public boolean isPaused();

    public void playPause();

    public void playPrevious();

    public void playNext();

    public void playPrepared();

    public void play();
}
