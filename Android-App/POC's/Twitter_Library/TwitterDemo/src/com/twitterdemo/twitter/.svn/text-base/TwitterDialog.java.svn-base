package com.twitterdemo.twitter;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.Display;
import android.view.ViewGroup;
import android.view.Window;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

public class TwitterDialog extends Dialog {

	static float density = 0;
    static final int FB_BLUE = 0xFF6D84B4;
    static float[] DIMENSIONS_LANDSCAPE;
    static float[] DIMENSIONS_PORTRAIT;
    static final FrameLayout.LayoutParams FILL = 
        new FrameLayout.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, 
                         ViewGroup.LayoutParams.FILL_PARENT);
    static int MARGIN = 4;
    static int PADDING = 2;
    static final String DISPLAY_STRING = "touch";
    
    private String mUrl;
    private ProgressDialog mSpinner;
    private WebView mWebView;
    private LinearLayout mContent;
    
    public TwitterDialog(Context context, String url) {
        super(context);
        mUrl = url;
        
        density = context.getResources().getDisplayMetrics().density;
        DIMENSIONS_LANDSCAPE = new float[2];
        DIMENSIONS_PORTRAIT = new float[2];
        
        DIMENSIONS_LANDSCAPE[0] = (int) ((460) * density + 0.5f);
        DIMENSIONS_LANDSCAPE[1] = (int) ((260) * density + 0.5f);
        DIMENSIONS_PORTRAIT[0] = (int) ((280) * density + 0.5f);
        DIMENSIONS_PORTRAIT[1] = (int) ((420) * density + 0.5f);
        
        MARGIN = (int) ((4) * density + 0.5f);
        PADDING = (int) ((2) * density + 0.5f);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        mSpinner = new ProgressDialog(getContext());
        mSpinner.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mSpinner.setMessage("Loading...");
        
        mContent = new LinearLayout(getContext());
        mContent.setOrientation(LinearLayout.VERTICAL);
        //setUpTitle();
        setUpWebView();
        Display display = getWindow().getWindowManager().getDefaultDisplay();
        final float scale = getContext().getResources().getDisplayMetrics().density;
        float[] dimensions = display.getWidth() < display.getHeight() ?
        		DIMENSIONS_PORTRAIT : DIMENSIONS_LANDSCAPE;
        addContentView(mContent, new FrameLayout.LayoutParams(
        		(int) (dimensions[0] * scale + 0.5f),
        		(int) (dimensions[1] * scale + 0.5f)));
    }

    
    
    private void setUpWebView() {
        mWebView = new WebView(getContext());
        mWebView.setVerticalScrollBarEnabled(false);
        mWebView.setHorizontalScrollBarEnabled(false);
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.loadUrl(mUrl);
        mWebView.setWebViewClient(new TwitterDialog.TwitterWebViewClient());
        mWebView.setLayoutParams(FILL);
        mContent.addView(mWebView);
    }
    
    private class TwitterWebViewClient extends WebViewClient {

    	 @Override
         public boolean shouldOverrideUrlLoading(WebView view, String url) {
    		 Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
             getContext().startActivity(intent);
    		 return true;
    		 
    	 }

        @Override
        public void onReceivedError(WebView view, int errorCode,
                String description, String failingUrl) {
            super.onReceivedError(view, errorCode, description, failingUrl);
            
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            
            super.onPageStarted(view, url, favicon);
            mSpinner.show();
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            mSpinner.dismiss();
        }   
        
    }

    
}
