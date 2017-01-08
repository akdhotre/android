package com.bignerdranch.android.photogallery;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.util.Log;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Created by adhotre on 11/10/16.
 */
public class ThumbnailDownloader<T> extends HandlerThread {
    private static String TAG = "ThumbnailDownloader";

    private static int MESSAGE_DOWNLOAD = 0;

    private boolean isQuit;

    private ConcurrentMap<T, String> mRequestMap = new ConcurrentHashMap<>();
    private Handler mRequestHandler;
    private Handler mResponseHandler;

    private ThumbnailDownloaderListener mThumbnailDownloaderListener;

    public interface ThumbnailDownloaderListener<T>{
        void onThumbnailDownloaded(T target, Bitmap bitmap);
    }

    public void setThumbnailDownloaderListener(ThumbnailDownloaderListener<T> thumbnailDownloaderListener){
        mThumbnailDownloaderListener = thumbnailDownloaderListener;
    }

    public ThumbnailDownloader(Handler responseHandler){
        super(TAG);
        mResponseHandler = responseHandler;

    }

    @Override
    protected void onLooperPrepared() {
        mRequestHandler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                if(msg.what == MESSAGE_DOWNLOAD){
                    T target = (T) msg.obj;
                    Log.i(TAG, "Got a request for URL: " + mRequestMap.get(target));
                    handleRequest(target);
                }
            }
        };

    }

    private void handleRequest(final T target) {
        final String url = mRequestMap.get(target);

        if(url == null){
            return;
        }

        try {
            byte[] bitmapBytes = new FlickrFetch().getUrlBytes(url);
            final Bitmap bitmap = BitmapFactory.decodeByteArray(bitmapBytes, 0, bitmapBytes.length);
            Log.d(TAG, "Bitmap created ...");

            mResponseHandler.post(new Runnable() {
                @Override
                public void run() {
                    if(mRequestMap.get(target) != url || isQuit){
                        return;
                    }

                    mRequestMap.remove(target);
                    mThumbnailDownloaderListener.onThumbnailDownloaded(target, bitmap);

                }
            });

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Override
    public boolean quit() {
        isQuit= true;
        return super.quit();
    }

    public void queueThumbnail(T target, String url){
        Log.d(TAG, "Got a URL: " + url);

        if(url == null){
            mRequestMap.remove(target);
        }else{
            mRequestMap.put(target, url);
            mRequestHandler.obtainMessage(MESSAGE_DOWNLOAD, target)
            .sendToTarget();
        }
    }
    public void clearQueue(){
        mRequestMap.clear();
    }


}
