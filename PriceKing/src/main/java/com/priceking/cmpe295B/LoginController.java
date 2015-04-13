package com.priceking.cmpe295B;

import java.io.IOException;
import java.net.UnknownHostException;
import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.codec.binary.Base64;
import org.apache.taglibs.standard.tag.el.sql.UpdateTag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.w3c.dom.css.Counter;

import Config.MongoConnection;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;

import pojo.Coupons;
import pojo.LoginRequest;
import pojo.LoginResponse;
import pojo.Product;
import pojo.SignUp;

@Controller
public class LoginController {
	
	private static final Logger logger = LoggerFactory.getLogger(HomeController.class);
	
	/**
	 * Simply selects the home view to render by returning its name.
	 */
	@RequestMapping(value = "/signin", method = RequestMethod.GET)
	public String signin(Locale locale, Model model) {
		logger.info("Welcome home! The client locale is {}.", locale);
		
		return "signin";
	}
	
	@RequestMapping(value = "/signup", method = RequestMethod.GET)
	public String signup(Locale locale, Model model) {
		logger.info("Welcome home! The client locale is {}.", locale);
				
		return "signup";
	}
	
	
	private String makePasswordHash(String password) {

		return new String(Base64.encodeBase64(password.getBytes()));

		    }


		private String decodePassword(String password) {

		byte[] decodedBytes = Base64.decodeBase64(password);

		return new String(decodedBytes);

		    }
		
		
	@RequestMapping(value = "/signin", method = RequestMethod.POST, consumes = "application/json")
	public @ResponseBody LoginResponse authenticate(
			@RequestBody LoginRequest loginRequest, Model model,
			HttpServletRequest request, HttpServletResponse response) 
			throws UnknownHostException {
		LoginResponse result = new LoginResponse();
		String username = loginRequest.getUsername();
		String password = loginRequest.getPassword();
		MongoClient mongoClient = MongoConnection.getNewMongoClient();

		DB configDB = mongoClient.getDB("priceking");
		DBCollection profileColl = configDB.getCollection("profiles");
		DBObject profileDoc = profileColl.findOne(new BasicDBObject("username",username));
		if (profileDoc!=null) {
			String passwd = profileDoc.get("password").toString();
			String decoded = decodePassword(passwd);
			if(decoded.equals(password))
			{
				result.setUid(username);
				result.setErrCode(0);
				result.setErrorMessage("success");
				result.setEmail("test@gmail.com");
				System.out.println("Login Successful");
			}
			else
			{
				result.setUid(username);
				result.setErrCode(1);
				result.setErrorMessage("incorrect password");
				result.setEmail(null);
				System.out.println("Login failed");
			}
		}
		else
		{
			result.setUid(username);
			result.setErrCode(1);
			result.setErrorMessage("incorrect username");
			result.setEmail(null);
			System.out.println("Login failed");
		}

		mongoClient.close();

		return result;
		
	}
	
	@RequestMapping(value = "/signup", method = RequestMethod.POST, consumes = "application/json")
	public void register(
			@RequestBody SignUp signup, Model model,
			HttpServletResponse response) 
			throws UnknownHostException {
		
		String firstname = signup.getFirstname();
		String lastname = signup.getLastname();
		String phone = signup.getPhone();
		String email = signup.getEmail();
		String pass = signup.getPassword();
		String pass1 = signup.getPassword1();
		if(pass!=pass1)
		{
			System.out.println("pass!=pass1");
		}
		MongoClient mongoClient = MongoConnection.getNewMongoClient();

		DB configDB = mongoClient.getDB("priceking");
		DBCollection collec = configDB.getCollection("profiles");
		//add to database
		BasicDBObject doc = new BasicDBObject();
		doc.put("firstname", firstname);
		doc.put(lastname, lastname);
		doc.put(phone, phone);
		doc.put("email", email);
		doc.put("password", decodePassword(pass));
DBCollection collec1 = configDB.getCollection("counters");
		
		BasicDBObject query = new BasicDBObject("_id", "vendorid");

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

}
		

