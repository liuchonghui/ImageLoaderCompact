package tool.imageloadercompact.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.math.BigDecimal;
import java.net.URI;

import tool.imageloadercompact.CompactImageView;
import tool.imageloadercompact.ImageLoaderCompact;
import tool.imageloadercompact.OnDiskCachesClearListener;
import tool.imageloadercompact.OnFetchBitmapListener;
import tool.imageloadercompact.Size;
import tool.imageloadercompact.activity.CustomWaitDialog;
import tool.imageloadercompact.activity.ImageBrowserActivity;
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
    RelativeLayout alianTopLayout;

    @SuppressLint("InflateParams")
    protected void intView(View view) {
        mUpdateDialog = new CustomWaitDialog(getActivity());
        mUpdateDialog.setCanceledOnTouchOutside(false);
        int dp65 = ImageLoaderCompact.getInstance().dp2px(getActivity(), 65);
        int dp6 = ImageLoaderCompact.getInstance().dp2px(getActivity(), 6);

        String url = "http://n.sinaimg.cn/transform/20150526/splR-avxeafs8127570.jpg";
        // 静态
        CompactImageView imageView1 = (CompactImageView) view.findViewById(R.id.anchorlist_logo);
        ImageLoaderCompact.getInstance().displayImage(
                getActivity(), url, imageView1);

        CompactImageView imageView2 = (CompactImageView) view.findViewById(R.id.giftlist_logo);
        ImageLoaderCompact.getInstance().displayImage(
                getActivity(), url, imageView2);

        CompactImageView imageView3 = (CompactImageView) view.findViewById(R.id.newslist_logo);
        ImageLoaderCompact.getInstance().displayImage(
                getActivity(), url, imageView3);
        // 动态
        alianTopLayout = (RelativeLayout) view.findViewById(R.id.alian_top_layout);
        CompactImageView imageview1 = new CompactImageView(getActivity());
        CompactImageView imageview2 = new CompactImageView(getActivity());
        CompactImageView imageview3 = new CompactImageView(getActivity());
        alianTopLayout.addView(imageview1);
        alianTopLayout.addView(imageview2);
        alianTopLayout.addView(imageview3);

        ViewGroup.LayoutParams lp1 = imageview1.getLayoutParams();
        lp1.width = dp65;
        lp1.height = dp65;
        imageview1.setLayoutParams(lp1);
        ViewGroup.LayoutParams lp2 = imageview2.getLayoutParams();
        lp2.width = dp65;
        lp2.height = dp65;
        imageview2.setLayoutParams(lp2);
        ViewGroup.LayoutParams lp3 = imageview3.getLayoutParams();
        lp3.width = dp65;
        lp3.height = dp65;
        imageview3.setLayoutParams(lp3);

        ((RelativeLayout.LayoutParams) imageview1.getLayoutParams())
                .addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        ((RelativeLayout.LayoutParams) imageview2.getLayoutParams())
                .addRule(RelativeLayout.CENTER_HORIZONTAL);
        ((RelativeLayout.LayoutParams) imageview3.getLayoutParams())
                .addRule(RelativeLayout.ALIGN_PARENT_RIGHT);

        imageview1.setPlaceholderId(R.mipmap.ic_launcher);
        imageview1.roundAsCircle(true);
        imageview2.setPlaceholderId(R.mipmap.ic_launcher);
        imageview3.setPlaceholderId(R.mipmap.ic_launcher);
        imageview3.roundedCornerRadius(dp6);

        ImageLoaderCompact.getInstance().displayImage(
                getActivity(), url, imageview1);
        ImageLoaderCompact.getInstance().displayImage(
                getActivity(), url, imageview2);
        ImageLoaderCompact.getInstance().displayImage(
                getActivity(), url, imageview3);
        // fitXY
        CompactImageView image1 = (CompactImageView) view.findViewById(R.id.anchor_logo);
        ImageLoaderCompact.getInstance().displayImage(
                getActivity(), url, image1);

        CompactImageView image2 = (CompactImageView) view.findViewById(R.id.gift_logo);
        ImageLoaderCompact.getInstance().displayImage(
                getActivity(), url, image2);

        CompactImageView image3 = (CompactImageView) view.findViewById(R.id.news_logo);
        ImageLoaderCompact.getInstance().displayImage(
                getActivity(), url, image3);

        // CacheSize
        TextView size = (TextView) view.findViewById(R.id.cache_size_text);
        Size value = ImageLoaderCompact.getInstance().getCacheSize();
        BigDecimal bd = new BigDecimal(String.valueOf(value.getMSize()));
        bd = bd.setScale(1, BigDecimal.ROUND_DOWN);
        size.setText(bd.toString());

        Button btn = (Button) view.findViewById(R.id.clear_cache);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ImageLoaderCompact.getInstance().clearDiskCaches(
                        new OnDiskCachesClearListener() {
                    @Override
                    public void onDiskCacheCleared() {
                        Toast.makeText(getActivity(), "缓存已清空", Toast.LENGTH_LONG).show();
                    }
                });
            }
        });

        // 异步获取Bitmap
        final ImageView asyncImageView = (ImageView) view.findViewById(R.id.async_logo);
        ImageLoaderCompact.getInstance().asyncFetchBitmapByUrl(url,
                new OnFetchBitmapListener() {

            @Override
            public void onFetchBitmapSuccess(String url, Bitmap bitmap) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    asyncImageView.setBackground(new BitmapDrawable(bitmap));
                }
            }

            @Override
            public void onFetchBitmapFailure(String url) {

            }
        });
        asyncImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ImageBrowserActivity.class);
                getActivity().startActivity(intent);
            }
        });
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
