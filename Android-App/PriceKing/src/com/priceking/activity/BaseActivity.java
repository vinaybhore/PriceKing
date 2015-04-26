package com.priceking.activity;

import java.util.ArrayList;
import java.util.List;

import net.simonvt.menudrawer.MenuDrawer;
import net.simonvt.menudrawer.Position;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.priceking.ApplicationEx;
import com.priceking.R;
import com.priceking.utils.Constants;
import com.priceking.utils.PriceKingUtils;

public class BaseActivity extends Activity {
	// For Sliding Menu COntroller
	private static final String STATE_ACTIVE_POSITION = "com.priceking.HomeActivity.activePosition";
	private static final String STATE_CONTENT_TEXT = "com.priceking.HomeActivity.contentText";

	public MenuDrawer mMenuDrawer;
	private CategoryAdapter adapter;

	private int mActivePosition = -1;
	private String mContentText;
	private ListView listView;

	private SharedPreferences.Editor prefEditor;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		getActionBar()
				.setIcon(getResources().getDrawable(R.drawable.ic_drawer));
		getActionBar().setHomeButtonEnabled(true);
		getActionBar().setDisplayHomeAsUpEnabled(false);

		if (savedInstanceState != null) {
			mActivePosition = savedInstanceState.getInt(STATE_ACTIVE_POSITION);
			mContentText = savedInstanceState.getString(STATE_CONTENT_TEXT);
		}

