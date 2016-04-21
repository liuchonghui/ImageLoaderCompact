package tool.imageloadercompact.glide;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;

import com.android.overlay.utils.LogUtils;
import com.bumptech.glide.BitmapRequestBuilder;
import com.bumptech.glide.DrawableTypeRequest;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.facebook.imagepipeline.core.ImagePipelineConfig;

import tool.imageloadercompact.CompactImageView;
import tool.imageloadercompact.ConnectionType;
import tool.imageloadercompact.OnFetchBitmapListener;
import tool.imageloadercompact.fresco.PacketCollector;

public class GlideManager {

    protected static GlideManager instance;
    protected boolean initialized = false;
    protected ImagePipelineConfig config;
    PacketCollector packetCollector;
//    OkHttpClient okHttpClient;

    static {
        instance = new GlideManager();
//        RunningEnvironment.getInstance().addManager(instance);
    }

    public static GlideManager getInstance() {
        return instance;
    }

    public GlideManager() {
    }

    public void onStart() {
    }

    public void onLoad() {
    }

    public boolean isInitialized() {
        return this.initialized;
    }


    public void clearDiskCaches() {
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
        LogUtils.d("FM", "fetchBitmapByUrl[" + url + "]");
        Bitmap bitmap = null;
//        if (url == null || url.length() == 0) {
//            return null;
//        }
//        ImageRequest imageRequest = ImageRequestBuilder
//                .newBuilderWithSource(Uri.parse(url))
//                .setProgressiveRenderingEnabled(true).build();
//        ImagePipeline imagePipeline = Fresco.getImagePipeline();
//        DataSource<CloseableReference<CloseableImage>> dataSource = imagePipeline
//                .fetchDecodedImage(imageRequest, RunningEnvironment
//                        .getInstance().getApplicationContext());
//        Packet packet = new Packet(packetCollector, url);
//        dataSource.subscribe(packet, CallerThreadExecutor.getInstance());
//        Packet newPacket = packetCollector.nextResult();
//        if (newPacket != null && url.equalsIgnoreCase(newPacket.getUrl())) {
//            bitmap = newPacket.getBitmap();
//        }
//        if (bitmap != null && bitmap.isRecycled()) {
//            bitmap = Bitmap.createBitmap(bitmap);
//        }
        return bitmap;
    }

    public void asyncFetchBitmapByUrl(final String url,
                                      final OnFetchBitmapListener l) {
//        ImageRequest imageRequest = ImageRequestBuilder
//                .newBuilderWithSource(Uri.parse(url))
//                .setProgressiveRenderingEnabled(true).build();
//        ImagePipeline imagePipeline = Fresco.getImagePipeline();
//        DataSource<CloseableReference<CloseableImage>> dataSource = imagePipeline
//                .fetchDecodedImage(imageRequest, RunningEnvironment
//                        .getInstance().getApplicationContext());
//        dataSource.subscribe(new BaseBitmapDataSubscriber() {
//            @Override
//            public void onNewResultImpl(@Nullable Bitmap bitmap) {
//                // You can use the bitmap in only limited ways
//                // No need to do any cleanup.
//                if (bitmap != null && !bitmap.isRecycled()) {
//                    if (l != null) {
//                        l.onFetchBitmapSuccess(url, bitmap);
//                    }
//                }
//            }
//
//            @Override
//            public void onFailureImpl(DataSource dataSource) {
//                // No cleanup required here.
//                if (l != null) {
//                    l.onFetchBitmapFailure(url);
//                }
//            }
//        }, UiThreadExecutorService.getInstance());
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
                            circularBitmapDrawable.setCircular(true);
                        } else {
                            circularBitmapDrawable.setCornerRadius(roundedCornerRadius);
                        }
                        imageView.setImageDrawable(circularBitmapDrawable);
                    }
                };
            }

            DrawableTypeRequest<String> request = Glide.with(ctx).load(url);
            BitmapRequestBuilder builder = request.asBitmap().centerCrop();
            if (null == target) {
                builder.placeholder(placeholderId).into(imageView);
            } else {
                builder.placeholder(placeholderId).into(target);
            }
        }
    }
}
