package com.priceking;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.priceking.entity.Categories;
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
	 * Contains Product Items
	 */
	public static List<Product> productList = new ArrayList<Product>();

	/**
	 * Contains Product Items
	 */
	public static List<Product> advertisementList = new ArrayList<Product>();

	/**
	 * Boolean that checks login status
	 */
	public static boolean isLoggedIn = false;

	/**
	 * contains the saved user name when logged in
	 */
	public static String userName;

	/**
	 * Product Instance
	 */
	public static Product product;

	/**
	 * List of Advertisements Categories
	 */
	public static List<String> advertisementCategories;

	/**
	 * List of different categories
	 */
	public static List<Categories> categoryList;

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

		/**
		 * Get the Login Status
		 */
		isLoggedIn();

		/**
		 * Set Advertisement List
		 */
		PriceKingUtils.setAdvertisementList();

		/**
		 * Set Category List
		 */
		PriceKingUtils.setCategoriesList();

	}

	/**
	 * returns the user name saved in shared preference
	 */
	public static void getSharedPreferencesValue() {
		sharedPreference = context.getSharedPreferences(context.getResources()
				.getString(R.string.app_user_credentials),
				context.MODE_WORLD_READABLE);

		if (sharedPreference != null) {
			userName = sharedPreference.getString(context.getResources()
					.getString(R.string.app_user_name), userName);
		}
	}

	/**
	 * Check Login Status
	 */
	public static void isLoggedIn() {
		ApplicationEx.getSharedPreferencesValue();

		if (TextUtils.isEmpty(ApplicationEx.userName)) {
			isLoggedIn = false;

		} else {
			isLoggedIn = true;
		}
	}
}
