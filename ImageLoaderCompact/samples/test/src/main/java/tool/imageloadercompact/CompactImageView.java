package tool.imageloadercompact;

import android.content.Context;
import android.content.res.TypedArray;
import android.net.Uri;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.generic.RoundingParams;
import com.facebook.drawee.view.SimpleDraweeView;

import tool.imageloadercompact.test.R;

public class CompactImageView extends SimpleDraweeView {

    public CompactImageView(Context context, GenericDraweeHierarchy hierarchy) {
        super(context, hierarchy);
        init(context, null);
    }

    public CompactImageView(Context context) {
        super(context);
        init(context, null);
    }

    public CompactImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public CompactImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    int placeholderId = 0;
    boolean roundAsCircle = false;
    int roundedCornerRadius = 0;

    private void init(Context context, AttributeSet attrs) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.GenericDraweeView);
        try {
            if (a.hasValue(R.styleable.GenericDraweeView_placeholderImage)) {
                placeholderId = a.getResourceId(
                        R.styleable.GenericDraweeView_placeholderImage,
                        placeholderId);
            }
            if (a.hasValue(R.styleable.GenericDraweeView_roundAsCircle)) {
                roundAsCircle = a.getBoolean(
                        R.styleable.GenericDraweeView_roundAsCircle,
                        roundAsCircle);
            }
            if (a.hasValue(R.styleable.GenericDraweeView_roundedCornerRadius)) {
                roundedCornerRadius = a.getDimensionPixelSize(
                        R.styleable.GenericDraweeView_roundedCornerRadius,
                        roundedCornerRadius);
            }
        } finally {
            a.recycle();
        }

    }

    protected ImageView getImageView() {
        return this;
    }

    public boolean isRoundAsCircle() {
        return roundAsCircle;
    }

    public int getPlaceholderId() {
        return this.placeholderId;
    }

    public int getRoundedCornerRadius() {
        return this.roundedCornerRadius;
    }

    public void setPlaceholderId(int placeholderId) {
        this.placeholderId = placeholderId;
        if (ImageLoaderCompact.useFresco) {
            GenericDraweeHierarchy hierarchy = getHierarchy();
            hierarchy.setPlaceholderImage(this.placeholderId);
        }
        invalidate();
    }

    public void roundAsCircle(boolean roundAsCircle) {
        this.roundAsCircle = roundAsCircle;
        if (ImageLoaderCompact.useFresco) {
            GenericDraweeHierarchy hierarchy = getHierarchy();
            RoundingParams rp = hierarchy.getRoundingParams();
            if (null == rp) {
                rp = new RoundingParams();
            }
            rp.setRoundAsCircle(this.roundAsCircle);
            hierarchy.setRoundingParams(rp);
        }
        invalidate();
    }

    public void roundedCornerRadius(int roundedCornerRadius) {
        this.roundedCornerRadius = roundedCornerRadius;
        if (ImageLoaderCompact.useFresco) {
            GenericDraweeHierarchy hierarchy = getHierarchy();
            RoundingParams rp = hierarchy.getRoundingParams();
            if (null == rp) {
                rp = new RoundingParams();
            }
            rp.setCornersRadius(this.roundedCornerRadius);
            hierarchy.setRoundingParams(rp);
        }
        invalidate();
    }

    @Override
    public void setImageURI(Uri uri) {
        if (ImageLoaderCompact.useFresco) {
            super.setImageURI(uri);
        } else {
            // 不行，Glide必须用当前Activity或Fragment的Context
//            if (roundAsCircle) {
//                Glide.with(getContext()).load(uri.getPath())
//                        .asBitmap().centerCrop().into(new BitmapImageViewTarget(getImageView()) {
//                    @Override
//                    protected void setResource(Bitmap resource) {
//                        RoundedBitmapDrawable circularBitmapDrawable =
//                                RoundedBitmapDrawableFactory.create(getContext().getResources(), resource);
//                        circularBitmapDrawable.setCircular(true);
//                        getImageView().setImageDrawable(circularBitmapDrawable);
//                    }
//                });
//
//            } else {
//                Glide.with(getContext())
//                        .load(uri.getEncodedPath())
//                        .placeholder(placeholderId).into(getImageView());
//            }
        }
    }


}
