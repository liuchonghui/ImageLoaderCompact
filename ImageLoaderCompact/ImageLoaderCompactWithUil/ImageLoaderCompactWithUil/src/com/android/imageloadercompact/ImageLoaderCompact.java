package com.android.imageloadercompact;

import android.app.Application;
import android.content.Context;
import android.graphics.Bitmap;

import com.android.imageloadercompact.glide.GlideManager;

public class ImageLoaderCompact implements CompactImpl {

    private volatile static ImageLoaderCompact instance;
    private Context applicationContext;

    private ImageLoaderCompact() {
        super();
        GlideManager.getInstance();
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
        GlideManager.getInstance().onStart();
    }

    @Override
    public void onLoad() {
        GlideManager.getInstance().onLoad();
    }

    @Override
    public void onInitialize() {
        GlideManager.getInstance().onInitialized();
    }

    @Override
    public boolean isInitialized() {
        return GlideManager.getInstance().isInitialized();
    }

    @Override
    public void clearDiskCaches(final OnDiskCachesClearListener l) {
        GlideManager.getInstance().clearDiskCaches(l);
    }

    @Override
    public Size getCacheSize() {
        return GlideManager.getInstance().getCacheSize();
    }

    @Override
    public void displayImage(Context ctx, String url, CompactImageView imageView) {
        GlideManager.getInstance().displayImage(ctx, url, imageView);
    }

    @Override
    public Bitmap fetchBitmapByUrl(String url) {
        return GlideManager.getInstance().fetchBitmapByUrl(url);
    }

    @Override
    public void asyncFetchBitmapByUrl(String url,
                                      OnFetchBitmapListener l) {
        GlideManager.getInstance().asyncFetchBitmapByUrl(url, l);
    }

    @Override
    public void onConnectionChanged(ConnectionType type) {
        GlideManager.getInstance().onConnectionChanged(type);
    }

    @Override
    public void onConnectionClosed() {
        GlideManager.getInstance().onConnectionClosed();
    }
}
