package com.owen.test.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.owen.test.R;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends FragmentActivity implements View.OnClickListener {


    private Button start, reset;
    private TextView title, content;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        start = (Button) findViewById(R.id.start);
        reset = (Button) findViewById(R.id.reset);
        title = (TextView) findViewById(R.id.title);
        content = (TextView) findViewById(R.id.content);
        start.setOnClickListener(this);
        reset.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.start) {


            final OkHttpClient client = new OkHttpClient();
            final String url = "https://www.baidu.com";


            Thread t = new Thread(new Runnable() {
                @Override
                public void run() {
                    Request request = new Request.Builder().url(url).build();
                    try {
                        Response response = client.newCall(request).execute();
                        Message msg = new Message();
                        msg.obj = response.body().string();
                        handler.sendMessage(msg);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
            t.start();

        } else if (v.getId() == R.id.reset) {
            //关闭网络请求 重置数据
            title.setText("准备请求网络数据");
            content.setText("");
        }
    }


    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            String response = (String) msg.obj;
            content.setText(response);
        }
    };

}
