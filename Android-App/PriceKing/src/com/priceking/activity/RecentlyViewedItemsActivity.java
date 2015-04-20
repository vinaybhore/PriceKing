package com.priceking.activity;

import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.google.analytics.tracking.android.EasyTracker;
import com.priceking.ApplicationEx;
import com.priceking.R;
import com.priceking.adapter.ProductListAdapter;
import com.priceking.data.DatabaseHandler;
import com.priceking.entity.Product;
import com.priceking.utils.PriceKingUtils;

/**
 * @author devpawar
 */
public class RecentlyViewedItemsActivity extends BaseActivity {
	private ListView productListView;
	private ProductListAdapter productListAdapter;
	private List<Product> productList = new ArrayList<Product>();
	private TextView noDataTextView;
	private DatabaseHandler db;
	private Product product;

	/** Called when the activity is first created. */

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mMenuDrawer.setContentView(R.layout.recently_viewed_list);

		getActionBar().setTitle("Recently Viewed Items");

		try {
			db = new DatabaseHandler(getApplicationContext());
			db.openInternalDB();
			productList = db
					.getproductList(DatabaseHandler.TABLE_RECENTLY_VIEWED);
			ApplicationEx.productList = productList;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.close();
		}

		noDataTextView = (TextView) findViewById(R.id.no_data);
		productListView = (ListView) findViewById(R.id.product_list_view);

		checkViewStatus();

		productListAdapter = new ProductListAdapter(productList, this, "list");
		productListView.setAdapter(productListAdapter);
		productListView.setDrawSelectorOnTop(true);
		productListView.setOnItemClickListener(onItemClickListener);
		productListView.setOnItemLongClickListener(onItemLongClickListener);

	}

	public void checkViewStatus() {
		if (productList == null || productList.size() == 0) {
			productListView.setVisibility(View.GONE);
			noDataTextView.setVisibility(View.VISIBLE);
		} else {
			productListView.setVisibility(View.VISIBLE);
			noDataTextView.setVisibility(View.GONE);
		}
	}

	/**
	 * Handles Long click listener of recently viewed items to delete a single
	 * item
	 */
	private OnItemLongClickListener onItemLongClickListener = new OnItemLongClickListener() {

		@Override
		public boolean onItemLongClick(AdapterView<?> parent, View view,
				int position, long id) {
			product = (Product) view.getTag(R.id.offer_id);
			AlertDialog.Builder dialog = new AlertDialog.Builder(
					RecentlyViewedItemsActivity.this);
			dialog.setTitle("Delete Item");
			dialog.setIcon(R.drawable.delete_all);
			dialog.setMessage("Are you sure you want to delete this item?");
			dialog.setCancelable(false);
			dialog.setPositiveButton(android.R.string.ok,
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,
								int whichButton) {
							try {
								db = new DatabaseHandler(
										getApplicationContext());
								db.openInternalDB();
								db.deleteRow(
										DatabaseHandler.TABLE_RECENTLY_VIEWED,
										product.getProductURL());
								productList = db
										.getproductList(DatabaseHandler.TABLE_RECENTLY_VIEWED);
								ApplicationEx.productList = productList;
								productListAdapter.setProductList(productList);
								productListAdapter.notifyDataSetChanged();
								checkViewStatus();
								PriceKingUtils.showToast(
										RecentlyViewedItemsActivity.this,
										"Item Deleted...");
							} catch (Exception e) {
								e.printStackTrace();
							} finally {
								db.close();
							}
						}
					});
			dialog.setNegativeButton(android.R.string.no,
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,
								int whichButton) {
						}
					});
			dialog.show();

			return true;
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
			Intent intent = new Intent(RecentlyViewedItemsActivity.this,
					RecentlyViewedDetailActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);

		}
	};

	/**
	 * Handles Menu Clicks
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {
		case R.id.delete_all_option:
			if (productList == null || productList.size() == 0) {
				checkViewStatus();
			} else {
				AlertDialog.Builder dialog = new AlertDialog.Builder(
						RecentlyViewedItemsActivity.this);
				dialog.setTitle("Delete All Products");
				dialog.setMessage("Are you sure you want to delete all recently viewed items?");
				dialog.setCancelable(false);
				dialog.setPositiveButton(android.R.string.ok,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int whichButton) {
								db = new DatabaseHandler(
										getApplicationContext());
								db.openInternalDB();
								db.deleteAllTableEntries(DatabaseHandler.TABLE_RECENTLY_VIEWED);
								productList = db
										.getproductList(DatabaseHandler.TABLE_RECENTLY_VIEWED);
								productListAdapter.setProductList(productList);
								productListAdapter.notifyDataSetChanged();
								checkViewStatus();
							}
						});
				dialog.setNegativeButton(android.R.string.no,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int whichButton) {
							}
						});
				dialog.show();
			}
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
		inflater.inflate(R.menu.delete_all, menu);
		return true;
	}

	@Override
	protected void onStart() {
		super.onStart();
		/**
		 * Start Google Analytics Tracking
		 */
		EasyTracker.getInstance(this).activityStart(this);
	}

	// @Override
	protected void onStop() {
		super.onStop();
		/**
		 * Stop Google Analytics Tracking
		 */
		EasyTracker.getInstance(this).activityStop(this);
	}

}