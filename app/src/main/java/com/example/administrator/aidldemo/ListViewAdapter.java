package com.example.administrator.aidldemo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by ChuPeng on 2017/3/17.
 */

public class ListViewAdapter extends BaseAdapter
{

    private Context context;
    private List<Book> bookList;
    private LayoutInflater layoutInflater;

    public ListViewAdapter(Context context, List<Book> bookList)
    {
        this.context = context;
        this.bookList = bookList;
        layoutInflater = LayoutInflater.from(context);
    }

    public int getCount()
    {
        return bookList.size();
    }

    public Object getItem(int position)
    {
        return bookList.get(position);
    }

    public long getItemId(int position)
    {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent)
    {
        View view = layoutInflater.inflate(R.layout.listview_item, null);
        Book book = bookList.get(position);
        TextView name = (TextView) view.findViewById(R.id.bookName);
        TextView price = (TextView) view.findViewById(R.id.bookPrice);
        name.setText(book.getName());
        price.setText(book.getPrice() + "");
        return view;
    }
}
