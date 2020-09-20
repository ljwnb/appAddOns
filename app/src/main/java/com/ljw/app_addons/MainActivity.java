package com.ljw.app_addons;

import android.content.Context;
import android.nfc.Tag;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private static final String TAG = "MainActivity";

    private Context mContext;
    private TextView tvCount;
    private Button btSendMsg;
    private Thread mThread;
    private MyThread MyThread2;
    private Handler mHandler;
    private Handler mHandler3;
    private int mMessageCnt = 0;
    private int mMessageCnt3 = 0;
    private HandlerThread mThread3;

    private int iCount = 0;

    class MyRunnable implements Runnable {
        public void run() {
            int count = 0;
            Looper.prepare();
            for (;;) {
                Log.d(TAG, "count :" + count++);
                Toast.makeText(mContext, "Mythread ", Toast.LENGTH_LONG).show();
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

        }
    }

    class MyThread extends Thread {
        private Looper mLooper;

        @Override
        public void run() {
            super.run();
            Looper.prepare();
            synchronized (this) {
                mLooper = Looper.myLooper();
                notifyAll();
            }
            Looper.loop();
        }

        public Looper getLooper() {
            if (!isAlive()) {
                return null;
            }
            // If the thread has been started, wait until the looper has been created.
            synchronized (this) {
                while (isAlive() && mLooper == null) {
                    try {
                        wait();
                    } catch (InterruptedException e) {
                    }
                }
            }
            return mLooper;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        initData();
    }

    private void initView() {
        tvCount = (TextView)findViewById(R.id.tvCount);

        btSendMsg = (Button)findViewById(R.id.btSendMsg);
        btSendMsg.setOnClickListener(this);
    }

    private void initData() {
        mContext = getApplicationContext();
        mThread = new Thread(new MyRunnable(), "MessageThread");
        mThread.start();

        MyThread2 = new MyThread();
        MyThread2.start();

        mHandler = new Handler(MyThread2.getLooper(), new Handler.Callback() {
            @Override
            public boolean handleMessage(@NonNull Message msg) {
                Log.d(TAG, "get Message " + mMessageCnt++);
                return false;
            }
        });

        mThread3 = new HandlerThread("MessageTestMyThread3");
        mThread3.start();

        mHandler3 = new Handler(mThread3.getLooper());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btSendMsg:
                tvCount.setText("点击次数: " + iCount++);
                Log.d(TAG, "Send Msg");
                Message msg = new Message();
                mHandler.sendMessage(msg);

                mHandler3.post(new Runnable() {
                    @Override
                    public void run() {
                        Log.d(TAG, "get Message for Thread3 " + mMessageCnt3++);
                    }
                });
                break;
            default:
                break;
        }
    }

}