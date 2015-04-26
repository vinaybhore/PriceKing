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
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.analytics.tracking.android.EasyTracker;
import com.priceking.ApplicationEx;
import com.priceking.R;
import com.priceking.adapter.WishListAdapter;
import com.priceking.data.DatabaseHandler;
import com.priceking.entity.Product;
import com.priceking.utils.PriceKingUtils;
import com.priceking.views.DragListener;
import com.priceking.views.DragNDropListView;
import com.priceking.views.DropListener;
import com.priceking.views.RemoveListener;

/**
 * @author devpawar
 */

/** Called when the activity is first created. */

public class WishListActivity extends BaseActivity {

	/**
	 * Used if Signed In
	 */
	private RelativeLayout wishListLayout;
	private ListView wishListView;
	private WishListAdapter wishListAdapter;
	private List<Product> wishList = new ArrayList<Product>();
	private TextView noDataTextView;
	private DatabaseHandler db;
	private Product product;

	/**
	 * Used if not Signed In
	 */
	private RelativeLayout signInLayout;
	private Button signInButton;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mMenuDrawer.setContentView(R.layout.wish_list);

		getActionBar().setTitle("Wish List");

		wishListLayout = (RelativeLayout) findViewById(R.id.wish_list_layout);
		signInLayout = (RelativeLayout) findViewById(R.id.sign_in_layout);
		noDataTextView = (TextView) findViewById(R.id.no_data);
		wishListView = (ListView) findViewById(R.id.wish_list_view);
		signInButton = (Button) findViewById(R.id.sign_in);
		signInButton.setOnClickListener(onClickListener);

		if (ApplicationEx.isLoggedIn) {
			wishListLayout.setVisibility(View.VISIBLE);
			signInLayout.setVisibility(View.GONE);
			try {
				db = new DatabaseHandler(getApplicationContext());
				db.openInternalDB();
				wishList = db.getMyWishList(ApplicationEx.userName);
				ApplicationEx.productList = wishList;
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				db.close();
			}

			checkViewStatus();

			wishListAdapter = new WishListAdapter(this,
					new int[] { R.layout.offer_list_row },
					new int[] { R.id.image }, wishList);
			wishListView.setDrawSelectorOnTop(true);
			wishListView.setAdapter(wishListAdapter);
			wishListView.setOnItemClickListener(onItemClickListener);
			wishListView.setOnItemLongClickListener(onItemLongClickListener);

			if (wishListView instanceof DragNDropListView) {
				((DragNDropListView) wishListView)
						.setDropListener(mDropListener);
				((DragNDropListView) wishListView)
						.setRemoveListener(mRemoveListener);
				((DragNDropListView) wishListView)
						.setDragListener(mDragListener);
			}

		} else {
			wishListLayout.setVisibility(View.GONE);
			signInLayout.setVisibility(View.VISIBLE);
		}
	}

	private OnClickListener onClickListener = new OnClickListener() {

		@Override
		public void onClick(View view) {
			int id = view.getId();
			switch (id) {
			case R.id.sign_in:
				Intent intent = new Intent(WishListActivity.this,
						SignInActivity.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);

				break;

			default:
				break;
			}
		}
	};

	public void checkViewStatus() {
		if (wishList == null || wishList.size() == 0) {
			wishListView.setVisibility(View.GONE);
			noDataTextView.setVisibility(View.VISIBLE);
		} else {
			wishListView.setVisibility(View.VISIBLE);
			noDataTextView.setVisibility(View.GONE);
		}
	}

	/**
	 * Handles Menu Clicks
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {
		case R.id.delete_all_option:
			if (wishList == null || wishList.size() == 0) {
				checkViewStatus();
			} else {
				AlertDialog.Builder dialog = new AlertDialog.Builder(
						WishListActivity.this);
				dialog.setTitle("Delete All Products");
				dialog.setMessage("Are you sure you want to delete all wish listed items?");
				dialog.setCancelable(false);
				dialog.setPositiveButton(android.R.string.ok,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int whichButton) {
								db = new DatabaseHandler(
										getApplicationContext());
								db.openInternalDB();
								db.deleteAllTableEntries(DatabaseHandler.TABLE_WISH_LIST);
								wishList = db
										.getproductList(DatabaseHandler.TABLE_WISH_LIST);
								wishListAdapter.setProductList(wishList);
								wishListAdapter.notifyDataSetChanged();
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
		if (ApplicationEx.isLoggedIn) {
			inflater.inflate(R.menu.delete_all, menu);
		}
		return true;
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
					WishListActivity.this);
			dialog.setTitle("Delete Wish List Item");
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
								db.deleteRow(DatabaseHandler.TABLE_WISH_LIST,
										product.getProductURL());
								wishList = db
										.getproductList(DatabaseHandler.TABLE_WISH_LIST);
								ApplicationEx.productList = wishList;
								wishListAdapter.setProductList(wishList);
								wishListAdapter.notifyDataSetChanged();
								checkViewStatus();
								PriceKingUtils.showToast(WishListActivity.this,
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
			Intent intent = new Intent(WishListActivity.this,
					ProductDetailActivity.class);
			ApplicationEx.product = product;
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);

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

	private DropListener mDropListener = new DropListener() {
		public void onDrop(int from, int to) {
			ListAdapter adapter = wishListAdapter;
			if (adapter instanceof WishListAdapter) {
				((WishListAdapter) adapter).onDrop(from, to);
				wishListView.invalidateViews();
				System.out.println("***********updated wishlist***********"
						+ wishList);
			}
		}
	};

	private RemoveListener mRemoveListener = new RemoveListener() {
		public void onRemove(int which) {
			ListAdapter adapter = wishListAdapter;
			if (adapter instanceof WishListAdapter) {
				((WishListAdapter) adapter).onRemove(which);
				wishListView.invalidateViews();
			}
		}
	};

	private DragListener mDragListener = new DragListener() {

		int backgroundColor = 0xe0FFFFFF;
		int defaultBackgroundColor;

		public void onDrag(int x, int y, ListView listView) {
			// TODO Auto-generated method stub
		}

		public void onStartDrag(View itemView) {
			itemView.setVisibility(View.INVISIBLE);
			defaultBackgroundColor = itemView.getDrawingCacheBackgroundColor();
			itemView.setBackgroundColor(backgroundColor);
		}

		public void onStopDrag(View itemView) {
			itemView.setVisibility(View.VISIBLE);
			itemView.setBackgroundColor(defaultBackgroundColor);
		}

	};

	@Override
	protected void onPause() {
		db = new DatabaseHandler(getApplicationContext());
		db.openInternalDB();
		db.deleteWishListTableEntries(DatabaseHandler.TABLE_WISH_LIST,
				ApplicationEx.userName);
		/**
		 * Add wish list to Database
		 */
		for (Product product : wishList) {
			db.addToWishList(product, ApplicationEx.userName);
		}
		super.onPause();
	}

}