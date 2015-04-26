package com.priceking.utils;

/**
 * File having all the constants used
 * 
 * @author DEVEN
 * 
 */
public final class Constants {
	/** Error dialog codes */
	public static final class PriceKingDialogCodes {
		public static final int ACTIVITYSUCCESS = 201;
		public static final int SUCCESS = 200;
		public static final int TIMEOUT = 1;
		public static final int NETWORK_ERROR = 2;
		public static final int BAD_REQUEST = 400;
		public static final int NOT_FOUND = 404;
		public static final int DATA_NOT_FOUND = 0;
		public static final int INTERNAL_SERVER_ERROR = 500;
	}

	/** Error dialog Messages */
	public static final class PriceKingDialogMessages {
		public static final String TIMEOUT = "Timeout occurred. Please try again...";
		public static final String NETWORK_ERROR = "Network error. Please try again...";
		public static final String BAD_REQUEST = "Bad request. Please try again...";
		public static final String NOT_FOUND = "Result not found. Please try again...";
		public static final String INTERNAL_SERVER_ERROR = "Internal Server error. Pleas try again...";
	}

	/** String identifiers for serialize json of User entity */
	public static final String FIRST_NAME = "firstname";
	public static final String LAST_NAME = "lastname";
	public static final String USER_NAME = "username";
	public static final String EMAIL = "email";
	public static final String PHONE = "phone";
	public static final String PASSWORD = "password";

	/**
	 * Consumer Key and Consumer Secret for Twitter4j
	 */
	public static final String CONSUMER_KEY = "ZjfnEnPpjJnRcCRHSi9juaopQ";
	public static final String CONSUMER_SECRET = "q43VbT1WAHW9pyyZqtkOwruru0BBnP4pSNzaNqP557qj4wb2sD";

	/**
	 * Categories in Sliding Menu
	 */
	public static final class Categories {
		public static final String HOME_CATEGORY = "Home";
		public static final String RECENTLY_VIEWED_CATEGORY = "Recently Viewed Items";
		public static final String STORES_CATEGORY = "Stores";
		public static final String WISHLIST_CATEGORY = "Your Wish List";
		public static final String DEALS_CATEGORY = "Today's Deals";
		public static final String CONTACT_US_CATEGORY = "Contact Us";
		public static final String SIGN_IN_CATEGORY = "Sign In";
		public static final String SIGN_OUT_CATEGORY = "Sign Out";
	}

}
