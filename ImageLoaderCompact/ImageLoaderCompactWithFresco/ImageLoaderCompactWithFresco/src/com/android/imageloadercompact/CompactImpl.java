package com.android.imageloadercompact;

import android.content.Context;
import android.graphics.Bitmap;

public interface CompactImpl {

    void onStart(Context ctx);

    void onLoad(Context ctx);

    void onInitialize(Context ctx);

    boolean isInitialized(Context ctx);

    void clearDiskCaches(Context ctx);

    Bitmap fetchBitmapByUrl(Context ctx, String url);

    void asyncFetchBitmapByUrl(Context ctx, String url, OnFetchBitmapListener l);
}
