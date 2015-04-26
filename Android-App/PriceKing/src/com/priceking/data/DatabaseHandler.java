package com.priceking.data;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.priceking.entity.Coupon;
import com.priceking.entity.Product;

/**
 * 
 * @author DEVEN Database Handler class to manage database activities
 */
public class DatabaseHandler extends SQLiteOpenHelper {

	/**
	 * Database Version
	 */
	private static final int DATABASE_VERSION = 9;
	/**
	 * Database Name
	 */
	private static final String DATABASE_NAME = "priceking.db";

	/**
	 * Database Tables & Columns
	 */

	private static final String KEY_ID = "_id";

	/**
	 * Table Product
	 */
	public static final String TABLE_PRODUCT = "product";
	public static final String TABLE_RECENTLY_VIEWED = "recently_viewed";
	public static final String TABLE_WISH_LIST = "wish_list";
	public static final String TABLE_ADVERTISEMENT = "advertisement";
	public static final String TABLE_COUPON = "coupon";
	private static final String KEY_PRODUCT_NAME = "NAME";
	private static final String KEY_MSRP = "MSRP";
	private static final String KEY_SALE_PRICE = "SALE_PRICE";
	private static final String KEY_CATEGORY = "CATEGORY";
	private static final String KEY_SHORT_DESCRIPTION = "SHORT_DESCRIPTION";
	private static final String KEY_THUMBNAIL_IMAGE = "THUMBNAIL_IMAGE";
	private static final String KEY_PRODUCT_URL = "PRODUCT_URL";
	private static final String KEY_CUSTOMER_RATING = "CUSTOMER_RATING";
	private static final String KEY_THUMBNAIL_BLOB = "THUMBNAIL_BLOB";

	/**
	 * Fields for Coupon Table
	 */
	private static final String KEY_COUPON_TITLE = "COUPON_TITLE";
	private static final String KEY_COUPON_THUMBNAIL_IMAGE = "COUPON_THUMBNAIL_IMAGE";
	private static final String KEY_IS_NOW_DEAL = "IS_NOW_DEAL";
	private static final String KEY_DEAL_URL = "DEAL_URL";
	private static final String KEY_COUPON_THUMBNAIL_BLOB = "COUPON_THUMBNAIL_BLOB";

	/**
	 * User Fields
	 */
	private static final String KEY_USER_EMAIL = "USER_EMAIL";

	private Context context;

	private SQLiteDatabase m_db;

