package com.priceking.entity;

import org.json.JSONObject;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Product Entity that contains all information about a single Product
 * 
 * @author DEVEN
 * 
 */
public class Product implements Entitiy, Parcelable {
	private String name;
	private double msrp;
	private double salePrice;
	private String category;
	private String shortDescription;
	private String thumbnailImage;
	private String productURL;
	private double customerRating;
	private byte[] thumbnailBlob;

	public Product() {

	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public double getMsrp() {
		return msrp;
	}

	public void setMsrp(double msrp) {
		this.msrp = msrp;
	}

	public double getSalePrice() {
		return salePrice;
	}

	public void setSalePrice(double salePrice) {
		this.salePrice = salePrice;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getShortDescription() {
		return shortDescription;
	}

	public void setShortDescription(String shortDescription) {
		this.shortDescription = shortDescription;
	}

	public String getThumbnailImage() {
		return thumbnailImage;
	}

	public void setThumbnailImage(String thumbnailImage) {
		this.thumbnailImage = thumbnailImage;
	}

	public String getProductURL() {
		return productURL;
	}

	public void setProductURL(String productURL) {
		this.productURL = productURL;
	}

	public double getCustomerRating() {
		return customerRating;
	}

	public void setCustomerRating(double customerRating) {
		this.customerRating = customerRating;
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

		this.setName(myProduct.has("productName") ? myProduct
				.getString("productName") : "");
		this.setMsrp(myProduct.has("msrp") ? myProduct.getDouble("msrp") : 0);
		this.setSalePrice(myProduct.has("price") ? myProduct.getDouble("price")
				: 0);
		this.setCategory(myProduct.has("productCategory") ? myProduct
				.getString("productCategory") : "");
		this.setShortDescription(myProduct.has("productDescription") ? myProduct
				.getString("productDescription") : "");
		this.setThumbnailImage(myProduct.has("thumbnailImage") ? myProduct
				.getString("thumbnailImage") : "");
		this.setProductURL(myProduct.has("productUrl") ? myProduct
				.getString("productUrl") : "");
		this.setCustomerRating(myProduct.has("customerRating") ? myProduct
				.getDouble("customerRating") : 0.0);

	}

	/**
	 * 
	 * @return creator
	 */
	public static Parcelable.Creator<Product> getCreator() {
		return CREATOR;
	}

	private Product(Parcel in) {
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
		out.writeString(name);
		out.writeDouble(msrp);
		out.writeDouble(salePrice);
		out.writeString(category);
		out.writeString(shortDescription);
		out.writeString(thumbnailImage);
		out.writeString(productURL);
		out.writeDouble(customerRating);
		out.writeByteArray(thumbnailBlob);

	}

	/**
	 * read Product Object from Parcel
	 * 
	 * @param in
	 */
	public void readFromParcel(Parcel in) {
		name = in.readString();
		msrp = in.readDouble();
		salePrice = in.readDouble();
		category = in.readString();
		shortDescription = in.readString();
		thumbnailImage = in.readString();
		productURL = in.readString();
		customerRating = in.readDouble();
		thumbnailBlob = new byte[in.readInt()];
	}

	public static final Parcelable.Creator<Product> CREATOR = new Parcelable.Creator<Product>() {
		public Product createFromParcel(Parcel in) {
			return new Product(in);
		}

		public Product[] newArray(int size) {
			return new Product[size];
		}
	};

}
