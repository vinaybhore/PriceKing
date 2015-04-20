package com.priceking.activity;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.TimeZone;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.provider.CalendarContract.Events;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.analytics.tracking.android.EasyTracker;
import com.priceking.ApplicationEx;
import com.priceking.R;
import com.priceking.data.DatabaseHandler;
import com.priceking.entity.Product;
import com.priceking.utils.PriceKingUtils;

/**
 * Represents the offer in detail. (Integrated with View Pager)
 * 
 * @author DEVEN
 * 
 */
public class ProductDetailActivity extends BaseActivity {

	private TextView nameTextView;
	private ImageView ratingImageView;
	private ImageView thumbnailImageView;
	private TextView msrpTextView;
	private TextView salePriceTextView;
	private TextView savingTextView;
	private TextView shortDescriptionTextView;
	private Button addToCalendarButton;
	private Button addToWishListButton;
	private Button buyNowButton;

	private Product product;

	private RelativeLayout mainLayout;
	private DatabaseHandler db;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mMenuDrawer.setContentView(R.layout.offer_detail);

		product = ApplicationEx.product;

		nameTextView = (TextView) findViewById(R.id.product_title);
		ratingImageView = (ImageView) findViewById(R.id.img_rating);
		thumbnailImageView = (ImageView) findViewById(R.id.img_thumbnail);
		msrpTextView = (TextView) findViewById(R.id.msrp);
		salePriceTextView = (TextView) findViewById(R.id.sale_price);
		savingTextView = (TextView) findViewById(R.id.saving);
		shortDescriptionTextView = (TextView) findViewById(R.id.description);
		addToCalendarButton = (Button) findViewById(R.id.add_to_calendar);
		addToWishListButton = (Button) findViewById(R.id.add_to_wish_list);
		buyNowButton = (Button) findViewById(R.id.buy_now);

		addToCalendarButton.setOnClickListener(onClickListener);
		addToWishListButton.setOnClickListener(onClickListener);
		buyNowButton.setOnClickListener(onClickListener);

