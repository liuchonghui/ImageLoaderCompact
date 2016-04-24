package tool.imageloadercompact.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.overlay.ApplicationUncaughtHandler;

import java.lang.reflect.Field;


/**
 * fragment基类
 *
 * @author liu_chonghui
 */
public class BaseFragment extends Fragment {

    protected View mView;

    /**
     * 当fragment和activity关联之后，调用这个方法
     */
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Thread.setDefaultUncaughtExceptionHandler(new ApplicationUncaughtHandler(getActivity()));
    }

    /**
     * 创建fragment中的视图的时候，调用这个方法。
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    /**
     * 当activity的onCreate()方法被返回之后，调用这个方法。
     */
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    /**
     * 当fragment中的视图被移除的时候，调用这个方法。
     */

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    /**
     * 当fragment和activity分离的时候，调用这个方法。
     */
    @Override
    public void onDetach() {
        super.onDetach();
        try {
            Field childFragmentManager = Fragment.class.getDeclaredField("mChildFragmentManager");
            childFragmentManager.setAccessible(true);
            childFragmentManager.set(this, null);
        } catch (Exception e) {
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    public void onNewIntent(Intent intent) {
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    /**
     * 如果view不为null 先移除而后返回
     *
     * @return
     */
    protected boolean isViewNull() {
        boolean isNull = true;
        if (mView != null) {
            ViewGroup parent = (ViewGroup) mView.getParent();
            if (parent != null) {
                parent.removeView(mView);
            }
            isNull = false;
        }
        return isNull;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        // remove掉保存的Fragment
        String FRAGMENTS_TAG = "android:support:fragments";
        outState.remove(FRAGMENTS_TAG);
    }
}
