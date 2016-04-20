package com.android.imageloadercompact;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.facebook.drawee.view.SimpleDraweeView;

public class CompactImageView extends SimpleDraweeView {

    public CompactImageView(Context context) {
        super(context);
    }

    public CompactImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CompactImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public CompactImageView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }
}
