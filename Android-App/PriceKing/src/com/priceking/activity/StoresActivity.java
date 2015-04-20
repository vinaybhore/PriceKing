package com.priceking.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;

import com.google.analytics.tracking.android.EasyTracker;
import com.priceking.R;
import com.priceking.adapter.GridImageAdapter;

public class StoresActivity extends BaseActivity {

	/**
	 * @author devpawar
	 */

	/** Called when the activity is first created. */

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mMenuDrawer.setContentView(R.layout.stores);

		getActionBar().setTitle("Stores");

		GridView gridView = (GridView) findViewById(R.id.grid_view);

		// Instance of ImageAdapter Class
		gridView.setAdapter(new GridImageAdapter(this));

		/**
		 * On Click event for Single Gridview Item
		 * */
		gridView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View v,
					int position, long id) {

				// PriceKingUtils.showToast(StoresActivity.this, "" + position);
			}
		});

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