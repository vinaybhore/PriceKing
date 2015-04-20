package com.priceking.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.google.analytics.tracking.android.EasyTracker;
import com.priceking.ApplicationEx;
import com.priceking.R;

public class SignInActivity extends BaseActivity {
	private Button signInButton;
	private Button signUpButton;
	private EditText emailEditText;
	private EditText passwordEditText;
	private SharedPreferences.Editor prefEditor;

	/**
	 * @author devpawar
	 */

	/** Called when the activity is first created. */

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mMenuDrawer.setContentView(R.layout.sign_in);

		signInButton = (Button) findViewById(R.id.sign_in);
		signUpButton = (Button) findViewById(R.id.create_account);
		emailEditText = (EditText) findViewById(R.id.email);
		passwordEditText = (EditText) findViewById(R.id.password);

		signInButton.setOnClickListener(onClickListener);
		signUpButton.setOnClickListener(onClickListener);

	}

	private OnClickListener onClickListener = new OnClickListener() {

		@Override
		public void onClick(View view) {
			int id = view.getId();
			Intent intent = null;
			switch (id) {
			case R.id.sign_in:
				String userName = emailEditText.getText().toString();
				prefEditor = ApplicationEx.sharedPreference.edit();
				prefEditor.putString(
						getResources().getString(R.string.app_user_name),
						userName).commit();
				ApplicationEx.isLoggedIn = true;
				ApplicationEx.userName = userName;
				intent = new Intent(SignInActivity.this, HomeActivity.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);

				break;
			case R.id.create_account:
				intent = new Intent(SignInActivity.this, SignUpActivity.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);
				break;

			default:
				break;
			}

		}
	};

	@Override
	protected void onStart() {
		super.onStart();
		/**
		 * Start Google Analytics Tracking
		 */
		EasyTracker.getInstance(this).activityStart(this);
	}

	@Override
	protected void onStop() {
		super.onStop();
		/**
		 * Stop Google Analytics Tracking
		 */
		EasyTracker.getInstance(this).activityStop(this);
	}

}