package com.android.imageloadercompact.glide;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;

import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;

/**
 * Packet with Bitmap.
 *
 * @author liu_chonghui
 */
public class Packet extends SimpleTarget<Bitmap> {
    PacketCollector pc;
    String url;
    Bitmap result;

    private Packet() {
    }

    public Packet(PacketCollector pc, String url) {
        this.pc = pc;
        this.url = url;
    }

    public Bitmap getBitmap() {
        return result;
    }

    public String getUrl() {
        return url;
    }

    @Override
    public void onResourceReady(Bitmap bitmap,
                                GlideAnimation<? super Bitmap> glideAnimation) {
        if (bitmap != null && !bitmap.isRecycled()) {
            result = bitmap;
            pc.processPacket(this);
        } else {
            pc.processPacket(new Packet());
        }
    }

    @Override
    public void onLoadFailed(Exception e, Drawable errorDrawable) {
        pc.processPacket(new Packet());
    }
}
