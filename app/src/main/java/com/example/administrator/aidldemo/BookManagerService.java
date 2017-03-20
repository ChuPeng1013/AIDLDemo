package com.example.administrator.aidldemo;

import android.app.ActivityManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.os.Process;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.util.Log;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by ChuPeng on 2017/3/15.
 */

public class BookManagerService extends Service
{

    private static final String TAG = "BookManagerService";
    //包含Book对象的list
    private CopyOnWriteArrayList<Book> mBookList = new CopyOnWriteArrayList<Book>();
    //private CopyOnWriteArrayList<IOnNewBookArrivedListener> mListenerList = new CopyOnWriteArrayList<IOnNewBookArrivedListener>();
    private RemoteCallbackList<IOnNewBookArrivedListener> mListenerList = new RemoteCallbackList<IOnNewBookArrivedListener>();
    private AtomicBoolean mIsServiceDestoryed = new AtomicBoolean(false);
    private String processName;
    private Book newBook;
    private int pid;
    private int bookId = 0;
    private int bookPrice = 10;
    //首先初始化，在 onCreate() 方法里面我进行了一些数据的初始化操作
    public void onCreate()
    {
        super.onCreate();
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
        Log.d(TAG, "当前进程为：" + processName + "执行onCreate()方法");
        new Thread(new ServiceWorker()).start();
    }
    //其次重写BookManager.Stub中的方法，这里面提供AIDL里面定义的方法接口的具体实现逻辑
    //由AIDL文件生成的BookManager
    private final IBookManager.Stub mBookManager = new IBookManager.Stub()
    {
        public List<Book> getBooks() throws RemoteException
        {
            return mBookList;
        }

        public Book getBook() throws RemoteException
        {
            return newBook;
        }

        public void addBook(Book book) throws RemoteException
        {
            mBookList.add(book);
        }

        public void registerListener(IOnNewBookArrivedListener listener)
        {
            /*if(!mListenerList.contains(listener))
            {
                mListenerList.add(listener);
            }
            else
            {
                Log.d(TAG, "already exists");
            }
            Log.d(TAG, "current size:" + mListenerList.size());*/

            //新用户进行注册
            mListenerList.register(listener);
        }

        public void unregisterListener(IOnNewBookArrivedListener listener)
        {
            /*if(mListenerList.contains(listener))
            {
                mListenerList.remove(listener);
                Log.d(TAG, "unregister listener succeed");
            }
            else
            {
                Log.d(TAG, "not find, can not unregister");
            }
            Log.d(TAG, "current size:" + mListenerList.size());*/

            //已经注册的用户取消注册
            mListenerList.unregister(listener);
        }

    };
    //最后重写onBind()方法，在里面返回写好的BookManager.Stub
    public IBinder onBind(Intent intent)
    {
        Log.d(TAG, "当前进程为：" + processName + "执行onBind方法");
        //实现AIDL中的方法后，返回给客户端
        return mBookManager;
    }

    private class ServiceWorker implements Runnable
    {
        public void run()
        {
            while (!mIsServiceDestoryed.get())
            {
                try
                {
                    Thread.sleep(5000);
                }
                catch (InterruptedException e)
                {
                    e.printStackTrace();
                }
                Book newBook = new Book();
                newBook.setName("newBook    " + String.valueOf(bookId));
                newBook.setPrice(bookPrice);
                bookId = bookId + 1;
                bookPrice = bookPrice + 10;
                //通知客户端新书到了
                noticeClient(newBook);
            }
        }
    }

    //新书到了通知客户端
    private void noticeClient(Book book)
    {
       /* try
        {
            mBookList.add(book);
            for(int i = 0; i < mListenerList.size(); i++)
            {
                IOnNewBookArrivedListener listener = mListenerList.get(i);
                listener.onNewBookArrived(book);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }*/
         try
        {
            final int n = mListenerList.beginBroadcast();
            mBookList.add(book);
            for(int i = 0; i < n; i++)
            {
                IOnNewBookArrivedListener listener = mListenerList.getBroadcastItem(i);
                if(listener != null)
                {
                    //通过调用客户端的onNewBookArrived()，通知已经注册的用户
                    listener.onNewBookArrived(book);
                }
            }
            mListenerList.finishBroadcast();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
