package com.priceking.cmpe295B;

import java.io.IOException;
import java.net.UnknownHostException;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import Config.MongoConnection;

import com.google.gson.Gson;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;

import pojo.Coupons;
import pojo.LoginRequest;
import pojo.LoginResponse;
import pojo.SignUp;

/**
 * Handles requests for the application home page.
 */
@Controller
public class HomeController {
	
	private static final Logger logger = LoggerFactory.getLogger(HomeController.class);
	
	@RequestMapping(value = "/adminDashboard", method = RequestMethod.GET)
	public String home1(Locale locale, Model model) {
		logger.info("Welcome home! The client locale is {}.", locale);
		
		
		return "adminDashboard";
	}
	
	
	
	@RequestMapping(value = "/vendorDashboard", method = RequestMethod.GET)
	public String dashboard(Locale locale, Model model) {
		logger.info("Welcome home! The client locale is {}.", locale);
		
		
		return "vendorDashboard";
	}
		
	@RequestMapping(value = "/getUsers", method = RequestMethod.GET)
	public @ResponseBody String getUsers(Locale locale, Model model) {
		logger.info("Fetching users from database:");
		
		MongoClient mongoClient = MongoConnection.getNewMongoClient();

	DB configDB = mongoClient.getDB("priceking");
	DBCollection collec = configDB.getCollection("profiles");
	BasicDBObject fields = new BasicDBObject();
	fields.put("_id", 0);
	fields.put("firstname", 1);
	fields.put("lastname", 1);
		
	DBCursor profileDoc = collec.find(new BasicDBObject(),fields);
	Gson gson = new Gson();
	ArrayList<DBObject> users = new ArrayList<DBObject>();
	while(profileDoc.hasNext())
	{
		users.add(profileDoc.next());
	}

	return gson.toJson(users);
}
	@RequestMapping(value = "/users", method = RequestMethod.POST, consumes = "application/json")
	public @ResponseBody LoginResponse authenticate(
			@RequestBody SignUp user, Model model,
			HttpServletRequest request, HttpServletResponse response) 
			throws UnknownHostException {
		BasicDBObject adduser = new BasicDBObject();
		adduser.put("firstname",user.getFirstname());	
		adduser.put("lastname", user.getLastname());
		adduser.put("email", user.getEmail());
		adduser.put("phone", user.getPhone());
		System.out.println(adduser);
		MongoClient mongoClient = MongoConnection.getNewMongoClient();
		DB configDB = mongoClient.getDB("priceking");
		DBCollection collec = configDB.getCollection("profiles");
		DBCollection collec1 = configDB.getCollection("counters");
		
		BasicDBObject query = new BasicDBObject("_id", "vendorid");

		BasicDBObject sort = new BasicDBObject();

		BasicDBObject update = new BasicDBObject("$inc", new BasicDBObject("seq", 1));

		BasicDBObject fields = new BasicDBObject();
		
		System.out.println(adduser);;



		DBObject productid = collec1.findAndModify(query, fields,sort, false, update, true, false);
		String id =  productid.get("seq").toString();
		int pid = (int) Float.valueOf(id).floatValue();
		System.out.println(pid);
		adduser.put("_id", pid);
		collec.insert(adduser);

		return null;
				
	}
	@RequestMapping(value = "/users/{id}", method = RequestMethod.PUT, consumes = "application/json")
	public void edit_user(
			@RequestBody SignUp editusers, Model model,
			HttpServletResponse response, @PathVariable int id) 
			throws UnknownHostException{
		
		BasicDBObject find = new BasicDBObject();
		BasicDBObject update = new BasicDBObject();
		find.put("_id", id);
		update.put("$set",new BasicDBObject("firstname","shahahahahahaha"));
		String firstname = editusers.getFirstname();
		String lastname = editusers.getLastname();
		String phone = editusers.getPhone();
		String email = editusers.getEmail();
		MongoClient mongoClient = MongoConnection.getNewMongoClient();

		DB configDB = mongoClient.getDB("priceking");
		DBCollection collec = configDB.getCollection("profiles");
		BasicDBObject doc = new BasicDBObject();
		doc.put("firstname", firstname);
		doc.put("lastname", lastname);
		doc.put("email", email);
		doc.put("phone", phone);
		System.out.println(doc);
		//collec.update(find, update);
		
	}
	
	
	
	/**
	 * Simply selects the home view to render by returning its name.
	 */
	@RequestMapping(value = "/", method = RequestMethod.GET)
	public String home(Locale locale, Model model) {
		logger.info("Welcome home! The client locale is {}.", locale);
		
		Date date = new Date();
		DateFormat dateFormat = DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.LONG, locale);
		
		String formattedDate = dateFormat.format(date);
		
		model.addAttribute("serverTime", formattedDate );
		
		return "home";
	}
	
	@RequestMapping(value = "/user/{id}", method = RequestMethod.DELETE, consumes = "application/json")
	public void delete_users(
			HttpServletRequest request, Model model,
			HttpServletResponse response, @PathVariable int id) 
			throws UnknownHostException{
		MongoClient mongoClient = MongoConnection.getNewMongoClient();
		DB configDB = mongoClient.getDB("priceking");
		DBCollection collec = configDB.getCollection("profiles");
		BasicDBObject doc = new BasicDBObject();
		System.out.println(doc);
		doc.put("_id", id);
		collec.remove(doc);
		System.out.println(doc);
	}
	/*@RequestMapping(value = "/getProducts", method = RequestMethod.GET)
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
}*/
		
}
