package com.capgemini.sample;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;

import com.capgemini.fetcher.ImageCache.ImageCacheParams;
import com.capgemini.fetcher.ImageFetcher;

/**
 * Class is defined as a fragment activity since the Image Fetcher needs a
 * fragment manager for linking. Probably we can change this to default activity
 * 
 * @author jadave
 * 
 */
public class ImageDownloadSample extends FragmentActivity {

	// directory which will be created for storing the images of this activity
	private static final String IMAGE_CACHE_DIR = "imagefetcher_sample";
	private ImageFetcher imageFetcher;
	private int imageWidth;
	private int imageHeight;

	// Hardcoded urls from one of the project
	private String[] imageArr = { "http://dl.dropbox.com/u/100393608/200x120_liten.png", "http://dl.dropbox.com/u/100393608/App/200x120_X2_Hydraulic_press.png", "http://dl.dropbox.com/u/100393608/App/200x120_X2_Uddeholm_Globe.png",
			"http://dl.dropbox.com/u/100393608/App/200x120_X2_Kretskort_F8R0795.png", "http://dl.dropbox.com/u/100393608/App/200x120_X2_Car-p4-5-cmyk.png", "http://dl.dropbox.com/u/100393608/App/200x120_X2_WashingMashine_Elektrolux.png",
			"http://dl.dropbox.com/u/100393608/App/200x120_X2_packaging_tupperware.png", "http://dl.dropbox.com/u/100393608/App/200x120_X2_Customer_meeting.png", "http://dl.dropbox.com/u/100393608/App/200x120_X2_milling.png",
			"http://dl.dropbox.com/u/100393608/App/200x120_X2_Heat-treatment.png", "http://dl.dropbox.com/u/100393608/App/200x120_X2_Lasertrad_Nimax_Stavax_hog.png", "http://dl.dropbox.com/u/100393608/App/200x120_X2_Markning-7548.png",
			"http://dl.dropbox.com/u/100393608/App/200x120_X2_Pitting_fri.png", "http://dl.dropbox.com/u/100393608/App/200x120_X2_Car_Seat.png", "http://dl.dropbox.com/u/100393608/App/200x120_X2_Headlight_B.png", "http://dl.dropbox.com/u/100393608/App/200x120_X2_Hot-wear2.png",
			"http://dl.dropbox.com/u/100393608/App/200x120_X2_Galling_failure.png", "http://dl.dropbox.com/u/100393608/App/200x120_X2_Chipping_failure.png", "http://dl.dropbox.com/u/100393608/App/200x120_X2_Heat-checking2.png",
			"http://dl.dropbox.com/u/100393608/App/200x120_X2_Plasticdeformation_failure.png", "http://dl.dropbox.com/u/100393608/App/200x120_X2_Stainless_Concept_Plant.png", "http://dl.dropbox.com/u/100393608/200x120_Research_Test-sample.png",
			"http://dl.dropbox.com/u/100393608/App/200x120_X2_UddeholmAcademy_2.png", "http://dl.dropbox.com/u/100393608/App/200x120_X2_college-1.png", "http://dl.dropbox.com/u/100393608/App/200x120_X2_Heritage.png",
			"http://dl.dropbox.com/u/100393608/App/200x120_X2_Herrgarden-2--12.png" };

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home_page);
		imageWidth = getResources().getDimensionPixelSize(R.dimen.image_width);
		imageHeight = getResources().getDimensionPixelSize(R.dimen.image_height);
		// Setting up cache directory
		ImageCacheParams cacheParams = new ImageCacheParams(this, IMAGE_CACHE_DIR);
		// Set memory cache to 25% of mem class
		cacheParams.setMemCacheSizePercent(this, 0.25f);
		// Setting up image fetcher
		imageFetcher = new ImageFetcher(this, imageWidth, imageHeight);
		imageFetcher.setLoadingImage(R.drawable.noimage);
		imageFetcher.addImageCache(getSupportFragmentManager(), cacheParams);

		ListView listView = (ListView) findViewById(R.id.list);
		listView.setAdapter(new ImageArrayAdapter());
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_home_page, menu);
		return true;
	}

	@Override
	public void onResume() {
		super.onResume();
		// In case the user comes back to your activity set it to false
		imageFetcher.setExitTasksEarly(false);
	}

	@Override
	public void onPause() {
		super.onPause();
		// In case the user moves away from your activity set it to true
		imageFetcher.setExitTasksEarly(true);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		// In case the user closes the activity close the cache
		imageFetcher.closeCache();
	}

	private class ImageArrayAdapter extends BaseAdapter {

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view = convertView;
			ViewHolder viewHolder = null;
			if (view == null) {
				view = LayoutInflater.from(ImageDownloadSample.this).inflate(R.layout.list_item_row, parent, false);
				viewHolder = new ViewHolder();
				viewHolder.imageView = (ImageView) view.findViewById(R.id.image);
			} else {
				viewHolder = (ViewHolder) view.getTag();
			}
			String imageURL = imageArr[position].trim();
			Log.d("ImageDownloadSample.ImageArrayAdapter", "getView" + "imageURL :: " + imageURL);
			// Downloading the actual image
			imageFetcher.loadImage(imageURL, viewHolder.imageView);
			view.setTag(viewHolder);
			return view;
		}

		@Override
		public int getCount() {
			return imageArr.length;
		}

		@Override
		public Object getItem(int position) {
			return imageArr[position];
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		private class ViewHolder {
			ImageView imageView;
		}
	}
}
