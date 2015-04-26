package com.priceking.services;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import com.priceking.ApplicationEx;
import com.priceking.data.DatabaseHandler;
import com.priceking.entity.Coupon;
import com.priceking.services.utils.HTTPRequest;
import com.priceking.utils.Constants;

/**
 * Service to retrieve Coupons for the day
 */
public class RetrieveCouponService implements Runnable {
	/**
	 * Listener for RetrieveCouponService
	 */
	public interface RetrieveCouponServiceListener {
		void onRetrieveCouponFinished(List<Coupon> couponList);

		void onRetrieveCouponFailed(int error, String message);
	}

	private static final String TAG = "RetrieveCouponService";
	/** Retrieve Coupon URL */
	private static String RETRIEVE_COUPON_URL = "";
	private RetrieveCouponServiceListener listener;
	private String jsonResponse;
	private int statusCode;
	private Context context;
	private String query;
	private List<Coupon> couponList = new ArrayList<Coupon>();
	private DatabaseHandler db;

	public RetrieveCouponService(Context context, String query) {
		this.context = context;
		try {
			this.query = URLEncoder.encode(query, "utf-8");
			;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Sends a GET request to retrieve Events
	 */
	public void run() {
		Message message = new Message();
		try {
			RETRIEVE_COUPON_URL = Services.COUPON_API_URL + Services.LAT
					+ ApplicationEx.latitude + Services.LONG
					+ ApplicationEx.longitude + Services.OFFSET;
			HTTPRequest request = new HTTPRequest(RETRIEVE_COUPON_URL, context);
			Log.d("Coupon Service", "URL::" + RETRIEVE_COUPON_URL);
			statusCode = request.execute(HTTPRequest.RequestMethod.GET);
			jsonResponse = request.getResponseString();
			message.what = statusCode;
			Log.d(TAG, "run::" + jsonResponse);
			CouponServiceHandler.sendMessage(message);
		} catch (Exception e) {
			message.what = statusCode;
			CouponServiceHandler.sendMessage(message);
			Log.e(TAG, "Coupon Service exception::" + e);
		}

	}

	private Handler CouponServiceHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case Constants.PriceKingDialogCodes.SUCCESS:
				if (!TextUtils.isEmpty(jsonResponse)) {
					listener.onRetrieveCouponFinished(parseRetrievedCoupons(jsonResponse));
				} else {
					listener.onRetrieveCouponFailed(
							Constants.PriceKingDialogCodes.NETWORK_ERROR,
							Constants.PriceKingDialogMessages.NETWORK_ERROR);
				}
				break;
			case Constants.PriceKingDialogCodes.DATA_NOT_FOUND:
				listener.onRetrieveCouponFailed(
						Constants.PriceKingDialogCodes.DATA_NOT_FOUND,
						Constants.PriceKingDialogMessages.NOT_FOUND);
				break;
			case Constants.PriceKingDialogCodes.INTERNAL_SERVER_ERROR:
				listener.onRetrieveCouponFailed(
						Constants.PriceKingDialogCodes.INTERNAL_SERVER_ERROR,
						Constants.PriceKingDialogMessages.INTERNAL_SERVER_ERROR);
				break;
			case Constants.PriceKingDialogCodes.NETWORK_ERROR:
				listener.onRetrieveCouponFailed(
						Constants.PriceKingDialogCodes.NETWORK_ERROR,
						Constants.PriceKingDialogMessages.NETWORK_ERROR);
				break;
			default:
				listener.onRetrieveCouponFailed(
						Constants.PriceKingDialogCodes.NETWORK_ERROR,
						Constants.PriceKingDialogMessages.NETWORK_ERROR);
				break;
			}
		}
	};

	/**
	 * Get listener
	 * 
	 * @return
	 */
	public RetrieveCouponServiceListener getListener() {
		return listener;
	}

	/**
	 * Set listener
	 * 
	 * @return
	 */
	public void setListener(RetrieveCouponServiceListener listener) {
		this.listener = listener;
	}

	/**
	 * Parse Products received from Web Service Response
	 * 
	 * @param response
	 * @return
	 */
	private List<Coupon> parseRetrievedCoupons(String response) {

		try {
			db = new DatabaseHandler(context);
			db.openInternalDB();
			db.deleteAllTableEntries(DatabaseHandler.TABLE_COUPON);

			JSONObject jsonObject = new JSONObject(jsonResponse);
			JSONObject myProductObject = null;

			if (jsonObject.has("deals")) {
				JSONArray couponArray = jsonObject.getJSONArray("deals");

				for (int i = 0; i < couponArray.length(); i++) {
					Coupon coupon = new Coupon();
					myProductObject = couponArray.getJSONObject(i);
					coupon.deserializeJSON(myProductObject);
					couponList.add(coupon);
					db.addCoupon(DatabaseHandler.TABLE_COUPON, coupon);
				}
			} else {
				listener.onRetrieveCouponFailed(
						Constants.PriceKingDialogCodes.DATA_NOT_FOUND,
						Constants.PriceKingDialogMessages.NOT_FOUND);

			}

			return couponList;
		} catch (JSONException e) {
			e.printStackTrace();
			listener.onRetrieveCouponFailed(
					Constants.PriceKingDialogCodes.DATA_NOT_FOUND,
					Constants.PriceKingDialogMessages.NOT_FOUND);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.close();
		}
		return null;
	}

}
