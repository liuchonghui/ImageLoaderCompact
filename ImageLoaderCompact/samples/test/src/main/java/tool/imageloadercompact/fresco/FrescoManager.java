package tool.imageloadercompact.fresco;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;

import com.android.overlay.RunningEnvironment;
import com.android.overlay.utils.LogUtils;
import com.facebook.cache.disk.DiskCacheConfig;
import com.facebook.common.executors.CallerThreadExecutor;
import com.facebook.common.executors.UiThreadExecutorService;
import com.facebook.common.internal.Supplier;
import com.facebook.common.references.CloseableReference;
import com.facebook.common.util.ByteConstants;
import com.facebook.datasource.DataSource;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.backends.pipeline.PipelineDraweeControllerBuilder;
import com.facebook.imagepipeline.cache.MemoryCacheParams;
import com.facebook.imagepipeline.core.ImagePipeline;
import com.facebook.imagepipeline.core.ImagePipelineConfig;
import com.facebook.imagepipeline.datasource.BaseBitmapDataSubscriber;
import com.facebook.imagepipeline.image.CloseableImage;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;

import java.io.File;
import java.math.BigDecimal;

import tool.imageloadercompact.CompactImageView;
import tool.imageloadercompact.ConnectionType;
import tool.imageloadercompact.OnDiskCachesClearListener;
import tool.imageloadercompact.OnFetchBitmapListener;
import tool.imageloadercompact.Size;
import tool.imageloadercompact.StorageUtils;
import tool.imageloadercompact.Utils;

public class FrescoManager {

    protected static FrescoManager instance;
    protected boolean initialized = false;
    protected ImagePipelineConfig config;
    PacketCollector packetCollector;
//    OkHttpClient okHttpClient;

    static {
        instance = new FrescoManager();
//        RunningEnvironment.getInstance().addManager(instance);
    }

    public static FrescoManager getInstance() {
        return instance;
    }

    public FrescoManager() {
        packetCollector = new PacketCollector();
//        okHttpClient = new OkHttpClient();
    }

    public void onStart() {
        Fresco.initialize(RunningEnvironment.getInstance().getApplicationContext(),
                getDefaultImagePipelineConfig(RunningEnvironment.getInstance().getApplicationContext()));
    }

    public void onLoad() {
    }

    public boolean isInitialized() {
        return null != Fresco.getDraweeControllerBuilderSupplier();
    }

    public ImagePipelineConfig getImagePipelineConfig() {
        if (config == null) {
            config = getDefaultImagePipelineConfig(RunningEnvironment
                    .getInstance().getApplicationContext());
        }
        return config;
    }

