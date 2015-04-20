package com.priceking.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.priceking.R;
import com.priceking.entity.Categories;

public class GridListAdapter extends BaseAdapter {
	private Context mContext;

	private List<Categories> categoryList;

	public GridListAdapter(Context context, List<Categories> categoryList) {
		mContext = context;
		this.categoryList = categoryList;
	}

	public int getCount() {
		return categoryList.size();
	}

	public Object getItem(int position) {
		return position;
	}

	public long getItemId(int position) {
		return position;
	}

	@Override
	// Override this method according to your need
	public View getView(int position, View convertView, ViewGroup viewGroup) {

		CategoryViewHolder categoryViewHolder;
		if (convertView == null) {
			LayoutInflater inflater = (LayoutInflater) mContext
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater
					.inflate(R.layout.common_categories_row, null);
			categoryViewHolder = new CategoryViewHolder();
			categoryViewHolder.name = (TextView) convertView
					.findViewById(R.id.title);
			categoryViewHolder.icon = (ImageView) convertView
					.findViewById(R.id.img_thumbnail);
		} else {
			categoryViewHolder = (CategoryViewHolder) convertView.getTag();
		}
		Categories category = categoryList.get(position);
		categoryViewHolder.name.setText(category.getName());
		convertView.setTag(R.id.offer_id, category);
		convertView.setTag(categoryViewHolder);

		categoryViewHolder.icon.setImageResource(category.getIcon());
		categoryViewHolder.name.setText(category.getName());

		return convertView;

	}

	/**
	 * View holder for Category stuff
	 * 
	 * @author DEVEN
	 * 
	 */
	private class CategoryViewHolder {
		TextView name;
		ImageView icon;
	}
}