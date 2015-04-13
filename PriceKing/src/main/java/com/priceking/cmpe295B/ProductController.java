package com.priceking.cmpe295B;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import pojo.Product;
import Config.MongoConnection;

import com.google.gson.Gson;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;

public class ProductController {
	private static final Logger logger = LoggerFactory.getLogger(ProductController.class);
	
	@RequestMapping(value = "/getProducts", method = RequestMethod.GET)
	public @ResponseBody String getProducts(Locale locale, Model model) {
		logger.info("Fetching products from database:");
		
		MongoClient mongoClient = MongoConnection.getNewMongoClient();

	DB configDB = mongoClient.getDB("priceking");
	DBCollection collec = configDB.getCollection("products");
	BasicDBObject fields = new BasicDBObject();
	fields.put("_id", 0);
	fields.put("name", 1);
	fields.put("brand", 1);
	DBCursor profileDoc = collec.find(new BasicDBObject(),fields);
	Gson gson = new Gson();
	ArrayList<DBObject> products = new ArrayList<DBObject>();
	while(profileDoc.hasNext())
	{
		products.add(profileDoc.next());
	}

	return gson.toJson(products);
}
	
	@RequestMapping(value = "/product/{id}", method = RequestMethod.DELETE, consumes = "application/json")
	public void delete_product(
			HttpServletRequest request, Model model,
			HttpServletResponse response, @PathVariable int id) 
			throws UnknownHostException{
		MongoClient mongoClient = MongoConnection.getNewMongoClient();
		DB configDB = mongoClient.getDB("priceking");
		DBCollection collec = configDB.getCollection("products");
		BasicDBObject doc = new BasicDBObject();
		System.out.println(doc);
		doc.put("_id", id);
		collec.remove(doc);
		System.out.println(doc);
	}
	@RequestMapping(value = "/product/{id}", method = RequestMethod.PUT, consumes = "application/json")
	public void edit_product(
			@RequestBody Product product, Model model,
			HttpServletResponse response, @PathVariable int id) 
			throws UnknownHostException {
		
		BasicDBObject find = new BasicDBObject();
		BasicDBObject update = new BasicDBObject();
		System.out.println(id);
		
		find.put("_id", id);
		update.put("$set",new BasicDBObject("name","shahahahahahaha"));
		String name = product.getName();
		String description = product.getDescription();
		
		String category = product.getCategory();
		
		String pic = product.getPicture();
		String url = product.getProducturl();
		String price = product.getPrice();
	
		MongoClient mongoClient = MongoConnection.getNewMongoClient();

		DB configDB = mongoClient.getDB("priceking");
		DBCollection collec = configDB.getCollection("products");
		//add to database
		BasicDBObject doc = new BasicDBObject();
		doc.put("product_name", name);
		doc.put("product_description", description);
		
		doc.put("product_category", category);
		
		doc.put("product_picture", pic);
		doc.put("product_url", url);
		doc.put("product_price", price);
		System.out.println(doc);
		/*DB configDB = mongoClient.getDB("priceking");
		DBCollection collec = configDB.getCollection("products");
		collec.update(find, update);*/
		
	}
	@RequestMapping(value = "/product", method = RequestMethod.POST, consumes = "application/json")
	public void add_prod(
			@RequestBody Product prod, Model model,
			HttpServletResponse response) 
			throws UnknownHostException {
		
		String name = prod.getName();
		String description = prod.getDescription();
		
		String category = prod.getCategory();
		
		String pic = prod.getPicture();
		String url = prod.getProducturl();
		String price = prod.getPrice();
	
		MongoClient mongoClient = MongoConnection.getNewMongoClient();

		DB configDB = mongoClient.getDB("priceking");
		DBCollection collec = configDB.getCollection("products");
		//add to database
		BasicDBObject doc = new BasicDBObject();
		doc.put("product_name", name);
		doc.put("product_description", description);
		
		doc.put("product_category", category);
		
		doc.put("product_picture", pic);
		doc.put("product_url", url);
		doc.put("product_price", price);
		
		DBCollection collec1 = configDB.getCollection("counters");
		
		BasicDBObject query = new BasicDBObject("_id", "productid");

		BasicDBObject sort = new BasicDBObject();

		BasicDBObject update = new BasicDBObject("$inc", new BasicDBObject("seq", 1));

		BasicDBObject fields = new BasicDBObject();
		
		System.out.println(doc);;



		/*DBObject productid = collec1.findAndModify(query, fields,sort, false, update, true, false);
		String id =  productid.get("seq").toString();
		int pid = (int) Float.valueOf(id).floatValue();
		System.out.println(pid);
		doc.put("_id", pid);
		collec.insert(doc);*/
						
	}

}
