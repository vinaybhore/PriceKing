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

import com.priceking.entity.Product;
import com.priceking.services.utils.HTTPRequest;
import com.priceking.utils.Constants;

/**
 * Service to retrieve weather details of the destination location.
 */
public class RetrieveProductService implements Runnable {
	/**
	 * Listener for WeatherService
	 */
	public interface RetrieveProductServiceListener {
		void onRetrieveProductFinished(List<Product> productList);

		void onRetrieveProductFailed(int error, String message);
	}

	private static final String TAG = "RetrieveProductService";
	/** Retrieve Product URL */
	private static String RETRIEVE_PRODUCT_URL = "";
	private RetrieveProductServiceListener listener;
	private String jsonResponse;
	private int statusCode;
	private Context context;
	private String query;
	private List<Product> productList = new ArrayList<Product>();

	public RetrieveProductService(Context context, String query) {
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
		// if (destinationLocation.contains(" "))
		// destinationLocation = destinationLocation.replace(" ", "%20");

		Message message = new Message();
		try {
			RETRIEVE_PRODUCT_URL = Services.PRODUCT_API_URL + query
					+ Services.FORMAT + Services.API_KEY;
			HTTPRequest request = new HTTPRequest(RETRIEVE_PRODUCT_URL, context);
			Log.d("Product Service", "URL::" + RETRIEVE_PRODUCT_URL);
			statusCode = request.execute(HTTPRequest.RequestMethod.GET);
			jsonResponse = request.getResponseString();
			message.what = statusCode;
			Log.d(TAG, "run::" + jsonResponse);
			productHandler.sendMessage(message);
		} catch (Exception e) {
			message.what = statusCode;
			productHandler.sendMessage(message);
			Log.e(TAG, "Event Service exception::" + e);
		}

	}

	private Handler productHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case Constants.PriceKingDialogCodes.SUCCESS:
				if (!TextUtils.isEmpty(jsonResponse)) {
					listener.onRetrieveProductFinished(parseRetrievedWeather(jsonResponse));
				} else {
					listener.onRetrieveProductFailed(
							Constants.PriceKingDialogCodes.NETWORK_ERROR,
							Constants.PriceKingDialogMessages.NETWORK_ERROR);
				}
				break;
			case Constants.PriceKingDialogCodes.DATA_NOT_FOUND:
				listener.onRetrieveProductFailed(
						Constants.PriceKingDialogCodes.DATA_NOT_FOUND,
						Constants.PriceKingDialogMessages.NOT_FOUND);
				break;
			case Constants.PriceKingDialogCodes.INTERNAL_SERVER_ERROR:
				listener.onRetrieveProductFailed(
						Constants.PriceKingDialogCodes.INTERNAL_SERVER_ERROR,
						Constants.PriceKingDialogMessages.INTERNAL_SERVER_ERROR);
				break;
			case Constants.PriceKingDialogCodes.NETWORK_ERROR:
				listener.onRetrieveProductFailed(
						Constants.PriceKingDialogCodes.NETWORK_ERROR,
						Constants.PriceKingDialogMessages.NETWORK_ERROR);
				break;
			default:
				listener.onRetrieveProductFailed(
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
	public RetrieveProductServiceListener getListener() {
		return listener;
	}

	/**
	 * Set listener
	 * 
	 * @return
	 */
	public void setListener(RetrieveProductServiceListener listener) {
		this.listener = listener;
	}

	/**
	 * Parse Products received from Web Service Response
	 * 
	 * @param response
	 * @return
	 */
	private List<Product> parseRetrievedWeather(String response) {

		try {
			JSONObject jsonObject = new JSONObject(jsonResponse);
			JSONObject myProductObject = null;

			if (jsonObject.has("items")) {
				JSONArray productArray = jsonObject.getJSONArray("items");

				for (int i = 0; i < productArray.length(); i++) {
					Product product = new Product();
					myProductObject = productArray.getJSONObject(i);
					product.deserializeJSON(myProductObject);
					productList.add(product);
				}
			} else {
				listener.onRetrieveProductFailed(
						Constants.PriceKingDialogCodes.DATA_NOT_FOUND,
						Constants.PriceKingDialogMessages.NOT_FOUND);

			}

			return productList;
		} catch (JSONException e) {
			e.printStackTrace();
			listener.onRetrieveProductFailed(
					Constants.PriceKingDialogCodes.DATA_NOT_FOUND,
					Constants.PriceKingDialogMessages.NOT_FOUND);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {

		}
		return null;
	}

}
