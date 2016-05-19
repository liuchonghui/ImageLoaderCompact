package com.android.imageloadercompact;

import android.content.Context;
import android.util.AttributeSet;

import com.android.imageloadercompact.fresco.photodraweeview.PhotoDraweeView;
import com.facebook.drawee.generic.GenericDraweeHierarchy;

public class CompactPhotoView extends PhotoDraweeView {
    public CompactPhotoView(Context context, GenericDraweeHierarchy hierarchy) {
        super(context, hierarchy);
    }

    public CompactPhotoView(Context context) {
        super(context);
    }

    public CompactPhotoView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CompactPhotoView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }
}
