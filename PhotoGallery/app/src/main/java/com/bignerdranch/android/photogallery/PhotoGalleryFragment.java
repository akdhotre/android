package com.bignerdranch.android.photogallery;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Created by adhotre on 11/5/16.
 */
public class PhotoGalleryFragment extends VisibleFragment {

    private static final String TAG = "PhotoGalleryFragment";

    private RecyclerView mPhotoGalleryRecyclerView;

    private List<GalleryItem> mGalleryItems = new ArrayList<GalleryItem>();

    private ThumbnailDownloader<PhotoHolder> mPhotoHolderThumbnailDownloader;

    public static PhotoGalleryFragment newInstance(){
        return new PhotoGalleryFragment();
    }

    private void updateItems(){
        String query = QueryPreferences.getStoredQuery(getActivity());
        new FetchItemsTask(query).execute();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_photo_gallery, menu);

        MenuItem menuItem = menu.findItem(R.id.menu_item_search);
        final SearchView searchView = (SearchView) menuItem.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener(){
            @Override
            public boolean onQueryTextChange(String newText) {
                Log.d(TAG, "onQueryTextChange: " + newText);
                return false;
            }

            @Override
            public boolean onQueryTextSubmit(String query) {
                Log.d(TAG, "onQueryTextSubmit: " + query);
                QueryPreferences.setStoredQuery(getActivity(), query);
                updateItems();
                return false;
            }
        });

        searchView.setOnClickListener(new SearchView.OnClickListener(){
            @Override
            public void onClick(View v) {
                String query = QueryPreferences.getStoredQuery(getActivity());
                searchView.setQuery(query, false);
            }
        });

        MenuItem toggleItem = menu.findItem(R.id.menu_item_toggle_polling);
        if(PollService.isServiceAlarmOn(getActivity())){
            toggleItem.setTitle(R.string.start_polling);
        }else{
            toggleItem.setTitle(R.string.stop_polling);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.menu_item_clear:
                QueryPreferences.setStoredQuery(getActivity(), null);
                updateItems();
                return true;

            case R.id.menu_item_toggle_polling:
                boolean shouldAlarmStart = !PollService.isServiceAlarmOn(getActivity());
                PollService.setAlarmService(getActivity(), shouldAlarmStart);
                getActivity().invalidateOptionsMenu();
                return true;

            default:
                return super.onOptionsItemSelected(item);

        }

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        setHasOptionsMenu(true);

        updateItems();

        PollService.setAlarmService(getActivity(), true);

        Handler responseHandler = new Handler();
        mPhotoHolderThumbnailDownloader = new ThumbnailDownloader<PhotoHolder>(responseHandler);
        mPhotoHolderThumbnailDownloader.setThumbnailDownloaderListener(new ThumbnailDownloader.ThumbnailDownloaderListener<PhotoHolder>() {
            @Override
            public void onThumbnailDownloaded(PhotoHolder target, Bitmap bitmap) {
                Drawable drawable = new BitmapDrawable(bitmap);
                target.bindDrawable(drawable);
            }
        });

        mPhotoHolderThumbnailDownloader.start();
        mPhotoHolderThumbnailDownloader.getLooper();

        Log.d(TAG, "Background thread started ...");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mPhotoHolderThumbnailDownloader.clearQueue();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_photo_gallery, container, false);

        mPhotoGalleryRecyclerView = (RecyclerView) view.findViewById(R.id.fragment_photo_gallery_recycler_view);
        mPhotoGalleryRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 3));

        setupAdapter();
        
        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mPhotoHolderThumbnailDownloader.quit();
        Log.d(TAG, "Background thread destroyed ...");

    }

    private void setupAdapter() {
        if(isAdded()){
            mPhotoGalleryRecyclerView.setAdapter(new PhotoAdapter(mGalleryItems));
        }
    }

    private class FetchItemsTask extends AsyncTask<Void, Void, List<GalleryItem>>{

        private String mQuery;

        public FetchItemsTask(String query) {
            mQuery = query;
        }

        @Override
        protected List<GalleryItem> doInBackground(Void... params) {
            if(mQuery == null){
                return new FlickrFetch().fetchRecentPhotos();
            }else{
                return new FlickrFetch().searchPhotos(mQuery);
            }

        }

        @Override
        protected void onPostExecute(List<GalleryItem> galleryItems) {
            super.onPostExecute(galleryItems);

            mGalleryItems = galleryItems;

            setupAdapter();

        }
    }

    private class PhotoHolder extends RecyclerView.ViewHolder
                    implements View.OnClickListener{

        private ImageView mImageView;
        private GalleryItem mGalleryItem;

        @Override
        public void onClick(View v) {
            Intent i = PhotoPageActivity.newIntance(getActivity(), mGalleryItem.getPhotoPageUri());
            startActivity(i);

        }

        public PhotoHolder(View itemView) {
            super(itemView);
            mImageView = (ImageView) itemView.findViewById(R.id.fragment_photo_gallery_image_view);
            mImageView.setOnClickListener(this);

        }

        private void bindDrawable(Drawable drawable){
            mImageView.setImageDrawable(drawable);

        }

        public void bindGalleryItem(GalleryItem galleryItem){
            mGalleryItem = galleryItem;
        }
    }

    private class PhotoAdapter extends RecyclerView.Adapter<PhotoHolder>{
        List<GalleryItem> mGalleryItems;

        public PhotoAdapter(List<GalleryItem> galleryItems) {
            super();
            this.mGalleryItems = galleryItems;

        }

        @Override
        public PhotoHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(getActivity());
            View view = inflater.inflate(R.layout.gallery_view, parent, false);
            return new PhotoHolder(view);

        }

        @Override
        public int getItemCount() {
            return mGalleryItems.size();
        }

        @Override
        public void onBindViewHolder(PhotoHolder holder, int position) {
            GalleryItem galleryItem = mGalleryItems.get(position);
            holder.bindGalleryItem(galleryItem);
            mPhotoHolderThumbnailDownloader.queueThumbnail(holder, galleryItem.getUrl());
        }
    }
}
