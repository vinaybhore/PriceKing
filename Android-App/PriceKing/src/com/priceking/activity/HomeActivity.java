package com.priceking.activity;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.ViewGroup.MarginLayoutParams;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.analytics.tracking.android.EasyTracker;
import com.priceking.ApplicationEx;
import com.priceking.R;
import com.priceking.adapter.AdvertisementGalleryAdapter;
import com.priceking.adapter.GridCategoryAdapter;
import com.priceking.data.DatabaseHandler;
import com.priceking.entity.Categories;
import com.priceking.entity.Product;
import com.priceking.services.RetrieveImageService;
import com.priceking.services.RetrieveImageService.RetrieveImageServiceListener;
import com.priceking.services.RetrieveProductService;
import com.priceking.services.RetrieveProductService.RetrieveProductServiceListener;
import com.priceking.services.RetrieveRatingImageService;
import com.priceking.services.RetrieveRatingImageService.RetrieveRatingImageServiceListener;
import com.priceking.utils.PriceKingUtils;
import com.priceking.views.ExpandableHeightGridView;

/**
 * Displays the news of a specific user selected news category
 * 
 * @author DEVEN
 * 
 */
public class HomeActivity extends BaseActivity implements
		RetrieveProductServiceListener, RetrieveImageServiceListener,
		RetrieveRatingImageServiceListener {

	private static final String LOG_TAG = "Home Activity";
	private ProgressDialog pd;
	private String query = "";
	private ImageView audioInputButton;
	private ImageView cameraImageView;
	private AutoCompleteTextView searchEditText;
	private ListView productListView;

	// Voice Input Details
	private static final int REQUEST_CODE = 1234;
	private static final int CAMERA_REQUEST_CODE = 1111;
	private Dialog match_text_dialog;
	private ArrayList<String> matches_text;

	// For Database
	private DatabaseHandler db;
	private int count;
	private int ratingCount;

	// For Advertisements
	private List<Product> advertisementList = new ArrayList<Product>();
	private ExpandableHeightGridView gridView;

	// Autocomplete Text View
	private static final String URL = "http://api.bing.com/osjson.aspx";

	// Soft Keyboard
	InputMethodManager imm;

	private ArrayList<String> autocomplete(String input) {
		ArrayList<String> resultList = null;

		HttpURLConnection conn = null;
		StringBuilder jsonResults = new StringBuilder();
		try {
			StringBuilder sb = new StringBuilder(URL);
			sb.append("?query=" + URLEncoder.encode(input, "utf8"));

			URL url = new URL(sb.toString());
			conn = (HttpURLConnection) url.openConnection();
			InputStreamReader in = new InputStreamReader(conn.getInputStream());

			// Load the results into a StringBuilder
			int read;
			char[] buff = new char[1024];
			while ((read = in.read(buff)) != -1) {
				jsonResults.append(buff, 0, read);
			}
		} catch (MalformedURLException e) {
			Log.e(LOG_TAG, "Error processing Places API URL", e);
			return resultList;
		} catch (IOException e) {
			Log.e(LOG_TAG, "Error connecting to Places API", e);
			return resultList;
		} finally {
			if (conn != null) {
				conn.disconnect();
			}
		}

		try {

			JSONArray jsonArray = new JSONArray(jsonResults.toString());
			System.out.println("********JSON ARRAY********" + jsonArray);
			JSONArray productArray = jsonArray.getJSONArray(1);

			// Extract the Place descriptions from the results
			resultList = new ArrayList<String>(productArray.length());
			for (int i = 0; i < productArray.length(); i++) {
				resultList.add(productArray.getString(i));
			}
		} catch (JSONException e) {
			Log.e(LOG_TAG, "Cannot process JSON results", e);
		}

		return resultList;
	}

	private class PlacesAutoCompleteAdapter extends ArrayAdapter<String>
			implements Filterable {
		private ArrayList<String> resultList;

		public PlacesAutoCompleteAdapter(Context context, int textViewResourceId) {
			super(context, textViewResourceId);
		}

		@Override
		public int getCount() {
			return resultList.size();
		}

		@Override
		public String getItem(int index) {
			return resultList.get(index);
		}

		@Override
		public Filter getFilter() {
			Filter filter = new Filter() {
				@Override
				protected FilterResults performFiltering(CharSequence constraint) {
					FilterResults filterResults = new FilterResults();
					if (constraint != null) {
						// Retrieve the autocomplete results.
						resultList = autocomplete(constraint.toString());

						// Assign the data to the FilterResults
						filterResults.values = resultList;
						filterResults.count = resultList.size();
					}
					return filterResults;
				}

				@Override
				protected void publishResults(CharSequence constraint,
						FilterResults results) {
					if (results != null && results.count > 0) {
						notifyDataSetChanged();
					} else {
						notifyDataSetInvalidated();
					}
				}
			};
			return filter;
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mMenuDrawer.setContentView(R.layout.home);

		try {
			db = new DatabaseHandler(getApplicationContext());
			db.openInternalDB();
			advertisementList = db
					.getproductList(DatabaseHandler.TABLE_ADVERTISEMENT);
			ApplicationEx.advertisementList = advertisementList;

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.close();
		}

		// if (ApplicationEx.isLoggedIn)
		// PriceKingUtils.showToast(HomeActivity.this, "Logged In"
		// + ApplicationEx.userName);
		// else
		// PriceKingUtils.showToast(HomeActivity.this, "Not Logged In...");

		imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

		audioInputButton = (ImageView) findViewById(R.id.audio_input);
		cameraImageView = (ImageView) findViewById(R.id.img_camera);
		gridView = (ExpandableHeightGridView) findViewById(R.id.grid_view);

		searchEditText = (AutoCompleteTextView) findViewById(R.id.et_search_query);
		searchEditText.setAdapter(new PlacesAutoCompleteAdapter(this,
				R.layout.list_item));

		gridView.setExpanded(true);
		// Instance of GridTextAdapter Class
		gridView.setAdapter(new GridCategoryAdapter(this,
				ApplicationEx.categoryList));

		/**
		 * On Click event for Single Gridview Item
		 * */
		gridView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Categories category = (Categories) view.getTag(R.id.offer_id);
				searchCategoryProducts(category.getName());
			}
		});

		Gallery gallery = (Gallery) findViewById(R.id.advertisement_gallery);

		if (advertisementList == null || advertisementList.size() == 0) {
			gallery.setVisibility(View.GONE);
		} else {
			gallery.setVisibility(View.VISIBLE);
			gallery.setSpacing(1);

			/*
			 * A structure describing general information about a display, such
			 * as its size, density, and font scaling.To access the
			 * DisplayMetrics members, initialize an object like this:
			 */
			DisplayMetrics metrics = new DisplayMetrics();
			getWindowManager().getDefaultDisplay().getMetrics(metrics);

			// Converts 14 dip into its equivalent px
			Resources r = getResources();
			float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
					100, r.getDisplayMetrics());

			// set gallery to left side
			MarginLayoutParams mlp = (MarginLayoutParams) gallery
					.getLayoutParams();
			mlp.setMargins((int) -(metrics.widthPixels / 2 + (px / 2)),
					mlp.topMargin, mlp.rightMargin, mlp.bottomMargin);

			AdvertisementGalleryAdapter adapter = new AdvertisementGalleryAdapter(
					HomeActivity.this, advertisementList);
			gallery.setAdapter(adapter);

			// clicklistener for Gallery
			gallery.setOnItemClickListener(new OnItemClickListener() {
				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {
					Product product = (Product) view.getTag(R.id.offer_id);
					ApplicationEx.selectedPosition = position;
					try {
						db = new DatabaseHandler(getApplicationContext());
						db.openInternalDB();
						if (!db.isRecentlyviewed(product.getProductURL()))
							db.addProduct(
									DatabaseHandler.TABLE_RECENTLY_VIEWED,
									product);
					} catch (Exception e) {
						e.printStackTrace();
					} finally {
						db.close();
					}
					Intent intent = new Intent(HomeActivity.this,
							ProductDetailActivity.class);
					intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					ApplicationEx.product = product;
					startActivity(intent);
				}
			});
		}

		searchEditText.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> adapterView, View view,
					int position, long id) {
				// User selected text
				query = (String) adapterView.getItemAtPosition(position);
				searchProducts();
				imm.hideSoftInputFromWindow(searchEditText.getWindowToken(), 0);
			}

		});

		searchEditText.setOnKeyListener(new OnKeyListener() {

			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if (keyCode == KeyEvent.KEYCODE_ENTER
						&& event.getAction() == KeyEvent.ACTION_DOWN) {
					searchProducts();
				}
				return false;
			}
		});

		audioInputButton.setOnClickListener(onClickListener);
		cameraImageView.setOnClickListener(onClickListener);
	}

	private OnClickListener onClickListener = new OnClickListener() {

		@Override
		public void onClick(View view) {
			int id = view.getId();
			Intent intent;
			switch (id) {
			case R.id.audio_input:
				imm.hideSoftInputFromWindow(searchEditText.getWindowToken(), 0);

				if (PriceKingUtils.isConnectionAvailable(HomeActivity.this)) {
					intent = new Intent(
							RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
					intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
							RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
					startActivityForResult(intent, REQUEST_CODE);
				} else {
					Toast.makeText(getApplicationContext(),
							"Plese Connect to Internet", Toast.LENGTH_LONG)
							.show();
				}

				break;

			case R.id.img_camera:
				imm.hideSoftInputFromWindow(searchEditText.getWindowToken(), 0);
				intent = new Intent(HomeActivity.this, CaptureActivity.class);
				startActivityForResult(intent, CAMERA_REQUEST_CODE);
				break;

			default:
				break;
			}

		}
	};

	@Override
	protected void onRestart() {
		searchEditText.setText("");
		super.onRestart();
	}

	/**
	 * Checks if the network connection is available, if yes then hit the web
	 * service call.
	 */
	public void searchCategoryProducts(String myQuery) {
		if (PriceKingUtils.isConnectionAvailable(HomeActivity.this)) {
			query = myQuery;
			getProducts();

		} else {

			PriceKingUtils.showToast(HomeActivity.this, getResources()
					.getString(R.string.network_error));
		}

	}

	/**
	 * Checks if the network connection is available, if yes then hit the web
	 * service call.
	 */
	public void searchProducts() {
		imm.hideSoftInputFromWindow(searchEditText.getWindowToken(), 0);

		if (PriceKingUtils.isConnectionAvailable(HomeActivity.this)) {
			if (!TextUtils.isEmpty(searchEditText.getText().toString())) {
				query = searchEditText.getText().toString();
				getProducts();
			} else {
				PriceKingUtils.showToast(HomeActivity.this, getResources()
						.getString(R.string.enter_valid_product));
			}

		} else {

			PriceKingUtils.showToast(HomeActivity.this, getResources()
					.getString(R.string.network_error));
		}

	}

	/**
	 * Products web service call
	 */
	private void getProducts() {
		count = 0;
		pd = ProgressDialog.show(HomeActivity.this, "", "Loading...");
		RetrieveProductService service = new RetrieveProductService(
				getApplicationContext(), query);
		service.setListener(this);
		ApplicationEx.operationsQueue.execute(service);
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
	 * Handles on item click of a list view
	 */
	private OnItemClickListener onItemClickListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> arg0, View view, int position,
				long arg3) {
			Product product = (Product) view.getTag(R.id.offer_id);
			ApplicationEx.selectedPosition = position;
			Intent intent = new Intent(HomeActivity.this,
					ProductDetailActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			intent.putExtra("product", product);
			startActivity(intent);

		}
	};

	/*
	 * Success Callback (non-Javadoc)
	 * 
	 * @see
	 * com.herald.services.RetrieveEventsService.RetrieveEventsServiceListener
	 * #onRetrieveEventsFinished(java.util.ArrayList)
	 */
	@Override
	public void onRetrieveProductFinished(List<Product> productList) {
		ApplicationEx.productList = productList;
		System.out.println("*****Reached Success*****");

		for (int i = 0; i < productList.size(); i++) {
			Product product = productList.get(i);
			if (!TextUtils.isEmpty(product.getThumbnailImage()))
				getImage(product.getThumbnailImage());
		}

	}

	private void getImage(String imageURL) {
		RetrieveImageService service = new RetrieveImageService(
				HomeActivity.this);
		service.setListener(this);
		service.setImageURL(imageURL);
		ApplicationEx.operationsQueue.execute(service);
	}

	private void getRatingImage(String imageURL) {
		RetrieveRatingImageService service = new RetrieveRatingImageService(
				HomeActivity.this);
		service.setListener(this);
		service.setImageURL(imageURL);
		ApplicationEx.operationsQueue.execute(service);
	}

	/*
	 * Failure Callback (non-Javadoc)
	 * 
	 * @see
	 * com.herald.services.RetrieveEventsService.RetrieveEventsServiceListener
	 * #onRetrieveEventsFailed(int, java.lang.String)
	 */
	@Override
	public void onRetrieveProductFailed(int error, String message) {
		if (pd != null || pd.isShowing())
			pd.dismiss();
		Toast.makeText(HomeActivity.this, message, Toast.LENGTH_SHORT).show();

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
			match_text_dialog = new Dialog(HomeActivity.this);
			match_text_dialog.setContentView(R.layout.dialog_matches_frag);
			match_text_dialog.setTitle("Select Matching Text");
			productListView = (ListView) match_text_dialog
					.findViewById(R.id.list);
			matches_text = data
					.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
			ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
					android.R.layout.simple_list_item_1, matches_text);
			productListView.setAdapter(adapter);
			adapter.notifyDataSetChanged();
			productListView
					.setOnItemClickListener(new AdapterView.OnItemClickListener() {
						@Override
						public void onItemClick(AdapterView<?> parent,
								View view, int position, long id) {

							query = matches_text.get(position);
							getProducts();

							if (match_text_dialog.isShowing())
								match_text_dialog.dismiss();
						}
					});
			match_text_dialog.show();
		} else if (requestCode == CAMERA_REQUEST_CODE
				&& resultCode == RESULT_OK) {
			query = data.getExtras().getString("ocr_result");
			if (query != null) {
				query = query.replaceAll("\\s+", " ").trim();
				getProducts();
			} else {
				PriceKingUtils.showToast(HomeActivity.this,
						"Please try again...");
			}

		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public void onGetImageFinished(RetrieveImageService getImageService,
			Drawable image) {
		count++;
		try {
			db = new DatabaseHandler(HomeActivity.this);
			db.openInternalDB();
			Bitmap bitmap = ((BitmapDrawable) image).getBitmap();
			ByteArrayOutputStream stream = new ByteArrayOutputStream();
			bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
			byte[] thumbnailBlob = stream.toByteArray();
			db.updateProductTable(DatabaseHandler.TABLE_PRODUCT,
					getImageService.getImageURL(), thumbnailBlob);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {

		}

		if (count == ApplicationEx.productList.size()) {
			count = 0;
			db.close();
			ratingCount = 0;
			for (int i = 0; i < ApplicationEx.productList.size(); i++) {
				Product product = ApplicationEx.productList.get(i);
				if (!TextUtils.isEmpty(product.getCustomerRatingImage())) {
					ratingCount++;
					getRatingImage(product.getCustomerRatingImage());
				}
			}

		}

	}

	@Override
	public void onGetImageFailed(RetrieveImageService getImageService,
			String error) {
		if (pd != null || pd.isShowing())
			pd.dismiss();
		PriceKingUtils.showToast(HomeActivity.this, error);
	}

	@Override
	public void onGetRatingImageFinished(
			RetrieveRatingImageService getImageService, Drawable image) {
		count++;
		try {
			db = new DatabaseHandler(HomeActivity.this);
			db.openInternalDB();
			Bitmap bitmap = ((BitmapDrawable) image).getBitmap();
			ByteArrayOutputStream stream = new ByteArrayOutputStream();
			bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
			byte[] thumbnailBlob = stream.toByteArray();
			db.updateProductTable(DatabaseHandler.TABLE_PRODUCT,
					getImageService.getImageURL(), thumbnailBlob);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {

		}

		if (count == ratingCount) {
			count = 0;
			db.close();
			if (pd != null || pd.isShowing())
				pd.dismiss();
			Intent intent = new Intent(HomeActivity.this,
					ProductListActivity.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
		}

	}

	@Override
	public void onGetRatingImageFailed(
			RetrieveRatingImageService getImageService, String error) {
		if (pd != null || pd.isShowing())
			pd.dismiss();
		PriceKingUtils.showToast(HomeActivity.this, error);

	}

}
