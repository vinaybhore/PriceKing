package com.priceking.cmpe295B;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import oauth.signpost.exception.OAuthCommunicationException;
import oauth.signpost.exception.OAuthExpectationFailedException;
import oauth.signpost.exception.OAuthMessageSignerException;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import pojo.APIResponse;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.kafka.core.TestProducer;
import com.mongodb.BasicDBObject;
import com.mongodb.util.JSONSerializers;
import com.semantics3.api.Products;

import dao.UserDAO;

@Controller
public class APIController {

	private static final Logger logger = LoggerFactory.getLogger(APIController.class);
	private final String USER_AGENT = "Mozilla/5.0";
	
	/*@RequestMapping(value = "/search", method = RequestMethod.GET)
	public @ResponseBody String semanticS3(Locale locale, Model model) throws IllegalStateException, IOException, OAuthMessageSignerException, OAuthExpectationFailedException, OAuthCommunicationException {
		Products products = new Products("SEM3B69184C48CF4F8CADD45699A981CC0A7","MWQyMDI3NWU4YWNjYmI2YTU2YjE5NzhlYTM5NGRkODk");
		products.productsField( "search", "iphone" );

		 Make the Request 
			JSONObject results = products.getProducts();
		 or 
			results = products.get();
	
		 View the results of the Request 
		System.out.println(results);
		Gson gson = new Gson();
		return gson.toJson(results);
	}*/
	
	@RequestMapping(value = "/search", method = RequestMethod.GET)
	public @ResponseBody String searchKeywordOnAPI(@RequestParam String userId,@RequestParam String keyword, Locale locale, Model model) throws IllegalStateException, IOException, OAuthMessageSignerException, OAuthExpectationFailedException, OAuthCommunicationException, InterruptedException {
		BasicDBObject searchObj = new BasicDBObject();
		searchObj.put("username", userId);
		searchObj.put("keyword", keyword);
		
		UserDAO userDAO = UserDAO.getInstance();
		
		boolean isUserValid = userDAO.isValidUser(userId);
		
		//System.out.println(searchObj);
		if(isUserValid)
			TestProducer.produceRequest(searchObj);
		else
			return "Invalid Username";
		
		return null;
	}
	
	
	@RequestMapping(value = "/walmart", method = RequestMethod.GET)
	public @ResponseBody ArrayList<APIResponse> walmartApiRequest() throws IllegalStateException, IOException {
		String url = "http://api.walmartlabs.com/v1/search?apiKey=j4x776bmsuzebx8nm4dxbchj&query=iphone6";
		 
		HttpClient client = new DefaultHttpClient();
		HttpGet request = new HttpGet(url);
		ArrayList<APIResponse> apiResponse = new ArrayList<APIResponse>();
		APIResponse walmartResponse;
 
		// add request header
		request.addHeader("User-Agent", USER_AGENT);
 
		HttpResponse response = client.execute(request);
 
		System.out.println("\nSending 'GET' request to URL : " + url);
		System.out.println("Response Code : " + 
                       response.getStatusLine().getStatusCode());
 
		BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
 
		StringBuffer result = new StringBuffer();
		String line = "";
		while ((line = rd.readLine()) != null) {
			result.append(line);
		}
		
		JSONObject jObject  = new JSONObject(result.toString());
		JSONArray items = jObject.getJSONArray("items");
		for(int i=0;i<items.length();i++){
			walmartResponse = new APIResponse();
			JSONObject item = items.getJSONObject(i);
			System.out.println(item);
			walmartResponse.setProductName(item.get("name").toString());
			walmartResponse.setPrice(Double.parseDouble(item.get("salePrice").toString()));
			walmartResponse.setProductCategory(item.get("categoryPath").toString());
			walmartResponse.setProductDescription(item.get("shortDescription").toString());
			walmartResponse.setThumbnailImage(item.get("thumbnailImage").toString());
			walmartResponse.setProductUrl(item.get("productUrl").toString());
			if(item.has("customerRating")){
			if(item.get("customerRating").toString().isEmpty())
				walmartResponse.setCustomerRating(0);
			else
				walmartResponse.setCustomerRating(Double.parseDouble(item.get("customerRating").toString()));
			}else{
				walmartResponse.setCustomerRating(0);
			}
			apiResponse.add(walmartResponse);
			
		}
		Gson gson = new Gson();
		System.out.println(gson.toJson(apiResponse));
		System.out.println(result.toString());
		return apiResponse;
		//return "adminDashboard";
	}
	
