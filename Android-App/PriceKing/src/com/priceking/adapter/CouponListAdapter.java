package com.priceking.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.priceking.R;
import com.priceking.entity.Coupon;

/**
 * Coupon List Adapter that holds daily list of coupons in a list view
 * 
 * @author DEVEN
 * 
 */
public class CouponListAdapter extends BaseAdapter {
	private List<Coupon> couponList = new ArrayList<Coupon>();
	private Context context;

	/**
	 * 
	 * @param NewsList
	 * @param applicationContext
	 */
	public CouponListAdapter(List<Coupon> couponList, Context applicationContext) {
		this.couponList = couponList;
		this.context = applicationContext;
	}

	/**
	 * Update the Coupon list
	 * 
	 * @param CouponList
	 */
	public void setCouponList(List<Coupon> couponList) {
		this.couponList = couponList;
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return couponList.size();
	}

	@Override
	public Object getItem(int position) {
		return couponList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		CouponViewHolder couponViewHolder;
		if (convertView == null) {
			LayoutInflater inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(R.layout.deal_list_row, null);
			couponViewHolder = new CouponViewHolder();
			couponViewHolder.name = (TextView) convertView
					.findViewById(R.id.title);
			couponViewHolder.thumbnailImage = (ImageView) convertView
					.findViewById(R.id.image);
			couponViewHolder.isNowAvailable = (TextView) convertView
					.findViewById(R.id.is_now_available);
			couponViewHolder.viewDeal = (Button) convertView
					.findViewById(R.id.btn_view_deal);

		} else {
			couponViewHolder = (CouponViewHolder) convertView.getTag();
		}
		Coupon coupon = couponList.get(position);
		couponViewHolder.name.setText(coupon.getTitle());
		couponViewHolder.isNowAvailable.setText("Available");
		couponViewHolder.viewDeal.setTag(coupon.getDealURL());
		convertView.setTag(R.id.offer_id, coupon);
		convertView.setTag(couponViewHolder);

		if (coupon.getThumbnailBlob() != null) {
			couponViewHolder.thumbnailImage
					.setImageDrawable(new BitmapDrawable(BitmapFactory
							.decodeByteArray(coupon.getThumbnailBlob(), 0,
									coupon.getThumbnailBlob().length)));
		} else {
			couponViewHolder.thumbnailImage
					.setImageResource(R.drawable.noimage);
		}

		couponViewHolder.viewDeal.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View view) {
				/**
				 * View Deal in the browser
				 */
				Intent myIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(view
						.getTag().toString()));
				context.startActivity(myIntent);

			}
		});

		return convertView;
	}

	/**
	 * View holder for Coupon stuff
	 * 
	 * @author DEVEN
	 * 
	 */
	private class CouponViewHolder {
		TextView name;
		ImageView thumbnailImage;
		TextView isNowAvailable;
		Button viewDeal;
	}
}
