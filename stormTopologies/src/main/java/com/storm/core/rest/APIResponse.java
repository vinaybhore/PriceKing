package com.storm.core.rest;

import java.io.Serializable;

public class APIResponse implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String productName;
	private String productDescription;
	private String productCategory;
	private double price;
	private String thumbnailImage;
	private double customerRating;
	private String productUrl;
	public String getProductName() {
		return productName;
	}
	public void setProductName(String productName) {
		this.productName = productName;
	}
	public String getProductDescription() {
		return productDescription;
	}
	public void setProductDescription(String productDescription) {
		this.productDescription = productDescription;
	}
	public String getProductCategory() {
		return productCategory;
	}
	public void setProductCategory(String productCategory) {
		this.productCategory = productCategory;
	}
	public double getPrice() {
		return price;
	}
	public void setPrice(double price) {
		this.price = price;
	}
	public String getThumbnailImage() {
		return thumbnailImage;
	}
	public void setThumbnailImage(String thumbnailImage) {
		this.thumbnailImage = thumbnailImage;
	}
	public double getCustomerRating() {
		return customerRating;
	}
	public void setCustomerRating(double customerRating) {
		this.customerRating = customerRating;
	}
	public String getProductUrl() {
		return productUrl;
	}
	public void setProductUrl(String productUrl) {
		this.productUrl = productUrl;
	}
	
	public int compareTo(APIResponse response) {
		double comparePrice=((APIResponse)response).getPrice();
        /* For Ascending order*/
        return  (int) (this.price-comparePrice);
	}
	

}