	@RequestMapping(value="/ebay", method=RequestMethod.GET)
	public @ResponseBody ArrayList<APIResponse> eBayApiRequest() throws ClientProtocolException, IOException{
		System.out.println("Fetching result from ebay");
		String url = "http://svcs.ebay.com/services/search/FindingService/v1?OPERATION-NAME=findItemsByKeywords&SECURITY-APPNAME=hardikjo-01e9-43b1-b721-ada76fc3b2f4&keywords=iphone6&RESPONSE-DATA-FORMAT=JSON";
		HttpClient client = new DefaultHttpClient();
		HttpGet request = new HttpGet(url);
 
		// add request header
		request.setHeader("User-Agent", USER_AGENT);
 
		HttpResponse response = client.execute(request);
		BufferedReader rd = new BufferedReader(
                new InputStreamReader(response.getEntity().getContent()));
		
		ArrayList<APIResponse> apiResponse = new ArrayList<APIResponse>();
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
		
		for(int i=0;i<items.length();i++){
			ebayResponse = new APIResponse();
			JSONObject item = items.getJSONObject(i);
			
			JSONArray sellingStatusArr = item.getJSONArray("sellingStatus");
			JSONObject sellingStatus = sellingStatusArr.getJSONObject(0);
			JSONArray currentPriceArr = sellingStatus.getJSONArray("currentPrice");
			JSONObject currentPriceObj =  currentPriceArr.getJSONObject(0);
			
			JSONArray primaryCategoryArr = item.getJSONArray("primaryCategory");
			JSONObject primaryCategory = primaryCategoryArr.getJSONObject(0);
			JSONArray categoryNameArr = primaryCategory.getJSONArray("categoryName");
			
			
			System.out.println(item.getJSONArray("title").get(0));
			ebayResponse.setProductName(item.getJSONArray("title").get(0).toString());
			ebayResponse.setPrice(Double.parseDouble(currentPriceObj.getString("__value__")));
			ebayResponse.setProductCategory(categoryNameArr.get(0).toString());
			ebayResponse.setProductDescription("N/A");
			ebayResponse.setThumbnailImage(item.get("galleryURL").toString());
			ebayResponse.setProductUrl(item.get("viewItemURL").toString());
			ebayResponse.setCustomerRating(0);
			apiResponse.add(ebayResponse);
			
		}
		Gson gson = new Gson();
		System.out.println(gson.toJson(apiResponse));
		return apiResponse;
	}
	
	@RequestMapping(value = "/bestbuy", method = RequestMethod.GET)
	public @ResponseBody ArrayList<APIResponse> bestBuyApiRequest() throws IllegalStateException, IOException {
		String keyword = "iphone5";
		String url = "http://api.remix.bestbuy.com/v1/products(search="+keyword+")?show=all&format=json&apiKey=x9wtbhpvwfp8kgx86ajzysr3";
		 
		HttpClient client = new DefaultHttpClient();
		HttpGet request = new HttpGet(url);
		ArrayList<APIResponse> apiResponse = new ArrayList<APIResponse>();
		APIResponse walmartResponse;
 
		// add request header
		request.addHeader("User-Agent", USER_AGENT);
 
		HttpResponse response = client.execute(request);
 
		System.out.println("\nSending 'GET' request to URL : " + url);
		System.out.println("Response Code : " + 
                       response.getStatusLine().getStatusCode());
 
		BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
 
		StringBuffer result = new StringBuffer();
		String line = "";
		while ((line = rd.readLine()) != null) {
			result.append(line);
		}
		
		JSONObject jObject  = new JSONObject(result.toString());
		JSONArray items = jObject.getJSONArray("products");
		System.out.println(items);
		for(int i=0;i<items.length();i++){
			walmartResponse = new APIResponse();
			JSONObject item = items.getJSONObject(i);
			System.out.println(item.get("name"));
			walmartResponse.setProductName(item.get("name").toString());
			walmartResponse.setPrice(Double.parseDouble(item.get("salePrice").toString()));
			JSONArray categoryPath = item.getJSONArray("categoryPath");
			String categorypath="";
			if(categoryPath.length()>0){
				for(int j=0;j<categoryPath.length();j++){
					JSONObject category = categoryPath.getJSONObject(i);
					if(j==categoryPath.length()-1)
						categorypath+=categorypath + category.get("name");
					else
						categorypath+=categorypath + category.get("name") +"/";
				}
			}
			walmartResponse.setProductCategory(categorypath);
			walmartResponse.setProductDescription(item.get("shortDescription").toString());
			walmartResponse.setThumbnailImage(item.get("thumbnailImage").toString());
			walmartResponse.setProductUrl(item.get("url").toString());
			if(item.get("customerReviewCount").toString().isEmpty() || "null".equals(item.get("customerReviewCount").toString()))
				walmartResponse.setCustomerRating(0);
			else
				walmartResponse.setCustomerRating(Double.parseDouble(item.get("customerReviewCount").toString()));
			apiResponse.add(walmartResponse);
			
		}
		Gson gson = new Gson();
		System.out.println(gson.toJson(apiResponse));
		System.out.println(result.toString());
		
		return apiResponse;
	}
	@RequestMapping(value = "/compare", method = RequestMethod.GET)
	public @ResponseBody ArrayList<APIResponse> comparator() throws IllegalStateException, IOException{
		ArrayList<APIResponse> walmartResponse = walmartApiRequest();
		ArrayList<APIResponse> ebayResponse = eBayApiRequest();
		ArrayList<APIResponse> bestBuyResponse = bestBuyApiRequest();
		
		ArrayList<ArrayList<APIResponse>> aggregated = new ArrayList<ArrayList<APIResponse>>();
		aggregated.add(walmartResponse);
		aggregated.add(ebayResponse);
		aggregated.add(bestBuyResponse);
		
		ArrayList<APIResponse> sorted = compareAndSort(aggregated);
		return sorted;
		
	}


	private ArrayList<APIResponse> compareAndSort(
			ArrayList<ArrayList<APIResponse>> aggregated) {
		ArrayList<APIResponse> listToSort = new ArrayList<APIResponse>();
		for (List <APIResponse> innerList: aggregated) {
			for(APIResponse response:innerList){
				listToSort.add(response);
			}
		}
		
		Collections.sort(listToSort, new Comparator<APIResponse>() {
		       public int compare(APIResponse o1, APIResponse o2) {
		            //Sorts by 'price' property
		            return o1.getPrice()<o2.getPrice()?-1:o1.getPrice()>o2.getPrice()?1:0;
		       }

		 });
		
		return listToSort;
	}
}
