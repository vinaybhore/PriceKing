package com.storm.core.rest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;

import com.google.gson.Gson;


@Controller
public class APIController {

	private static final Logger logger = LoggerFactory.getLogger(APIController.class);
	private final String USER_AGENT = "Mozilla/5.0";
	public TestProducer producer = new TestProducer();
	public List<String> walmartCall(String searchKey) throws IllegalStateException, IOException {
		// searchKey = URLDecoder.decode(searchKey, "utf-8");
		searchKey = URLEncoder.encode(searchKey, "utf-8");
		String url = "http://api.walmartlabs.com/v1/search?query=" + searchKey + "&format=json&facet=on&apiKey=cw6dbssy2uetx7t67adabjhn";
		HttpClient client = new DefaultHttpClient();
		HttpGet request = new HttpGet(url);
		ArrayList<String> apiResponse = new ArrayList<String>();
		APIResponse walmartResponse;

		// add request header
		request.addHeader("User-Agent", USER_AGENT);

		HttpResponse response = client.execute(request);
		if (response.getStatusLine().getStatusCode() == 200) {
			System.out.println("\nSending 'GET' request to URL : " + url);
			System.out.println("Response Code : " + response.getStatusLine().getStatusCode());

			BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));

			StringBuffer result = new StringBuffer();
			String line = "";
			while ((line = rd.readLine()) != null) {
				result.append(line);
			}
			Gson gson = new Gson();

