package com.priceking.entity;

import org.json.JSONObject;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Coupon Entity that contains all information about a single Coupon
 * 
 * @author DEVEN
 * 
 */
public class Coupon implements Entitiy, Parcelable {
	private String title;
	private String thumbnailImage;
	private boolean isNowDeal;
	private String dealURL;
	private byte[] thumbnailBlob;

	public Coupon() {

	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getThumbnailImage() {
		return thumbnailImage;
	}

	public void setThumbnailImage(String thumbnailImage) {
		this.thumbnailImage = thumbnailImage;
	}

	public boolean isNowDeal() {
		return isNowDeal;
	}

	public void setNowDeal(boolean isNowDeal) {
		this.isNowDeal = isNowDeal;
	}

	public String getDealURL() {
		return dealURL;
	}

	public void setDealURL(String dealURL) {
		this.dealURL = dealURL;
	}

	public byte[] getThumbnailBlob() {
		return thumbnailBlob;
	}

	public void setThumbnailBlob(byte[] thumbnailBlob) {
		this.thumbnailBlob = thumbnailBlob;
	}

	@Override
	public JSONObject serializeJSON() throws Exception {
		return null;
	}

	/**
	 * Method used to deserialize json for Product object
	 */
	@Override
	public void deserializeJSON(JSONObject myProduct) throws Exception {

		this.setTitle(myProduct.has("title") ? myProduct.getString("title")
				: "");
		this.setThumbnailImage(myProduct.has("mediumImageUrl") ? myProduct
				.getString("mediumImageUrl") : "");
		this.setNowDeal(myProduct.has("isNowDeal") ? myProduct
				.getBoolean("isNowDeal") : false);
		this.setDealURL(myProduct.has("dealUrl") ? myProduct
				.getString("dealUrl") : "");
	}

	/**
	 * 
	 * @return creator
	 */
	public static Parcelable.Creator<Coupon> getCreator() {
		return CREATOR;
	}

	private Coupon(Parcel in) {
		readFromParcel(in);
	}

	@Override
	public int describeContents() {
		return 0;
	}

	/**
	 * write Product Object to parcel
	 */
	@Override
	public void writeToParcel(Parcel out, int flags) {
		out.writeString(title);
		out.writeString(thumbnailImage);
		out.writeValue(isNowDeal);
		out.writeString(dealURL);
		out.writeByteArray(thumbnailBlob);
	}

	/**
	 * read Product Object from Parcel
	 * 
	 * @param in
	 */
	public void readFromParcel(Parcel in) {
		title = in.readString();
		thumbnailImage = in.readString();
		isNowDeal = (Boolean) in.readValue(null);
		dealURL = in.readString();
		thumbnailBlob = new byte[in.readInt()];
	}

	public static final Parcelable.Creator<Coupon> CREATOR = new Parcelable.Creator<Coupon>() {
		public Coupon createFromParcel(Parcel in) {
			return new Coupon(in);
		}

		public Coupon[] newArray(int size) {
			return new Coupon[size];
		}
	};

}
