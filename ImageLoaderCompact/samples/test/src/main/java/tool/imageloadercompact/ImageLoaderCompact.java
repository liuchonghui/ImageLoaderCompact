package tool.imageloadercompact;

import android.content.Context;
import android.graphics.Bitmap;
import android.widget.ImageView;

import tool.imageloadercompact.fresco.FrescoManager;
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

    boolean initialize = false;

    @Override
    public void onStart() {
        if (useFresco) {
            FrescoManager.getInstance().onStart();
        }
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
        if (useFresco) {
            return FrescoManager.getInstance().isInitialized();
        }
        return initialize;
    }

    @Override
    public void clearDiskCaches() {
        if (useFresco) {
            FrescoManager.getInstance().clearDiskCaches();
        }
    }

    @Override
    public void displayImage(String url, ImageView imageView) {
        if (useFresco) {
            FrescoManager.getInstance().displayImage(url, imageView);
        }
    }

    @Override
    public Bitmap fetchBitmapByUrl(String url) {
        if (useFresco) {
            return FrescoManager.getInstance().fetchBitmapByUrl(url);
        }
        return null;
    }

    @Override
    public void asyncFetchBitmapByUrl(String url,
                                      OnFetchBitmapListener l) {
        if (useFresco) {
            FrescoManager.getInstance().asyncFetchBitmapByUrl(url, l);
        }
    }

    @Override
    public void onConnectionChanged(ConnectionType type) {
        if (useFresco) {
            FrescoManager.getInstance().onConnectionChanged(type);
        }
    }

    @Override
    public void onConnectionClosed() {
        if (useFresco) {
            FrescoManager.getInstance().onConnectionClosed();
        }
    }
}
