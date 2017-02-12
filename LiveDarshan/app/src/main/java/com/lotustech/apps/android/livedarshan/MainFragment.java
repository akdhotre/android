package com.lotustech.apps.android.livedarshan;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MainFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MainFragment extends Fragment {

    private static final String TAG = "MainFragment";

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment MainFragment.
     */
    public static MainFragment newInstance() {
        MainFragment fragment = new MainFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_main, container, false);

        TextView mTuljapurLive = (TextView) v.findViewById(R.id.tuljapurlive_btn);
        mTuljapurLive.setOnClickListener(createListener("http://cam.live-s.cdn.bitgravity.com:1935/content:cdn-live/cam/live/secure/feed008?e=0&h=9e65498ec6c77c462ed63a051d55a801"));

        TextView mKashiLive = (TextView) v.findViewById(R.id.kashilive_btn);
        mKashiLive.setOnClickListener(createListener("http://cam.live-s.cdn.bitgravity.com:1935/content:cdn-live/cam/live/secure/Kashi_Vishwanath?e=0&h=1d434391393bd1b37e858a2571fbbf3d"));

        TextView mPandalpurLive = (TextView) v.findViewById(R.id.pandalpurlive_btn);
        mPandalpurLive.setOnClickListener(createListener("http://cam.live-s.cdn.bitgravity.com:1935/content:cdn-live/cam/live/secure/feed002?e=0&h=c3ccf04373172e4d9e7b9392327d9b88"));

        ImageButton mQuestionButton = (ImageButton) v.findViewById(R.id.question_btn);
        mQuestionButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent faqIntent = new Intent(getActivity(), FaqActivity.class);
                startActivity(faqIntent);
            }
        });

        ImageButton mEmailButton = (ImageButton) v.findViewById(R.id.email_btn);
        mEmailButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                        "mailto","akapps@gmail.com", null));
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, "LiveDarshan App - Bug/Question/Feedback");
                startActivity(Intent.createChooser(emailIntent, "Send Email"));
            }
        });

        ImageButton mShareButton = (ImageButton) v.findViewById(R.id.share_btn);
        mShareButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, "Recommend Tuljapur/Kashi/Pandalpur - LiveDarshan App(AdFree) at 'http://google.com'");
                sendIntent.setType("text/plain");
                startActivity(sendIntent);

            }
        });


        return v;
    }

    private View.OnClickListener createListener(final String url){
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isInternetConnected = isInternetConnected(getActivity());
                Log.d(TAG, "Is internet connected: " + isInternetConnected);

                if(!isInternetConnected(getActivity())){
                    Toast.makeText(getActivity(), R.string.no_internet, Toast.LENGTH_LONG).show();
                    return;
                }



                String mDisplayWidth = getResources().getString(R.string.media_width).equals("-1") ? "" : "&width="+getResources().getString(R.string.media_width);
                String mDisplayHeight = getResources().getString(R.string.media_height).equals("-1") ? "" : "&height=" + getResources().getString(R.string.media_height);

                Intent intent = new Intent(getActivity(), WebViewActivity.class);
                intent.putExtra("url", url + mDisplayWidth + mDisplayHeight);

                Log.d(TAG, "connecting " + url + mDisplayWidth + mDisplayHeight);
                Toast.makeText(getActivity(), "connecting " + url + mDisplayWidth + mDisplayHeight, Toast.LENGTH_LONG).show();

                startActivity(intent);
            }
        };
    }

    private boolean isInternetConnected(final Context context) {
        final ConnectivityManager connectivityManager = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE));
        return connectivityManager.getActiveNetworkInfo() != null && connectivityManager.getActiveNetworkInfo().isConnected();
    }
}
