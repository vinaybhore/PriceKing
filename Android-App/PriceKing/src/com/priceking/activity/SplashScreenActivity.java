package com.priceking.activity;

import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.Window;

import com.google.analytics.tracking.android.EasyTracker;
import com.priceking.ApplicationEx;
import com.priceking.R;
import com.priceking.data.DatabaseHandler;
import com.priceking.entity.Product;
import com.priceking.services.RetrieveAdvertisementsService;
import com.priceking.services.RetrieveAdvertisementsService.RetrieveAdvertisementServiceListener;
import com.priceking.services.RetrieveImageService;
import com.priceking.services.RetrieveImageService.RetrieveImageServiceListener;
import com.priceking.utils.PriceKingUtils;

/**
 * Splash Screen
 * 
 * @author DEVEN
 * 
 */
public class SplashScreenActivity extends Activity implements
		RetrieveAdvertisementServiceListener, RetrieveImageServiceListener {

	private static final int DELAY = 100;
	private static final int START_HOME_ACTIVITY = 1;

	// private ProgressDialog pd;

	// For Database
	private DatabaseHandler db;
	private int count;
	private int ratingCount;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.splash_screen);

		/**
		 * Search Advertisements
		 */
		searchAdvertisements();

	}

	/**
	 * Checks if the network connection is available, if yes then hit the web
	 * service call.
	 */
	public void searchAdvertisements() {
		if (PriceKingUtils.isConnectionAvailable(SplashScreenActivity.this)) {
			getAdvertisements();
		} else {
			mHandler.sendEmptyMessageDelayed(START_HOME_ACTIVITY, DELAY);
		}

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

	/**
	 * Advertisment web service call
	 */
	private void getAdvertisements() {
		// ApplicationEx.productImages.clear();
		count = 0;
		RetrieveAdvertisementsService service = new RetrieveAdvertisementsService(
				getApplicationContext(),
				PriceKingUtils.getRandomAdvertisement());
		service.setListener(this);
		ApplicationEx.operationsQueue.execute(service);
	}

	/*
	 * Success Callback (non-Javadoc)
	 * 
	 * @see
	 * com.herald.services.RetrieveEventsService.RetrieveEventsServiceListener
	 * #onRetrieveEventsFinished(java.util.ArrayList)
	 */
	@Override
	public void onRetrieveAdvertisementFinished(List<Product> advertisementList) {
		ApplicationEx.advertisementList = advertisementList;
		System.out.println("*****Reached Success*****");

		for (int i = 0; i < advertisementList.size(); i++) {
			count++;
			Product advertisement = advertisementList.get(i);
			if (advertisement.getThumbnailImage() != null
					|| !TextUtils.isEmpty(advertisement.getThumbnailImage()))
				getImage(advertisement.getThumbnailImage());
		}

	}

	private void getImage(String imageURL) {
		if (!ApplicationEx.productImages.containsKey(imageURL)) {
			RetrieveImageService service = new RetrieveImageService(
					SplashScreenActivity.this);
			service.setListener(this);
			service.setImageURL(imageURL);
			ApplicationEx.operationsQueue.execute(service);
		}
	}

	/*
	 * Failure Callback (non-Javadoc)
	 * 
	 * @see
	 * com.herald.services.RetrieveEventsService.RetrieveEventsServiceListener
	 * #onRetrieveEventsFailed(int, java.lang.String)
	 */
	@Override
	public void onRetrieveAdvertisementFailed(int error, String message) {
		// if (pd != null || pd.isShowing())
		// pd.dismiss();
		mHandler.sendEmptyMessageDelayed(START_HOME_ACTIVITY, DELAY);

	}

	@Override
	public void onGetImageFinished(RetrieveImageService getImageService,
			Drawable image) {
		try {
			if (image != null) {
				ApplicationEx.productImages.put(getImageService.getImageURL(),
						image);
			}

			// db = new DatabaseHandler(SplashScreenActivity.this);
			// db.openInternalDB();
			// Bitmap bitmap = ((BitmapDrawable) image).getBitmap();
			// ByteArrayOutputStream stream = new ByteArrayOutputStream();
			// bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
			// byte[] thumbnailBlob = stream.toByteArray();
			// db.updateProductTable(DatabaseHandler.TABLE_ADVERTISEMENT,
			// getImageService.getImageURL(), thumbnailBlob);
			// stream.flush();
			// bitmap.recycle();
			// thumbnailBlob = null;
			// db.close();

		} catch (Exception e) {
			e.printStackTrace();
		} finally {

		}

		if (count == ApplicationEx.advertisementList.size()) {
			count = 0;
			// saveImagesToSDCard();
			mHandler.sendEmptyMessageDelayed(START_HOME_ACTIVITY, DELAY);
		}

	}

	// public void saveImagesToSDCard() {
	// File sdCardDirectory = Environment.getExternalStorageDirectory();
	// // Create a new folder in SD Card
	// File dir = new File(sdCardDirectory.getAbsolutePath() + "/PriceKing/");
	// dir.mkdirs();
	//
	// for (Map.Entry<String, Drawable> entry : ApplicationEx.productImages
	// .entrySet()) {
	// Bitmap bitmap = ((BitmapDrawable) entry.getValue()).getBitmap();
	// File image = new File(dir, entry.getKey() + ".png");
	// // Encode the file as a PNG image.
	// FileOutputStream outStream;
	// try {
	//
	// outStream = new FileOutputStream(image);
	// bitmap.compress(Bitmap.CompressFormat.PNG, 100, outStream);
	// /* 100 to keep full quality of the image */
	//
	// outStream.flush();
	// outStream.close();
	// } catch (FileNotFoundException e) {
	// e.printStackTrace();
	// } catch (IOException e) {
	// e.printStackTrace();
	// }
	// }
	// }

	@Override
	public void onGetImageFailed(RetrieveImageService getImageService,
			String error) {
		mHandler.sendEmptyMessageDelayed(START_HOME_ACTIVITY, DELAY);
	}

	/**
	 * Handler to navigate to the Home Activity after the specific delay.
	 */
	Handler mHandler = new Handler() {
		@Override
		public void handleMessage(android.os.Message msg) {
			Intent intent = new Intent();
			intent.setClass(SplashScreenActivity.this, HomeActivity.class);
			startActivity(intent);
			finish();
		};
	};

	/**
	 * Exit the app on back press key
	 */
	public void onBackPressed() {
		if (mHandler != null) {
			mHandler.removeMessages(START_HOME_ACTIVITY);
		}
		super.onBackPressed();
	}

}
