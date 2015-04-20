package com.priceking.activity;

import java.io.FileInputStream;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Shader.TileMode;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.analytics.tracking.android.EasyTracker;
import com.priceking.R;
import com.priceking.views.CoverFlow;

public class ContactUsActivity extends BaseActivity {

	/**
	 * @author devpawar
	 */

	/** Called when the activity is first created. */

	private CoverFlow coverFlow;
	private TextView categoryTextView;
	private TextView emailIdTextView;
	private Button linkedinButton;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mMenuDrawer.setContentView(R.layout.contact_us);

		getActionBar().setTitle("Contact Us");

		coverFlow = new CoverFlow(this);
		coverFlow = (CoverFlow) findViewById(R.id.cf);
		coverFlow.setAdapter(new ImageAdapter(this));
		categoryTextView = (TextView) findViewById(R.id.name);
		emailIdTextView = (TextView) findViewById(R.id.email);
		linkedinButton = (Button) findViewById(R.id.linkedin);

		emailIdTextView.setOnClickListener(onClickListener);
		linkedinButton.setOnClickListener(onClickListener);

		ImageAdapter coverImageAdapter = new ImageAdapter(this);

		// coverImageAdapter.createReflectedImages();

		coverFlow.setAdapter(coverImageAdapter);
		coverFlow.setSpacing(-55);
		coverFlow.setSelection(0, true);
		coverFlow.setAnimationDuration(1000);
		coverFlow.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int position, long arg3) {

			}
		});
		categoryTextView.setText("Deven Pawar");
		setEmail("devenpawar18@gmail.com");
		linkedinButton.setTag("http://www.linkedin.com/in/devenpawar");

		coverFlow.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int position, long arg3) {
				switch (position) {
				case 0:
					categoryTextView.setText("Deven Pawar");
					setEmail("devenpawar18@gmail.com");
					linkedinButton
							.setTag("http://www.linkedin.com/in/devenpawar");
					break;
				case 1:
					categoryTextView.setText("Hardik Joshi");
					setEmail("hardikjoshi90@gmail.com");
					linkedinButton
							.setTag("https://www.linkedin.com/in/hardikjoshi9");
					break;
				case 2:
					categoryTextView.setText("Naiya Shah");
					setEmail("shah.naiya8291@gmail.com");
					linkedinButton
							.setTag("https://www.linkedin.com/in/naiyashah");
					break;
				case 3:
					categoryTextView.setText("Vinay Bhore");
					setEmail("vinay.bhore@sjsu.edu");
					linkedinButton
							.setTag("https://www.linkedin.com/in/vinaybhore");
					break;
				default:
					break;
				}
			}

			public void onNothingSelected(AdapterView<?> arg0) {

			}
		});

	}

	// Set Email Ids in TextView
	public void setEmail(String emailID) {
		SpannableString content = new SpannableString(emailID);
		content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
		emailIdTextView.setText(content);

	}

	// View LinkedIn Profile of the selected member
	public void viewLinkedInProfile(String url) {
		Intent i = new Intent(Intent.ACTION_VIEW);
		i.setData(Uri.parse(url));
		startActivity(i);
	}

	private OnClickListener onClickListener = new OnClickListener() {

		@Override
		public void onClick(View view) {
			int id = view.getId();
			switch (id) {
			case R.id.email:
				sendEmail(emailIdTextView.getText().toString());
				break;
			case R.id.linkedin:
				viewLinkedInProfile(linkedinButton.getTag().toString());
				break;

			default:
				break;
			}

		}
	};

	/**
	 * Contact Us through Email
	 */
	public void sendEmail(String email) {
		Intent i = new Intent(Intent.ACTION_SEND);
		i.setType("message/rfc822");
		i.putExtra(Intent.EXTRA_EMAIL, new String[] { email });
		i.putExtra(Intent.EXTRA_SUBJECT, "Feedback Regarding PriceKing");
		try {
			startActivity(Intent.createChooser(i, "Send mail..."));
		} catch (android.content.ActivityNotFoundException ex) {
			Toast.makeText(ContactUsActivity.this,
					"There are no email clients installed.", Toast.LENGTH_SHORT)
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

	public class ImageAdapter extends BaseAdapter {
		int mGalleryItemBackground;
		private Context mContext;

		private FileInputStream fis;

		private Integer[] mImageIds = { R.drawable.deven, R.drawable.hardik,
				R.drawable.naiya, R.drawable.vinay };

		private ImageView[] mImages;

		public ImageAdapter(Context c) {
			mContext = c;
			mImages = new ImageView[mImageIds.length];
		}

		public Bitmap createReflectedImages(Integer integer) {
			// The gap we want between the reflection and the original image
			final int reflectionGap = 0;
			Bitmap bitmapWithReflection = null;

			// int index = 0;
			int imageId = integer;
			Bitmap originalImage = BitmapFactory.decodeResource(getResources(),
					imageId);
			int width = originalImage.getWidth();
			int height = originalImage.getHeight();

			// This will not scale but will flip on the Y axis
			Matrix matrix = new Matrix();
			matrix.preScale(1, -1);

			// Create a Bitmap with the flip matrix applied to it.
			// We only want the bottom half of the image
			Bitmap reflectionImage = Bitmap.createBitmap(originalImage, 0,
					height / 2, width, height / 2, matrix, false);

			// Create a new bitmap with same width but taller to fit
			// reflection
			bitmapWithReflection = Bitmap.createBitmap(width,
					(height + height / 2), Config.ARGB_8888);

			// Create a new Canvas with the bitmap that's big enough for
			// the image plus gap plus reflection
			Canvas canvas = new Canvas(bitmapWithReflection);
			// Draw in the original image
			canvas.drawBitmap(originalImage, 0, 0, null);
			// Draw in the gap
			Paint deafaultPaint = new Paint();
			canvas.drawRect(0, height, width, height + reflectionGap,
					deafaultPaint);
			// Draw in the reflection
			canvas.drawBitmap(reflectionImage, 0, height + reflectionGap, null);

			// Create a shader that is a linear gradient that covers the
			// reflection
			Paint paint = new Paint();
			LinearGradient shader = new LinearGradient(0,
					originalImage.getHeight(), 0,
					bitmapWithReflection.getHeight() + reflectionGap,
					0x70ffffff, 0x00ffffff, TileMode.CLAMP);
			// Set the paint to use this shader (linear gradient)
			paint.setShader(shader);
			// Set the Transfer mode to be porter duff and destination in
			paint.setXfermode(new PorterDuffXfermode(Mode.DST_IN));
			// Draw a rectangle using the paint with our linear gradient
			canvas.drawRect(0, height, width, bitmapWithReflection.getHeight()
					+ reflectionGap, paint);
			return bitmapWithReflection;
		}

		public int getCount() {
			return mImageIds.length;
		}

		public Object getItem(int position) {
			return position;
		}

		public long getItemId(int position) {
			return position;
		}

		public View getView(int position, View convertView, ViewGroup parent) {

			// Use this code if you want to load from resources
			ImageView i = new ImageView(mContext);
			Bitmap img = createReflectedImages(mImageIds[position]);
			i.setImageBitmap(img);
			i.setLayoutParams(new CoverFlow.LayoutParams(500, 500));
			i.setScaleType(ImageView.ScaleType.FIT_XY);

			img = null;
			System.gc();

			// Make sure we set anti-aliasing otherwise we get jaggies
			BitmapDrawable drawable = (BitmapDrawable) i.getDrawable();
			drawable.setAntiAlias(true);
			return i;

			// return mImages[position];
		}

		/**
		 * Returns the size (0.0f to 1.0f) of the views depending on the
		 * 'offset' to the center.
		 */
		public float getScale(boolean focused, int offset) {
			/* Formula: 1 / (2 ^ offset) */
			return Math.max(0, 1.0f / (float) Math.pow(2, Math.abs(offset)));
		}

	}
}