package com.example.moedas;

import android.os.Parcel;
import android.os.Parcelable;

public class Coin implements Parcelable {

    private String name;
    private Double buy;

    public Coin(String name, Double buy) {
        this.name=name;
        this.buy=buy;
    }

    protected Coin(Parcel in) {
        name = in.readString();
        if (in.readByte() == 0) {
            buy = null;
        } else {
            buy = in.readDouble();
        }
    }

    public static final Creator<Coin> CREATOR = new Creator<Coin>() {
        @Override
        public Coin createFromParcel(Parcel in) {
            return new Coin(in);
        }

        @Override
        public Coin[] newArray(int size) {
            return new Coin[size];
        }
    };

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getBuy() {
        return buy;
    }

    public String getStringBuy() {
        return buy.toString();
    }

    public void setBuy(double buy) {
        this.buy = buy;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(name);
        if (buy == null) {
            parcel.writeByte((byte) 0);
        } else {
            parcel.writeByte((byte) 1);
            parcel.writeDouble(buy);
        }
    }
}