    public File getImagePipeLine() {
        DiskCacheConfig diskCacheConfig = FrescoManager.getInstance()
                .getImagePipelineConfig().getMainDiskCacheConfig();
        Supplier<File> supply = diskCacheConfig.getBaseDirectoryPathSupplier();
        File imagepipe = supply.get();
        return new File(imagepipe, diskCacheConfig.getBaseDirectoryName());
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
            Fresco.getImagePipeline().clearDiskCaches();
            try {
                File file = StorageUtils
                        .getIndividualCacheDirectory(
                                RunningEnvironment.getInstance().getApplicationContext());
                Utils.delAllFile(file.getPath());
            } catch (Exception e) {
                e.printStackTrace();
            }

            Size size = getCacheSize(RunningEnvironment.getInstance().getApplicationContext());
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

    public PipelineDraweeControllerBuilder newDraweeControllerBuilder() {
        return Fresco.newDraweeControllerBuilder();
    }

    //    @SuppressWarnings("rawtypes")
//    protected NetworkFetcher getConfigNetworkFetcher() {
//        if (okHttpClient == null) {
//            okHttpClient = new OkHttpClient();
//        }
//        WifiStateNetworkFetcher fetcher = new WifiStateNetworkFetcher(okHttpClient) {
//            @Override
//            public void fetch(OkHttpNetworkFetchState fetchState, NetworkFetcher.Callback callback) {
//                if (!forbidden) {
//                    super.fetch(fetchState, callback);
//                } else {
//                    LogUtils.d("FM", "WifiStateNetworkFetcher[FORBIDDEN]no-fetch");
//                    callback.onCancellation();
//                }
//            }
//        };
//        return fetcher;
//    }
//
//    class WifiStateNetworkFetcher extends OkHttpNetworkFetcher {
//
//        public WifiStateNetworkFetcher(OkHttpClient okHttpClient) {
//            super(okHttpClient);
//        }
//
//    }
//
//    boolean forbidden = false;
//
    public void onConnectionChanged(ConnectionType type) {
        if (!initialized) {
            return;
        }
//        if (CommonUtils.isWifiDownload(RunningEnvironment.getInstance()
//                .getApplicationContext())) {
//            // 允许下载
//            forbidden = false;
//        } else {
//            // 禁止下载
//            forbidden = true;
//        }
    }

    public void onConnectionClosed() {
    }

    public void onInitialized() {
        initialized = true;
    }

    public synchronized Bitmap fetchBitmapByUrl(String url) {
        LogUtils.d("FM", "fetchBitmapByUrl[" + url + "]");
        Bitmap bitmap = null;
        if (url == null || url.length() == 0) {
            return null;
        }
        ImageRequest imageRequest = ImageRequestBuilder
                .newBuilderWithSource(Uri.parse(url))
                .setProgressiveRenderingEnabled(true).build();
        ImagePipeline imagePipeline = Fresco.getImagePipeline();
        DataSource<CloseableReference<CloseableImage>> dataSource = imagePipeline
                .fetchDecodedImage(imageRequest, RunningEnvironment
                        .getInstance().getApplicationContext());
        Packet packet = new Packet(packetCollector, url);
        dataSource.subscribe(packet, CallerThreadExecutor.getInstance());
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
        ImageRequest imageRequest = ImageRequestBuilder
                .newBuilderWithSource(Uri.parse(url))
                .setProgressiveRenderingEnabled(true).build();
        ImagePipeline imagePipeline = Fresco.getImagePipeline();
        DataSource<CloseableReference<CloseableImage>> dataSource = imagePipeline
                .fetchDecodedImage(imageRequest, RunningEnvironment
                        .getInstance().getApplicationContext());
        dataSource.subscribe(new BaseBitmapDataSubscriber() {
            @Override
            public void onNewResultImpl(@Nullable Bitmap bitmap) {
                // You can use the bitmap in only limited ways
                // No need to do any cleanup.
                if (bitmap != null && !bitmap.isRecycled()) {
                    if (l != null) {
                        l.onFetchBitmapSuccess(url, bitmap);
                    }
                }
            }

            @Override
            public void onFailureImpl(DataSource dataSource) {
                // No cleanup required here.
                if (l != null) {
                    l.onFetchBitmapFailure(url);
                }
            }
        }, UiThreadExecutorService.getInstance());
    }

    // 分配的可用内存
    private static final int MAX_HEAP_SIZE = (int) Runtime.getRuntime()
            .maxMemory();
    // 使用的缓存数量
    private static final int MAX_MEMORY_CACHE_SIZE = MAX_HEAP_SIZE / 4;
    // 小图极低磁盘空间缓存的最大值（特性：可将大量的小图放到额外放在另一个磁盘空间防止大图占用磁盘空间而删除了大量的小图）
    private static final int MAX_SMALL_DISK_VERYLOW_CACHE_SIZE = 10 * ByteConstants.MB;
    // 小图低磁盘空间缓存的最大值（特性：可将大量的小图放到额外放在另一个磁盘空间防止大图占用磁盘空间而删除了大量的小图）
    private static final int MAX_SMALL_DISK_LOW_CACHE_SIZE = 20 * ByteConstants.MB;
    // 小图磁盘缓存的最大值（特性：可将大量的小图放到额外放在另一个磁盘空间防止大图占用磁盘空间而删除了大量的小图）
    private static final int MAX_SMALL_DISK_CACHE_SIZE = 40 * ByteConstants.MB;
    // 默认图极低磁盘空间缓存的最大值
    private static final int MAX_DISK_CACHE_VERYLOW_SIZE = 20 * ByteConstants.MB;
    // 默认图低磁盘空间缓存的最大值
    private static final int MAX_DISK_CACHE_LOW_SIZE = 60 * ByteConstants.MB;
    // 默认图磁盘缓存的最大值
    private static final int MAX_DISK_CACHE_SIZE = 100 * ByteConstants.MB;
    // 小图所放路径的文件夹名
    private static final String IMAGE_PIPELINE_SMALL_CACHE_DIR = "imagepipeline_cache";
    // 默认图所放路径的文件夹名
    private static final String IMAGE_PIPELINE_CACHE_DIR = "imagepipeline_cache";

    public ImagePipelineConfig getDefaultImagePipelineConfig(Context context) {
        // 内存配置
        final MemoryCacheParams bitmapCacheParams = new MemoryCacheParams(
                MAX_MEMORY_CACHE_SIZE, // 内存缓存中总图片的最大大小,以字节为单位。
                Integer.MAX_VALUE, // 内存缓存中图片的最大数量。
                MAX_MEMORY_CACHE_SIZE, // 内存缓存中准备清除但尚未被删除的总图片的最大大小,以字节为单位。
                Integer.MAX_VALUE, // 内存缓存中准备清除的总图片的最大数量。
                MAX_HEAP_SIZE / 2); // 内存缓存中单个图片的最大大小。

        // 修改内存图片缓存数量，空间策略（这个方式有点恶心）
        Supplier<MemoryCacheParams> mSupplierMemoryCacheParams = new Supplier<MemoryCacheParams>() {
            @Override
            public MemoryCacheParams get() {
                return bitmapCacheParams;
            }
        };

        // 小图片的磁盘配置
        DiskCacheConfig diskSmallCacheConfig = DiskCacheConfig
                .newBuilder()
                .setBaseDirectoryPath(
                        context.getApplicationContext().getCacheDir())// 缓存图片基路径
                .setBaseDirectoryName(IMAGE_PIPELINE_SMALL_CACHE_DIR)// 文件夹名
                        // .setCacheErrorLogger(cacheErrorLogger)//日志记录器用于日志错误的缓存。
                        // .setCacheEventListener(cacheEventListener)//缓存事件侦听器。
                        // .setDiskTrimmableRegistry(diskTrimmableRegistry)//类将包含一个注册表的缓存减少磁盘空间的环境。
                .setMaxCacheSize(MAX_DISK_CACHE_SIZE)// 默认缓存的最大大小。
                .setMaxCacheSizeOnLowDiskSpace(MAX_SMALL_DISK_LOW_CACHE_SIZE)// 缓存的最大大小,使用设备时低磁盘空间。
                .setMaxCacheSizeOnVeryLowDiskSpace(
                        MAX_SMALL_DISK_VERYLOW_CACHE_SIZE)// 缓存的最大大小,当设备极低磁盘空间
                        // .setVersion(version)
                .build();

        // 默认图片的磁盘配置
        DiskCacheConfig diskCacheConfig = DiskCacheConfig
                .newBuilder()
                .setBaseDirectoryPath(
                        StorageUtils.getCacheDirectory(context))// 缓存图片基路径
                .setBaseDirectoryName(IMAGE_PIPELINE_CACHE_DIR)// 文件夹名
                        // .setCacheErrorLogger(cacheErrorLogger)//日志记录器用于日志错误的缓存。
                        // .setCacheEventListener(cacheEventListener)//缓存事件侦听器。
                        // .setDiskTrimmableRegistry(diskTrimmableRegistry)//类将包含一个注册表的缓存减少磁盘空间的环境。
                .setMaxCacheSize(MAX_DISK_CACHE_SIZE)// 默认缓存的最大大小。
                .setMaxCacheSizeOnLowDiskSpace(MAX_DISK_CACHE_LOW_SIZE)// 缓存的最大大小,使用设备时低磁盘空间。
                .setMaxCacheSizeOnVeryLowDiskSpace(MAX_DISK_CACHE_VERYLOW_SIZE)// 缓存的最大大小,当设备极低磁盘空间
                        // .setVersion(version)
                .build();

        // 缓存图片配置
        ImagePipelineConfig.Builder configBuilder = ImagePipelineConfig
                .newBuilder(context)
                        // .setAnimatedImageFactory(AnimatedImageFactory
                        // animatedImageFactory)//图片加载动画
                .setBitmapMemoryCacheParamsSupplier(mSupplierMemoryCacheParams)// 内存缓存配置（一级缓存，已解码的图片）
                        // .setCacheKeyFactory(cacheKeyFactory)//缓存Key工厂
                        // .setEncodedMemoryCacheParamsSupplier(encodedCacheParamsSupplier)//内存缓存和未解码的内存缓存的配置（二级缓存）
                        // .setExecutorSupplier(executorSupplier)//线程池配置
                        // .setImageCacheStatsTracker(imageCacheStatsTracker)//统计缓存的命中率
                        // .setImageDecoder(ImageDecoder imageDecoder) //图片解码器配置
                        // .setIsPrefetchEnabledSupplier(Supplier<Boolean>
                        // isPrefetchEnabledSupplier)//图片预览（缩略图，预加载图等）预加载到文件缓存
                        // .setSmallImageDiskCacheConfig(diskSmallCacheConfig)//磁盘缓存配置（小图片，可选～三级缓存的小图优化缓存）
                        // .setMemoryTrimmableRegistry(memoryTrimmableRegistry)
                        // //内存用量的缩减,有时我们可能会想缩小内存用量。比如应用中有其他数据需要占用内存，不得不把图片缓存清除或者减小
                        // 或者我们想检查看看手机是否已经内存不够了。
                        // .setNetworkFetcher(getConfigNetworkFetcher())//自定的网络层配置：如OkHttp，Volley
                        // .setPoolFactory(poolFactory)//线程池工厂配置
                        // .setProgressiveJpegConfig(progressiveJpegConfig)//渐进式JPEG图
                        // .setRequestListeners(requestListeners)//图片请求监听
                        // .setResizeAndRotateEnabledForNetwork(boolean
                        // resizeAndRotateEnabledForNetwork)//调整和旋转是否支持网络图片
                .setMainDiskCacheConfig(diskCacheConfig)
                .setDownsampleEnabled(true);//配合resize方法使用
        return configBuilder.build();
    }

    public void displayImage(Context ctx, String url, CompactImageView imageView) {
        if (imageView != null && url != null && url.length() > 0) {
            imageView.setImageURI(Uri.parse(url));
        }
    }

    public Size getCacheSize(Context ctx) {
        Size result = new Size();
        File file = StorageUtils.getIndividualCacheDirectory(ctx);
        Size individualCacheSize = Utils.getDirSize(file);
        File file2 = getImagePipeLine();
        Size imagePipelineSize = Utils.getDirSize(file2);
        result.setValue(individualCacheSize.getValue() + imagePipelineSize.getValue());
        return result;
    }
}
