package com.priceking.cmpe295B;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import Config.MongoConnection;

import com.mongodb.AggregationOutput;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
@Controller
public class ChartsController {
	
	private static final Logger logger = LoggerFactory.getLogger(ChartsController.class);
	@SuppressWarnings("deprecation")
	@RequestMapping(value = "/analytics", method = RequestMethod.GET)
	public String analytics(Locale locale, Model model) {
		logger.info("Welcome home! The client locale is {}.", locale);
		return "analytics";
	}
	@SuppressWarnings("deprecation")
	@RequestMapping(value = "/analyticsChart", method = RequestMethod.GET)
	public @ResponseBody ArrayList<Object>  analyticsChart(Locale locale, Model model) {
		MongoClient mongoClient = MongoConnection.getNewMongoClient();
		ArrayList<Object> response = new ArrayList<Object>();

		DB configDB = mongoClient.getDB("priceking");
		DBCollection productCollection = configDB.getCollection("products");
		DBObject group = new BasicDBObject("$group", new BasicDBObject("_id",new BasicDBObject("category", "$category")).append("count", new BasicDBObject("$sum", 1)) )
		 ;
		HashMap<String,Integer> productMap = new HashMap<String,Integer>();
		HashMap<String,Integer> couponMap = new HashMap<String,Integer>();
		
		AggregationOutput productOutput = productCollection.aggregate(group);
		for (DBObject result : productOutput.results()) {
			 System.out.println("Product-----------------"+result);
			 BasicDBObject id = (BasicDBObject) result.get("_id");
			 productMap.put(id.get("category").toString(), Integer.parseInt(result.get("count").toString()));
		}
		DBCollection couponCollection = configDB.getCollection("coupons");
		DBObject group1 = new BasicDBObject("$group", new BasicDBObject("_id",new BasicDBObject("category", "$category")).append("count", new BasicDBObject("$sum", 1)) );
		
		AggregationOutput couponOutput = couponCollection.aggregate(group);
		for (DBObject result : couponOutput.results()) {
			 System.out.println("Coupon------------------------------------------"+result);
			 BasicDBObject id = (BasicDBObject) result.get("_id");
			 couponMap.put(id.get("category").toString(), Integer.parseInt(result.get("count").toString()));
		}
		BasicDBObject aggregate;
		for(String key: productMap.keySet()){
			aggregate = new BasicDBObject();
			aggregate.put("Category", key);
			aggregate.put("productCount", productMap.get(key));
			if(couponMap.containsKey(key))
				aggregate.put("couponCount", couponMap.get(key));
			else
				aggregate.put("couponCount", 0);
			response.add(aggregate);
		}
		
		for(String key:couponMap.keySet()){
			if(productMap.containsKey(key))
				continue;
			else{
				aggregate = new BasicDBObject();
				aggregate.put("Category", key);
				aggregate.put("productCount", 0);
				aggregate.put("couponCount", couponMap.get(key));
				response.add(aggregate);
			}
		}
		
		for(Object obj:response)
			System.out.println(obj);
		
		return response;
	}
	@SuppressWarnings("deprecation")
	@RequestMapping(value = "/analyticsChart1", method = RequestMethod.GET)
	public @ResponseBody ArrayList<Object>  analyticsChart1(Locale locale, Model model) {
		MongoClient mongoClient = MongoConnection.getNewMongoClient();
		ArrayList<Object> response = new ArrayList<Object>();
		
		DB configDB = mongoClient.getDB("priceking");
		DBCollection productCollection = configDB.getCollection("products");
		DBObject group = new BasicDBObject("$group", new BasicDBObject("_id",new BasicDBObject("name", "$name")).append("count", new BasicDBObject("$sum", 1)) )
		 ;
		System.out.println("gggggggggg"+group);
		HashMap<String,Integer> productMap = new HashMap<String,Integer>();
		HashMap<String,Integer> couponMap = new HashMap<String,Integer>();
		
		AggregationOutput productOutput = productCollection.aggregate(group);
		for (DBObject result : productOutput.results()) {
			 System.out.println("Product-----------------"+result);
			BasicDBObject id = (BasicDBObject) result.get("_id");
			 productMap.put(id.get("name").toString(), Integer.parseInt(result.get("count").toString()));
		}
		DBCollection couponCollection = configDB.getCollection("coupons");
		DBObject group1 = new BasicDBObject("$group", new BasicDBObject("_id",new BasicDBObject("name", "$name")).append("count", new BasicDBObject("$sum", 1)) );
		System.out.println("cpggggg"+group1);
		AggregationOutput couponOutput = couponCollection.aggregate(group);
		for (DBObject result : couponOutput.results()) {
			 System.out.println("Coupon------------------------------------------"+result);
			 BasicDBObject id = (BasicDBObject) result.get("_id");
			 couponMap.put(id.get("name").toString(), Integer.parseInt(result.get("count").toString()));
		}
		BasicDBObject aggregate;
		for(String key: productMap.keySet()){
			aggregate = new BasicDBObject();
			aggregate.put("Name", key);
			
			if(couponMap.containsKey(key))
				aggregate.put("couponCount", couponMap.get(key));
			else
				aggregate.put("couponCount", 0);
			response.add(aggregate);
		}
		
		for(String key:couponMap.keySet()){
			if(productMap.containsKey(key))
				continue;
			else{
				aggregate = new BasicDBObject();
				aggregate.put("Name", key);
				
				aggregate.put("couponCount", couponMap.get(key));
				response.add(aggregate);
			}
		}
		
		for(Object obj:response)
			System.out.println(obj);
		
		return response;
	}
	
	@SuppressWarnings("deprecation")
	@RequestMapping(value = "/analyticsChart3", method = RequestMethod.GET)
	public @ResponseBody ArrayList<Object>  analyticsChart3(Locale locale, Model model) {
		MongoClient mongoClient = MongoConnection.getNewMongoClient();
		ArrayList<Object> response = new ArrayList<Object>();
		
		DB configDB = mongoClient.getDB("priceking");
		DBCollection productCollection = configDB.getCollection("products");
		DBObject group = new BasicDBObject("$group", new BasicDBObject(new BasicDBObject("_id", "$name").append("price",new BasicDBObject( "$push",new BasicDBObject("price","$price"))) ));
		System.out.println("gggggggggg333333"+group);
		
		
		
		
		
		AggregationOutput productOutput = productCollection.aggregate(group);
		BasicDBObject aggregate;
		for (DBObject result : productOutput.results()) {
			aggregate=new BasicDBObject();
			ArrayList<Object> priceArray = new ArrayList<Object>();
			aggregate.put("name", result.get("_id").toString());
			aggregate.put("price", result.get("price"));
			response.add(aggregate);
			
		
		}
		
		
		
		
		
		for(Object obj:response)
			System.out.println(obj);
		
		return response;
	}
	
	
}
