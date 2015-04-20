package com.priceking.activity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.analytics.tracking.android.EasyTracker;
import com.priceking.ApplicationEx;
import com.priceking.R;
import com.priceking.adapter.ProductListAdapter;
import com.priceking.data.DatabaseHandler;
import com.priceking.entity.Product;
import com.priceking.utils.PriceKingUtils;

/**
 * Displays list of Products
 * 
 * @author DEVEN
 * 
 */
public class ProductListActivity extends BaseActivity {

	private static final String LOG_TAG = "Product List Activity";
	private ListView productListView;
	private ProductListAdapter productListAdapter;
	private List<Product> productList = new ArrayList<Product>();
	private ProgressDialog pd;
	private TextView noDataTextView;
	private DatabaseHandler db;
	private Spinner filterSpinner;
	private TextView resultTextView;
	private RelativeLayout headerLayout;
	private ImageView viewMode;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mMenuDrawer.setContentView(R.layout.offer_list);
		try {
			db = new DatabaseHandler(getApplicationContext());
			db.openInternalDB();
			productList = db.getproductList(DatabaseHandler.TABLE_PRODUCT);
			ApplicationEx.productList = productList;

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.close();
		}

		noDataTextView = (TextView) findViewById(R.id.no_data);
		productListView = (ListView) findViewById(R.id.product_list_view);
		filterSpinner = (Spinner) findViewById(R.id.filter_spinner);
		resultTextView = (TextView) findViewById(R.id.result_view);
		headerLayout = (RelativeLayout) findViewById(R.id.header_view);
		viewMode = (ImageView) findViewById(R.id.view_icon);

		ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, getResources()
						.getStringArray(R.array.filter_options));
		dataAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		filterSpinner.setAdapter(dataAdapter);

		filterSpinner.setOnItemSelectedListener(onItemSelectedListener);

		if (productList == null || productList.size() == 0) {
			productListView.setVisibility(View.GONE);
			headerLayout.setVisibility(View.GONE);
			noDataTextView.setVisibility(View.VISIBLE);
		} else {
			headerLayout.setVisibility(View.VISIBLE);
			productListView.setVisibility(View.VISIBLE);
			noDataTextView.setVisibility(View.GONE);
		}

		resultTextView.setText("Top " + productList.size() + " Results");
		Collections.sort(productList, new PriceKingUtils.MSRPComparator());
		productListView.setDrawSelectorOnTop(true);
		viewMode.setOnClickListener(onClickListener);
		productListAdapter = new ProductListAdapter(productList, this, "list");
		productListView.setAdapter(productListAdapter);
		productListView.setOnItemClickListener(onItemClickListener);
	}

	private OnItemSelectedListener onItemSelectedListener = new OnItemSelectedListener() {

		@Override
		public void onItemSelected(AdapterView<?> parent, View view,
				int position, long id) {

			if (parent.getItemAtPosition(position).toString()
					.equalsIgnoreCase("MSRP")) {
				Collections.sort(productList,
						new PriceKingUtils.MSRPComparator());
				productListAdapter.setProductList(productList);
				productListAdapter.notifyDataSetChanged();

			} else if (parent.getItemAtPosition(position).toString()
					.equalsIgnoreCase("Sale Price")) {
				Collections.sort(productList,
						new PriceKingUtils.SalePriceComparator());
				productListAdapter.setProductList(productList);
				productListAdapter.notifyDataSetChanged();

			} else if (parent.getItemAtPosition(position).toString()
					.equalsIgnoreCase("Customer Rating")) {
				Collections.sort(productList,
						new PriceKingUtils.RatingComparator());
				productListAdapter.setProductList(productList);
				productListAdapter.notifyDataSetChanged();

			}
		}

		@Override
		public void onNothingSelected(AdapterView<?> parent) {
			// TODO Auto-generated method stub

		}
	};

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

	private OnClickListener onClickListener = new OnClickListener() {

		@Override
		public void onClick(View view) {
			int id = view.getId();
			switch (id) {
			case R.id.view_icon:
				if (viewMode.getTag().equals("list")) {
					viewMode.setImageDrawable(getResources().getDrawable(
							R.drawable.grid_icon));
					viewMode.setTag("grid");
					productListAdapter = new ProductListAdapter(productList,
							ProductListActivity.this, "grid");
					productListView.setAdapter(productListAdapter);
					productListView.setOnItemClickListener(onItemClickListener);

				} else {
					viewMode.setTag("list");
					viewMode.setImageDrawable(getResources().getDrawable(
							R.drawable.list_icon));
					productListAdapter = new ProductListAdapter(productList,
							ProductListActivity.this, "list");
					productListView.setAdapter(productListAdapter);
					productListView.setOnItemClickListener(onItemClickListener);
				}

				break;

			default:
				break;
			}

		}
	};

	/**
	 * Handles on item click of a list view
	 */
	private OnItemClickListener onItemClickListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> arg0, View view, int position,
				long arg3) {
			Product product = (Product) view.getTag(R.id.offer_id);
			ApplicationEx.selectedPosition = position;
			try {
				db = new DatabaseHandler(getApplicationContext());
				db.openInternalDB();
				if (!db.isRecentlyviewed(product.getProductURL()))
					db.addProduct(DatabaseHandler.TABLE_RECENTLY_VIEWED,
							product);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				db.close();
			}
			Intent intent = new Intent(ProductListActivity.this,
					ProductDetailActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			ApplicationEx.product = product;
			startActivity(intent);

		}
	};

}
