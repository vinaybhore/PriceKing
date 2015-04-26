package com.priceking.activity;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.TimeZone;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.provider.CalendarContract.Events;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
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
public class RecentlyViewedDetailActivity extends BaseActivity implements
		OnGestureListener {

	private MyFragmentAdapter myFragmentAdapter;
	private ViewPager mPager;
	private TextView pageIndicator;
	private int currentPage = 0;
	private static final int SWIPE_MIN_DISTANCE = 50;
	private static final int SWIPE_MAX_OFF_PATH = 250;
	private static final int SWIPE_THRESHOLD_VELOCITY = 200;
	private GestureDetector detector = new GestureDetector(this);

	private Product product;

	private RelativeLayout mainLayout;
	private Button webSiteButton;

	private DatabaseHandler db;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mMenuDrawer.setContentView(R.layout.offer);

		pageIndicator = (TextView) findViewById(R.id.text);
		mPager = (ViewPager) findViewById(R.id.pager);
		myFragmentAdapter = new MyFragmentAdapter(getApplicationContext());
		mPager.setAdapter(myFragmentAdapter);

		/**
		 * sets the current page at the position which you want.
		 */
		mPager.setCurrentItem(ApplicationEx.selectedPosition);

		/**
		 * Sets the page indicator
		 */
		pageIndicator.setText("" + (ApplicationEx.selectedPosition + 1) + " /"
				+ ApplicationEx.productList.size());

		/**
		 * Set the touch event listener
		 */
		mPager.setOnTouchListener(new View.OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				return detector.onTouchEvent(event);
			}
		});

		mPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
			@Override
			public void onPageSelected(int position) {
				currentPage = position;
				currentPage = currentPage % ApplicationEx.productList.size();
				pageIndicator.setText((currentPage + 1) + " /"
						+ ApplicationEx.productList.size());

			}

			@Override
			public void onPageScrollStateChanged(int state) {
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
			}
		});

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
			Toast.makeText(RecentlyViewedDetailActivity.this,
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
	 * Handles back press
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
				Toast.makeText(RecentlyViewedDetailActivity.this,
						"Facebook app isn't found", Toast.LENGTH_LONG).show();

				Intent settingIntent = new Intent(
						RecentlyViewedDetailActivity.this,
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
				Toast.makeText(RecentlyViewedDetailActivity.this,
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
				Toast.makeText(RecentlyViewedDetailActivity.this,
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
	 * View Pager Adapter that allows user to see other Product details by
	 * swipping
	 * 
	 * @author DEVEN
	 * 
	 */
	public class MyFragmentAdapter extends PagerAdapter {
		private Context context;
		private View layout = null;
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

		public MyFragmentAdapter(Context context) {
			this.context = context;
		}

		@Override
		public int getCount() {
			return ApplicationEx.productList.size() * 2;
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			((ViewGroup) container).removeView((View) object);
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == ((View) arg1);
		}

		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			LayoutInflater inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			layout = inflater.inflate(R.layout.recently_offer_detail, null);
			mainLayout = (RelativeLayout) layout.findViewById(R.id.main_layout);

			nameTextView = (TextView) layout.findViewById(R.id.product_title);
			ratingImageView = (ImageView) layout.findViewById(R.id.img_rating);
			thumbnailImageView = (ImageView) layout
					.findViewById(R.id.img_thumbnail);
			msrpTextView = (TextView) layout.findViewById(R.id.msrp);
			salePriceTextView = (TextView) layout.findViewById(R.id.sale_price);
			savingTextView = (TextView) layout.findViewById(R.id.saving);
			shortDescriptionTextView = (TextView) layout
					.findViewById(R.id.description);
			addToCalendarButton = (Button) layout
					.findViewById(R.id.add_to_calendar);
			addToWishListButton = (Button) layout
					.findViewById(R.id.add_to_wish_list);
			buyNowButton = (Button) layout.findViewById(R.id.buy_now);

			addToCalendarButton.setOnClickListener(onClickListener);
			addToWishListButton.setOnClickListener(onClickListener);
			buyNowButton.setOnClickListener(onClickListener);

			if (position == ApplicationEx.productList.size())
				product = ApplicationEx.productList
						.get(ApplicationEx.productList.size() - 1);
			else if (position > ApplicationEx.productList.size())
				product = ApplicationEx.productList.get(1);
			else
				product = ApplicationEx.productList.get(position);

			if (product != null) {
				nameTextView.setText(product.getName());
				if (ApplicationEx.productImages.containsKey(product
						.getThumbnailImage())
						&& ApplicationEx.productImages.get(product
								.getThumbnailImage()) != null) {

					thumbnailImageView
							.setImageDrawable(ApplicationEx.productImages
									.get(product.getThumbnailImage()));
				} else {
					thumbnailImageView.setImageResource(R.drawable.noimage);
				}

				if (product.getCustomerRating() > 0
						&& product.getCustomerRating() < 1.1) {
					ratingImageView.setImageDrawable(getResources()
							.getDrawable(R.drawable.one_star));
				} else if (product.getCustomerRating() > 1
						&& product.getCustomerRating() < 2.1) {
					ratingImageView.setImageDrawable(getResources()
							.getDrawable(R.drawable.two_star));
				} else if (product.getCustomerRating() > 2
						&& product.getCustomerRating() < 3.1) {
					ratingImageView.setImageDrawable(getResources()
							.getDrawable(R.drawable.three_star));
				} else if (product.getCustomerRating() > 3
						&& product.getCustomerRating() < 4.1) {
					ratingImageView.setImageDrawable(getResources()
							.getDrawable(R.drawable.four_star));
				} else if (product.getCustomerRating() > 4
						&& product.getCustomerRating() < 5.1) {
					ratingImageView.setImageDrawable(getResources()
							.getDrawable(R.drawable.five_star));
				} else {
					ratingImageView.setImageDrawable(getResources()
							.getDrawable(R.drawable.four_star));
				}

				msrpTextView.setText(PriceKingUtils.formatCurrencyUSD(product
						.getMsrp()));

				shortDescriptionTextView.setText(product.getShortDescription());

				String salePriceText = "<font color=#000000>Deal Price: </font> <font color=#990000>"
						+ PriceKingUtils.formatCurrencyUSD(product
								.getSalePrice()) + "</font>";

				salePriceTextView.setText(Html.fromHtml(salePriceText));

				if (product.getMsrp() == 0.0) {
					savingTextView.setVisibility(View.GONE);
				} else {
					savingTextView.setVisibility(View.VISIBLE);
					String savingText = "<font color=#000000>You Save: </font> <font color=#990000>"
							+ PriceKingUtils.calculateSavings(
									product.getMsrp(), product.getSalePrice())
							+ "</font>";
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

			container.addView(layout);
			return layout;
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
								db.addToWishList(product,
										ApplicationEx.userName);
								addToWishListButton
										.setText("Added to wish list");
							} else {
								PriceKingUtils.showToast(
										RecentlyViewedDetailActivity.this,
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
						Calendar cal = Calendar.getInstance(TimeZone
								.getDefault());
						DatePickerDialog datePicker = new DatePickerDialog(
								RecentlyViewedDetailActivity.this,
								datePickerListener, cal.get(Calendar.YEAR),
								cal.get(Calendar.MONTH),
								cal.get(Calendar.DAY_OF_MONTH));
						datePicker.setCancelable(false);
						datePicker.setTitle("Select the date");
						datePicker.show();

					} catch (Exception e) {
						PriceKingUtils.showToast(
								RecentlyViewedDetailActivity.this,
								"An error occured while showing Date Picker\n\n"
										+ " Error Details:\n" + e.toString());
					}

					break;
				default:
					break;
				}

			}
		};

	}

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

	@Override
	public boolean onDown(MotionEvent e) {
		// TODO Auto-generated method stub
		return false;
	}

	/**
	 * method to identify gesture (left or right swipe)
	 */
	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {
		try {
			// if (Math.abs(e1.getY() - e2.getY()) > SWIPE_MAX_OFF_PATH) {
			//
			// return false;
			// }

			// // right to left swipe
			// if (e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE
			// && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY
			// && currentPage == (ApplicationEx.productList.size() - 1)) {
			//
			// mPager.setCurrentItem(0, false);
			// return true;
			// } else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE
			// && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY
			// && currentPage == 0) {
			// mPager.setCurrentItem(ApplicationEx.productList.size() - 1,
			// false);
			// return true;
			// }
		} catch (Exception e) {
			// e.printStackTrace();
		}

		return false;
	}

	@Override
	public void onLongPress(MotionEvent e) {

	}

	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
			float distanceY) {
		return false;
	}

	@Override
	public void onShowPress(MotionEvent e) {

	}

	@Override
	public boolean onSingleTapUp(MotionEvent e) {
		return false;
	}

}