package com.priceking.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.priceking.ApplicationEx;
import com.priceking.R;
import com.priceking.entity.Product;

public class AdvertisementGalleryAdapter extends BaseAdapter {
	private Context mContext;

	private List<Product> advertisementList;

	public AdvertisementGalleryAdapter(Context context,
			List<Product> advertisementList) {
		mContext = context;
		this.advertisementList = advertisementList;
	}

	public int getCount() {
		return advertisementList.size();
	}

	public Object getItem(int position) {
		return position;
	}

	public long getItemId(int position) {
		return position;
	}

	// Override this method according to your need
	public View getView(int position, View convertView, ViewGroup viewGroup) {

		AdvertisementViewHolder advertisementViewHolder;
		if (convertView == null) {
			LayoutInflater inflater = (LayoutInflater) mContext
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(R.layout.advertisement_list_row,
					null);
			advertisementViewHolder = new AdvertisementViewHolder();
			advertisementViewHolder.name = (TextView) convertView
					.findViewById(R.id.title);
			advertisementViewHolder.thumbnailImage = (ImageView) convertView
					.findViewById(R.id.img_thumbnail);
		} else {
			advertisementViewHolder = (AdvertisementViewHolder) convertView
					.getTag();
		}
		Product product = advertisementList.get(position);
		advertisementViewHolder.name.setText(product.getName());
		convertView.setTag(R.id.offer_id, product);
		convertView.setTag(advertisementViewHolder);

		if (ApplicationEx.productImages.containsKey(product
				.getThumbnailImage())
				&& ApplicationEx.productImages.get(product
						.getThumbnailImage()) != null) {

			advertisementViewHolder.thumbnailImage
					.setImageDrawable(ApplicationEx.productImages
							.get(product.getThumbnailImage()));
		} else {
			advertisementViewHolder.thumbnailImage
					.setImageResource(R.drawable.noimage);
		}

		return convertView;

	}

	/**
	 * View holder for Product stuff
	 * 
	 * @author DEVEN
	 * 
	 */
	private class AdvertisementViewHolder {
		TextView name;
		ImageView thumbnailImage;
	}
}
