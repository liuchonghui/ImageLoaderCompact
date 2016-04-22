package tool.imageloadercompact;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.AttributeSet;

import tool.imageloadercompact.photodraweeview.PhotoDraweeView;
import tool.imageloadercompact.photoview.IPhotoView;
import tool.imageloadercompact.photoview.PhotoViewAttacher;

public class CompactPhotoView extends PhotoDraweeView implements IPhotoView {

    private PhotoViewAttacher mAttacher;

    private ScaleType mPendingScaleType;

    public CompactPhotoView(Context context) {
        super(context);
    }

    public CompactPhotoView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public CompactPhotoView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        if (ImageLoaderCompact.useFresco) {
            return;
        }
        super.setScaleType(ScaleType.MATRIX);
        mAttacher = new PhotoViewAttacher(this);

        if (null != mPendingScaleType) {
            setScaleType(mPendingScaleType);
            mPendingScaleType = null;
        }
    }

    @Override
    public boolean canZoom() {
        if (null != mAttacher) {
            return mAttacher.canZoom();
        }
        return false;
    }

    @Override
    public RectF getDisplayRect() {
        if (null != mAttacher) {
            return mAttacher.getDisplayRect();
        }
        return null;
    }

    @Override
    public float getMinScale() {
        if (null != mAttacher) {
            return mAttacher.getMinScale();
        }
        return 0;
    }

    @Override
    public float getMidScale() {
        if (null != mAttacher) {
            return mAttacher.getMidScale();
        }
        return 0;
    }

    @Override
    public float getMaxScale() {
        if (null != mAttacher) {
            return mAttacher.getMaxScale();
        }
        return 0;
    }

    @Override
    public float getScale() {
        if (null != mAttacher) {
            return mAttacher.getScale();
        }
        return 0;
    }

    @Override
    public ScaleType getScaleType() {
        if (null != mAttacher) {
            return mAttacher.getScaleType();
        }
        return ScaleType.CENTER_CROP;
    }

    @Override
    public void setAllowParentInterceptOnEdge(boolean allow) {
        if (null != mAttacher) {
            mAttacher.setAllowParentInterceptOnEdge(allow);
        }
    }

    @Override
    public void setMinScale(float minScale) {
        if (null != mAttacher) {
            mAttacher.setMinScale(minScale);
        }
    }

    @Override
    public void setMidScale(float midScale) {
        if (null != mAttacher) {
            mAttacher.setMidScale(midScale);
        }
    }

    @Override
    public void setMaxScale(float maxScale) {
        if (null != mAttacher) {
            mAttacher.setMaxScale(maxScale);
        }
    }

    @Override
    // setImageBitmap calls through to this method
    public void setImageDrawable(Drawable drawable) {
        super.setImageDrawable(drawable);
        if (ImageLoaderCompact.useFresco) {
            return;
        }
        if (null != mAttacher) {
            mAttacher.update();
        }
    }

    @Override
    public void setImageResource(int resId) {
        super.setImageResource(resId);
        if (ImageLoaderCompact.useFresco) {
            return;
        }
        if (null != mAttacher) {
            mAttacher.update();
        }
    }

    @Override
    public void setImageURI(Uri uri) {
        super.setImageURI(uri);
        if (ImageLoaderCompact.useFresco) {
            return;
        }
        if (null != mAttacher) {
            mAttacher.update();
        }
    }

    @Override
    public void setOnMatrixChangeListener(PhotoViewAttacher.OnMatrixChangedListener listener) {
        if (null != mAttacher) {
            mAttacher.setOnMatrixChangeListener(listener);
        }
    }

    @Override
    public void setOnLongClickListener(OnLongClickListener l) {
        if (null != mAttacher) {
            mAttacher.setOnLongClickListener(l);
        }
    }

    @Override
    public void setOnPhotoTapListener(PhotoViewAttacher.OnPhotoTapListener listener) {
        if (null != mAttacher) {
            mAttacher.setOnPhotoTapListener(listener);
        }
    }

    @Override
    public void setOnViewTapListener(PhotoViewAttacher.OnViewTapListener listener) {
        if (null != mAttacher) {
            mAttacher.setOnViewTapListener(listener);
        }
    }

    @Override
    public void setScaleType(ScaleType scaleType) {
        if (null != mAttacher) {
            mAttacher.setScaleType(scaleType);
        } else {
            mPendingScaleType = scaleType;
        }
    }

    @Override
    public void setZoomable(boolean zoomable) {
        if (null != mAttacher) {
            mAttacher.setZoomable(zoomable);
        }
    }

    @Override
    public void zoomTo(float scale, float focalX, float focalY) {
        if (null != mAttacher) {
            mAttacher.zoomTo(scale, focalX, focalY);
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        if (!ImageLoaderCompact.useFresco) {
            if (null != mAttacher) {
                mAttacher.cleanup();
            }
        }
        super.onDetachedFromWindow();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        try {
            super.onDraw(canvas);
        } catch (Exception e) {

        }
    }
}
