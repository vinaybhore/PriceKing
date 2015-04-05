package com.priceking;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;

import com.priceking.entity.Product;
import com.priceking.utils.PriceKingUtils;

/**
 * Application level class to initialize and maintain various application life
 * cycle specific details
 */

/**
 * @author devpawar
 */

public class ApplicationEx extends android.app.Application {
	/**
	 * used to set core number of threads
	 */
	private static final int CORE_POOL_SIZE = 6;
	public static boolean isNetworkAvailableFlag;
	/**
	 * used to set the maximum allowed number of threads.
	 */
	private static final int MAXIMUM_POOL_SIZE = 6;
	/**
	 * executes each submitted task using one of possibly several pooled threads
	 */
	public static ThreadPoolExecutor operationsQueue;
	/** Shared Preference to store login credentials. */
	public static SharedPreferences sharedPreference;
	/** Application Context */
	public static Context context;

	/**
	 * User selected Product on the product list screen
	 */
	public static int selectedPosition = 0;

	/**
	 * True if the detailed screen is opened first time from list view (used for
	 * controlling animation)
	 */
	public static boolean isFirstLoad = false;

	/**
	 * Contains Product Items
	 */
	public static List<Product> productList = new ArrayList<Product>();

	public static Map<String, Drawable> images = new HashMap<String, Drawable>(); // Drawables
																					// in
																					// HashMap
																					// should

	// be saved as WeakReference or
	// SoftReference

	/**
	 * @see android.app.Application#onCreate()
	 */
	@Override
	public void onCreate() {
		super.onCreate();
		context = getApplicationContext();

		/**
		 * Check if the Network is available before making any network call.
		 */
		isNetworkAvailableFlag = PriceKingUtils.isConnectionAvailable(context);
		operationsQueue = new ThreadPoolExecutor(CORE_POOL_SIZE,
				MAXIMUM_POOL_SIZE, 100000L, TimeUnit.MILLISECONDS,
				new LinkedBlockingQueue<Runnable>());

		// getSharedPreferencesValue();

	}

	// /**
	// * Get the current city from shared preference
	// */
	// public static void getSharedPreferencesValue() {
	// sharedPreference = context.getSharedPreferences(context.getResources()
	// .getString(R.string.area), context.MODE_WORLD_READABLE);
	//
	// if (sharedPreference != null) {
	// myCity = sharedPreference.getString(context.getResources()
	// .getString(R.string.city), "");
	// }
	// }

}
