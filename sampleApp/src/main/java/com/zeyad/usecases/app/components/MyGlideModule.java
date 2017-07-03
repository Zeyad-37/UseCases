package com.zeyad.usecases.app.components;

import android.content.Context;

import com.bumptech.glide.Glide;
import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.module.GlideModule;

/** @author by ZIaDo on 4/18/17. */
public class MyGlideModule implements GlideModule {
    @Override
    public void applyOptions(Context context, GlideBuilder builder) {
        //        builder.setDiskCache(new DiskCacheFactory(context, ".", IMAGE_CACHE_SIZE));
    }

    @Override
    public void registerComponents(Context context, Glide glide) {}
}
