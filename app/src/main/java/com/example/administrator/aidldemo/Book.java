package com.example.administrator.aidldemo;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by ChuPeng on 2017/3/15.
 */

public class Book implements Parcelable
{
    private String name;
    private int price;

    public Book()
    {

    }

    protected Book(Parcel in)
    {
        name = in.readString();
        price = in.readInt();
    }

    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeString(name);
        dest.writeInt(price);
    }

    public int describeContents()
    {
        return 0;
    }

    public static final Creator<Book> CREATOR = new Creator<Book>()
    {
        public Book createFromParcel(Parcel in)
        {
            return new Book(in);
        }

        public Book[] newArray(int size)
        {
            return new Book[size];
        }
    };

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public int getPrice()
    {
        return price;
    }

    public void setPrice(int price)
    {
        this.price = price;
    }
}
