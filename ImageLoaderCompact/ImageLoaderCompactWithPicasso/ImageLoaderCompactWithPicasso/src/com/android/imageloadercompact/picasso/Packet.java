package com.android.imageloadercompact.picasso;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;

import com.squareup.picasso.Picasso;

/**
 * Packet with Bitmap.
 *
 * @author liu_chonghui
 */
public class Packet extends SimpleTarget {
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
    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
        if (bitmap != null && !bitmap.isRecycled()) {
            result = bitmap;
            pc.processPacket(this);
        } else {
            pc.processPacket(new Packet());
        }
    }

    @Override
    public void onBitmapFailed(Drawable errorDrawable) {
        pc.processPacket(new Packet());
    }

}
