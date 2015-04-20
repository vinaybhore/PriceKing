package com.priceking.activity;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.Html;
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
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.analytics.tracking.android.EasyTracker;
import com.priceking.ApplicationEx;
import com.priceking.R;
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
				default:
					break;
				}

			}
		};

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
			if (Math.abs(e1.getY() - e2.getY()) > SWIPE_MAX_OFF_PATH) {

				return false;
			}

			// right to left swipe
			if (e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE
					&& Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY
					&& currentPage == (ApplicationEx.productList.size() - 1)) {

				mPager.setCurrentItem(0, false);
				return true;
			} else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE
					&& Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY
					&& currentPage == 0) {
				mPager.setCurrentItem(ApplicationEx.productList.size() - 1,
						false);
				return true;
			}
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