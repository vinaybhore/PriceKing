package com.priceking.activity;

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

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.priceking.ApplicationEx;
import com.priceking.R;
import com.priceking.adapter.ProductListAdapter;
import com.priceking.entity.Product;
import com.priceking.services.RetrieveImageService;
import com.priceking.services.RetrieveImageService.RetrieveImageServiceListener;
import com.priceking.services.RetrieveProductService;
import com.priceking.services.RetrieveProductService.RetrieveProductServiceListener;
import com.priceking.utils.PriceKingUtils;

/**
 * Displays the news of a specific user selected news category
 * 
 * @author DEVEN
 * 
 */
public class ProductListActivity extends Activity implements
		RetrieveProductServiceListener, RetrieveImageServiceListener {

	private static final String LOG_TAG = "Product List Activity";
	private ListView productListView;
	private ProductListAdapter productListAdapter;
	private List<Product> productList = new ArrayList<Product>();
	private ProgressDialog pd;
	private String query = "";
	private TextView noDataTextView;
	private EditText searchEditText;
	private Button searchButton;
	private ImageButton audioInputButton;

	// Voice Input Details
	private static final int REQUEST_CODE = 1234;
	private Dialog match_text_dialog;
	private ArrayList<String> matches_text;

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
		setContentView(R.layout.offer_list);

		imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

		noDataTextView = (TextView) findViewById(R.id.no_data);
		searchEditText = (EditText) findViewById(R.id.et_search_query);
		searchButton = (Button) findViewById(R.id.btn_search);
		audioInputButton = (ImageButton) findViewById(R.id.audio_input);

		searchButton.setOnClickListener(onClickListener);
		audioInputButton.setOnClickListener(onClickListener);

		AutoCompleteTextView autoCompView = (AutoCompleteTextView) findViewById(R.id.et_search_query);
		autoCompView.setAdapter(new PlacesAutoCompleteAdapter(this,
				R.layout.list_item));
		autoCompView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> adapterView, View view,
					int position, long id) {
				// User selected text
				// String str = (String)
				// adapterView.getItemAtPosition(position);
				imm.hideSoftInputFromWindow(searchEditText.getWindowToken(), 0);
			}

		});

		autoCompView.setOnKeyListener(new OnKeyListener() {

			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if (keyCode == KeyEvent.KEYCODE_ENTER
						&& event.getAction() == KeyEvent.ACTION_DOWN) {
					searchProducts();
				}
				return false;
			}
		});

		// query = getIntent().getExtras().getString("query");
		// getActionBar().setTitle(query);
		getActionBar().setHomeButtonEnabled(false);
		/**
		 * whether to show Standard Home Icon or not
		 */
		getActionBar().setDisplayHomeAsUpEnabled(true);

		productListView = (ListView) findViewById(R.id.product_list_view);
		productListAdapter = new ProductListAdapter(productList, this);
		productListView.setAdapter(productListAdapter);

		productListView.setOnItemClickListener(onItemClickListener);
	}

	private OnClickListener onClickListener = new OnClickListener() {

		@Override
		public void onClick(View view) {
			int id = view.getId();
			switch (id) {

			case R.id.btn_search:
				searchProducts();
				break;

			case R.id.audio_input:

				imm.hideSoftInputFromWindow(searchEditText.getWindowToken(), 0);

				if (PriceKingUtils
						.isConnectionAvailable(ProductListActivity.this)) {
					Intent intent = new Intent(
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

			default:
				break;
			}

		}
	};

	/**
	 * Checks if the network connection is available, if yes then hit the web
	 * service call.
	 */
	public void searchProducts() {
		imm.hideSoftInputFromWindow(searchEditText.getWindowToken(), 0);

		if (PriceKingUtils.isConnectionAvailable(ProductListActivity.this)) {
			if (!TextUtils.isEmpty(searchEditText.getText().toString())) {
				query = searchEditText.getText().toString();
				pd = ProgressDialog.show(ProductListActivity.this, "",
						"Loading...");
				getProducts();
			} else {
				PriceKingUtils.showToast(ProductListActivity.this,
						getResources().getString(R.string.enter_valid_product));
			}

		} else {

			noDataTextView.setVisibility(View.VISIBLE);
			productListView.setVisibility(View.GONE);
			PriceKingUtils.showToast(ProductListActivity.this, getResources()
					.getString(R.string.network_error));
		}

	}

	/**
	 * Products web service call
	 */
	private void getProducts() {
		RetrieveProductService service = new RetrieveProductService(
				getApplicationContext(), query);
		service.setListener(this);
		ApplicationEx.operationsQueue.execute(service);
	}

	/**
	 * Handles back press
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {

		case android.R.id.home:
			finish();
			break;
		default:
			break;
		}
		return super.onOptionsItemSelected(item);
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
	 * Handles on item click of a list view
	 */
	private OnItemClickListener onItemClickListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> arg0, View view, int position,
				long arg3) {
			Product product = (Product) view.getTag(R.id.offer_id);
			ApplicationEx.selectedPosition = position;
			Intent intent = new Intent(ProductListActivity.this,
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
		productListAdapter.setProductList(productList);
		ApplicationEx.productList = productList;
		if (productList.size() > 0) {
			noDataTextView.setVisibility(View.GONE);
			productListView.setVisibility(View.VISIBLE);
		} else {
			noDataTextView.setVisibility(View.VISIBLE);
			productListView.setVisibility(View.GONE);
		}
		System.out.println("*****Reached Success*****");

		if (pd != null || pd.isShowing())
			pd.dismiss();

		for (int i = 0; i < productList.size(); i++) {
			Product product = productList.get(i);
			getImage(product.getThumbnailImage());
		}
	}

	private void getImage(String imageURL) {
		if (!ApplicationEx.images.containsKey(imageURL)) {
			ApplicationEx.images.put(imageURL, null);
			RetrieveImageService service = new RetrieveImageService(
					ProductListActivity.this);
			service.setListener(this);
			service.setImageURL(imageURL);
			ApplicationEx.operationsQueue.execute(service);
		}
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
		noDataTextView.setVisibility(View.VISIBLE);
		productListView.setVisibility(View.GONE);
		Toast.makeText(ProductListActivity.this, message, Toast.LENGTH_SHORT)
				.show();

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
			match_text_dialog = new Dialog(ProductListActivity.this);
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
							pd = ProgressDialog.show(ProductListActivity.this,
									"", "Loading...");
							getProducts();

							if (match_text_dialog.isShowing())
								match_text_dialog.dismiss();
						}
					});
			match_text_dialog.show();
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public void onGetImageFinished(RetrieveImageService getImageService,
			Drawable image) {
		if (pd != null || pd.isShowing())
			pd.dismiss();
		System.out.println("***************Image***************" + image);
		if (image != null) {
			ApplicationEx.images.put(getImageService.getImageURL(), image);

			ImageView imageView = (ImageView) productListView
					.findViewWithTag(getImageService.getImageURL());

			if (imageView != null)
				imageView.setImageDrawable(image);
		}

		productListAdapter.notifyDataSetChanged();

	}

	@Override
	public void onGetImageFailed(RetrieveImageService getImageService,
			String error) {
		if (pd != null || pd.isShowing())
			pd.dismiss();
		PriceKingUtils.showToast(ProductListActivity.this, error);
		ApplicationEx.images.remove(getImageService.getImageURL());

	}

}