	public DatabaseHandler(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		this.context = context;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		System.out
				.println("###############################Creating tables########################");

		/**
		 * Creating Product Table
		 */
		String CREATE_PRODUCT_TABLE = "CREATE TABLE " + TABLE_PRODUCT + "("
				+ KEY_ID + " INTEGER PRIMARY KEY," + KEY_PRODUCT_NAME
				+ " TEXT," + KEY_MSRP + " INTEGER," + KEY_SALE_PRICE
				+ " INTEGER," + KEY_CATEGORY + " TEXT," + KEY_SHORT_DESCRIPTION
				+ " TEXT," + KEY_THUMBNAIL_IMAGE + " TEXT," + KEY_PRODUCT_URL
				+ " TEXT," + KEY_CUSTOMER_RATING + " INTEGER,"
				+ KEY_THUMBNAIL_BLOB + " BLOB" + ")";

		/**
		 * Creating Recently Viewed Items Table
		 */
		String CREATE_RECENTLY_VIEWED_TABLE = "CREATE TABLE "
				+ TABLE_RECENTLY_VIEWED + "(" + KEY_ID
				+ " INTEGER PRIMARY KEY," + KEY_PRODUCT_NAME + " TEXT,"
				+ KEY_MSRP + " INTEGER," + KEY_SALE_PRICE + " INTEGER,"
				+ KEY_CATEGORY + " TEXT," + KEY_SHORT_DESCRIPTION + " TEXT,"
				+ KEY_THUMBNAIL_IMAGE + " TEXT," + KEY_PRODUCT_URL + " TEXT,"
				+ KEY_CUSTOMER_RATING + " INTEGER," + KEY_THUMBNAIL_BLOB
				+ " BLOB" + ")";

		/**
		 * Creating Wish List Table
		 */
		String CREATE_WISH_LIST_TABLE = "CREATE TABLE " + TABLE_WISH_LIST + "("
				+ KEY_ID + " INTEGER PRIMARY KEY," + KEY_USER_EMAIL + " TEXT,"
				+ KEY_PRODUCT_NAME + " TEXT," + KEY_MSRP + " INTEGER,"
				+ KEY_SALE_PRICE + " INTEGER," + KEY_CATEGORY + " TEXT,"
				+ KEY_SHORT_DESCRIPTION + " TEXT," + KEY_THUMBNAIL_IMAGE
				+ " TEXT," + KEY_PRODUCT_URL + " TEXT," + KEY_CUSTOMER_RATING
				+ " INTEGER," + KEY_THUMBNAIL_BLOB + " BLOB" + ")";

		/**
		 * Creating Advertisement Table
		 */
		String CREATE_ADVERTISEMENT_TABLE = "CREATE TABLE "
				+ TABLE_ADVERTISEMENT + "(" + KEY_ID + " INTEGER PRIMARY KEY,"
				+ KEY_PRODUCT_NAME + " TEXT," + KEY_MSRP + " INTEGER,"
				+ KEY_SALE_PRICE + " INTEGER," + KEY_CATEGORY + " TEXT,"
				+ KEY_SHORT_DESCRIPTION + " TEXT," + KEY_THUMBNAIL_IMAGE
				+ " TEXT," + KEY_PRODUCT_URL + " TEXT," + KEY_CUSTOMER_RATING
				+ " INTEGER," + KEY_THUMBNAIL_BLOB + " BLOB" + ")";

		/**
		 * Creating Coupon Table
		 */
		String CREATE_COUPON_TABLE = "CREATE TABLE " + TABLE_COUPON + "("
				+ KEY_ID + " INTEGER PRIMARY KEY," + KEY_COUPON_TITLE
				+ " TEXT," + KEY_COUPON_THUMBNAIL_IMAGE + " TEXT,"
				+ KEY_IS_NOW_DEAL + " INTEGER," + KEY_DEAL_URL + " TEXT,"
				+ KEY_COUPON_THUMBNAIL_BLOB + " BLOB" + ")";

		db.execSQL(CREATE_PRODUCT_TABLE);
		db.execSQL(CREATE_RECENTLY_VIEWED_TABLE);
		db.execSQL(CREATE_WISH_LIST_TABLE);
		db.execSQL(CREATE_ADVERTISEMENT_TABLE);
		db.execSQL(CREATE_COUPON_TABLE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		/**
		 * Drop older table if existed
		 */
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_PRODUCT);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_RECENTLY_VIEWED);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_WISH_LIST);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_ADVERTISEMENT);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_COUPON);

		/**
		 * Create tables again
		 */
		onCreate(db);
	}

	/**
	 * Opens the Database.
	 */
	public void openInternalDB() {
		if ((m_db == null) || (m_db.isOpen() == false)) {
			m_db = this.getWritableDatabase();
		}
	}

	/**
	 * Closes the Database.
	 */
	public void closeDB() {
		if (m_db != null) {
			m_db.close();
			m_db = null;
		}
	}

	/**
	 * Deletes one Record from the Table with given table Name & Condition.
	 * 
	 * @param tableName
	 * @param whereConition
	 * @param values
	 */
	public void deleteRow(String TABLE_NAME, String DELETE_PARAM) {
		SQLiteDatabase db = this.getWritableDatabase();
		int deletedItems = 0;
		db.delete(TABLE_NAME, KEY_PRODUCT_URL + " = ?",
				new String[] { DELETE_PARAM });
		Log.d("", "===No. of deleted items===" + deletedItems);
		db.close();
	}

	/**
	 * Delete table entries of the table with given Table Name BASED ON QUERY
	 * 
	 * @param TABLE_NAME
	 */
	public void deleteTableEntries(String TABLE_NAME, String productName) {
		int rows = m_db.delete(TABLE_NAME, KEY_PRODUCT_NAME + "=?",
				new String[] { productName });
		System.out.println("****deleted rows******" + rows);
	}

	/**
	 * Deletes all wish list elements based on user
	 * 
	 * @param TABLE_NAME
	 */
	public void deleteWishListTableEntries(String TABLE_NAME, String userEmail) {
		int rows = m_db.delete(TABLE_NAME, KEY_USER_EMAIL + "=?",
				new String[] { userEmail });
		System.out.println("****deleted rows******" + rows);
	}

	/**
	 * Deletes all table entries of the table with given Table Name.
	 * 
	 * @param TABLE_NAME
	 */
	public void deleteAllTableEntries(String TABLE_NAME) {
		int rows = m_db.delete(TABLE_NAME, null, null);
		System.out.println("****deleted rows******" + rows);
	}

	public void updateProductTable(String TABLE_NAME, String productImage,
			byte[] imageBlob) {
		SQLiteDatabase db = this.getWritableDatabase();
		try {
			ContentValues values = new ContentValues();

			values.put(KEY_THUMBNAIL_BLOB, imageBlob);
			db.update(TABLE_NAME, values, KEY_THUMBNAIL_IMAGE + "=?",
					new String[] { productImage });

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.close();
		}
	}

	/**
	 * Update Deals Table
	 * 
	 * @param TABLE_NAME
	 * @param productImage
	 * @param imageBlob
	 */
	public void updateDealsTable(String TABLE_NAME, String dealsImage,
			byte[] imageBlob) {
		SQLiteDatabase db = this.getWritableDatabase();
		try {
			ContentValues values = new ContentValues();
			values.put(KEY_COUPON_THUMBNAIL_BLOB, imageBlob);
			db.update(TABLE_NAME, values, KEY_COUPON_THUMBNAIL_IMAGE + "=?",
					new String[] { dealsImage });
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.close();
		}
	}

	/**
	 * Adds Product to Database.
	 */
	public void addProduct(String TABLE_NAME, Product product) {
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(KEY_PRODUCT_NAME, product.getName());
		values.put(KEY_MSRP, product.getMsrp());
		values.put(KEY_SALE_PRICE, product.getSalePrice());
		values.put(KEY_CATEGORY, product.getCategory());
		values.put(KEY_SHORT_DESCRIPTION, product.getShortDescription());
		values.put(KEY_THUMBNAIL_IMAGE, product.getThumbnailImage());
		values.put(KEY_PRODUCT_URL, product.getProductURL());
		values.put(KEY_CUSTOMER_RATING, product.getCustomerRating());
		values.put(KEY_THUMBNAIL_BLOB, product.getThumbnailBlob());
		long result = db.insert(TABLE_NAME, null, values);
		System.out.println("Product Insertion Result" + result);
		db.close();
	}

	/**
	 * Adds Coupon to Database.
	 */
	public void addCoupon(String TABLE_NAME, Coupon coupon) {
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(KEY_COUPON_TITLE, coupon.getTitle());
		values.put(KEY_COUPON_THUMBNAIL_IMAGE, coupon.getThumbnailImage());
		if (coupon.isNowDeal())
			values.put(KEY_IS_NOW_DEAL, 1);
		else
			values.put(KEY_IS_NOW_DEAL, 0);
		values.put(KEY_DEAL_URL, coupon.getDealURL());
		values.put(KEY_COUPON_THUMBNAIL_BLOB, coupon.getThumbnailBlob());
		long result = db.insert(TABLE_NAME, null, values);
		System.out.println("Product Insertion Result" + result);
		db.close();
	}

	/**
	 * Adds a product to wish list
	 */
	public void addToWishList(Product product, String userEmail) {
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(KEY_PRODUCT_NAME, product.getName());
		values.put(KEY_USER_EMAIL, userEmail);
		values.put(KEY_MSRP, product.getMsrp());
		values.put(KEY_SALE_PRICE, product.getSalePrice());
		values.put(KEY_CATEGORY, product.getCategory());
		values.put(KEY_SHORT_DESCRIPTION, product.getShortDescription());
		values.put(KEY_THUMBNAIL_IMAGE, product.getThumbnailImage());
		values.put(KEY_PRODUCT_URL, product.getProductURL());
		values.put(KEY_CUSTOMER_RATING, product.getCustomerRating());
		values.put(KEY_THUMBNAIL_BLOB, product.getThumbnailBlob());
		long result = db.insert(DatabaseHandler.TABLE_WISH_LIST, null, values);
		System.out.println("Added to WishList" + result);
		db.close();
	}

	/**
	 * 
	 * @return numbers of rows in Table Data
	 */
	public int getCursorCount() {
		String selectQuery = "SELECT  * FROM " + TABLE_PRODUCT;
		SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);
		int count = cursor.getCount();
		cursor.close();
		db.close();
		return count;
	}

	/**
	 * 
	 * @return numbers of rows in Table Data
	 */
	public boolean isRecentlyviewed(String productURL) {
		SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = db.rawQuery(
				"select 1 from recently_viewed where PRODUCT_URL=?",
				new String[] { productURL });
		boolean exists = (cursor.getCount() > 0);
		cursor.close();
		return exists;
	}

	/**
	 * 
	 * @return Checks if the Product is in the Wish List
	 */
	public boolean isInWishList(String userEmail, String productURL) {
		SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = db.rawQuery(
				"select 1 from wish_list where USER_EMAIL=? and PRODUCT_URL=?",
				new String[] { userEmail, productURL });
		boolean exists = (cursor.getCount() > 0);
		cursor.close();
		return exists;
	}

	/**
	 * 
	 * @return product list from database.
	 */
	public List<Product> getproductList(String TABLE_NAME) {

		List<Product> productList = new ArrayList<Product>();
		String selectQuery = "SELECT  * FROM " + TABLE_NAME;
		SQLiteDatabase db = this.getWritableDatabase();

		Cursor cursor = db.rawQuery(selectQuery, null);
		if (cursor.moveToFirst()) {
			do {
				Product product = new Product();
				product.setName(cursor.getString(cursor
						.getColumnIndex(DatabaseHandler.KEY_PRODUCT_NAME)));
				product.setMsrp(cursor.getDouble(cursor
						.getColumnIndex(DatabaseHandler.KEY_MSRP)));
				product.setSalePrice(cursor.getDouble(cursor
						.getColumnIndex(DatabaseHandler.KEY_SALE_PRICE)));
				product.setCategory(cursor.getString(cursor
						.getColumnIndex(DatabaseHandler.KEY_CATEGORY)));
				product.setShortDescription(cursor.getString(cursor
						.getColumnIndex(DatabaseHandler.KEY_SHORT_DESCRIPTION)));
				product.setThumbnailImage(cursor.getString(cursor
						.getColumnIndex(DatabaseHandler.KEY_THUMBNAIL_IMAGE)));
				product.setProductURL(cursor.getString(cursor
						.getColumnIndex(DatabaseHandler.KEY_PRODUCT_URL)));
				product.setCustomerRating(cursor.getDouble(cursor
						.getColumnIndex(DatabaseHandler.KEY_CUSTOMER_RATING)));
				product.setThumbnailBlob(cursor.getBlob(cursor
						.getColumnIndex(DatabaseHandler.KEY_THUMBNAIL_BLOB)));
				productList.add(product);
			} while (cursor.moveToNext());
		}
		cursor.close();
		db.close();
		return productList;
	}

	/**
	 * 
	 * @return Coupon list from database.
	 */
	public List<Coupon> getCouponList(String TABLE_NAME) {

		List<Coupon> couponList = new ArrayList<Coupon>();
		String selectQuery = "SELECT  * FROM " + TABLE_NAME;
		SQLiteDatabase db = this.getWritableDatabase();

		Cursor cursor = db.rawQuery(selectQuery, null);
		if (cursor.moveToFirst()) {
			do {
				Coupon coupon = new Coupon();
				coupon.setTitle(cursor.getString(cursor
						.getColumnIndex(DatabaseHandler.KEY_COUPON_TITLE)));
				coupon.setThumbnailImage(cursor.getString(cursor
						.getColumnIndex(DatabaseHandler.KEY_COUPON_THUMBNAIL_IMAGE)));
				if (Integer.parseInt(cursor.getString(cursor
						.getColumnIndex(DatabaseHandler.KEY_IS_NOW_DEAL))) == 1)
					coupon.setNowDeal(true);
				else
					coupon.setNowDeal(false);
				coupon.setDealURL(cursor.getString(cursor
						.getColumnIndex(DatabaseHandler.KEY_DEAL_URL)));
				coupon.setThumbnailBlob(cursor.getBlob(cursor
						.getColumnIndex(DatabaseHandler.KEY_COUPON_THUMBNAIL_BLOB)));
				couponList.add(coupon);
			} while (cursor.moveToNext());
		}
		cursor.close();
		db.close();
		return couponList;
	}

	/**
	 * 
	 * @return User Wish List from database.
	 */
	public ArrayList<Product> getMyWishList(String userEmail) {

		ArrayList<Product> wishList = new ArrayList<Product>();
		SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = db.rawQuery(
				"select * from wish_list where USER_EMAIL=?",
				new String[] { userEmail });

		if (cursor.moveToFirst()) {
			do {
				Product product = new Product();
				product.setName(cursor.getString(cursor
						.getColumnIndex(DatabaseHandler.KEY_PRODUCT_NAME)));
				product.setMsrp(cursor.getDouble(cursor
						.getColumnIndex(DatabaseHandler.KEY_MSRP)));
				product.setSalePrice(cursor.getDouble(cursor
						.getColumnIndex(DatabaseHandler.KEY_SALE_PRICE)));
				product.setCategory(cursor.getString(cursor
						.getColumnIndex(DatabaseHandler.KEY_CATEGORY)));
				product.setShortDescription(cursor.getString(cursor
						.getColumnIndex(DatabaseHandler.KEY_SHORT_DESCRIPTION)));
				product.setThumbnailImage(cursor.getString(cursor
						.getColumnIndex(DatabaseHandler.KEY_THUMBNAIL_IMAGE)));
				product.setProductURL(cursor.getString(cursor
						.getColumnIndex(DatabaseHandler.KEY_PRODUCT_URL)));
				product.setCustomerRating(cursor.getDouble(cursor
						.getColumnIndex(DatabaseHandler.KEY_CUSTOMER_RATING)));
				product.setThumbnailBlob(cursor.getBlob(cursor
						.getColumnIndex(DatabaseHandler.KEY_THUMBNAIL_BLOB)));
				wishList.add(product);
			} while (cursor.moveToNext());
		}
		cursor.close();
		db.close();
		return wishList;
	}

}
