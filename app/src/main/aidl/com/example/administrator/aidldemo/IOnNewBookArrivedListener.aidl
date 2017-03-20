// IOnNewBookArrivedListener.aidl
package com.example.administrator.aidldemo;

// 声明任何非默认类型和导入语句
//导入所需要使用的非默认支持数据类型的包
import com.example.administrator.aidldemo.Book;

interface IOnNewBookArrivedListener
{
    void onNewBookArrived(in Book newBook);
}
