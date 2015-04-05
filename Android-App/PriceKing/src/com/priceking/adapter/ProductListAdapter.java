package com.priceking.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.priceking.ApplicationEx;
import com.priceking.R;
import com.priceking.entity.Product;

/**
 * News Adapter that holds News list in a list view
 * 
 * @author DEVEN
 * 
 */
public class ProductListAdapter extends BaseAdapter {
	private List<Product> productList = new ArrayList<Product>();
	private int lastPosition = -1;
	private Context context;

	/**
	 * 
	 * @param NewsList
	 * @param applicationContext
	 */
	public ProductListAdapter(List<Product> productList, Context applicationContext) {
		this.productList = productList;
		this.context = applicationContext;
	}

	/**
	 * Update the News list
	 * 
	 * @param NewsList
	 */
	public void setProductList(List<Product> productList) {
		this.productList = productList;
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return productList.size();
	}

	@Override
	public Object getItem(int position) {
		return productList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ContactsViewHolder contactsViewHolder;
		if (convertView == null) {
			LayoutInflater inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(R.layout.offer_list_row, null);
			contactsViewHolder = new ContactsViewHolder();
			contactsViewHolder.name = (TextView) convertView
					.findViewById(R.id.item);
			contactsViewHolder.thumbnailImage = (ImageView) convertView
					.findViewById(R.id.image);
		} else {
			contactsViewHolder = (ContactsViewHolder) convertView.getTag();
		}
		Product product = productList.get(position);
		contactsViewHolder.name.setText(product.getName());
		convertView.setTag(R.id.offer_id, product);
		convertView.setTag(contactsViewHolder);

		String imageURL = product.getThumbnailImage();
		contactsViewHolder.thumbnailImage.setTag(imageURL);

		if (ApplicationEx.images.containsKey(imageURL)
				&& ApplicationEx.images.get(imageURL) != null) {
			contactsViewHolder.thumbnailImage
					.setImageDrawable(ApplicationEx.images.get(imageURL));
		} else {
			contactsViewHolder.thumbnailImage
					.setImageResource(R.drawable.noimage);
		}

		Animation animation = AnimationUtils.loadAnimation(context,
				(position > lastPosition) ? R.anim.up_from_bottom
						: R.anim.down_from_top);
		convertView.startAnimation(animation);
		lastPosition = position;

		return convertView;
	}

	/**
	 * View holder for News title
	 * 
	 * @author DEVEN
	 * 
	 */
	private class ContactsViewHolder {
		TextView name;
		ImageView thumbnailImage;
	}
}