			JSONObject jObject = new JSONObject(result.toString());
			if (jObject.has("items")) {
				JSONArray items = jObject.getJSONArray("items");
				try {
					for (int i = 0; i < items.length(); i++) {
						walmartResponse = new APIResponse();
						JSONObject item = items.getJSONObject(i);
						System.out.println(item.get("name"));
						if (item.has("name")) {
							if (item.get("name").toString().isEmpty()) {
								walmartResponse.setProductName("No product name available");
							} else {
								walmartResponse.setProductName(item.get("name").toString());
							}
						} else {
							walmartResponse.setProductName("No product name available");
						}
						if (item.has("salePrice")) {
							if (item.get("salePrice").toString().isEmpty()) {
								walmartResponse.setPrice(0);
							} else {
								walmartResponse.setPrice(Double.parseDouble(item.get("salePrice").toString()));
							}
						} else {
							walmartResponse.setPrice(0);
						}

						if (item.has("categoryPath")) {
							if (item.get("categoryPath").toString().isEmpty()) {
								walmartResponse.setProductCategory("NA");
							} else {
								walmartResponse.setProductCategory(item.get("categoryPath").toString());
							}
						} else {
							walmartResponse.setProductCategory("NA");
						}

						if (item.has("shortDescription")) {
							if (item.get("shortDescription").toString().isEmpty()) {
								walmartResponse.setProductDescription("NA");
							} else {
								walmartResponse.setProductDescription(item.get("shortDescription").toString());
							}
						} else {
							walmartResponse.setProductDescription("NA");
						}
						if (item.has("thumbnailImage")) {
							if (item.get("thumbnailImage").toString().isEmpty()) {
								walmartResponse.setThumbnailImage("No thumbnail image");
							} else {
								walmartResponse.setThumbnailImage(item.get("thumbnailImage").toString());
							}
						} else {
							walmartResponse.setThumbnailImage("No thumbnail image");
						}
						if (item.has("productUrl")) {
							if (item.get("productUrl").toString().isEmpty()) {
								walmartResponse.setProductUrl("No product URL");
							} else {
								walmartResponse.setProductUrl(item.get("productUrl").toString());
							}
						} else {
							walmartResponse.setProductUrl("http://www.walmart.com/");
						}

						if (item.has("customerRating")) {
							if (item.get("customerRating").toString().isEmpty()) {
								walmartResponse.setCustomerRating(0);
							} else {
								walmartResponse.setCustomerRating(Double.parseDouble(item.get("customerRating").toString()));
							}
						} else {
							walmartResponse.setCustomerRating(0);
						}

						apiResponse.add(gson.toJson(walmartResponse));

					}
					// return apiResponse;

				} catch (RuntimeException ex) {
					return null;
				}

			}
		}
		return apiResponse;

	}
	
	public List<String> ebayCall(String searchKey) throws ClientProtocolException, IOException{
		searchKey =  URLEncoder.encode(searchKey, "utf-8");
		String url = "http://svcs.ebay.com/services/search/FindingService/v1?OPERATION-NAME=findItemsByKeywords&SECURITY-APPNAME=hardikjo-01e9-43b1-b721-ada76fc3b2f4&keywords="+searchKey+"&RESPONSE-DATA-FORMAT=JSON";
		HttpClient client = new DefaultHttpClient();
		HttpGet request = new HttpGet(url);
 
		// add request header
		request.setHeader("User-Agent", USER_AGENT);
 
		HttpResponse response = client.execute(request);
		BufferedReader rd = new BufferedReader(
                new InputStreamReader(response.getEntity().getContent()));
		
		ArrayList<String> apiResponse = new ArrayList<String>();
		APIResponse ebayResponse;

		StringBuffer result = new StringBuffer();
		String line = "";
		while ((line = rd.readLine()) != null) {
			result.append(line);
		}

		JSONObject jObject  = new JSONObject(result.toString());
		JSONArray serverResponse = jObject.getJSONArray("findItemsByKeywordsResponse");
		JSONObject searchArray = serverResponse.getJSONObject(0);
		JSONArray searchResult = searchArray.getJSONArray("searchResult");
		System.out.println(searchResult);
		JSONObject itemObject = (JSONObject) searchResult.get(0);
		JSONArray items = itemObject.getJSONArray("item");
		Gson gson = new Gson();
		for(int i=0;i<items.length();i++){
			
			ebayResponse = new APIResponse();
			JSONObject item = items.getJSONObject(i);
			JSONArray purchaseAvailibility = item.getJSONArray("listingInfo");
			JSONObject buyItNowAvailableArr = purchaseAvailibility.getJSONObject(0);
			JSONArray buyItNowAvailable = buyItNowAvailableArr.getJSONArray("buyItNowAvailable");
			String canBuyNow = buyItNowAvailable.getString(0);

			if(canBuyNow.equals("true")){
				JSONArray sellingStatusArr = item.getJSONArray("sellingStatus");
				JSONObject sellingStatus = sellingStatusArr.getJSONObject(0);
				JSONArray currentPriceArr = sellingStatus.getJSONArray("currentPrice");
				JSONObject currentPriceObj =  currentPriceArr.getJSONObject(0);
				
				JSONArray primaryCategoryArr = item.getJSONArray("primaryCategory");
				JSONObject primaryCategory = primaryCategoryArr.getJSONObject(0);
				JSONArray categoryNameArr = primaryCategory.getJSONArray("categoryName");
				
				System.out.println(item.getJSONArray("listingInfo"));
				System.out.println(item.getJSONArray("title").get(0));
				ebayResponse.setProductName(item.getJSONArray("title").get(0).toString());
				ebayResponse.setPrice(Double.parseDouble(currentPriceObj.getString("__value__")));
				ebayResponse.setProductCategory(categoryNameArr.get(0).toString());
				if(item.has("subtitle")){
					ebayResponse.setProductDescription(item.getJSONArray("subtitle").get(0).toString());
				}else{
					ebayResponse.setProductDescription("NA");
				}
				
				if(item.has("galleryURL")){
					ebayResponse.setThumbnailImage(item.getJSONArray("galleryURL").getString(0).toString());
				}else{
					ebayResponse.setThumbnailImage("NA");
				}
				ebayResponse.setProductUrl(item.getJSONArray("viewItemURL").getString(0).toString());
				ebayResponse.setCustomerRating(0);
				apiResponse.add(gson.toJson(ebayResponse));
			}
			
			
		}
		
		return apiResponse;
	}
	
	public List<String> bestBuyCall(String searchKey) throws ClientProtocolException, IOException{
		searchKey =  URLEncoder.encode(searchKey, "utf-8");
		String url = "http://api.remix.bestbuy.com/v1/products(search="+searchKey+")?show=all&format=json&apiKey=x9wtbhpvwfp8kgx86ajzysr3";
		 
		HttpClient client = new DefaultHttpClient();
		HttpGet request = new HttpGet(url);
		ArrayList<String> apiResponse = new ArrayList<String>();
		APIResponse bestBuyResponse;
 
		// add request header
		request.addHeader("User-Agent", USER_AGENT);
 
		HttpResponse response = client.execute(request);
 
		System.out.println("\nSending 'GET' request to URL : " + url);
		System.out.println("Response Code : " + 
                       response.getStatusLine().getStatusCode());
		if(response.getStatusLine().getStatusCode()!=400 && response.getStatusLine().getStatusCode()!=403 &&  response.getStatusLine().getStatusCode()!=500){
			BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
			 
			StringBuffer result = new StringBuffer();
			String line = "";
			while ((line = rd.readLine()) != null) {
				result.append(line);
			}
			
			Gson gson = new Gson();
			JSONObject jObject  = new JSONObject(result.toString());
			if(jObject.has("products")){
			JSONArray items = jObject.getJSONArray("products");
			System.out.println(items);
			for(int i=0;i<items.length();i++){
				bestBuyResponse = new APIResponse();
				JSONObject item = items.getJSONObject(i);
				System.out.println(item.get("name"));
				bestBuyResponse.setProductName(item.get("name").toString());
				bestBuyResponse.setPrice(Double.parseDouble(item.get("salePrice").toString()));
				JSONArray categoryPath = item.getJSONArray("categoryPath");
				String categorypath="";
				if(categoryPath.length()>0){
					for(int j=0;j<categoryPath.length();j++){
						JSONObject category = categoryPath.getJSONObject(j);
						if(j==categoryPath.length()-1)
							categorypath+=categorypath + category.get("name");
						else
							categorypath+=categorypath + category.get("name") +"/";
					}
				}
				bestBuyResponse.setProductCategory(categorypath);
				bestBuyResponse.setProductDescription(item.get("shortDescription").toString());
				bestBuyResponse.setThumbnailImage(item.get("thumbnailImage").toString());
				bestBuyResponse.setProductUrl(item.get("url").toString());
				if(item.get("customerReviewCount").toString().isEmpty() || "null".equals(item.get("customerReviewCount").toString())){
					bestBuyResponse.setCustomerRating(0);
				}else{
					bestBuyResponse.setCustomerRating(Double.parseDouble(item.get("customerReviewCount").toString()));
				}
				apiResponse.add(gson.toJson(bestBuyResponse));
				
			}
			}
		}
 
		return apiResponse;
	}
	
	public String compareAndSort(String input) {
		Gson gson = new Gson();
		ArrayList<APIResponse> listToSort = new ArrayList<APIResponse>();
		System.out.println("response came here::");
		System.out.println(gson.toJson(input));
		JSONArray responseArray = new JSONArray(input);
		for(int i=0;i<responseArray.length();i++){
			
				JSONObject responseObj = responseArray.getJSONObject(i);
				APIResponse response = new APIResponse();
				response.setProductName(responseObj.getString("productName"));
				response.setProductDescription(responseObj.getString("productDescription"));
				response.setProductCategory(responseObj.getString("productCategory"));
				response.setPrice(Double.parseDouble(responseObj.get("price").toString()));
				response.setProductUrl(responseObj.getString("productUrl"));
				response.setThumbnailImage(responseObj.getString("thumbnailImage"));
				response.setCustomerRating(Double.parseDouble(responseObj.get("customerRating").toString()));
				listToSort.add(response);
		}
		
		
		Collections.sort(listToSort, new Comparator<APIResponse>() {
		       public int compare(APIResponse o1, APIResponse o2) {
		            //Sorts by 'price' property
		            return o1.getPrice()<o2.getPrice()?-1:o1.getPrice()>o2.getPrice()?1:0;
		       }

		 });
		
		System.out.println(gson.toJson(listToSort));
		return gson.toJson(listToSort);
	}
}
