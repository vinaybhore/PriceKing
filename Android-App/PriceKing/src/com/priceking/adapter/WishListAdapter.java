package com.priceking.adapter;

import java.util.ArrayList;
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
import com.priceking.utils.PriceKingUtils;

/**
 * Wish List Adapter that holds wish list items in a list view
 * 
 * @author DEVEN
 * 
 */
public class WishListAdapter extends BaseAdapter {
	private List<Product> productList = new ArrayList<Product>();
	private Context context;

	private int[] mIds;
	private int[] mLayouts;
	private LayoutInflater mInflater;

	/**
	 * 
	 * @param NewsList
	 * @param applicationContext
	 */
	public WishListAdapter(Context applicationContext, List<Product> productList) {
		init(context, new int[] { android.R.layout.simple_list_item_1 },
				new int[] { android.R.id.text1 }, productList);
	}

	public WishListAdapter(Context context, int[] itemLayouts, int[] itemIDs,
			List<Product> productList) {
		init(context, itemLayouts, itemIDs, productList);
	}

	private void init(Context context, int[] layouts, int[] ids,
			List<Product> productList) {
		// Cache the LayoutInflate to avoid asking for a new one each time.
		mInflater = LayoutInflater.from(context);
		mIds = ids;
		mLayouts = layouts;
		this.productList = productList;
		this.context = context;
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
		ProductViewHolder productsViewHolder;
		if (convertView == null) {
			// LayoutInflater inflater = (LayoutInflater) context
			// .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			// convertView = inflater.inflate(R.layout.offer_list_row, null);
			convertView = mInflater.inflate(mLayouts[0], null);

			productsViewHolder = new ProductViewHolder();
			productsViewHolder.name = (TextView) convertView
					.findViewById(R.id.title);
			productsViewHolder.salePrice = (TextView) convertView
					.findViewById(R.id.sale_price);
			productsViewHolder.msrp = (TextView) convertView
					.findViewById(R.id.msrp);
			productsViewHolder.horizontalView = (View) convertView
					.findViewById(R.id.horizontal_line);
			productsViewHolder.category = (TextView) convertView
					.findViewById(R.id.category);
			productsViewHolder.thumbnailImage = (ImageView) convertView
					.findViewById(R.id.image);
			productsViewHolder.custRatingImage = (ImageView) convertView
					.findViewById(R.id.img_rating);
		} else {
			productsViewHolder = (ProductViewHolder) convertView.getTag();
		}
		Product product = productList.get(position);
		productsViewHolder.name.setText(product.getName());
		productsViewHolder.msrp.setText(PriceKingUtils
				.formatCurrencyUSD(product.getMsrp()));
		productsViewHolder.salePrice.setText(PriceKingUtils
				.formatCurrencyUSD(product.getSalePrice()));
		productsViewHolder.category.setText(product.getCategory());
		convertView.setTag(R.id.offer_id, product);
		convertView.setTag(productsViewHolder);

		if (ApplicationEx.productImages
				.containsKey(product.getThumbnailImage())
				&& ApplicationEx.productImages.get(product.getThumbnailImage()) != null) {

			productsViewHolder.thumbnailImage
					.setImageDrawable(ApplicationEx.productImages.get(product
							.getThumbnailImage()));
		} else {
			productsViewHolder.thumbnailImage
					.setImageResource(R.drawable.noimage);
		}

		if (product.getCustomerRating() > 0
				&& product.getCustomerRating() < 1.1) {
			productsViewHolder.custRatingImage.setImageDrawable(context
					.getResources().getDrawable(R.drawable.one_star));
		} else if (product.getCustomerRating() > 1
				&& product.getCustomerRating() < 2.1) {
			productsViewHolder.custRatingImage.setImageDrawable(context
					.getResources().getDrawable(R.drawable.two_star));
		} else if (product.getCustomerRating() > 2
				&& product.getCustomerRating() < 3.1) {
			productsViewHolder.custRatingImage.setImageDrawable(context
					.getResources().getDrawable(R.drawable.three_star));
		} else if (product.getCustomerRating() > 3
				&& product.getCustomerRating() < 4.1) {
			productsViewHolder.custRatingImage.setImageDrawable(context
					.getResources().getDrawable(R.drawable.four_star));
		} else if (product.getCustomerRating() > 4
				&& product.getCustomerRating() < 5.1) {
			productsViewHolder.custRatingImage.setImageDrawable(context
					.getResources().getDrawable(R.drawable.five_star));
		} else {
			productsViewHolder.custRatingImage.setImageDrawable(context
					.getResources().getDrawable(R.drawable.four_star));
		}

		return convertView;
	}

	/**
	 * View holder for Product stuff
	 * 
	 * @author DEVEN
	 * 
	 */
	private class ProductViewHolder {
		TextView name;
		ImageView thumbnailImage;
		ImageView custRatingImage;
		TextView salePrice;
		TextView msrp;
		TextView category;
		View horizontalView;
	}

	public void onRemove(int which) {
		if (which < 0 || which > productList.size())
			return;
		productList.remove(which);
	}

	public void onDrop(int from, int to) {
		Product temp = productList.get(from);
		productList.remove(from);
		productList.add(to, temp);
	}
}
