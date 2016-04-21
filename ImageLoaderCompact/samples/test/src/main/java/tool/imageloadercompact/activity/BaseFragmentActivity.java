package tool.imageloadercompact.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;

import tool.imageloadercompact.ImageLoaderCompact;

//import com.sina.fresco.FrescoManager;

/**
 * activity基类
 *
 * @author liu_chonghui
 */
public class BaseFragmentActivity extends FragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!ImageLoaderCompact.getInstance().isInitialized()) {
            ImageLoaderCompact.getInstance().onStart();
        }
    }

    @Override
    public void onAttachFragment(Fragment fragment) {
        super.onAttachFragment(fragment);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        // remove掉保存的Fragment
        String FRAGMENTS_TAG = "android:support:fragments";
        outState.remove(FRAGMENTS_TAG);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }

    @SuppressLint("NewApi")
    @Override
    public void onTrimMemory(int level) {
        switch (level) {
            case TRIM_MEMORY_COMPLETE:
            case TRIM_MEMORY_RUNNING_LOW:
            case TRIM_MEMORY_RUNNING_CRITICAL:
            case TRIM_MEMORY_UI_HIDDEN:
                break;
        }
        super.onTrimMemory(level);
    }
}
