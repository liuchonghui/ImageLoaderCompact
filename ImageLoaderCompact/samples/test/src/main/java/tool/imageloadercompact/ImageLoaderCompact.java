package tool.imageloadercompact;

import android.content.Context;
import android.graphics.Bitmap;

import tool.imageloadercompact.fresco.FrescoManager;
import tool.imageloadercompact.glide.GlideManager;
import tool.imageloadercompact.test.BuildConfig;


public class ImageLoaderCompact implements CompactImpl {

    private volatile static ImageLoaderCompact instance;
    public static boolean useFresco;

    static {
        if (BuildConfig.useFresco) {
            ImageLoaderCompact.useFresco = true;
        } else {
            ImageLoaderCompact.useFresco = false;
        }
    }

    private ImageLoaderCompact() {
        super();
        if (BuildConfig.useFresco) {
            FrescoManager.getInstance();
        } else {
            GlideManager.getInstance();
        }
    }

    public static ImageLoaderCompact getInstance() {
        if (null == instance) {
            synchronized (ImageLoaderCompact.class) {
                if (null == instance) {
                    instance = new ImageLoaderCompact();
                }
            }
        }
        return instance;
    }

    public int dp2px(Context ctx, float dipValue) {
        final float scale = ctx.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

    boolean initialize = false;

    @Override
    public void onStart() {
//        if (useFresco) {
            FrescoManager.getInstance().onStart();
//        }
    }

    @Override
    public void onLoad() {
        if (useFresco) {
            FrescoManager.getInstance().onLoad();
        }
    }

    @Override
    public void onInitialize() {
        if (useFresco) {
            FrescoManager.getInstance().onInitialized();
        }
    }

    @Override
    public boolean isInitialized() {
//        if (useFresco) {
            return FrescoManager.getInstance().isInitialized();
//        }
//        return initialize;
    }

    @Override
    public void clearDiskCaches(final OnDiskCachesClearListener l) {
        if (useFresco) {
            FrescoManager.getInstance().clearDiskCaches(l);
        } else {
            GlideManager.getInstance().clearDiskCaches(l);
        }
    }

    @Override
    public Size getCacheSize() {
        if (useFresco) {
            return FrescoManager.getInstance().getCacheSize();
        } else {
            return GlideManager.getInstance().getCacheSize();
        }
    }

    @Override
    public void displayImage(Context ctx, String url, CompactImageView imageView) {
        if (useFresco) {
            FrescoManager.getInstance().displayImage(ctx, url, imageView);
        } else {
            GlideManager.getInstance().displayImage(ctx, url, imageView);
        }
    }

    @Override
    public Bitmap fetchBitmapByUrl(String url) {
        if (useFresco) {
            return FrescoManager.getInstance().fetchBitmapByUrl(url);
        } else {
            return GlideManager.getInstance().fetchBitmapByUrl(url);
        }
    }

    @Override
    public void asyncFetchBitmapByUrl(String url,
                                      OnFetchBitmapListener l) {
        if (useFresco) {
            FrescoManager.getInstance().asyncFetchBitmapByUrl(url, l);
        } else {
            GlideManager.getInstance().asyncFetchBitmapByUrl(url, l);
        }
    }

    @Override
    public void onConnectionChanged(ConnectionType type) {
        if (useFresco) {
            FrescoManager.getInstance().onConnectionChanged(type);
        } else {
            GlideManager.getInstance().onConnectionChanged(type);
        }
    }

    @Override
    public void onConnectionClosed() {
        if (useFresco) {
            FrescoManager.getInstance().onConnectionClosed();
        } else {
            GlideManager.getInstance().onConnectionClosed();
        }
    }
}
