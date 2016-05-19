package com.android.imageloadercompact.uil;

import android.content.Context;
import android.graphics.Bitmap;
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
import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;

import java.io.File;
import java.math.BigDecimal;


public class UilManager implements CompactImpl {

    protected static UilManager instance;
    protected boolean initialized = false;
    PacketCollector packetCollector;
//    OkHttpClient okHttpClient;

    static {
        instance = new UilManager();
    }

    public static UilManager getInstance() {
        return instance;
    }

    public UilManager() {
        packetCollector = new PacketCollector();
    }

    public void onStart() {
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
                ImageLoaderCompact.getInstance().getApplicationContext())
                .memoryCacheExtraOptions(480, 800) // default = device screen dimensions
                .threadPoolSize(3) // default
                .threadPriority(Thread.NORM_PRIORITY - 1) // default
                .tasksProcessingOrder(QueueProcessingType.FIFO) // default
                .denyCacheImageMultipleSizesInMemory()
                .memoryCache(new LruMemoryCache(2 * 1024 * 1024))
                .memoryCacheSize(2 * 1024 * 1024)
                .memoryCacheSizePercentage(13) // default
                .diskCacheSize(50 * 1024 * 1024)
                .diskCacheFileCount(100)
                .defaultDisplayImageOptions(DisplayImageOptions.createSimple()) // default
                .writeDebugLogs()
                .build();
        ImageLoader.getInstance().init(config);
    }

    public void onLoad() {
    }

    public void onInitialize() {
    }

    public boolean isInitialized() {
        return ImageLoader.getInstance().isInited();
    }

    public Size getCacheSize() {
        Size size = new Size();
        File cacheDir = StorageUtils.getCacheDirectory(
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
                File cacheDir = StorageUtils.getCacheDirectory(
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
        ImageLoader.getInstance().loadImage(url, packet);
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
        SimpleTarget target = new SimpleTarget() {
            @Override
            public void onResourceReady(String url, Bitmap bitmap) {
                if (l != null) {
                    l.onFetchBitmapSuccess(url, bitmap);
                }
            }

            @Override
            public void onLoadFailed() {
                if (l != null) {
                    l.onFetchBitmapFailure(url);
                }
            }
        };
        ImageLoader.getInstance().loadImage(url, target);
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
                        imageView.invalidate();
                    }
                };
            }

            if (placeholderId > 0) {
                imageView.setBackgroundResource(placeholderId);
            }
            ImageLoader.getInstance().loadImage(url, target);
        }
    }

}
