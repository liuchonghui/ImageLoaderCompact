package com.android.imageloadercompact;

import android.content.Context;
import android.graphics.Bitmap;

public class ImageLoaderCompact implements CompactImpl {

    private volatile static ImageLoaderCompact instance;

    private ImageLoaderCompact() {
        super();
    }

    public static ImageLoaderCompact getInstance() {
        if (null == instance) {
            synchronized (ImageLoaderCompact.class) {
                if (null == instance) {
                    instance = new ImageLoaderCompact();
                }
            }
        }
        return instance;
    }

    boolean initialize = false;

    @Override
    public void onStart(Context ctx) {

    }

    @Override
    public void onLoad(Context ctx) {

    }

    @Override
    public void onInitialize(Context ctx) {

    }

    @Override
    public boolean isInitialized(Context ctx) {
        return initialize;
    }

    @Override
    public void clearDiskCaches(Context ctx) {
    }

    @Override
    public Bitmap fetchBitmapByUrl(Context ctx, String url) {
        return null;
    }

    @Override
    public void asyncFetchBitmapByUrl(Context ctx, String url,
                                      OnFetchBitmapListener l) {

    }


}
