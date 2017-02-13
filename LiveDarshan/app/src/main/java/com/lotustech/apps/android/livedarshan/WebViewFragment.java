package com.lotustech.apps.android.livedarshan;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import static com.lotustech.apps.android.livedarshan.R.id.webview;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * Use the {@link WebViewFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class WebViewFragment extends Fragment {

    private WebView mWebView;

    public static WebViewFragment newInstance(String url) {
        //Log.d(TAG, "new webview to load url: " + url);

        WebViewFragment fragment = new WebViewFragment();
        Bundle args = new Bundle();
        args.putString("url", url);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setHasOptionsMenu(false);
        Toast.makeText(getActivity(), R.string.live_streaming_warning, Toast.LENGTH_LONG).show();

    }
//
//    @Override
//    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
//        super.onCreateOptionsMenu(menu, inflater);
//        inflater.inflate(R.menu.menu, menu);
//    }

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_web_view, container, false);

        Bundle arguments = getArguments();
        String url = arguments.getString("url");
        //Log.d(TAG, "new webview to load url: " + url);


        mWebView = (WebView) view.findViewById(webview);
        mWebView.getSettings().setJavaScriptEnabled(true);

        //noinspection deprecation
        mWebView.setWebViewClient(new WebViewClient() {
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                view.loadUrl("file:///android_asset/error.html");
                //Log.e(TAG, String.format("Encountered error url[%s], description[%s] ", failingUrl, description));
            }
        });

        mWebView.loadUrl(url);
        return view;
    }

    @Override
    public void onDetach() {
        super.onDetach();

        mWebView.destroy();
    }
}
