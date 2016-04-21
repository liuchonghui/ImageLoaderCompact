package tool.imageloadercompact.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.net.URI;

import tool.imageloadercompact.CompactImageView;
import tool.imageloadercompact.ImageLoaderCompact;
import tool.imageloadercompact.activity.CustomWaitDialog;
import tool.imageloadercompact.test.R;

/**
 * @author liu_chonghui
 */
public class MainFragment extends BaseFragment {

    protected int getPageLayout() {
        return R.layout.activity_main;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        startTransaction();

        initData();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    protected void startTransaction() {
        getActivity().overridePendingTransition(R.anim.push_left_in,
                R.anim.push_still);
    }

    protected void initData() {
        if (getActivity().getIntent() != null) {
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        if (!isViewNull()) {
            return mView;
        }
        mView = inflater.inflate(getPageLayout(), container, false);
        intView(mView);
        return mView;
    }

    CustomWaitDialog mUpdateDialog;

    @SuppressLint("InflateParams")
    protected void intView(View view) {
        mUpdateDialog = new CustomWaitDialog(getActivity());
        mUpdateDialog.setCanceledOnTouchOutside(false);

        String url = "http://n.sinaimg.cn/transform/20150526/splR-avxeafs8127570.jpg";
        CompactImageView imageView = (CompactImageView) view.findViewById(R.id.anchorlist_logo);
        ImageLoaderCompact.getInstance().displayImage(getActivity(), url, imageView);
//        imageView.setImageURI(Uri.parse(url));
        CompactImageView imageview = (CompactImageView) view.findViewById(R.id.newslist_logo);
        ImageLoaderCompact.getInstance().displayImage(getActivity(), url, imageview);

    }

    protected void flushPage() {
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
    }

    boolean firstResume = true;

    @Override
    public void onResume() {
        super.onResume();
        if (firstResume) {
            firstResume = false;
        }
        if (!firstResume) {
        }
    }

    public boolean holdGoBack() {
        // if (myOneKeyShare != null && myOneKeyShare.isShow()) {
        // return true;
        // }
        // if (popupAttacher != null && popupAttacher.isShowing()) {
        // return true;
        // }
        return false;
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        boolean flag = false;
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (holdGoBack()) {
                // if (myOneKeyShare != null && myOneKeyShare.isShow()) {
                // myOneKeyShare.close();
                // }
                // if (popupAttacher != null && popupAttacher.isShowing()) {
                // popupAttacher.closePop();
                // }
                flag = true;
            }
        }
        return flag;
    }

    public void leaveCurrentPage() {
        getActivity().finish();
        getActivity().overridePendingTransition(R.anim.push_still,
                R.anim.push_right_out);
    }

}
