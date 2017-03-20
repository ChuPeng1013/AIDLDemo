package com.example.administrator.aidldemo;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Process;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity
{
    private static final String TAG = "MainActivity";
    private static final int MESSAGE_NEW_BOOK_ARRIVED = 1;
    private Button bindButton;
    private Button unbindButton;
    private ListView listView;
    private ListViewAdapter adapter;
    private List<Book> bookList;
    private String processName;
    private int pid;
    private IBookManager mRemoteBookManager;
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bindButton = (Button) findViewById(R.id.bind);
        unbindButton = (Button) findViewById(R.id.unbind);
        listView = (ListView) findViewById(R.id.listView);
        bookList = new ArrayList<Book>();
        //得到当前进程的pid
        pid = Process.myPid();
        //通过pid得到进程名
        ActivityManager activityManager = (ActivityManager) getApplicationContext().getSystemService(Context.ACTIVITY_SERVICE);
        for(ActivityManager.RunningAppProcessInfo process : activityManager.getRunningAppProcesses())
        {
            if(process.pid == pid)
            {
                processName = process.processName;
                break;
            }
        }
        bindButton.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                Intent intent = new Intent(MainActivity.this, BookManagerService.class);
                bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
            }
        });
        unbindButton.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                try
                {
                    Log.d(TAG, "unregister listener:" + mOnNewBookArrivedListener);
                    mRemoteBookManager.unregisterListener(mOnNewBookArrivedListener);
                    unbindService(serviceConnection);
                    Log.d(TAG, "unbindService:" + serviceConnection);
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }

            }
        });
    }

    private ServiceConnection serviceConnection = new ServiceConnection()
    {
        public void onServiceConnected(ComponentName name, IBinder service)
        {
            //通过绑定服务拿到服务端返回的Binder对象并且转换成AIDL接口所属的类型
            IBookManager bookManager = IBookManager.Stub.asInterface(service);
            try
            {
                mRemoteBookManager = bookManager;
                List<Book> listBefore = bookManager.getBooks();
                Log.d(TAG, "当前进程为：" + processName);
                Log.d(TAG, listBefore.getClass().getCanonicalName());
                Log.d(TAG, "当前书的数量为：" + listBefore.size());
                //对需要提醒的客户通过调用服务端的registerListener()方法进行注册
                bookManager.registerListener(mOnNewBookArrivedListener);
            }
            catch(Exception e)
            {
                e.printStackTrace();
            }
        }

        public void onServiceDisconnected(ComponentName name)
        {
            mRemoteBookManager = null;
            Log.d(TAG, "binder died");
        }
    };

    private Handler mHandler = new Handler()
    {
        public void handleMessage(Message msg)
        {
            switch (msg.what)
            {
                case MESSAGE_NEW_BOOK_ARRIVED:
                    try
                    {
                        Log.d(TAG, "receive new book:" + msg.obj);
                        List<Book> newList = mRemoteBookManager.getBooks();
                        Log.d(TAG, "当前书的数量为：" + newList.size());
                        bookList.add((Book) msg.obj);
                        adapter = new ListViewAdapter(MainActivity.this, bookList);
                        adapter.notifyDataSetChanged();
                        listView.setAdapter(adapter);
                    }
                    catch(Exception e)
                    {
                        e.printStackTrace();
                    }
                    break;
                default:
                    super.handleMessage(msg);
            }
        }
    };

    //需要注册提醒功能的用户
    private IOnNewBookArrivedListener mOnNewBookArrivedListener = new IOnNewBookArrivedListener.Stub()
    {
        public void onNewBookArrived(Book newBook) throws RemoteException
        {
            Message msg = new Message();
            msg.what = MESSAGE_NEW_BOOK_ARRIVED;
            msg.obj = newBook;
            mHandler.sendMessage(msg);
        }
    };

    protected void onDestroy()
    {
        if(mRemoteBookManager != null && mRemoteBookManager.asBinder().isBinderAlive())
        {
            try
            {
                Log.d(TAG, "unregister listener:" + mOnNewBookArrivedListener);
                mRemoteBookManager.unregisterListener(mOnNewBookArrivedListener);
                unbindService(serviceConnection);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
        super.onDestroy();
    }
}
