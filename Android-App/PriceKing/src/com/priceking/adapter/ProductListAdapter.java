package com.priceking.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.priceking.R;
import com.priceking.entity.Product;
import com.priceking.utils.PriceKingUtils;

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
	private String viewMode;

	/**
	 * 
	 * @param NewsList
	 * @param applicationContext
	 */
	public ProductListAdapter(List<Product> productList,
			Context applicationContext, String viewMode) {
		this.productList = productList;
		this.context = applicationContext;
		this.viewMode = viewMode;
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
			LayoutInflater inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			if (viewMode.equalsIgnoreCase("list")) {
				convertView = inflater.inflate(R.layout.offer_list_row, null);
			} else {
				convertView = inflater.inflate(R.layout.grid_list_row, null);
			}
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

		if (product.getThumbnailBlob() != null) {
			productsViewHolder.thumbnailImage
					.setImageDrawable(new BitmapDrawable(BitmapFactory
							.decodeByteArray(product.getThumbnailBlob(), 0,
									product.getThumbnailBlob().length)));
		} else {
			productsViewHolder.thumbnailImage
					.setImageResource(R.drawable.noimage);
		}

		if (product.getCustomerRatingBlob() != null) {
			productsViewHolder.custRatingImage
					.setImageDrawable(new BitmapDrawable(BitmapFactory
							.decodeByteArray(product.getCustomerRatingBlob(),
									0, product.getCustomerRatingBlob().length)));
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
}
