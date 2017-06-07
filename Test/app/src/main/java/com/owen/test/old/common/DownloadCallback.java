package com.owen.test.old.common;

import android.net.NetworkInfo;

/**
 * Created by owen on 2017/6/6.
 */

public interface DownloadCallback<T> {
    interface Progress {
        int ERROR = -1;
        int CONNECT_SUCCESS = 0;
        int GET_INPUT_STREAM_SUCCESS = 1;
        int PROCESS_INPUT_STREAM_IN_PROGRESS = 2;
        int PROCESS_INPUT_STREAM_IN_SUCCESS = 3;
    }

    void updateFromDownload(T result);

    NetworkInfo getActiveNetWorkInfo();

    void onProgressUpdate(int progressCode, int percentComplete);

    void finishDownloading();

}
