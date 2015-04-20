package com.priceking.services;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;

import com.priceking.services.utils.HTTPRequest;

public class RetrieveRatingImageService implements Runnable {
	public interface RetrieveRatingImageServiceListener {
		void onGetRatingImageFinished(
				RetrieveRatingImageService getImageService, Drawable image);

		void onGetRatingImageFailed(RetrieveRatingImageService getImageService,
				String error);
	}

	private RetrieveRatingImageServiceListener listener;
	private Drawable image;
	private String imageURL;
	private String errorMessage;
	private Context context;

	public RetrieveRatingImageService(Context context) {
		this.context = context;
	}

	public void run() {
		HTTPRequest request = new HTTPRequest(imageURL, context);

		try {
			request.execute(HTTPRequest.RequestMethod.GET);
			image = request.getResponseDrawable();
		} catch (Exception e) {
			image = null;
			errorMessage = e.getLocalizedMessage();
		}

		handler.sendEmptyMessage(0);
	}

	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (image != null)
				listener.onGetRatingImageFinished(
						RetrieveRatingImageService.this, image);
			else
				listener.onGetRatingImageFailed(
						RetrieveRatingImageService.this, errorMessage);
		}
	};

	public RetrieveRatingImageServiceListener getListener() {
		return listener;
	}

	public void setListener(RetrieveRatingImageServiceListener listener) {
		this.listener = listener;
	}

	public String getImageURL() {
		return imageURL;
	}

	public void setImageURL(String imageURL) {
		this.imageURL = imageURL;
	}
}