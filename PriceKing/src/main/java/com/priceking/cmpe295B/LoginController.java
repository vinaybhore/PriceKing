package com.priceking.cmpe295B;

import java.io.IOException;
import java.net.UnknownHostException;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
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
import Config.PricekingConfig;

import com.google.gson.Gson;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.ServerAddress;

import dao.SessionDAO;
import dao.UserDAO;
import pojo.Coupons;
import pojo.LoginRequest;
import pojo.LoginResponse;
import pojo.Product;
import pojo.SignUp;

@Controller
public class LoginController {
	
	private static final Logger logger = LoggerFactory.getLogger(HomeController.class);
	
	
		private final UserDAO userDAO;
		private final SessionDAO sessionDAO;
		private final String pricekingDB = PricekingConfig.DBNAME;

		public LoginController() throws UnknownHostException {
			MongoClient mongoClient = new MongoClient(new ServerAddress(
					"localhost", 27017));
			// final MongoClient mongoClient = new MongoClient(new
			// MongoClientURI(mongoURIString));
			final DB pricekingDB = mongoClient.getDB(PricekingConfig.DBNAME);

			// blogPostDAO = new BlogPostDAO(blogDatabase);
			userDAO = new UserDAO(pricekingDB);
			sessionDAO = new SessionDAO(pricekingDB);
			// sessionState.put("session","0");
		}
	
	
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
	
		@RequestMapping(value = "/signin", method = RequestMethod.POST, consumes = "application/json")
		public @ResponseBody LoginResponse authenticate(
			@RequestBody LoginRequest loginRequest, Model model,
			HttpServletRequest request, HttpServletResponse response) 
			throws UnknownHostException {
		LoginResponse result = new LoginResponse();
		String username = loginRequest.getUsername();
		String password = loginRequest.getPassword();
		
		UserDAO userDAO = UserDAO.getInstance();
		userDAO.validateLogin(username, password, result);
		return result;		
	}	
	@RequestMapping(value = "/signup", method = RequestMethod.POST, consumes = "application/json")
	public void register(
			@RequestBody SignUp signup, Model model,
			HttpServletResponse response) 
			throws UnknownHostException {	
		UserDAO userDAO = UserDAO.getInstance();
		userDAO.addUser(signup);
		/*String firstname = signup.getFirstname();
		String lastname = signup.getLastname();
		String phone = signup.getPhone();
		String email = signup.getEmail();
		String pass = signup.getPassword();
		String pass1 = signup.getPassword1();
		if(pass!=pass1){
			System.out.println("pass!=pass1");
		}
		MongoClient mongoClient = MongoConnection.getNewMongoClient();
		DB configDB = mongoClient.getDB("priceking");
		DBCollection collec = configDB.getCollection("profiles");
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
		collec.insert(doc);*/			
	}
	
	@RequestMapping(value = "checkUserName", method = RequestMethod.GET)
	public @ResponseBody String checkUserName(@RequestParam (value="name") String name){
		//System.out.println(name);
		
		Gson gson = new Gson();
		MongoClient mongoClient;
		
			try {
				mongoClient = new MongoClient(PricekingConfig.SERVER, PricekingConfig.PORT);
				DB db = mongoClient.getDB(pricekingDB);
				DBCollection coll = db.getCollection("profiles");
				
				BasicDBObject query = new BasicDBObject();
				query.put("_id", name);
				DBObject userDoc = coll.findOne(query);
				boolean usernameFound = false;
				if(userDoc!=null)
					usernameFound = true;
				mongoClient.close();
				if(usernameFound)
					return gson.toJson("BAD_REQUEST");
				
			} catch (UnknownHostException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
					
		return gson.toJson("ACCEPTED");
	}


}
		