		if (product != null) {
			nameTextView.setText(product.getName());
			if (product.getThumbnailBlob() != null) {
				thumbnailImageView.setImageDrawable(new BitmapDrawable(
						BitmapFactory.decodeByteArray(
								product.getThumbnailBlob(), 0,
								product.getThumbnailBlob().length)));
			} else {
				thumbnailImageView.setImageResource(R.drawable.noimage);
			}

			if (product.getCustomerRatingBlob() != null) {
				ratingImageView.setImageDrawable(new BitmapDrawable(
						BitmapFactory.decodeByteArray(
								product.getCustomerRatingBlob(), 0,
								product.getCustomerRatingBlob().length)));
			}

			msrpTextView.setText(PriceKingUtils.formatCurrencyUSD(product
					.getMsrp()));

			shortDescriptionTextView.setText(product.getShortDescription());

			String salePriceText = "<font color=#000000>Deal Price: </font> <font color=#990000>"
					+ PriceKingUtils.formatCurrencyUSD(product.getSalePrice())
					+ "</font>";

			salePriceTextView.setText(Html.fromHtml(salePriceText));

			if (product.getMsrp() == 0.0) {
				savingTextView.setVisibility(View.GONE);
			} else {
				savingTextView.setVisibility(View.VISIBLE);
				String savingText = "<font color=#000000>You Save: </font> <font color=#990000>"
						+ PriceKingUtils.calculateSavings(product.getMsrp(),
								product.getSalePrice()) + "</font>";
				savingTextView.setText(Html.fromHtml(savingText));

			}

			if (!TextUtils.isEmpty(ApplicationEx.userName)) {
				addToWishListButton.setVisibility(View.VISIBLE);
				db = new DatabaseHandler(getApplicationContext());
				db.openInternalDB();

				if (db.isInWishList(ApplicationEx.userName,
						product.getProductURL())) {
					addToWishListButton.setText("Added to wish list");
				}
				db.close();
			} else {
				addToWishListButton.setVisibility(View.GONE);
			}

			getActionBar().setTitle("Product Details");
		}

	}

	/**
	 * Method to share Product Details via email
	 */
	private void sendEmail() {
		String[] TO = { "" };
		String[] CC = { "" };
		Intent emailIntent = new Intent(Intent.ACTION_SEND);
		emailIntent.setData(Uri.parse("mailto:"));
		emailIntent.setType("text/plain");

		emailIntent.putExtra(Intent.EXTRA_EMAIL, TO);
		emailIntent.putExtra(Intent.EXTRA_CC, CC);
		emailIntent.putExtra(Intent.EXTRA_SUBJECT, product.getName());
		emailIntent.putExtra(Intent.EXTRA_TEXT, product.getProductURL());

		try {
			startActivity(Intent.createChooser(emailIntent, "Send mail..."));
			Log.i("Finished sending email...", "");
		} catch (android.content.ActivityNotFoundException ex) {
			Toast.makeText(ProductDetailActivity.this,
					"There is no email client installed.", Toast.LENGTH_SHORT)
					.show();
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
	 * Handles Menu Clicks
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Intent tweetIntent;
		PackageManager packManager;
		List<ResolveInfo> resolvedInfoList;
		boolean resolved;

		switch (item.getItemId()) {
		case R.id.facebook:
			tweetIntent = new Intent(Intent.ACTION_SEND);
			tweetIntent.putExtra(Intent.EXTRA_TEXT, product.getProductURL());
			tweetIntent.setType("text/plain");

			packManager = getPackageManager();
			resolvedInfoList = packManager.queryIntentActivities(tweetIntent,
					PackageManager.MATCH_DEFAULT_ONLY);

			resolved = false;
			for (ResolveInfo resolveInfo : resolvedInfoList) {
				if (resolveInfo.activityInfo.packageName
						.startsWith("com.facebook.katana")) {
					tweetIntent.setClassName(
							resolveInfo.activityInfo.packageName,
							resolveInfo.activityInfo.name);
					resolved = true;
					break;
				}
			}
			if (resolved) {
				startActivity(tweetIntent);
			} else {
				Toast.makeText(ProductDetailActivity.this,
						"Facebook app isn't found", Toast.LENGTH_LONG).show();

				Intent settingIntent = new Intent(ProductDetailActivity.this,
						FacebookShareActivity.class);
				settingIntent.putExtra("facebook_url", product.getProductURL());
				startActivity(settingIntent);
			}
			break;
		case R.id.twitter:
			tweetIntent = new Intent(Intent.ACTION_SEND);
			tweetIntent.putExtra(Intent.EXTRA_TEXT, product.getProductURL());
			tweetIntent.setType("text/plain");

			packManager = getPackageManager();
			resolvedInfoList = packManager.queryIntentActivities(tweetIntent,
					PackageManager.MATCH_DEFAULT_ONLY);

			resolved = false;
			for (ResolveInfo resolveInfo : resolvedInfoList) {
				if (resolveInfo.activityInfo.packageName
						.startsWith("com.twitter.android")) {
					tweetIntent.setClassName(
							resolveInfo.activityInfo.packageName,
							resolveInfo.activityInfo.name);
					resolved = true;
					break;
				}
			}
			if (resolved) {
				startActivity(tweetIntent);
			} else {
				Toast.makeText(ProductDetailActivity.this,
						"Twitter app isn't found", Toast.LENGTH_LONG).show();

			}
			break;
		case R.id.linked_in:
			tweetIntent = new Intent(Intent.ACTION_SEND);
			tweetIntent.putExtra(Intent.EXTRA_TEXT, product.getProductURL());
			tweetIntent.setType("text/plain");

			packManager = getPackageManager();
			resolvedInfoList = packManager.queryIntentActivities(tweetIntent,
					PackageManager.MATCH_DEFAULT_ONLY);

			resolved = false;
			for (ResolveInfo resolveInfo : resolvedInfoList) {
				if (resolveInfo.activityInfo.packageName
						.startsWith("com.linkedin.android")) {
					tweetIntent.setClassName(
							resolveInfo.activityInfo.packageName,
							resolveInfo.activityInfo.name);
					resolved = true;
					break;
				}
			}
			if (resolved) {
				startActivity(tweetIntent);
			} else {
				Toast.makeText(ProductDetailActivity.this,
						"LinkedIn app isn't found", Toast.LENGTH_LONG).show();
			}
			break;
		case R.id.mail:
			sendEmail();
			break;
		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * Creating Menu
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.offer_menu, menu);
		return true;
	}

	/**
	 * Onclick listener
	 */
	private OnClickListener onClickListener = new OnClickListener() {

		@Override
		public void onClick(View view) {
			Intent intent = null;
			int id = view.getId();
			switch (id) {
			case R.id.buy_now:
				/**
				 * for opening the links in browser.
				 */
				Intent myIntent = new Intent(Intent.ACTION_VIEW,
						Uri.parse(product.getProductURL()));
				startActivity(myIntent);
				break;
			case R.id.add_to_wish_list:
				try {
					if (!TextUtils.isEmpty(ApplicationEx.userName)) {
						addToWishListButton.setVisibility(View.VISIBLE);
						db = new DatabaseHandler(getApplicationContext());
						db.openInternalDB();
						if (!db.isInWishList(ApplicationEx.userName,
								product.getProductURL())) {
							db.addToWishList(product, ApplicationEx.userName);
							addToWishListButton.setText("Added to wish list");
						} else {
							PriceKingUtils.showToast(
									ProductDetailActivity.this,
									"Already added to wish list");
						}
					} else {
						addToWishListButton.setVisibility(View.GONE);
					}
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					db.close();
				}
				break;
			case R.id.add_to_calendar:
				try {
					Calendar cal = Calendar.getInstance(TimeZone.getDefault());
					DatePickerDialog datePicker = new DatePickerDialog(
							ProductDetailActivity.this, datePickerListener,
							cal.get(Calendar.YEAR), cal.get(Calendar.MONTH),
							cal.get(Calendar.DAY_OF_MONTH));
					datePicker.setCancelable(false);
					datePicker.setTitle("Select the date");
					datePicker.show();

				} catch (Exception e) {
					PriceKingUtils.showToast(ProductDetailActivity.this,
							"An error occured while showing Date Picker\n\n"
									+ " Error Details:\n" + e.toString());
				}

				break;
			default:
				break;
			}

		}
	};

	/**
	 * Date Picker Dialog
	 */
	private DatePickerDialog.OnDateSetListener datePickerListener = new DatePickerDialog.OnDateSetListener() {

		// when dialog box is closed, below method will be called.
		public void onDateSet(DatePicker view, int selectedYear,
				int selectedMonth, int selectedDay) {
			String year = String.valueOf(selectedYear);
			String month = String.valueOf(selectedMonth + 1);
			String day = String.valueOf(selectedDay);

			addEventToCalendar(Integer.parseInt(year), Integer.parseInt(month),
					Integer.parseInt(day));

		}
	};

	/**
	 * Add Event to Calendar
	 */
	public void addEventToCalendar(int year, int month, int day) {
		Intent calIntent = new Intent(Intent.ACTION_INSERT);
		calIntent.setType("vnd.android.cursor.item/event");
		calIntent.putExtra(Events.TITLE, "Reminder: " + product.getName());
		calIntent.putExtra(Events.DESCRIPTION, product.getShortDescription());

		GregorianCalendar calDate = new GregorianCalendar(2012, 7, 15);
		calIntent.putExtra(CalendarContract.EXTRA_EVENT_ALL_DAY, true);
		calIntent.putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME,
				calDate.getTimeInMillis());
		calIntent.putExtra(CalendarContract.EXTRA_EVENT_END_TIME,
				calDate.getTimeInMillis());

		startActivity(calIntent);
	}

}