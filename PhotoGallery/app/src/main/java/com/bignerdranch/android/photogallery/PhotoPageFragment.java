package com.bignerdranch.android.photogallery;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import java.io.InputStream;

/**
 * Created by adhotre on 11/16/16.
 */
public class PhotoPageFragment  extends VisibleFragment{
    private static final String ARG_URI = "photo_page_url";

    private Uri mUri;
    private WebView mWebView;
    private ProgressBar mProgressBar;


    public static PhotoPageFragment newInstance(Uri uri){
        Bundle bundle = new Bundle();
        bundle.putParcelable(ARG_URI, uri);

        PhotoPageFragment photoPageFragment = new PhotoPageFragment();
        photoPageFragment.setArguments(bundle);
        return photoPageFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mUri = (Uri) getArguments().getParcelable(ARG_URI);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_photo_page, container, false);

        mProgressBar = (ProgressBar) v.findViewById(R.id.fragment_photo_page_progress_bar);
        mProgressBar.setMax(100);

        mWebView = (WebView) v.findViewById(R.id.fragment_photo_page_view);
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.setWebChromeClient(new WebChromeClient(){
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                if(newProgress == 100){
                    mProgressBar.setVisibility(View.GONE);

                }else{
                    mProgressBar.setVisibility(View.VISIBLE);
                    mProgressBar.setProgress(newProgress);
                }
            }

            @Override
            public void onReceivedTitle(WebView view, String title) {
                AppCompatActivity appCompatActivity = (AppCompatActivity) getActivity();
                appCompatActivity.getSupportActionBar().setSubtitle(title);
            }
        });
        mWebView.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                return false;
            }
        });

        try{
//            InputStream io = Resources.getSystem().openRawResource(R.raw.tuljapurlive);
//            byte[] data = new byte[io.available()];
//            io.read(data);
//            String html = new String(data);

            String html = "<html><head>"+
                    "<style type=\"text/css\">"+
                    "html, body {margin: 0px; padding: 0px; background-color: #191919}"+
                    "</style>"+
                    "<title></title>"+
                    "<script type=\"text/javascript\" src=\"http://player.bc.cdn.bitgravity.com/10/jquery.js\"></script>"+
                    "<script type=\"text/javascript\" src=\"http://player.bc.cdn.bitgravity.com/10/functions.js\"></script>"+
                    "<script type=\"text/javascript\" src=\"http://player.bc.cdn.bitgravity.com/10/swfobject.js\"></script>"+
                    "<style media=\"screen\" type=\"text/css\">#bg_player_location {visibility:hidden}</style></head>"+
                    "<body>"+
                    "<object data=\"http://player.bc.cdn.bitgravity.com/10/BitGravityPlayer.swf\" name=\"BitGravityPlayer\" id=\"BitGravityPlayer\" type=\"application/x-shockwave-flash\" align=\"left\" height=\"380\" width=\"640\"><param value=\"true\" name=\"allowFullScreen\"><param value=\"always\" name=\"allowScriptAccess\"><param value=\"File=http://cam.live.cdn.bitgravity.com/cam/live/secure/feed008?e=0%26h=9e65498ec6c77c462ed63a051d55a801&amp;streamType=live&amp;Mode=live&amp;ScrubMode=simple&amp;VideoFit=automatic&amp;ForceReconnect=0&amp;Volume=1&amp;AutoPlay=true\" name=\"flashvars\"></object>"+
                    "<script type=\"text/javascript\">"+
                    "$(document).ready(function() {"+
                    "info.height = \"380\";"+
                    "info.width = \"640\";"+
                    "htmlvars.divID = \"bg_player_location\";"+
                    "var params = {};"+
                    "params.allowFullScreen = \"true\";"+
                    "params.allowScriptAccess = \"always\";"+
                    "var flashvars = {};"+
                    "flashvars.File = \"http://cam.live.cdn.bitgravity.com/cam/live/secure/feed008?e=0%26h=9e65498ec6c77c462ed63a051d55a801"+
                    "flashvars.streamType = \"live\";"+
                    "flashvars.Mode = \"live\";"+
                    "flashvars.ScrubMode = \"simple\";"+
                    "flashvars.VideoFit = \"automatic\";"+
                    "flashvars.ForceReconnect = \"0\";"+
                    "flashvars.Volume = \"1\";"+
                    "flashvars.AutoPlay = \"true\";"+
                    "        swfobject.embedSWF(info.BitGravityswf, htmlvars.divID, info.width, info.height, info.swfVersionStr, info.xiSwfUrlStr, flashvars, params, attributes);"+
                    "    });"+
                    "</script>"+
                "</body></html>";


            Log.d("PhotoPageFragement", "Read the file ...\n" + html);

            //mWebView.loadUrl(mUri.toString());
            //mWebView.loadData(html, "text/html", null);
            mWebView.loadUrl("http://cam.live-s.cdn.bitgravity.com:1935/content:cdn-live/cam/live/secure/feed008?e=0%26h=9e65498ec6c77c462ed63a051d55a801");

        }catch(Exception e){
            e.printStackTrace();
        }
        return  v;
    }
}
