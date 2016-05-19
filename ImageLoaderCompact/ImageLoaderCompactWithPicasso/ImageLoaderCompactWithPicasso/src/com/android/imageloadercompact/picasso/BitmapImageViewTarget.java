package com.android.imageloadercompact.picasso;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

/**
 * @author liuchonghui
 */
public class BitmapImageViewTarget extends SimpleTarget {
    ImageView view;

    public BitmapImageViewTarget(ImageView view) {
        this.view = view;
    }

    @Override
    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
        if (bitmap != null && !bitmap.isRecycled()) {
            setResource(bitmap);
        }
    }

    @Override
    public void onBitmapFailed(Drawable errorDrawable) {

    }

    protected void setResource(Bitmap resource) {
        view.setImageBitmap(resource);
    }
}
