package com.android.imageloadercompact.uil;

import android.graphics.Bitmap;

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
    public void onResourceReady(String url, Bitmap bitmap) {
        if (bitmap != null && !bitmap.isRecycled()) {
            result = bitmap;
            pc.processPacket(this);
        } else {
            pc.processPacket(new Packet());
        }
    }

    @Override
    public void onLoadFailed() {
        pc.processPacket(new Packet());
    }
}
