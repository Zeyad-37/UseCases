package com.zeyad.generic.usecase.dataaccesslayer.components;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.gson.Gson;
import com.zeyad.genericusecase.data.requests.FileIORequest;
import com.zeyad.genericusecase.data.services.GenericNetworkQueueIntentService;

import java.io.File;

import static com.zeyad.genericusecase.data.services.GenericNetworkQueueIntentService.DOWNLOAD_FILE;
import static com.zeyad.genericusecase.data.services.GenericNetworkQueueIntentService.JOB_TYPE;
import static com.zeyad.genericusecase.data.services.GenericNetworkQueueIntentService.PAYLOAD;

/**
 * Simple implementation of {@link ImageView} with extended features like setting an
 * image from an url and an internal file cache using the application cache directory.
 */
public class AutoLoadImageView extends ImageView {

    private static final String TAG = AutoLoadImageView.class.getName(), CLOUD = "Cloud", DISK = "Disk",
            JPG = ".jpg", PNG = ".png", BASE_IMAGE_NAME_CACHED = "image_";
    private static String CACHE_DIR;
    private String mImageUrl;
    private Context mContext;
    private int mImagePlaceHolderResourceId = -1, mImageOnErrorResourceId = -1, mImageFallBackResourceId = -1;

    public AutoLoadImageView(Context context) {
        super(context);
        mContext = context.getApplicationContext();
        CACHE_DIR = mContext.getCacheDir().getAbsolutePath();
    }

    public AutoLoadImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context.getApplicationContext();
        CACHE_DIR = mContext.getCacheDir().getAbsolutePath();
    }

    public AutoLoadImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mContext = context.getApplicationContext();
        CACHE_DIR = mContext.getCacheDir().getAbsolutePath();
    }

    /**
     * Set an image from a remote url.
     *
     * @param imageUrl The url of the resource to load.
     */
    public AutoLoadImageView setImageUrl(final String imageUrl) {
        if (imageUrl != null) {
            mImageUrl = imageUrl;
            loadImageFromUrl(imageUrl);
        } else return this;
        return this;
    }

    /**
     * Set a place holder used for loading when an image is being downloaded from the internet.
     *
     * @param resourceId The resource id to use as a place holder.
     */
    public AutoLoadImageView setImagePlaceHolder(int resourceId) {
        mImagePlaceHolderResourceId = resourceId;
        return this;
    }

    /**
     * Set a place holder used for loading when an image is being downloaded from the internet.
     *
     * @param resourceId The resource id to use as a place holder.
     */
    public AutoLoadImageView setImageFallBackResourceId(int resourceId) {
        mImageFallBackResourceId = resourceId;
        return this;
    }

    /**
     * Set a place holder used for loading when an image is being downloaded from the internet.
     *
     * @param resourceId The resource id to use as a place holder.
     */
    public AutoLoadImageView setImageOnErrorResourceId(int resourceId) {
        mImageOnErrorResourceId = resourceId;
        return this;
    }

    /**
     * Loads and image from the internet (and cache it) or from the internal cache.
     *
     * @param imageUrl The remote image url to load.
     */
    private void loadImageFromUrl(final String imageUrl) {
        File img = buildFileFromFilename(getFileNameFromUrl(imageUrl));
        if (img.exists())
            loadBitmap(img, DISK);
        else {
            loadBitmap(img, CLOUD);
            mContext.startService(new Intent(mContext, GenericNetworkQueueIntentService.class)
                    .putExtra(JOB_TYPE, DOWNLOAD_FILE)
                    .putExtra(PAYLOAD, new Gson().toJson(new FileIORequest.FileIORequestBuilder(imageUrl, img)
                            .build())));
        }
    }

    /**
     * Run the operation of loading a bitmap on the UI thread.
     *
     * @param channel The channel of retrieving the bitmap, Cloud or local.
     */
    private void loadBitmap(File img, String channel) {
        if (channel.equalsIgnoreCase(CLOUD))
            Glide.with(mContext)
                    .load(mImageUrl)
                    .placeholder(mImagePlaceHolderResourceId)
                    .fallback(mImageFallBackResourceId)
                    .error(mImageOnErrorResourceId)
//                    .override(getWidth(), getHeight())
                    .diskCacheStrategy(DiskCacheStrategy.RESULT)
                    .into(this);
        else if (channel.equalsIgnoreCase(DISK))
            Glide.with(mContext)
                    .load(Uri.fromFile(img))
                    .placeholder(mImagePlaceHolderResourceId)
                    .fallback(mImageFallBackResourceId)
                    .error(mImageOnErrorResourceId)
//                    .override(getWidth(), getHeight())
//                    .diskCacheStrategy(DiskCacheStrategy.RESULT)
                    .into(this);
    }

    /**
     * Invalidate and expire the cache.
     */
    public AutoLoadImageView evictAll(File cacheDir) {
        if (cacheDir != null && cacheDir.listFiles() != null)
            for (File file : cacheDir.listFiles())
                file.delete();
        return this;
    }

    // TODO: 3/24/16 Find more efficient way!
    public Bitmap getBitmap() {
        BitmapDrawable bitmapDrawable = (BitmapDrawable) getDrawable();
        return (bitmapDrawable.getBitmap() != null) ? bitmapDrawable.getBitmap() : null;
    }

    public Bitmap getCachedDrawable() {
        buildDrawingCache();
        return getDrawingCache();
    }

    /**
     * Creates a file name from an image url
     *
     * @param imageUrl The image url used to build the file name.
     * @return An String representing a unique file name.
     */
    private String getFileNameFromUrl(String imageUrl) {
        //we could generate an unique MD5/SHA-1 here
        String hash = String.valueOf(imageUrl.hashCode());
        if (hash.startsWith("-"))
            hash = hash.substring(1);
        return BASE_IMAGE_NAME_CACHED + hash + JPG;
    }

    /**
     * Creates a file name from an image url
     *
     * @param fileName The image url used to build the file name.
     * @return A {@link File} representing a unique element.
     */
    private File buildFileFromFilename(String fileName) {
        return new File(CACHE_DIR + File.separator + fileName);
    }
}