		mMenuDrawer = MenuDrawer.attach(BaseActivity.this, Position.LEFT);
		mMenuDrawer.setTouchMode(MenuDrawer.MENU_DRAG_WINDOW);
		mMenuDrawer.setMenuView(R.layout.activity_sliding_menu);
		mMenuDrawer.setMenuSize(1000);

	}

	@Override
	protected void onResume() {
		// Set Category List on Slider Menu
		listView = (ListView) findViewById(R.id.list);
		adapter = new CategoryAdapter(this, PriceKingUtils.getCategoryList());
		listView.setDrawSelectorOnTop(true);
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(onItemClickListner);
		super.onResume();
	}

	/**
	 * Handles back press
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {

		case android.R.id.home:
			final int drawerState = mMenuDrawer.getDrawerState();
			if (drawerState == MenuDrawer.STATE_CLOSED
					|| drawerState == MenuDrawer.STATE_CLOSING) {
				mMenuDrawer.openMenu();
			}
			break;
		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putInt(STATE_ACTIVE_POSITION, mActivePosition);
		outState.putString(STATE_CONTENT_TEXT, mContentText);
	}

	/**
	 * Handles back press
	 */
	@Override
	public void onBackPressed() {
		final int drawerState = mMenuDrawer.getDrawerState();
		if (drawerState == MenuDrawer.STATE_OPEN
				|| drawerState == MenuDrawer.STATE_OPENING) {
			mMenuDrawer.closeMenu();
			return;
		}

		super.onBackPressed();
	}

	/**
	 * Handles On click listener for Sliding Menu
	 */
	private OnItemClickListener onItemClickListner = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> arg0, View view, int position,
				long arg3) {
			String selectedCategory = (String) view.getTag(R.id.id_name);
			mActivePosition = position;
			mMenuDrawer.setActiveView(view, position);
			mMenuDrawer.closeMenu();
			Intent intent = null;
			if (selectedCategory
					.equalsIgnoreCase(Constants.Categories.HOME_CATEGORY)) {
				if (!(BaseActivity.this instanceof HomeActivity)) {
					intent = new Intent(BaseActivity.this, HomeActivity.class);
					intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					startActivity(intent);
				}
			} else if (selectedCategory
					.equalsIgnoreCase(Constants.Categories.RECENTLY_VIEWED_CATEGORY)) {
				if (!(BaseActivity.this instanceof RecentlyViewedItemsActivity)) {
					intent = new Intent(BaseActivity.this,
							RecentlyViewedItemsActivity.class);
					intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					startActivity(intent);
				}
			} else if (selectedCategory
					.equalsIgnoreCase(Constants.Categories.STORES_CATEGORY)) {
				if (!(BaseActivity.this instanceof StoresActivity)) {
					intent = new Intent(BaseActivity.this, StoresActivity.class);
					intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					startActivity(intent);
				}
			} else if (selectedCategory
					.equalsIgnoreCase(Constants.Categories.WISHLIST_CATEGORY)) {
				if (!(BaseActivity.this instanceof WishListActivity)) {
					intent = new Intent(BaseActivity.this,
							WishListActivity.class);
					intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					startActivity(intent);
				}
			} else if (selectedCategory
					.equalsIgnoreCase(Constants.Categories.DEALS_CATEGORY)) {
				if (!(BaseActivity.this instanceof DealsActivity)) {
					intent = new Intent(BaseActivity.this, DealsActivity.class);
					intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					startActivity(intent);
				}
			} else if (selectedCategory
					.equalsIgnoreCase(Constants.Categories.CONTACT_US_CATEGORY)) {
				if (!(BaseActivity.this instanceof ContactUsActivity)) {
					intent = new Intent(BaseActivity.this,
							ContactUsActivity.class);
					intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					startActivity(intent);
				}
			} else if (selectedCategory
					.equalsIgnoreCase(Constants.Categories.SIGN_IN_CATEGORY)) {
				if (!(BaseActivity.this instanceof SignInActivity)) {
					intent = new Intent(BaseActivity.this, SignInActivity.class);
					intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					startActivity(intent);
				}
			} else if (selectedCategory
					.equalsIgnoreCase(Constants.Categories.SIGN_OUT_CATEGORY)) {
				ApplicationEx.isLoggedIn = false;
				prefEditor = ApplicationEx.sharedPreference.edit();
				prefEditor.putString(
						getResources().getString(R.string.app_user_name), "")
						.commit();
				ApplicationEx.userName = "";
				if (!(BaseActivity.this instanceof HomeActivity)) {
					intent = new Intent(BaseActivity.this, HomeActivity.class);
					intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					startActivity(intent);
				} else {
					onResume();
				}
			}

		}
	};

	public class CategoryAdapter extends BaseAdapter {
		private List<String> categoryList = new ArrayList<String>();
		private Context context;

		public CategoryAdapter(Context applicationContext, List<String> teamList) {
			this.context = applicationContext;
			this.categoryList = teamList;
		}

		/**
		 * Updates list view whenever data is changed.
		 * 
		 * @param checkingAccountsList
		 */
		public void setActivityList(List<String> activeList) {
			this.categoryList = activeList;
			notifyDataSetChanged();
		}

		@Override
		public int getCount() {
			return categoryList.size();
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see android.widget.Adapter#getItem(int)
		 */
		@Override
		public Object getItem(int position) {
			return categoryList.get(position);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see android.widget.Adapter#getItemId(int)
		 */
		@Override
		public long getItemId(int position) {
			return position;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see android.widget.Adapter#getView(int, android.view.View,
		 * android.view.ViewGroup)
		 */
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ActivitiesViewHolder activitiesViewHolder;
			if (convertView == null) {
				LayoutInflater inflater = (LayoutInflater) context
						.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				convertView = inflater.inflate(
						R.layout.activity_sliding_menu_list_row, null);
				activitiesViewHolder = new ActivitiesViewHolder();
				activitiesViewHolder.itemTextView = (TextView) convertView
						.findViewById(R.id.item);
				activitiesViewHolder.imageView = (ImageView) convertView
						.findViewById(R.id.img);
			} else {
				activitiesViewHolder = (ActivitiesViewHolder) convertView
						.getTag();
			}
			String item = categoryList.get(position);
			activitiesViewHolder.itemTextView.setText(item);
			if (position == 0)
				activitiesViewHolder.imageView.setImageDrawable(getResources()
						.getDrawable(R.drawable.home));
			else if (position == 1)
				activitiesViewHolder.imageView.setImageDrawable(getResources()
						.getDrawable(R.drawable.recent));
			else if (position == 2)
				activitiesViewHolder.imageView.setImageDrawable(getResources()
						.getDrawable(R.drawable.store));
			else if (position == 3)
				activitiesViewHolder.imageView.setImageDrawable(getResources()
						.getDrawable(R.drawable.favorite));
			else if (position == 4)
				activitiesViewHolder.imageView.setImageDrawable(getResources()
						.getDrawable(R.drawable.gift));
			else if (position == 5)
				activitiesViewHolder.imageView.setImageDrawable(getResources()
						.getDrawable(R.drawable.contact));
			else if (position == 6 && ApplicationEx.isLoggedIn)
				activitiesViewHolder.imageView.setImageDrawable(getResources()
						.getDrawable(R.drawable.signout));
			else if (position == 6 && !ApplicationEx.isLoggedIn)
				activitiesViewHolder.imageView.setImageDrawable(getResources()
						.getDrawable(R.drawable.signin));
			convertView.setTag(activitiesViewHolder);
			convertView.setTag(R.id.id_name, item);
			convertView.setTag(R.id.mdActiveViewPosition, position);
			if (position == mActivePosition) {
				mMenuDrawer.setActiveView(convertView, position);
			}

			return convertView;
		}

		/**
		 * Temporary holder class to hold references to the relevant Views in
		 * layout
		 * 
		 * @author devpawar
		 * 
		 */
		private class ActivitiesViewHolder {
			TextView itemTextView;
			ImageView imageView;
		}
	}

}
