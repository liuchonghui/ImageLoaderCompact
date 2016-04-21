package tool.imageloadercompact;

import android.content.Context;
import android.content.res.TypedArray;
import android.net.Uri;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.facebook.drawee.view.SimpleDraweeView;

import tool.imageloadercompact.test.R;

public class CompactImageView extends SimpleDraweeView {

    public CompactImageView(Context context) {
        super(context);
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
//        if (ImageLoaderCompact.useFresco) {
//            return;
//        }
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

    @Override
    public void setImageURI(Uri uri) {
        if (ImageLoaderCompact.useFresco) {
            super.setImageURI(uri);
        } else {
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
                Glide.with(getContext())
                        .load(uri.getEncodedPath())
                        .placeholder(placeholderId).into(getImageView());
//            }
        }
    }
}
