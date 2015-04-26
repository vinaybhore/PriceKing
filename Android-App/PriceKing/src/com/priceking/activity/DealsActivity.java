package com.priceking.activity;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.LocationManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.analytics.tracking.android.EasyTracker;
import com.priceking.ApplicationEx;
import com.priceking.R;
import com.priceking.adapter.CouponListAdapter;
import com.priceking.data.DatabaseHandler;
import com.priceking.entity.Coupon;
import com.priceking.services.GPSTracker;
import com.priceking.services.RetrieveCouponService;
import com.priceking.services.RetrieveCouponService.RetrieveCouponServiceListener;
import com.priceking.services.RetrieveImageService;
import com.priceking.services.RetrieveImageService.RetrieveImageServiceListener;
import com.priceking.utils.PriceKingUtils;

/**
 * @author devpawar
 */
public class DealsActivity extends BaseActivity implements
		RetrieveCouponServiceListener, RetrieveImageServiceListener {
	// For Database
	private DatabaseHandler db;
	private int count;

	private ProgressDialog pd;

	private List<Coupon> couponList = new ArrayList<Coupon>();
	private ListView couponListView;
	private CouponListAdapter couponListAdapter;
	private TextView noDataTextView;

	private LocationManager mLocationManager;

	/** Called when the activity is first created. */

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mMenuDrawer.setContentView(R.layout.deals);

		getActionBar().setTitle("Today's Deals");

		noDataTextView = (TextView) findViewById(R.id.no_data);
		couponListView = (ListView) findViewById(R.id.deals_list_view);

		/**
		 * Call daily deals web service
		 */
		searchDeals();

	}

	/**
	 * Checks if the network connection is available, if yes then hit the web
	 * service call.
	 */
	public void searchDeals() {
		if (PriceKingUtils.isConnectionAvailable(DealsActivity.this)) {
			getLocation();
			if (ApplicationEx.latitude == 0.0 && ApplicationEx.longitude == 0.0) {
				AlertDialog.Builder dialog = new AlertDialog.Builder(
						DealsActivity.this);
				dialog.setTitle("Turn On GPS");
				dialog.setIcon(R.drawable.gps);
				dialog.setMessage("Please turn on the GPS to get current location");
				dialog.setCancelable(false);
				dialog.setPositiveButton(android.R.string.ok,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int whichButton) {
								finish();
							}
						});

				dialog.show();
			} else {
				getLocation();
				getCoupons();
			}
		} else {
			loadPrevioudData();
			PriceKingUtils.showToast(DealsActivity.this, getResources()
					.getString(R.string.network_error));
		}

	}

	/**
	 * Retrieves current location lat-long
	 */
	public void getLocation() {
		GPSTracker gps = new GPSTracker(this);
		ApplicationEx.latitude = gps.getLatitude();
		ApplicationEx.longitude = gps.getLongitude();
	}

	/**
	 * Loads previously stored data (offline support)
	 */
	public void loadPrevioudData() {
		try {
			db = new DatabaseHandler(getApplicationContext());
			db.openInternalDB();
			couponList = db.getCouponList(DatabaseHandler.TABLE_COUPON);

			if (couponList == null || couponList.size() == 0) {
				couponListView.setVisibility(View.GONE);
				noDataTextView.setVisibility(View.VISIBLE);
			} else {
				couponListView.setVisibility(View.VISIBLE);
				noDataTextView.setVisibility(View.GONE);
				couponListAdapter = new CouponListAdapter(couponList,
						DealsActivity.this);
				couponListView.setAdapter(couponListAdapter);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.close();
		}
	}

	private void getCoupons() {
		count = 0;
		pd = ProgressDialog.show(DealsActivity.this, "", "Loading...");
		RetrieveCouponService service = new RetrieveCouponService(
				getApplicationContext(), "");
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

	private void getImage(String imageURL) {
		RetrieveImageService service = new RetrieveImageService(
				DealsActivity.this);
		service.setListener(this);
		service.setImageURL(imageURL);
		ApplicationEx.operationsQueue.execute(service);
	}

	@Override
	public void onRetrieveCouponFinished(List<Coupon> couponList) {
		this.couponList = couponList;
		/**
		 * download thumbnail images
		 */
		for (int i = 0; i < couponList.size(); i++) {
			Coupon coupon = couponList.get(i);
			if (!TextUtils.isEmpty(coupon.getThumbnailImage()))
				getImage(coupon.getThumbnailImage());
		}

	}

	@Override
	public void onRetrieveCouponFailed(int error, String message) {
		if (pd != null || pd.isShowing())
			pd.dismiss();
		Toast.makeText(DealsActivity.this, message, Toast.LENGTH_SHORT).show();
	};

	@Override
	public void onGetImageFinished(RetrieveImageService getImageService,
			Drawable image) {
		count++;
		try {
			db = new DatabaseHandler(DealsActivity.this);
			db.openInternalDB();
			Bitmap bitmap = ((BitmapDrawable) image).getBitmap();
			ByteArrayOutputStream stream = new ByteArrayOutputStream();
			bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
			byte[] thumbnailBlob = stream.toByteArray();
			db.updateDealsTable(DatabaseHandler.TABLE_COUPON,
					getImageService.getImageURL(), thumbnailBlob);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {

		}

		if (count == couponList.size()) {
			count = 0;
			db.close();
			try {
				db = new DatabaseHandler(getApplicationContext());
				db.openInternalDB();
				couponList = db.getCouponList(DatabaseHandler.TABLE_COUPON);
				if (pd != null || pd.isShowing())
					pd.dismiss();

				if (couponList == null || couponList.size() == 0) {
					couponListView.setVisibility(View.GONE);
					noDataTextView.setVisibility(View.VISIBLE);
				} else {
					couponListView.setVisibility(View.VISIBLE);
					noDataTextView.setVisibility(View.GONE);
					couponListAdapter = new CouponListAdapter(couponList,
							DealsActivity.this);
					couponListView.setAdapter(couponListAdapter);
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				db.close();
			}

		}

	}

	@Override
	public void onGetImageFailed(RetrieveImageService getImageService,
			String error) {
		if (pd != null || pd.isShowing())
			pd.dismiss();
		PriceKingUtils.showToast(DealsActivity.this, error);
	}

}