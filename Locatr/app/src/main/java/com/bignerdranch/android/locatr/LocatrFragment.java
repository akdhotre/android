package com.bignerdranch.android.locatr;

import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;

/**
 * Created by adhotre on 11/20/16.
 */
public class LocatrFragment extends SupportMapFragment {

    private static final String TAG = "LocatrFragment";

    private GoogleApiClient mClient;
    private Bitmap mMapImage;
    private GalleryItem mMapItem;
    private Location mCurrentLocation;
    private GoogleMap mMap;

    public static LocatrFragment newInstance() {
        return new LocatrFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        mClient = new GoogleApiClient.Builder(getActivity()).
                        addApi(LocationServices.API).
                        addConnectionCallbacks(
                            new GoogleApiClient.ConnectionCallbacks() {
                                @Override
                                public void onConnected(@Nullable Bundle bundle) {
                                    getActivity().invalidateOptionsMenu();
                                }

                                @Override
                                public void onConnectionSuspended(int i) {

                                }
                            }
                        ).build();

        getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                mMap = googleMap;
                updateUI();

            }
        });
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        inflater.inflate(R.menu.fragment_locatr, menu);

        MenuItem menuItem = menu.findItem(R.id.action_locate);
        menuItem.setEnabled(mClient.isConnected());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_locate:
                findImage();
                return true;
            default:
                return super.onOptionsItemSelected(item);

        }
    }

    private void findImage() {
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setNumUpdates(1);
        locationRequest.setInterval(0);

        if (ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            Log.e(TAG, "Cannot access this device Location");
            return;
        }

        LocationServices.FusedLocationApi.requestLocationUpdates(mClient, locationRequest, new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                Log.d(TAG, "Got a fix: " + location);
                new SearchTask().execute(location);
            }
        });
    }

    private void updateUI(){
        if(mMap == null || mMapImage == null){
            return;
        }

        LatLng itemPoint = new LatLng(mMapItem.getLat(), mMapItem.getLon());
        LatLng myPoint = new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude());

        BitmapDescriptor itemBitMap = BitmapDescriptorFactory.fromBitmap(mMapImage);

        MarkerOptions itemMarker = new MarkerOptions().
                                        position(itemPoint).
                                        icon(itemBitMap);

        MarkerOptions myMarker = new MarkerOptions().
                                        position(myPoint);

        mMap.clear();
        mMap.addMarker(itemMarker);
        mMap.addMarker(myMarker);

        LatLngBounds bounds = new LatLngBounds.Builder().
                                    include(itemPoint).
                                    include(myPoint).
                                    build();
        int margin = getResources().getDimensionPixelSize(R.dimen.map_inset_margin);
        CameraUpdate update = CameraUpdateFactory.newLatLngBounds(bounds, margin);
        mMap.animateCamera(update);

    }

    @Override
    public void onStart() {
        super.onStart();

        getActivity().invalidateOptionsMenu();

        mClient.connect();

    }

    @Override
    public void onStop() {
        super.onStop();
        mClient.disconnect();
    }

    private class SearchTask extends AsyncTask<Location, Void, Void> {
        private GalleryItem mGalleryItem;
        private Bitmap mBitmap;
        private Location mLocation;

        @Override
        protected Void doInBackground(Location... params) {
            mLocation = params[0];

            FlickrFetch flickrFetch = new FlickrFetch();
            List<GalleryItem> photos = flickrFetch.searchPhotos(params[0]);

            if (photos.size() == 0) {
                return null;
            }

            mGalleryItem = photos.get(0);

            try {
                byte[] bytes = flickrFetch.getUrlBytes(mGalleryItem.getUrl());
                mBitmap = BitmapFactory.decodeByteArray(bytes, 0 , bytes.length);

            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            mMapImage = mBitmap;
            mCurrentLocation = mLocation;
            mMapItem = mGalleryItem;

            updateUI();

        }
    }
}