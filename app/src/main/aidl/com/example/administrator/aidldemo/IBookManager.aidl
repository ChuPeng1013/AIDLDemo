// IBookManager.aidl
//第二类AIDL文件
//作用是定义方法接口
package com.example.administrator.aidldemo;

// Declare any non-default types here with import statements
// 声明任何非默认类型和导入语句
//导入所需要使用的非默认支持数据类型的包
import com.example.administrator.aidldemo.Book;
import com.example.administrator.aidldemo.IOnNewBookArrivedListener;
interface IBookManager
{
    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */
    //所有的返回值前都不需要加任何东西，不管是什么数据类型
    List<Book> getBooks();

    Book getBook();

    //传参时除了Java基本类型以及String，CharSequence之外的类型
    //都需要在前面加上定向tag，具体加什么量需而定
    void addBook(in Book book);

    //注册提醒功能
    void registerListener(IOnNewBookArrivedListener listener);

    //取消注册提醒功能
    void unregisterListener(IOnNewBookArrivedListener listener);
}
