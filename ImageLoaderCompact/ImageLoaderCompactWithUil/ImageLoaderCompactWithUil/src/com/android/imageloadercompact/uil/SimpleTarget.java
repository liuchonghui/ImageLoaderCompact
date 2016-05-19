package com.android.imageloadercompact.uil;

import android.graphics.Bitmap;
import android.view.View;

import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

/**
 * @author liuchonghui
 */
public abstract class SimpleTarget implements ImageLoadingListener {
    @Override
    public void onLoadingStarted(String s, View view) {

    }

    @Override
    public void onLoadingFailed(String s, View view, FailReason failReason) {
        onLoadFailed();
    }

    @Override
    public void onLoadingComplete(String s, View view, Bitmap bitmap) {
        onResourceReady(s, bitmap);
    }

    @Override
    public void onLoadingCancelled(String s, View view) {
        onLoadFailed();
    }

    public abstract void onResourceReady(String url, Bitmap bitmap);

    public abstract void onLoadFailed();
}
