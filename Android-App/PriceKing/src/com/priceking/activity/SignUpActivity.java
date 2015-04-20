package com.priceking.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.google.analytics.tracking.android.EasyTracker;
import com.priceking.R;
import com.priceking.entity.User;
import com.priceking.utils.PriceKingUtils;

public class SignUpActivity extends BaseActivity {
	private EditText firstNameEditText;
	private EditText lastNameEditText;
	private EditText userNameEditText;
	private EditText emaiEditText;
	private EditText passwordEditText;
	private EditText confirmPasswordEditText;
	private EditText PhoneEditText;
	private Button signUpButton;
	private Button signInButton;

	private String firstName;
	private String lastName;
	private String email;
	private String password;
	private String conformPassword;
	private String phoneNumber;
	private String userName;

	/**
	 * @author devpawar
	 */

	/** Called when the activity is first created. */

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mMenuDrawer.setContentView(R.layout.sign_up);

		firstNameEditText = (EditText) findViewById(R.id.first_name);
		lastNameEditText = (EditText) findViewById(R.id.last_name);
		userNameEditText = (EditText) findViewById(R.id.username);
		emaiEditText = (EditText) findViewById(R.id.email);
		passwordEditText = (EditText) findViewById(R.id.password);
		confirmPasswordEditText = (EditText) findViewById(R.id.confirm_password);
		PhoneEditText = (EditText) findViewById(R.id.phone);
		signInButton = (Button) findViewById(R.id.sign_in_now);
		signUpButton = (Button) findViewById(R.id.create_account);

		signInButton.setOnClickListener(onClickListener);
		signUpButton.setOnClickListener(onClickListener);

	}

	private OnClickListener onClickListener = new OnClickListener() {

		@Override
		public void onClick(View view) {
			int id = view.getId();
			Intent intent = null;
			switch (id) {
			case R.id.sign_in_now:
				intent = new Intent(SignUpActivity.this, SignInActivity.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);
				break;
			case R.id.create_account:
				boolean isError = false;
				firstName = firstNameEditText.getText().toString();
				lastName = lastNameEditText.getText().toString();
				userName = userNameEditText.getText().toString();
				email = emaiEditText.getText().toString();
				password = passwordEditText.getText().toString();
				conformPassword = confirmPasswordEditText.getText().toString();
				phoneNumber = PhoneEditText.getText().toString();

				if (TextUtils.isEmpty(firstName)) {
					isError = true;
					firstNameEditText.setError("Invalid first name");
				}

				if (TextUtils.isEmpty(lastName)) {
					isError = true;
					lastNameEditText.setError("Invalid last name");
				}

				if (TextUtils.isEmpty(userName)) {
					isError = true;
					userNameEditText.setError("Invalid username");
				}

				if (!PriceKingUtils.isValidEmail(email)) {
					isError = true;
					emaiEditText.setError("Invalid email");
				}

				if (!PriceKingUtils.isValidPassword(password)) {
					isError = true;
					passwordEditText
							.setError("Password length must be greater than 6");
				}

				if (!password.equals(conformPassword)) {
					isError = true;
					confirmPasswordEditText
							.setError("Please check that your passwords match and try again");
				}

				if (!PriceKingUtils.isValidMobile(phoneNumber)) {
					isError = true;
					PhoneEditText.setError("Invalid phone number");
				}

				if (!isError) {
					User user = new User();
					user.setFirstname(firstName);
					user.setLastname(lastName);
					user.setUsername(userName);
					user.setEmail(email);
					user.setPassword(password);
					user.setPhone(phoneNumber);
					PriceKingUtils.showToast(SignUpActivity.this,
							"Account Successfully Created...");
				}

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