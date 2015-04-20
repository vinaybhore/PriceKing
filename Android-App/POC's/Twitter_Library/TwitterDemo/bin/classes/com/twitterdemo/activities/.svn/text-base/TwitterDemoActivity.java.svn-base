package com.twitterdemo.activities;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.http.AccessToken;
import twitter4j.http.RequestToken;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.twitterdemo.twitter.TwitterDialog;
import com.twitterdemo.twitter.TwitterManager;
import com.twitterdemo.twitter.TwitterManager.TwitterResponseListener;

public class TwitterDemoActivity extends Activity implements TwitterResponseListener {
	
	/** Callback scheme url which is used to handle callbacks from twitter login response. It should be same as specified in corresponding Activity in AndroidManifest.xml*/
	private String CALLBACK_URL = "demo://twitterdemo";
	
	private TwitterManager manager;
	private RequestToken token;
	private TwitterDialog twitterDialog;
	
	private ProgressDialog progressDialog;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        manager = TwitterManager.getInstance();
        manager.setPreferences(getSharedPreferences("DEMO_TWITTER_PREF", 0));
        
        /** Load login page if not logged in already. */
		if(!manager.checkIfAlreadyAuthenticated())
			askOAuth();
		
		final EditText feedText = (EditText) findViewById(R.id.feed);
		
		Button submitButton = (Button) findViewById(R.id.submit);
		submitButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				manager.updateStatus(TwitterDemoActivity.this, feedText.getText().toString());
			}
		});
		
    }
    
    /**
     * Obtains a login dialog for authorization and returns access token
     */
    private void askOAuth() {
		manager = TwitterManager.getInstance();
		manager.createNew();
		final Twitter twitter = manager.getTwitter();
		twitter.setOAuthConsumer(TwitterManager.CONSUMER_KEY, TwitterManager.CONSUMER_SECRET);
		if (progressDialog == null)
			progressDialog = ProgressDialog.show(this, "", "Loading..");
		Thread thread = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					token = twitter.getOAuthRequestToken(CALLBACK_URL);
					TwitterDemoActivity.this.runOnUiThread(new Runnable() {
						@Override
						public void run() {
							if (progressDialog != null) {
								progressDialog.cancel();
								progressDialog = null;
							}
							String authUrl = token.getAuthorizationURL();
							twitterDialog = new TwitterDialog(TwitterDemoActivity.this, Uri.parse(authUrl).toString());
							twitterDialog.show();
						}
					});
				} catch (TwitterException e) {
					TwitterDemoActivity.this.runOnUiThread(new Runnable() {
						@Override
						public void run() {
							if (progressDialog != null) {
								progressDialog.cancel();
								progressDialog = null;
							}
							Toast.makeText(TwitterDemoActivity.this,"Network Error", Toast.LENGTH_LONG).show();
							if(manager != null)
								manager.createNew();
						}
					});
				}
			}
		});
		thread.start();
	}
    
    /**
	 * As soon as the user successfully authorized the app, we are notified
	 * here. Now we need to get the verifier from the callback URL, retrieve
	 * token and token_secret and feed them to twitter4j (as well as
	 * consumer key and secret).
	 */
	@Override
	protected void onNewIntent(Intent intent) {
		
		super.onNewIntent(intent);
		Uri uri = intent.getData();
		if (uri != null && uri.toString().startsWith(CALLBACK_URL)) {
			
			TwitterManager manager = null;
			String verifier = uri.getQueryParameter("oauth_verifier");
			if(twitterDialog != null && twitterDialog.isShowing())
				twitterDialog.dismiss();
			
			try {
				// this will populate token and token_secret in consumer
				manager = TwitterManager.getInstance();
				Twitter twitter = manager.getTwitter();
				AccessToken a = twitter.getOAuthAccessToken(token.getToken(),token.getTokenSecret(),verifier);
				manager.setPreferences(getSharedPreferences("DEMO_TWITTER_PREF", MODE_WORLD_WRITEABLE));
				manager.setUp(a);
				
			}
			catch (Exception e) {
				Toast.makeText(this, "Ex in saving preferences", Toast.LENGTH_LONG).show();
			}
		}
	}

	@Override
	public void twitterResponseFailure() {
		// TODO Auto-generated method stub
	}

	@Override
	public void twitterResponseSuccess() {
		// TODO Auto-generated method stub
	}
}