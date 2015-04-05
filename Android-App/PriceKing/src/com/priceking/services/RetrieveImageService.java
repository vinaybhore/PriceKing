package com.priceking.services;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;

import com.priceking.services.utils.HTTPRequest;

public class RetrieveImageService implements Runnable {
	public interface RetrieveImageServiceListener {
		void onGetImageFinished(RetrieveImageService getImageService,
				Drawable image);

		void onGetImageFailed(RetrieveImageService getImageService, String error);
	}

	private RetrieveImageServiceListener listener;
	private Drawable image;
	private String imageURL;
	private String errorMessage;
	private Context context;

	public RetrieveImageService(Context context) {
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
				listener.onGetImageFinished(RetrieveImageService.this, image);
			else
				listener.onGetImageFailed(RetrieveImageService.this,
						errorMessage);
		}
	};

	public RetrieveImageServiceListener getListener() {
		return listener;
	}

	public void setListener(RetrieveImageServiceListener listener) {
		this.listener = listener;
	}

	public String getImageURL() {
		return imageURL;
	}

	public void setImageURL(String imageURL) {
		this.imageURL = imageURL;
	}
}