# ImageLoaderCompact
      Compact invoking with 4 imageloaders
# Contains:
      Fresco-0.8.0+                        ImageLoaderCompactWithFresco
      Glide-3.5.2                          ImageLoaderCompactWithGlide
      Picasso-2.5.2                        ImageLoaderCompactWithPicasso
      Universal-Image-Loader-1.9.5         ImageLoaderCompactWithUil
      Choose (only) one above you like.
      
      :samples:app      PS. Demo by 'compile' way
      :samples:app2     PS. Demo by 'compile project' way
      
# How to use:
      1. Select one imageloader:
      in build.gradle:
            
            String useCompactFresco = "${USE_COMPACT_FRESCO}"
            if ("true".equals(useCompactFresco)) {
                compile 'tools.android:ImageLoaderCompactWithFresco:1.0.4'
            }
            String useCompactGlide = "${USE_COMPACT_GLIDE}"
            if ("true".equals(useCompactGlide)) {
                compile 'tools.android:ImageLoaderCompactWithGlide:1.0.5'  // 1.0.5 for ARGB_8888 and 1.0.4 for ARGB_565
            }
            String useCompactPicasso = "${USE_COMPACT_PICASSO}"
            if ("true".equals(useCompactPicasso)) {
                compile 'tools.android:ImageLoaderCompactWithPicasso:1.0.0'
            }
            String useCompactUil = "${USE_COMPACT_UIL}"
            if ("true".equals(useCompactUil)) {
                compile 'tools.android:ImageLoaderCompactWithUil:1.0.0'
            }
            
          or
            String useCompactFresco = "${USE_COMPACT_FRESCO}"
            if ("true".equals(useCompactFresco)) {
                compile project(':ImageLoaderCompactWithFresco:ImageLoaderCompactWithFresco')
            }
            String useCompactGlide = "${USE_COMPACT_GLIDE}"
            if ("true".equals(useCompactGlide)) {
                compile project(':ImageLoaderCompactWithGlide:ImageLoaderCompactWithGlide')
            }
            String useCompactPicasso = "${USE_COMPACT_PICASSO}"
            if ("true".equals(useCompactPicasso)) {
                compile project(':ImageLoaderCompactWithPicasso:ImageLoaderCompactWithPicasso')
            }
            String useCompactUil = "${USE_COMPACT_UIL}"
            if ("true".equals(useCompactUil)) {
                compile project(':ImageLoaderCompactWithUil:ImageLoaderCompactWithUil')
            }
      2. Load images:     
      in layout.xml
            <com.android.imageloadercompact.CompactImageView
                xmlns:compact="http://schemas.android.com/apk/res-auto"
                android:id="@+id/logo"
                android:layout_width="65dp"
                android:layout_height="65dp"
                android:layout_alignParentTop="true"
                android:layout_centerHorizontal="true"
                android:scaleType="centerCrop"
                compact:placeholderImage="@mipmap/ic_launcher" />
            CompactImageView imageview1 = (CompactImageView) findViewById(R.id.logo);
            ImageLoaderCompact.getInstance().displayImage(getActivity(), url, imageview1);
      
      or in source code
            container = (RelativeLayout) view.findViewById(R.id.container);
            CompactImageView imageview1 = new CompactImageView(getActivity());
            container.addView(imageview1);
            ViewGroup.LayoutParams lp1 = imageview1.getLayoutParams();
            lp1.width = dp65;
            lp1.height = dp65;
            imageview1.setLayoutParams(lp1);
            ((RelativeLayout.LayoutParams) imageview1.getLayoutParams())
                .addRule(RelativeLayout.ALIGN_PARENT_LEFT);
            imageview1.setPlaceholderId(R.mipmap.ic_launcher);
            ImageLoaderCompact.getInstance().displayImage(getActivity(), url, imageview1);
      3. Invoking compacted:
      Count cache size:
            final TextView size = (TextView) view.findViewById(R.id.cache_size_text);
            Size value = ImageLoaderCompact.getInstance().getCacheSize();
            BigDecimal bd = new BigDecimal(String.valueOf(value.getMSize()));
            bd = bd.setScale(1, BigDecimal.ROUND_DOWN);
            size.setText(bd.toString());
            
      Clear cache:
            Button btn = (Button) view.findViewById(R.id.clear_cache);
            btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ImageLoaderCompact.getInstance().clearDiskCaches(
                        new OnDiskCachesClearListener() {
                    @Override
                    public void onDiskCacheCleared() {
                        Toast.makeText(getActivity(), "cache cleared", Toast.LENGTH_LONG).show();

                        Size value = ImageLoaderCompact.getInstance().getCacheSize();
                        BigDecimal bd = new BigDecimal(String.valueOf(value.getMSize()));
                        bd = bd.setScale(1, BigDecimal.ROUND_DOWN);
                        size.setText(bd.toString());
                    }
                });
              }
            });
            
      Async fetch bitmap:
            final ImageView asyncImageView = (ImageView) view.findViewById(R.id.async_logo);
            ImageLoaderCompact.getInstance().asyncFetchBitmapByUrl(url, new OnFetchBitmapListener() {

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
            
      PhotoView support in Fresco/Glide/Picasso/Universal-Image-Loader:
            <com.android.imageloadercompact.CompactPhotoView
                android:id="@+id/detail_photo_image"
                android:layout_width="match_parent"
                android:layout_height="match_parent"
                android:layout_centerInParent="true" />
            CompactPhotoView photo = (CompactPhotoView) view.findViewById(R.id.detail_photo_image);
            ImageLoaderCompact.getInstance().displayImage(getActivity(), url, photo);
            
      As circle:      
            <com.android.imageloadercompact.CompactImageView
                xmlns:compact="http://schemas.android.com/apk/res-auto"
                android:id="@+id/logo"
                android:layout_width="65dp"
                android:layout_height="65dp"
                android:layout_alignParentLeft="true"
                android:layout_alignParentTop="true"
                android:scaleType="centerCrop"
                compact:placeholderImage="@mipmap/ic_launcher"
                compact:roundAsCircle="true" />
          or
            CompactImageView imageView1 = (CompactImageView) view.findViewById(R.id.logo);
            imageview1.setPlaceholderId(R.mipmap.ic_launcher);
            imageview1.roundAsCircle(true);
            
      Corner radius:
            <com.android.imageloadercompact.CompactImageView
                xmlns:compact="http://schemas.android.com/apk/res-auto"
                android:id="@+id/logo"
                android:layout_width="65dp"
                android:layout_height="65dp"
                android:layout_alignParentRight="true"
                android:layout_alignParentTop="true"
                android:scaleType="centerCrop"
                compact:placeholderImage="@mipmap/ic_launcher"
                compact:roundedCornerRadius="6dp" />
          or
            CompactImageView imageView1 = (CompactImageView) view.findViewById(R.id.logo);
            imageview1.setPlaceholderId(R.mipmap.ic_launcher);
            imageview1.roundedCornerRadius(dp6);
            
# License
      This project is BSD-licensed, you are good to go.        
            
            
