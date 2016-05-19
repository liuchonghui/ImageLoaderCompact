package com.android.imageloadercompact;

import android.app.Application;
import android.content.Context;
import android.graphics.Bitmap;

import com.android.imageloadercompact.picasso.PicassoManager;

public class ImageLoaderCompact implements CompactImpl {

    private volatile static ImageLoaderCompact instance;
    private Context applicationContext;

    private ImageLoaderCompact() {
        super();
        PicassoManager.getInstance();
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
        PicassoManager.getInstance().onStart();
    }

    @Override
    public void onLoad() {
        PicassoManager.getInstance().onLoad();
    }

    @Override
    public void onInitialize() {
        PicassoManager.getInstance().onInitialized();
    }

    @Override
    public boolean isInitialized() {
        return PicassoManager.getInstance().isInitialized();
    }

    @Override
    public void clearDiskCaches(final OnDiskCachesClearListener l) {
        PicassoManager.getInstance().clearDiskCaches(l);
    }

    @Override
    public Size getCacheSize() {
        return PicassoManager.getInstance().getCacheSize();
    }

    @Override
    public void displayImage(Context ctx, String url, CompactImageView imageView) {
        PicassoManager.getInstance().displayImage(ctx, url, imageView);
    }

    @Override
    public Bitmap fetchBitmapByUrl(String url) {
        return PicassoManager.getInstance().fetchBitmapByUrl(url);
    }

    @Override
    public void asyncFetchBitmapByUrl(String url,
                                      OnFetchBitmapListener l) {
        PicassoManager.getInstance().asyncFetchBitmapByUrl(url, l);
    }

    @Override
    public void onConnectionChanged(ConnectionType type) {
        PicassoManager.getInstance().onConnectionChanged(type);
    }

    @Override
    public void onConnectionClosed() {
        PicassoManager.getInstance().onConnectionClosed();
    }
}
