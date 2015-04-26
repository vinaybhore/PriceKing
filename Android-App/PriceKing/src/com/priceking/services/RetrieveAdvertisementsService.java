package com.priceking.services;

import java.net.URLDecoder;
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

import com.priceking.data.DatabaseHandler;
import com.priceking.entity.Product;
import com.priceking.services.utils.HTTPRequest;
import com.priceking.utils.Constants;

/**
 * Service to retrieve Advertisements for user
 */
public class RetrieveAdvertisementsService implements Runnable {
	/**
	 * Listener for AdvertisementsService
	 */
	public interface RetrieveAdvertisementServiceListener {
		void onRetrieveAdvertisementFinished(List<Product> advertisementList);

		void onRetrieveAdvertisementFailed(int error, String message);
	}

	private static final String TAG = "RetrieveAdvertisementService";
	/** Retrieve Advertisement URL */
	private static String RETRIEVE_PRODUCT_URL = "";
	private RetrieveAdvertisementServiceListener listener;
	private String jsonResponse;
	private int statusCode;
	private Context context;
	private String query;
	private List<Product> advertisementList = new ArrayList<Product>();
	private DatabaseHandler db;

	public RetrieveAdvertisementsService(Context context, String query) {
		this.context = context;
		try {
			this.query = URLEncoder.encode(query, "utf-8");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Sends a GET request to retrieve Events
	 */
	public void run() {
		// if (destinationLocation.contains(" "))
		// destinationLocation = destinationLocation.replace(" ", "%20");

		Message message = new Message();
		try {
			RETRIEVE_PRODUCT_URL = Services.PRODUCT_API_URL + query;
			HTTPRequest request = new HTTPRequest(RETRIEVE_PRODUCT_URL, context);
			Log.d("Product Service", "URL::" + RETRIEVE_PRODUCT_URL);
			statusCode = request.execute(HTTPRequest.RequestMethod.GET);
			jsonResponse = request.getResponseString();
			message.what = statusCode;
			Log.d(TAG, "run::" + jsonResponse);
			advertisementHandler.sendMessage(message);
		} catch (Exception e) {
			message.what = statusCode;
			advertisementHandler.sendMessage(message);
			Log.e(TAG, "Event Service exception::" + e);
		}

	}

	private Handler advertisementHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case Constants.PriceKingDialogCodes.SUCCESS:
				if (!TextUtils.isEmpty(jsonResponse)) {
					listener.onRetrieveAdvertisementFinished(parseAdvertisements(jsonResponse));
				} else {
					listener.onRetrieveAdvertisementFailed(
							Constants.PriceKingDialogCodes.NETWORK_ERROR,
							Constants.PriceKingDialogMessages.NETWORK_ERROR);
				}
				break;
			case Constants.PriceKingDialogCodes.DATA_NOT_FOUND:
				listener.onRetrieveAdvertisementFailed(
						Constants.PriceKingDialogCodes.DATA_NOT_FOUND,
						Constants.PriceKingDialogMessages.NOT_FOUND);
				break;
			case Constants.PriceKingDialogCodes.INTERNAL_SERVER_ERROR:
				listener.onRetrieveAdvertisementFailed(
						Constants.PriceKingDialogCodes.INTERNAL_SERVER_ERROR,
						Constants.PriceKingDialogMessages.INTERNAL_SERVER_ERROR);
				break;
			case Constants.PriceKingDialogCodes.NETWORK_ERROR:
				listener.onRetrieveAdvertisementFailed(
						Constants.PriceKingDialogCodes.NETWORK_ERROR,
						Constants.PriceKingDialogMessages.NETWORK_ERROR);
				break;
			default:
				listener.onRetrieveAdvertisementFailed(
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
	public RetrieveAdvertisementServiceListener getListener() {
		return listener;
	}

	/**
	 * Set listener
	 * 
	 * @return
	 */
	public void setListener(RetrieveAdvertisementServiceListener listener) {
		this.listener = listener;
	}

	/**
	 * Parse Advertisements received from Web Service Response
	 * 
	 * @param response
	 * @return
	 */
	private List<Product> parseAdvertisements(String response) {

		try {
			db = new DatabaseHandler(context);
			db.openInternalDB();
			db.deleteAllTableEntries(DatabaseHandler.TABLE_ADVERTISEMENT);

			JSONArray productArray = new JSONArray(response);
			JSONObject myProductObject = null;

			for (int i = 0; i < productArray.length(); i++) {
				Product product = new Product();
				myProductObject = productArray.getJSONObject(i);
				product.deserializeJSON(myProductObject);
				advertisementList.add(product);
				db.addProduct(DatabaseHandler.TABLE_ADVERTISEMENT, product);
			}

			return advertisementList;
		} catch (JSONException e) {
			e.printStackTrace();
			listener.onRetrieveAdvertisementFailed(
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
