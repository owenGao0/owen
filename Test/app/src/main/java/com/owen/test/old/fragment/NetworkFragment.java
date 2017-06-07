package com.owen.test.old.fragment;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.owen.test.old.common.DownloadCallback;
import com.owen.test.old.common.DownloadTask;

/**
 * Created by owen on 2017/6/6.
 */

public class NetworkFragment extends Fragment {
    public static final String TAG = "NetworkFragment";

    private static final String URL_KEY = "UrlKey";

    private DownloadTask mDownloadTask;
    private String mUrlString;

    public static NetworkFragment getInstance(FragmentManager fragmentManager, String url) {
        NetworkFragment networkFragment = new NetworkFragment();
        Bundle args = new Bundle();
        args.putString(URL_KEY, url);
        networkFragment.setArguments(args);
        fragmentManager.beginTransaction().add(networkFragment, TAG).commit();
        return networkFragment;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mUrlString = getArguments().getString(URL_KEY);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public void startDownload() {
        cancleDownload();
        mDownloadTask = new DownloadTask((DownloadCallback<String>) getActivity());
        mDownloadTask.execute(mUrlString);


    }

    public void cancleDownload() {
        if (mDownloadTask != null) {
            mDownloadTask.cancel(true);
        }
    }
}
