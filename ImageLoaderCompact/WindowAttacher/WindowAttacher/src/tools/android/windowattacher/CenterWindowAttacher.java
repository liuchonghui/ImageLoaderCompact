package tools.android.windowattacher;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.support.v4.app.Fragment;
/**
 * Created by liuchonghui on 16/8/8.
 */
@SuppressLint("ValidFragment")
public class CenterWindowAttacher extends FooterWindowAttacher {

    public CenterWindowAttacher(Activity attachedActivity,
                                Fragment attachedFragment, int layoutResId) {
        super(attachedActivity, attachedFragment, layoutResId);
    }

    public CenterWindowAttacher(Activity attachedActivity,
                                Fragment attachedFragment, int layoutResId, int animationViewGroupId) {
        super(attachedActivity, attachedFragment, layoutResId,
                animationViewGroupId);
    }

    public CenterWindowAttacher(Activity attachedActivity, int layoutResId) {
        super(attachedActivity, layoutResId);
    }

    public CenterWindowAttacher(Activity attachedActivity, int layoutResId,
                                int animationViewGroupId) {
        super(attachedActivity, layoutResId, animationViewGroupId);
    }
}