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
import com.priceking.entity.User;
import com.priceking.services.SignUpService;
import com.priceking.services.SignUpService.SignUpServiceListener;
import com.priceking.utils.PriceKingUtils;

public class SignInActivity extends BaseActivity implements
		SignUpServiceListener {
	private Button signInButton;
	private Button signUpButton;
	private EditText emailEditText;
	private EditText passwordEditText;
	private String userName;
	private String passsword;
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
				userName = emailEditText.getText().toString();
				passsword = passwordEditText.getText().toString();
				getSignIn();
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

	/**
	 * Sign In Service
	 */
	private void getSignIn() {
		User user = new User();
		user.setUsername(userName);
		user.setPassword(passsword);
		SignUpService service = new SignUpService(SignInActivity.this, user,
				"sign_in");
		service.setListener(this);
		ApplicationEx.operationsQueue.execute(service);
	}

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

	@Override
	public void onSignUpFinished() {
		prefEditor = ApplicationEx.sharedPreference.edit();
		prefEditor.putString(getResources().getString(R.string.app_user_name),
				userName).commit();
		ApplicationEx.isLoggedIn = true;
		ApplicationEx.userName = userName;
		Intent intent = new Intent(SignInActivity.this, HomeActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(intent);

	}

	@Override
	public void onSignUpFailed(int error, String message) {
		PriceKingUtils.showToast(SignInActivity.this,
				"Invalid Credentials. Please try again");

	}

}