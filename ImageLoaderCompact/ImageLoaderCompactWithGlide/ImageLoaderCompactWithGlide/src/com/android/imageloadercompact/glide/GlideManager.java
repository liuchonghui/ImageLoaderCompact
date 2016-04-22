package com.android.imageloadercompact.glide;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;

import com.android.imageloadercompact.CompactImageView;
import com.android.imageloadercompact.CompactImpl;
import com.android.imageloadercompact.ConnectionType;
import com.android.imageloadercompact.ImageLoaderCompact;
import com.android.imageloadercompact.OnDiskCachesClearListener;
import com.android.imageloadercompact.OnFetchBitmapListener;
import com.android.imageloadercompact.Size;
import com.android.imageloadercompact.StorageUtils;
import com.android.imageloadercompact.Utils;
import com.bumptech.glide.BitmapRequestBuilder;
import com.bumptech.glide.BitmapTypeRequest;
import com.bumptech.glide.DrawableTypeRequest;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.GlideBitmapDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.Target;

import java.io.File;
import java.math.BigDecimal;


public class GlideManager implements CompactImpl {

    protected static GlideManager instance;
    protected boolean initialized = false;
    PacketCollector packetCollector;
//    OkHttpClient okHttpClient;

    static {
        instance = new GlideManager();
    }

    public static GlideManager getInstance() {
        return instance;
    }

    public GlideManager() {
        packetCollector = new PacketCollector();
    }

    public void onStart() {
    }

    public void onLoad() {
    }

    public void onInitialize() {
    }

    public boolean isInitialized() {
        return this.initialized;
    }

    public Size getCacheSize() {
        Size size = new Size();
        File cacheDir = Glide.getPhotoCacheDir(
                ImageLoaderCompact.getInstance().getApplicationContext());
        if (cacheDir.isDirectory()) {
            size = Utils.getDirSize(cacheDir);
        }
        return size;
    }

    public void clearDiskCaches(OnDiskCachesClearListener l) {
        new Thread(new DiskCacheClearRunnable(l)).start();
    }

    class DiskCacheClearRunnable implements Runnable {
        OnDiskCachesClearListener l;
        public DiskCacheClearRunnable(OnDiskCachesClearListener l) {
            this.l = l;
        }

        void doClean() {
            try {
                File cacheDir = Glide.getPhotoCacheDir(
                        ImageLoaderCompact.getInstance().getApplicationContext());
                Utils.delAllFile(cacheDir.getPath());
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                File file = StorageUtils
                        .getIndividualCacheDirectory(
                                ImageLoaderCompact.getInstance().getApplicationContext());
                Utils.delAllFile(file.getPath());
            } catch (Exception e) {
                e.printStackTrace();
            }

            Size size = getCacheSize();
            BigDecimal bd = new BigDecimal(String.valueOf(size.getMSize()));
            bd = bd.setScale(1, BigDecimal.ROUND_DOWN);
            if (0 != bd.doubleValue()) {
                clearDiskCaches(l);
            } else {
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        if (l != null) {
                            l.onDiskCacheCleared();
                        }
                    }
                });
            }
        }

        @Override
        public void run() {
            doClean();
        }
    }

    public void onConnectionChanged(ConnectionType type) {
        if (!initialized) {
            return;
        }
    }

    public void onConnectionClosed() {
    }

    public void onInitialized() {
        initialized = true;
    }

    public synchronized Bitmap fetchBitmapByUrl(String url) {
        Bitmap bitmap = null;
        if (url == null || url.length() == 0) {
            return null;
        }
        Packet packet = new Packet(packetCollector, url);
        Glide.with(ImageLoaderCompact.getInstance().getApplicationContext())
                .load(url).into((Target) packet);
        Packet newPacket = packetCollector.nextResult();
        if (newPacket != null && url.equalsIgnoreCase(newPacket.getUrl())) {
            bitmap = newPacket.getBitmap();
        }
        if (bitmap != null && bitmap.isRecycled()) {
            bitmap = Bitmap.createBitmap(bitmap);
        }
        return bitmap;
    }

    public void asyncFetchBitmapByUrl(final String url,
                                      final OnFetchBitmapListener l) {
        SimpleTarget target = new SimpleTarget<GlideBitmapDrawable>() {
            @Override
            public void onResourceReady(GlideBitmapDrawable bitmapDrawable, GlideAnimation glideAnimation) {
                if (l != null) {
                    l.onFetchBitmapSuccess(url, bitmapDrawable.getBitmap());
                }
            }

            @Override
            public void onLoadFailed(Exception e, Drawable errorDrawable) {
                if (l != null) {
                    l.onFetchBitmapFailure(url);
                }
            }
        };
        Glide.with(ImageLoaderCompact.getInstance().getApplicationContext())
                .load(url).into(target);
    }

    public void displayImage(final Context ctx, final String url, final CompactImageView imageView) {
        if (imageView != null && url != null && url.length() > 0) {
            final boolean roundAsCircle = imageView.isRoundAsCircle();
            final int placeholderId = imageView.getPlaceholderId();
            final int roundedCornerRadius = imageView.getRoundedCornerRadius();
            BitmapImageViewTarget target = null;
            if (roundAsCircle || roundedCornerRadius > 0) {
                target = new BitmapImageViewTarget(imageView) {
                    @Override
                    protected void setResource(Bitmap resource) {
                        RoundedBitmapDrawable circularBitmapDrawable =
                                RoundedBitmapDrawableFactory.create(ctx.getResources(), resource);
                        if (roundAsCircle) {
//                            circularBitmapDrawable.setCircular(true);
                            circularBitmapDrawable.setCornerRadius(Integer.MAX_VALUE);
                        } else {
                            circularBitmapDrawable.setCornerRadius(roundedCornerRadius);
                        }
                        imageView.setImageDrawable(circularBitmapDrawable);
                    }
                };
            }

            DrawableTypeRequest<String> request = Glide.with(ctx).load(url);
            BitmapTypeRequest btr = request.asBitmap();
            BitmapRequestBuilder builder = btr.centerCrop();
            if (null == target) {
                builder.placeholder(placeholderId).into(imageView);
            } else {
                builder.placeholder(placeholderId).into(target);
            }
        }
    }

}
