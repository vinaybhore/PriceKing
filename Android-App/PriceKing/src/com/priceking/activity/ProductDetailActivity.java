package com.priceking.activity;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
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
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.priceking.ApplicationEx;
import com.priceking.R;
import com.priceking.entity.Product;

/**
 * Represents the offer in detail. (Integrated with View Pager)
 * 
 * @author DEVEN
 * 
 */
public class ProductDetailActivity extends Activity implements
		OnGestureListener, AnimationListener {

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
	private Animation anim;
	private Animation fade_anim;
	private Button webSiteButton;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.offer);

		ApplicationEx.isFirstLoad = false;

		getActionBar().setHomeButtonEnabled(false);
		/**
		 * whether to show Standard Home Icon or not
		 */
		getActionBar().setDisplayHomeAsUpEnabled(true);

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
		// EasyTracker.getInstance(this).activityStart(this);
	}

	@Override
	protected void onStop() {
		super.onStop();
		/**
		 * Stop Google Analytics Tracking
		 */
		// EasyTracker.getInstance(this).activityStop(this);
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

		case android.R.id.home:
			finish();
			break;
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
	 * View Pager Adapter that allows user to see other Product details by
	 * swipping
	 * 
	 * @author DEVEN
	 * 
	 */
	public class MyFragmentAdapter extends PagerAdapter {
		private Context context;
		private View layout = null;
		private TextView name;
		private TextView shortDescription;
		private TextView msrp;
		private TextView salePrice;
		private TextView customerRating;
		private ImageView thumbnailImageView;
		protected static final float SHAKE_THRESHOLD = 1000;

		public MyFragmentAdapter(Context context) {
			this.context = context;
			/**
			 * load the animation
			 */
			anim = AnimationUtils.loadAnimation(getApplicationContext(),
					R.anim.bounce);
			fade_anim = AnimationUtils.loadAnimation(getApplicationContext(),
					R.anim.fade_in);
			fade_anim.setDuration(1000);

			/**
			 * set animation listener
			 */
			anim.setAnimationListener(ProductDetailActivity.this);
			fade_anim.setAnimationListener(ProductDetailActivity.this);

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
			layout = inflater.inflate(R.layout.offer_detail, null);
			mainLayout = (RelativeLayout) layout.findViewById(R.id.main_layout);

			name = (TextView) layout.findViewById(R.id.title);
			msrp = (TextView) layout.findViewById(R.id.msrp);
			salePrice = (TextView) layout.findViewById(R.id.sale_price);
			customerRating = (TextView) layout.findViewById(R.id.rating);
			thumbnailImageView = (ImageView) layout
					.findViewById(R.id.img_thumbnail);

			webSiteButton = (Button) layout.findViewById(R.id.web_site);
			webSiteButton.setOnClickListener(onClickListener);

			if (position == ApplicationEx.productList.size())
				product = ApplicationEx.productList
						.get(ApplicationEx.productList.size() - 1);
			else if (position > ApplicationEx.productList.size())
				product = ApplicationEx.productList.get(1);
			else
				product = ApplicationEx.productList.get(position);

			if (product != null) {
				name.setText(product.getName());
				msrp.setText("" + product.getMsrp());
				salePrice.setText("" + product.getSalePrice());
				customerRating.setText("" + product.getCustomerRating());
				thumbnailImageView.setImageDrawable(ApplicationEx.images
						.get(product.getThumbnailImage()));

				getActionBar().setTitle("Product Details");
			}

			webSiteButton.setVisibility(View.VISIBLE);
			if (!ApplicationEx.isFirstLoad) {
				mainLayout.setAnimation(fade_anim);
				webSiteButton.setAnimation(anim);
				ApplicationEx.isFirstLoad = true;
			} else {
				mainLayout.setVisibility(View.VISIBLE);
				webSiteButton.setVisibility(View.VISIBLE);
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
				case R.id.web_site:
					intent = new Intent(ProductDetailActivity.this,
							WebViewActivity.class);
					intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					intent.putExtra("url", product.getProductURL());
					startActivity(intent);
					break;
				default:
					break;
				}

			}
		};

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.view.animation.Animation.AnimationListener#onAnimationEnd
	 * (android .view.animation.Animation)
	 */
	@Override
	public void onAnimationEnd(Animation animation) {
		if (mainLayout.getVisibility() == View.INVISIBLE) {
			mainLayout.setVisibility(View.VISIBLE);

		}
		if (webSiteButton.getVisibility() == View.INVISIBLE)
			webSiteButton.setVisibility(View.VISIBLE);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * android.view.animation.Animation.AnimationListener#onAnimationRepeat(
	 * android.view.animation.Animation)
	 */
	@Override
	public void onAnimationRepeat(Animation animation) {

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.view.animation.Animation.AnimationListener#onAnimationStart
	 * (android .view.animation.Animation)
	 */
	@Override
	public void onAnimationStart(Animation animation) {

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
			e.printStackTrace();
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