package com.priceking.utils;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.TextUtils;
import android.widget.Toast;

import com.priceking.ApplicationEx;
import com.priceking.R;
import com.priceking.entity.Categories;
import com.priceking.entity.Product;

/**
 * Utils class of PriceKing App that contains common and utility methods.
 * 
 * @author DEVEN
 * 
 */
public class PriceKingUtils {

	/**
	 * 
	 * @param checkNull
	 * @return empty string if the value is null
	 */
	public static String checkForNull(String checkNull) {
		if (checkNull != null && !checkNull.equalsIgnoreCase("null"))
			return checkNull;
		else
			return "";
	}

	/**
	 * method to check for network availability. returns true for available and
	 * false for unavailable
	 */
	public static boolean isConnectionAvailable(Context context) {
		ConnectivityManager conn = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo wifiNetwork = conn
				.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
		NetworkInfo mobileNetwork = conn
				.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
		if (wifiNetwork != null && wifiNetwork.isAvailable() == true
				&& wifiNetwork.isConnectedOrConnecting() == true) {
			return true;
		} else if (mobileNetwork != null && mobileNetwork.isAvailable() == true
				&& mobileNetwork.isConnectedOrConnecting() == true) {
			return true;
		} else
			return false;
	}

	/**
	 * Shows the status in Dialog.
	 * 
	 * @param context
	 * @param message
	 */
	public static void showStatus(Activity activity, String message,
			String title) {
		AlertDialog.Builder dialog = new AlertDialog.Builder(activity);
		dialog.setCancelable(false);
		dialog.setPositiveButton(android.R.string.ok,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
					}
				});
		dialog.show();
	}

	/**
	 * Will construct the AlertDialog.Builder object for convenience
	 * 
	 * @param context
	 *            Application Context
	 * @param message
	 *            Message to be displayed.
	 * @return AlertDialog.Builder object with the message set.
	 */
	public static AlertDialog.Builder getDialogForStatus(Activity activity,
			String message, String title) {
		AlertDialog.Builder dialog = new AlertDialog.Builder(activity);
		dialog.setCancelable(false);
		if (!TextUtils.isEmpty(title)) {
			dialog.setTitle(title);
		}

		dialog.setMessage(message);
		return dialog;
	}

	/**
	 * Converts long to date
	 * 
	 * @param timeStamp
	 *            date
	 * @return date converts the date in required format
	 */
	public static String convertToDate(long timeStamp) {
		if (timeStamp <= 0) {
			return "";
		}
		Date date = new Date(timeStamp);
		DateFormat formatter = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
		String strDate = formatter.format(date);
		return strDate;
	}

	/**
	 * Displays Toast with the provided message
	 * 
	 * @param context
	 * @param message
	 */
	public static void showToast(Context context, String message) {
		Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
	}

	/**
	 * Category List
	 * 
	 * @return List of Categories
	 */
	public static List<String> getCategoryList() {
		List<String> categoryList = new ArrayList<String>();
		categoryList.add(Constants.Categories.HOME_CATEGORY);
		categoryList.add(Constants.Categories.RECENTLY_VIEWED_CATEGORY);
		categoryList.add(Constants.Categories.STORES_CATEGORY);
		categoryList.add(Constants.Categories.WISHLIST_CATEGORY);
		categoryList.add(Constants.Categories.DEALS_CATEGORY);
		categoryList.add(Constants.Categories.CONTACT_US_CATEGORY);
		if (ApplicationEx.isLoggedIn)
			categoryList.add(Constants.Categories.SIGN_OUT_CATEGORY);
		else
			categoryList.add(Constants.Categories.SIGN_IN_CATEGORY);

		return categoryList;

	}

	/**
	 * validating email id
	 * 
	 * @param email
	 * @return
	 */
	public static boolean isValidEmail(String email) {
		String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
				+ "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

		Pattern pattern = Pattern.compile(EMAIL_PATTERN);
		Matcher matcher = pattern.matcher(email);
		return matcher.matches();
	}

	/**
	 * validating password with retype password
	 * 
	 * @param pass
	 * @return
	 */
	public static boolean isValidPassword(String pass) {
		if (pass != null && pass.length() > 6) {
			return true;
		}
		return false;
	}

	/**
	 * Validating Phone Number
	 * 
	 * @param phone2
	 * @return
	 */
	public static boolean isValidMobile(String phone) {
		boolean check;
		if (phone.length() < 6 || phone.length() > 13) {
			check = false;
		} else {
			check = true;
		}
		return check;
	}

	/**
	 * Comparator to compare MSRP of Product
	 */
	public static class MSRPComparator implements Comparator<Product> {

		@Override
		public int compare(Product p1, Product p2) {
			if (p1.getMsrp() < p2.getMsrp())
				return -1;
			if (p1.getMsrp() > p2.getMsrp())
				return 1;
			return 0;
		}
	}

	/**
	 * Comparator to compare MSRP of Product
	 */
	public static class SalePriceComparator implements Comparator<Product> {

		@Override
		public int compare(Product p1, Product p2) {
			if (p1.getSalePrice() < p2.getSalePrice())
				return -1;
			if (p1.getSalePrice() > p2.getSalePrice())
				return 1;
			return 0;
		}
	}

	/**
	 * Comparator to compare MSRP of Product
	 */
	public static class RatingComparator implements Comparator<Product> {

		@Override
		public int compare(Product p1, Product p2) {
			if (p1.getCustomerRating() > p2.getCustomerRating())
				return -1;
			if (p1.getCustomerRating() < p2.getCustomerRating())
				return 1;
			return 0;
		}
	}

	/**
	 * List of Advertisement Categories
	 */
	public static void setAdvertisementList() {
		ApplicationEx.advertisementCategories = new ArrayList<String>();
		ApplicationEx.advertisementCategories.add("cell phones");
		ApplicationEx.advertisementCategories.add("watches");
		ApplicationEx.advertisementCategories.add("laptops");
		ApplicationEx.advertisementCategories.add("shoes");
		ApplicationEx.advertisementCategories.add("perfumes");
		ApplicationEx.advertisementCategories.add("sunglasses");
		ApplicationEx.advertisementCategories.add("grocery");
		ApplicationEx.advertisementCategories.add("cosmetics");
		ApplicationEx.advertisementCategories.add("medicines");
		ApplicationEx.advertisementCategories.add("pest control");

	}

	/**
	 * Sets the list of categories
	 */
	public static void setCategoriesList() {
		ApplicationEx.categoryList = new ArrayList<Categories>();
		// Keep all Images in array
		Integer[] icons = { R.drawable.icon_laptop, R.drawable.icon_cellphone,
				R.drawable.icon_watch, R.drawable.icon_perfumes,
				R.drawable.icon_jackets, R.drawable.icon_shoes,
				R.drawable.sports, R.drawable.sunglasses, R.drawable.grocery,
				R.drawable.cosmetics, R.drawable.medicines,
				R.drawable.pest_control };

		// Keep all Strings in array
		String[] categories_array = { "Laptops", "Cell Phones",
				"Wrist Watches", "Perfumes", "Jackets", "Shoes",
				"Sports Utilities", "Sunglasses", "Grocery", "Cosmetics",
				"Medicines", "Pest Control" };

		for (int i = 0; i < icons.length; i++) {
			Categories categories = new Categories();
			categories.setIcon(icons[i]);
			categories.setName(categories_array[i]);
			ApplicationEx.categoryList.add(categories);
		}

	}

	/**
	 * 
	 * @return a random advertisement category
	 */
	public static String getRandomAdvertisement() {
		Random r = new Random();
		return ApplicationEx.advertisementCategories.get(r
				.nextInt(ApplicationEx.advertisementCategories.size()));

	}

	/**
	 * Format Currency in USD
	 */
	public static String formatCurrencyUSD(double number) {
		//
		// Format currency for US locale in US Locale,
		// the decimal point symbol is a comma and currency
		// symbol is $.
		//
		NumberFormat format = NumberFormat.getCurrencyInstance(Locale.US);
		String currency = format.format(number);
		return currency;
	}

	/**
	 * Calculate savings on MSRP
	 */
	public static String calculateSavings(double msrp, double salePrice) {
		float percentage = (float) ((salePrice * 100) / msrp);
		percentage = 100 - percentage;
		BigDecimal bd = new BigDecimal(Float.toString(percentage));
		bd = bd.setScale(2, BigDecimal.ROUND_HALF_UP);
		percentage = bd.floatValue();

		return formatCurrencyUSD(msrp - salePrice) + " (" + percentage + "%)";
	}

}
