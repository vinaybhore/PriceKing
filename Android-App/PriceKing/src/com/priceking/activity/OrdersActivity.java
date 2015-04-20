package com.priceking.activity;

import android.os.Bundle;

import com.google.analytics.tracking.android.EasyTracker;
import com.priceking.R;
import com.priceking.utils.PriceKingUtils;

public class OrdersActivity extends BaseActivity {

	/**
	 * @author devpawar
	 */

	/** Called when the activity is first created. */

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mMenuDrawer.setContentView(R.layout.temp);

		PriceKingUtils
				.showToast(OrdersActivity.this, "This is Orders Activity");

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

}