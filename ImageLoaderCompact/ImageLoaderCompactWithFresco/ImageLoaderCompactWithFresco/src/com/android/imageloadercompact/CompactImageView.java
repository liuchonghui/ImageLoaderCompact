package com.android.imageloadercompact;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.android.imageloadercompact.fresco.R;
import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.generic.RoundingParams;
import com.facebook.drawee.view.SimpleDraweeView;

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

    public CompactImageView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
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
        GenericDraweeHierarchy hierarchy = getHierarchy();
        hierarchy.setPlaceholderImage(this.placeholderId);
        invalidate();
    }

    public void roundAsCircle(boolean roundAsCircle) {
        this.roundAsCircle = roundAsCircle;
        GenericDraweeHierarchy hierarchy = getHierarchy();
        RoundingParams rp = hierarchy.getRoundingParams();
        if (null == rp) {
            rp = new RoundingParams();
        }
        rp.setRoundAsCircle(this.roundAsCircle);
        hierarchy.setRoundingParams(rp);
        invalidate();
    }

    public void roundedCornerRadius(int roundedCornerRadius) {
        this.roundedCornerRadius = roundedCornerRadius;
        GenericDraweeHierarchy hierarchy = getHierarchy();
        RoundingParams rp = hierarchy.getRoundingParams();
        if (null == rp) {
            rp = new RoundingParams();
        }
        rp.setCornersRadius(this.roundedCornerRadius);
        hierarchy.setRoundingParams(rp);
        invalidate();
    }
}
