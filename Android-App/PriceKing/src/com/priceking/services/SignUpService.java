package com.priceking.services;

import org.json.JSONObject;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.priceking.entity.User;
import com.priceking.services.utils.HTTPRequest;
import com.priceking.utils.Constants;

/**
 * Service for Sign Up Service
 */
public class SignUpService implements Runnable {
	/**
	 * Listener for SignUpService
	 */
	public interface SignUpServiceListener {
		void onSignUpFinished();

		void onSignUpFailed(int error, String message);
	}

	private static final String TAG = "SignUpService";
	/** Retrieve Sign Up URL */
	private static String SIGN_UP_URL = "";
	private SignUpServiceListener listener;
	private String jsonResponse;
	private int statusCode;
	private Context context;
	private User user;
	private String type = "";
	private String inputdata = "";

	public SignUpService(Context context, User user, String type) {
		this.context = context;
		this.user = user;
		this.type = type;
	}

	/**
	 * Sends a POST request to to Sign Up
	 */
	public void run() {
		Message message = new Message();
		try {
			if (type.equalsIgnoreCase("sign_up")) {
				SIGN_UP_URL = Services.SIGN_UP_URL;
			} else {
				SIGN_UP_URL = Services.SIGN_IN_URL;
			}
			HTTPRequest request = new HTTPRequest(SIGN_UP_URL, context);
			request.addHeader("Content-Type", "application/json");
			// request.addHeader("Content-Encoding", "gzip");
			request.addHeader("Accept", "application/json");

			JSONObject userJSONObject = null;
			if (type.equalsIgnoreCase("sign_up")) {
				userJSONObject = user.serializeJSON();
				inputdata = userJSONObject.toString();
			} else {
				userJSONObject = user.serializeSignIn();
				inputdata = userJSONObject.toString();
			}

			request.setInputData(inputdata);

			Log.d("Product Service", "URL::" + SIGN_UP_URL);
			statusCode = request.execute(HTTPRequest.RequestMethod.POST);
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
				System.out
						.println("**************************Success***************************");
				listener.onSignUpFinished();
				break;
			case Constants.PriceKingDialogCodes.DATA_NOT_FOUND:
				listener.onSignUpFailed(
						Constants.PriceKingDialogCodes.DATA_NOT_FOUND,
						Constants.PriceKingDialogMessages.NOT_FOUND);
				break;
			case Constants.PriceKingDialogCodes.INTERNAL_SERVER_ERROR:
				listener.onSignUpFailed(
						Constants.PriceKingDialogCodes.INTERNAL_SERVER_ERROR,
						Constants.PriceKingDialogMessages.INTERNAL_SERVER_ERROR);
				break;
			case Constants.PriceKingDialogCodes.NETWORK_ERROR:
				listener.onSignUpFailed(
						Constants.PriceKingDialogCodes.NETWORK_ERROR,
						Constants.PriceKingDialogMessages.NETWORK_ERROR);
				break;
			default:
				listener.onSignUpFailed(
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
	public SignUpServiceListener getListener() {
		return listener;
	}

	/**
	 * Set listener
	 * 
	 * @return
	 */
	public void setListener(SignUpServiceListener listener) {
		this.listener = listener;
	}

	/**
	 * Parse Sign Up response from Web Service Response
	 * 
	 * @param response
	 * @return
	 */
	private void parseRetrievedProduct(String response) {

	}

}
