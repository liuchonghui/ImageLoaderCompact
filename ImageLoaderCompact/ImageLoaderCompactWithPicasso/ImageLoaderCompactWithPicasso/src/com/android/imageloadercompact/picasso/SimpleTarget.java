package com.android.imageloadercompact.picasso;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;

import com.squareup.picasso.Picasso;

/**
 * @author liuchonghui
 */
public abstract class SimpleTarget implements com.squareup.picasso.Target {
    public abstract void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from);

    public abstract void onBitmapFailed(Drawable errorDrawable);

    public void onPrepareLoad(Drawable placeHolderDrawable) {}
}
