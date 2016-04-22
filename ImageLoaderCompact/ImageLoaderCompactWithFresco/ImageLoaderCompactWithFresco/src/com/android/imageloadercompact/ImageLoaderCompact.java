package com.android.imageloadercompact;

import android.app.Application;
import android.content.Context;
import android.graphics.Bitmap;

import com.android.imageloadercompact.fresco.FrescoManager;

public class ImageLoaderCompact implements CompactImpl {

    private volatile static ImageLoaderCompact instance;
    private Context applicationContext;

    private ImageLoaderCompact() {
        super();
        FrescoManager.getInstance();
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

    public void init(Application application) {
        applicationContext = application.getApplicationContext();
    }

    public Context getApplicationContext() {
        return this.applicationContext;
    }

    public int dp2px(Context ctx, float dipValue) {
        final float scale = ctx.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

    boolean initialize = false;

    @Override
    public void onStart() {
        FrescoManager.getInstance().onStart();
    }

    @Override
    public void onLoad() {
        FrescoManager.getInstance().onLoad();
    }

    @Override
    public void onInitialize() {
        FrescoManager.getInstance().onInitialized();
    }

    @Override
    public boolean isInitialized() {
        return FrescoManager.getInstance().isInitialized();
    }

    @Override
    public void clearDiskCaches(final OnDiskCachesClearListener l) {
        FrescoManager.getInstance().clearDiskCaches(l);
    }

    @Override
    public Size getCacheSize() {
        return FrescoManager.getInstance().getCacheSize();
    }

    @Override
    public void displayImage(Context ctx, String url, CompactImageView imageView) {
        FrescoManager.getInstance().displayImage(ctx, url, imageView);
    }

    @Override
    public Bitmap fetchBitmapByUrl(String url) {
        return FrescoManager.getInstance().fetchBitmapByUrl(url);
    }

    @Override
    public void asyncFetchBitmapByUrl(String url,
                                      OnFetchBitmapListener l) {
        FrescoManager.getInstance().asyncFetchBitmapByUrl(url, l);
    }

    @Override
    public void onConnectionChanged(ConnectionType type) {
        FrescoManager.getInstance().onConnectionChanged(type);
    }

    @Override
    public void onConnectionClosed() {
        FrescoManager.getInstance().onConnectionClosed();
    }
}
