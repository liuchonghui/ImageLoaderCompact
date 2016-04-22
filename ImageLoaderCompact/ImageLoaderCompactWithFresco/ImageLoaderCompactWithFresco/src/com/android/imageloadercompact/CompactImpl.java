package com.android.imageloadercompact;

import android.content.Context;
import android.graphics.Bitmap;

public interface CompactImpl {

    void onConnectionChanged(ConnectionType type);

    void onConnectionClosed();

    void onStart();

    void onLoad();

    void onInitialize();

    boolean isInitialized();

    void clearDiskCaches(OnDiskCachesClearListener l);

    Size getCacheSize();

    void displayImage(Context ctx, String url, CompactImageView imageView);

    Bitmap fetchBitmapByUrl(String url);

    void asyncFetchBitmapByUrl(String url, OnFetchBitmapListener l);
}
