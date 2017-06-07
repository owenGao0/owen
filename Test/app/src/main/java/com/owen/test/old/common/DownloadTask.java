package com.owen.test.old.common;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by owen on 2017/6/6.
 */

public class DownloadTask extends AsyncTask<String, Integer, DownloadTask.Result> {

    private DownloadCallback<String> mCallback;

    public DownloadTask(DownloadCallback<String> callback) {
        setCallback(callback);
    }

    void setCallback(DownloadCallback<String> callback) {
        mCallback = callback;
    }


    static class Result {
        public String mResultValue;
        public Exception mException;

        public Result(String resultValue) {
            mResultValue = resultValue;

        }

        public Result(Exception exception) {
            mException = exception;

        }

    }

    @Override
    protected void onPreExecute() {

        if (mCallback != null) {
            NetworkInfo networkInfo = mCallback.getActiveNetWorkInfo();
            if (networkInfo == null || !networkInfo.isConnected() || (networkInfo.getType() != ConnectivityManager.TYPE_MOBILE && networkInfo.getType() != ConnectivityManager.TYPE_WIFI)) {
                mCallback.updateFromDownload(null);
                cancel(true);
            }
        }
    }

    @Override
    protected Result doInBackground(String... urls) {
        Result result = null;

        if (!isCancelled() && urls != null && urls.length > 0) {
            String urlString = urls[0];
            try {
                URL url = new URL(urlString);
                String resultString = downloadUrl(url);

                if (resultString != null) {
                    result = new Result(resultString);
                } else {
                    throw new IOException("NO response received");
                }

            } catch (Exception e) {
                result = new Result(e);
            }
        }


        return result;
    }

    @Override
    protected void onPostExecute(Result result) {
        if (result != null && mCallback != null) {
            if (result.mException != null) {
                mCallback.updateFromDownload(result.mException.getMessage());
            } else if (result.mResultValue != null) {
                mCallback.updateFromDownload(result.mResultValue);
            }
            mCallback.finishDownloading();
        }
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
    }

    private String downloadUrl(URL url) throws IOException {

        InputStream stream = null;
        HttpsURLConnection connection = null;
        String result = null;
        try {
            connection = (HttpsURLConnection) url.openConnection();

            connection.setReadTimeout(3000);
            connection.setConnectTimeout(3000);
            connection.setRequestMethod("GET");
            connection.setDoInput(true);
            connection.connect();

            publishProgress(DownloadCallback.Progress.CONNECT_SUCCESS, 0);

            int responseCode = connection.getResponseCode();
            if (responseCode != HttpsURLConnection.HTTP_OK) {
                throw new IOException("HTTP error code:" + responseCode);
            }

            stream = connection.getInputStream();
            publishProgress(DownloadCallback.Progress.GET_INPUT_STREAM_SUCCESS, 0);
            if (stream != null) {
                result = readStream(stream, 500);
            }
        } finally {

            if (stream != null) {
                stream.close();
            }
            if (connection != null) {
                connection.disconnect();
            }

        }

        Log.i("result", "result:" + result);
        return result;
    }


    private String readStream(InputStream stream, int maxLength) throws IOException {
        String result = null;

        InputStreamReader reader = new InputStreamReader(stream, "utf-8");
        char[] buffer = new char[maxLength];

        int numChars = 0;
        int readSize = 0;

        while (numChars < maxLength && readSize != -1) {
            numChars += readSize;
            int pct = (100 * numChars) / maxLength;
            publishProgress(DownloadCallback.Progress.PROCESS_INPUT_STREAM_IN_PROGRESS, pct);
            readSize = reader.read(buffer, numChars, buffer.length - numChars);
        }

        if (numChars != -1) {
            numChars = Math.min(numChars, maxLength);
            result = new String(buffer, 0, numChars);


            publishProgress(DownloadCallback.Progress.PROCESS_INPUT_STREAM_IN_SUCCESS, 100);
        }

        return result;
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        if (values != null && values.length > 0) {
            mCallback.onProgressUpdate(values[0], values[1]);
        }
    }
}
