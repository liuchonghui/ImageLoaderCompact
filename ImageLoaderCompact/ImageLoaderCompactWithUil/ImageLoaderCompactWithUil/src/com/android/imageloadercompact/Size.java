package com.android.imageloadercompact;

import android.os.Parcel;
import android.os.Parcelable;

public class Size implements Parcelable {

    public Size() {
        super();
    }

    private long value = 0L;

    public long getValue() {
        return value;
    }

    public void setValue(long value) {
        if (value < 0) {
            value = 0;
        }
        this.value = value;
    }

    public String getStringSize() {
        return String.valueOf(value);
    }

    public double getMSize() {
        double size = (double) value / 1024 / 1024;
        return size;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.value);
    }

    public static final Creator<Size> CREATOR = new Creator<Size>() {

        @Override
        public Size createFromParcel(Parcel arg0) {
            return new Size(arg0);
        }

        @Override
        public Size[] newArray(int arg0) {
            return new Size[arg0];
        }
    };

    public Size(Parcel in) {
        this.value = in.readLong();
    }
}
