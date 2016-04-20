package com.android.imageloadercompact.fresco;

import android.graphics.Bitmap;
import android.support.annotation.Nullable;

import com.facebook.datasource.DataSource;
import com.facebook.imagepipeline.datasource.BaseBitmapDataSubscriber;

/**
 * Packet with Bitmap.
 * @author liu_chonghui
 */
public class Packet extends BaseBitmapDataSubscriber {
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
	public void onNewResultImpl(@Nullable Bitmap bitmap) {
		if (bitmap != null && !bitmap.isRecycled()) {
			result = bitmap;
			pc.processPacket(this);
		} else {
			pc.processPacket(new Packet());
		}
	}

	@SuppressWarnings("rawtypes")
	@Override
	public void onFailureImpl(DataSource dataSource) {
		pc.processPacket(new Packet());
	}
}
