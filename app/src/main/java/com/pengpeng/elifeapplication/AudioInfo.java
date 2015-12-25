package com.pengpeng.elifeapplication;

import android.content.ContentUris;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

/**
 * Created by pengpeng on 15-12-8.
 建立一个类表示音频信息
 */
public class AudioInfo {
    private static final String TAG = "AudioInfo";

    public long getCurrentAudioId(Cursor cursor) {     //媒体数据库的ＩＤ信息提取给Cursor，得到音频ＩＤ
        int idColumn = cursor.getColumnIndex(MediaStore.Audio.Media._ID);
        long thisId = cursor.getLong(idColumn);
        Log.i(TAG + " thisId", String.valueOf(thisId));
        return thisId;
    }

    public String getCurrentAudioTitle(Cursor cursor) {      //媒体数据库的名字信息提取给Cursor，得到音频名字
        int titleColumn = cursor.getColumnIndex(MediaStore.Audio.Media.TITLE);
        String thisTitle = cursor.getString(titleColumn);
        Log.i(TAG + " thisTitle", thisTitle);
        return thisTitle;
    }

    public Uri getCurrentAudioUri(Cursor cursor){       //得到数据库cursor中音频ＩＤ，从媒体数据库提取音频ＵＲＬ后加上其ＩＤ，得到最后ＵＲＬ
        long thisId = getCurrentAudioId(cursor);
        Uri contentUri = ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, thisId);
        Log.i(TAG + " contentUri", contentUri.toString());
        return contentUri;
    }
}
