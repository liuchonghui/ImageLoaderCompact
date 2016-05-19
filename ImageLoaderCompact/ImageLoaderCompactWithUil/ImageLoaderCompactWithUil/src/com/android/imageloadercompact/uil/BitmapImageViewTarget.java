package com.android.imageloadercompact.uil;

import android.graphics.Bitmap;
import android.widget.ImageView;

/**
 * @author liuchonghui
 */
public class BitmapImageViewTarget extends SimpleTarget {
    ImageView view;

    public BitmapImageViewTarget(ImageView view) {
        this.view = view;
    }

    protected void setResource(Bitmap resource) {
        view.setImageBitmap(resource);
    }

    @Override
    public void onResourceReady(String url, Bitmap bitmap) {
        if (bitmap != null && !bitmap.isRecycled()) {
            setResource(bitmap);
        }
    }

    @Override
    public void onLoadFailed() {
    }
}
