package tool.imageloadercompact;

import android.content.Context;
import android.content.res.TypedArray;
import android.net.Uri;
import android.util.AttributeSet;

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

    private void init(Context context, AttributeSet attrs) {
        if (ImageLoaderCompact.useFresco) {
            return;
        }
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
        } finally {
            a.recycle();
        }

    }

    @Override
    public void setImageURI(Uri uri) {
        if (ImageLoaderCompact.useFresco) {
            super.setImageURI(uri);
        } else {

        }
    }
}
