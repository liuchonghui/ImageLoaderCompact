package tool.imageloadercompact;

import android.content.Context;
import android.graphics.Bitmap;
import android.widget.ImageView;

public interface CompactImpl {

    void onConnectionChanged(ConnectionType type);

    void onConnectionClosed();

    void onStart();

    void onLoad();

    void onInitialize();

    boolean isInitialized();

    void clearDiskCaches();

    void displayImage(String url, ImageView imageView);

    Bitmap fetchBitmapByUrl(String url);

    void asyncFetchBitmapByUrl(String url, OnFetchBitmapListener l);
}
