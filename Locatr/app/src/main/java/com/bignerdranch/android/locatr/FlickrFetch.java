package com.bignerdranch.android.locatr;

import android.location.Location;
import android.net.Uri;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by adhotre on 11/5/16.
 */
public class FlickrFetch {
    private String TAG = "FlickrFetch";

    private static final String FETCH_RECENT_METHOD = "flickr.photos.recent";

    private static final String SEARCH_METHOD = "flickr.photos.search";

    private static final Uri ENDPOINT = Uri.parse("https://api.flickr.com/services/rest/").
            buildUpon().
            appendQueryParameter("api_key","f38faefbe6da9dfe01a8dd4a43f6b7b6").
            appendQueryParameter("format","json").
            appendQueryParameter("nojsoncallback","1").
            appendQueryParameter("extras","url_s,geo").
            build();


    private String buildUrl(String method, String query){
        Uri.Builder urlBuilder = ENDPOINT.buildUpon().appendQueryParameter("method",method);

        if(SEARCH_METHOD.equalsIgnoreCase(method)){
            urlBuilder.appendQueryParameter("text", query);
        }
        Log.d(TAG, "Calling URL: " + urlBuilder.build().toString());
        return urlBuilder.build().toString();

    }

    private String buildUri(Location location){
        return ENDPOINT.buildUpon().
                            appendQueryParameter("method", SEARCH_METHOD).
                            appendQueryParameter("lat", "" + location.getLatitude()).
                            appendQueryParameter("lon", ""+location.getLongitude()).
                            build().
                            toString();
    }

    public List<GalleryItem> searchPhotos(Location location){
        String uri = buildUri(location);
        return downloadGalleryItems(uri);
    }

    public List<GalleryItem> fetchRecentPhotos(){

        return searchPhotos("tiger");
//        String uri = buildUrl(FETCH_RECENT_METHOD, null);
//        return downloadGalleryItems(uri);

    }

    public List<GalleryItem> searchPhotos(String query){
        String uri = buildUrl(SEARCH_METHOD, query);
        return downloadGalleryItems(uri);

    }

    public List<GalleryItem> downloadGalleryItems(String uri){
        List<GalleryItem> items = new ArrayList<GalleryItem>();

        try{
            String jsonString = getUrlString(uri);
            Log.d(TAG, "Received JSON: " + jsonString);

            try {
                JSONObject jsonBody = new JSONObject(jsonString);
                parseItems(items, jsonBody);
            } catch (JSONException e) {
                e.printStackTrace();
            }


        }catch(IOException ioe){
            Log.d(TAG, "Failed to fetch items", ioe);
            ioe.printStackTrace();
        }

        return items;

    }

    private void parseItems(List<GalleryItem> items, JSONObject jsonBody) throws JSONException {
        JSONObject photosJsonObject = jsonBody.getJSONObject("photos");
        JSONArray photoJsonArray = photosJsonObject.getJSONArray("photo");
        for (int i=0; i<photoJsonArray.length();i++) {
            JSONObject photoJsonObject = photoJsonArray.getJSONObject(i);

            GalleryItem galleryItem = new GalleryItem();

            galleryItem.setId(photoJsonObject.getString("id"));
            galleryItem.setCaption(photoJsonObject.getString("title"));

            if(!photoJsonObject.has("url_s")){
                continue;
            }

            galleryItem.setUrl(photoJsonObject.getString("url_s"));
            galleryItem.setOwner(photoJsonObject.getString("owner"));
            galleryItem.setLat(photoJsonObject.getDouble("latitude"));
            galleryItem.setLon(photoJsonObject.getDouble("longitude"));

            items.add(galleryItem);
        }
    }

    public byte[] getUrlBytes(String urlSpec) throws IOException {
        URL url = new URL(urlSpec);
        HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            InputStream in  = httpURLConnection.getInputStream();

            if(httpURLConnection.getResponseCode() != HttpURLConnection.HTTP_OK){
                throw new IOException(httpURLConnection.getResponseMessage() + ": with " + urlSpec);
            }

            int bytesRead = 0;
            byte[] buffer = new byte[1024];
            while((bytesRead = in.read(buffer)) > 0){
                out.write(buffer, 0, bytesRead);
            }
            out.close();

            return out.toByteArray();

        }finally{
            httpURLConnection.disconnect();
        }
    }

    public String getUrlString(String urlSpec) throws IOException {
        return new String(getUrlBytes(urlSpec));
    }


}
