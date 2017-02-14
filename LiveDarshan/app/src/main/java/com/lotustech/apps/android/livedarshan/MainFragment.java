package com.lotustech.apps.android.livedarshan;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
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

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *Kite2*Ebay
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
        mTuljapurLive.setOnClickListener(createListener(getString(R.string.tuljapurlive_url)));

        TextView mKashiLive = (TextView) v.findViewById(R.id.kashilive_btn);
        mKashiLive.setOnClickListener(createListener(getString(R.string.kashilive_url)));

        TextView mPandalpurLive = (TextView) v.findViewById(R.id.pandalpurlive_btn);
        mPandalpurLive.setOnClickListener(createListener(getString(R.string.pandalpurlive_url)));

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
                        "mailto",getString(R.string.dev_email_address), null));
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
                sendIntent.putExtra(Intent.EXTRA_TEXT, "I recommend adfree Tuljapur/Kashi/Pandalpur - LiveDarshan App at "+getString(R.string.app_url));
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
                //Log.d(TAG, "Is internet connected: " + isInternetConnected);

                if(!isInternetConnected){
                    Toast.makeText(getActivity(), R.string.no_internet, Toast.LENGTH_LONG).show();
                    return;
                }

                DisplayMetrics displaymetrics = new DisplayMetrics();
                getActivity().getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
                int height = (int) (displaymetrics.heightPixels/displaymetrics.density);
                int width = (int) (displaymetrics.widthPixels/displaymetrics.density);

                //Log.d(TAG, String.format("Screen width[%d], height[%d] ..." , width, height));

                String mDisplayWidth = "&width=" + height;
                String mDisplayHeight = "&height=" + width;

                Intent intent = new Intent(getActivity(), WebViewActivity.class);
                intent.putExtra("url", url + mDisplayWidth + mDisplayHeight);

                //Log.d(TAG, "connecting " + url + mDisplayWidth + mDisplayHeight);

                startActivity(intent);
            }
        };
    }

    private boolean isInternetConnected(final Context context) {
        final ConnectivityManager connectivityManager = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE));
        return connectivityManager.getActiveNetworkInfo() != null && connectivityManager.getActiveNetworkInfo().isConnected();
    }
}
