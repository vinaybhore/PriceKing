package com.priceking.cmpe295B;

import java.net.UnknownHostException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import pojo.Coupons;
import Config.MongoConnection;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;

public class CouponController {
	
	private static final Logger logger = LoggerFactory.getLogger(CouponController.class);
	@RequestMapping(value = "/coupon", method = RequestMethod.POST, consumes = "application/json")
	public void add_coup(
			@RequestBody Coupons coup, Model model,
			HttpServletResponse response) 
			throws UnknownHostException {
		
		String name = coup.getName();
		String description = coup.getDescription();
		
	
		MongoClient mongoClient = MongoConnection.getNewMongoClient();

		DB configDB = mongoClient.getDB("priceking");
		DBCollection collec = configDB.getCollection("coupons");
		//add to database
		BasicDBObject doc = new BasicDBObject();
		doc.put("name", name);
		doc.put("description", description);
		
		DBCollection collec1 = configDB.getCollection("counters");
		
		BasicDBObject query = new BasicDBObject("_id", "couponid");

		BasicDBObject sort = new BasicDBObject();

		BasicDBObject update = new BasicDBObject("$inc", new BasicDBObject(

		"seq", 1));

		BasicDBObject fields = new BasicDBObject();



		DBObject productid = collec1.findAndModify(query, fields,

		sort, false, update, true, false);
		String id =  productid.get("seq").toString();
		int pid = (int) Float.valueOf(id).floatValue();
		System.out.println(pid);
		doc.put("_id", pid);
		collec.insert(doc);
	}
	
	@RequestMapping(value = "/coupon/{id}", method = RequestMethod.PUT, consumes = "application/json")
	public void edit_coupon(
			@RequestBody Coupons coupon, Model model,
			HttpServletResponse response, @PathVariable int id) 
			throws UnknownHostException{
		
		BasicDBObject find = new BasicDBObject();
		BasicDBObject update = new BasicDBObject();
		find.put("_id", id);
		update.put("$set",new BasicDBObject("name","shahahahahahaha"));
		String name = coupon.getName();
		String description = coupon.getDescription();
		MongoClient mongoClient = MongoConnection.getNewMongoClient();

		DB configDB = mongoClient.getDB("priceking");
		DBCollection collec = configDB.getCollection("coupons");
		BasicDBObject doc = new BasicDBObject();
		doc.put("name", name);
		doc.put("description", description);
		System.out.println(doc);
		//collec.update(find, update);
		
	}
	
	@RequestMapping(value = "/coupon/{id}", method = RequestMethod.DELETE, consumes = "application/json")
	public void delete_coupon(
			HttpServletRequest request, Model model,
			HttpServletResponse response, @PathVariable int id) 
			throws UnknownHostException{
		MongoClient mongoClient = MongoConnection.getNewMongoClient();
		DB configDB = mongoClient.getDB("priceking");
		DBCollection collec = configDB.getCollection("coupons");
		BasicDBObject doc = new BasicDBObject();
		System.out.println(doc);
		doc.put("_id", id);
		collec.remove(doc);
		System.out.println(doc);
	}
	

}
