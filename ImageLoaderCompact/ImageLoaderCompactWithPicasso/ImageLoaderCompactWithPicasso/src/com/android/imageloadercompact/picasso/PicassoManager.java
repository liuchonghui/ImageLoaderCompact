package com.android.imageloadercompact.picasso;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.view.ViewGroup;

import com.android.imageloadercompact.CompactImageView;
import com.android.imageloadercompact.CompactImpl;
import com.android.imageloadercompact.ConnectionType;
import com.android.imageloadercompact.ImageLoaderCompact;
import com.android.imageloadercompact.OnDiskCachesClearListener;
import com.android.imageloadercompact.OnFetchBitmapListener;
import com.android.imageloadercompact.Size;
import com.android.imageloadercompact.StorageUtils;
import com.android.imageloadercompact.Utils;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;

import java.io.File;
import java.math.BigDecimal;


public class PicassoManager implements CompactImpl {

    protected static PicassoManager instance;
    protected boolean initialized = false;
    PacketCollector packetCollector;

    static {
        instance = new PicassoManager();
    }

    public static PicassoManager getInstance() {
        return instance;
    }

    public PicassoManager() {
        packetCollector = new PacketCollector();
    }

    public void onStart() {
        initialized = true;
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
        File cacheDir = createPicassoDefaultCacheDir(
                ImageLoaderCompact.getInstance().getApplicationContext());
        if (cacheDir.isDirectory()) {
            size = Utils.getDirSize(cacheDir);
        }
        return size;
    }

    File createPicassoDefaultCacheDir(Context context) {
        File cache = new File(context.getApplicationContext().getCacheDir(), "picasso-cache");
        if (!cache.exists()) {
            //noinspection ResultOfMethodCallIgnored
            cache.mkdirs();
        }
        return cache;
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
                File cacheDir = createPicassoDefaultCacheDir(
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
        Picasso.with(ImageLoaderCompact.getInstance().getApplicationContext())
                .load(url).into(packet);
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
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                if (l != null) {
                    l.onFetchBitmapSuccess(url, bitmap);
                }
            }

            @Override
            public void onBitmapFailed(Drawable errorDrawable) {
                if (l != null) {
                    l.onFetchBitmapFailure(url);
                }
            }
        };
        Picasso.with(ImageLoaderCompact.getInstance().getApplicationContext())
                .load(url).noPlaceholder().into(target);
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
                        imageView.setTag(android.R.id.custom, null);
                    }
                };
            }

            RequestCreator builder = Picasso.with(ctx).load(url);
            if (placeholderId > 0) {
                builder = builder.placeholder(placeholderId);
            } else {
                builder = builder.noPlaceholder();
            }
            ViewGroup.LayoutParams lp = imageView.getLayoutParams();
            if (lp.width > 0 && lp.height > 0) {
                builder = builder.resize(lp.width, lp.height).centerCrop();
            }
            if (null == target) {
                builder.into(imageView);
            } else {
                imageView.setTag(android.R.id.custom, target);
                builder.into(target);
            }
        }
    }
}