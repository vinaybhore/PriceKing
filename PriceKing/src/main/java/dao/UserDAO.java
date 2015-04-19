package dao;

import java.io.UnsupportedEncodingException;
import java.net.UnknownHostException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Random;

import pojo.LoginResponse;
import pojo.SignUp;
import sun.misc.BASE64Encoder;
import Config.MongoConnection;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.MongoException;
import com.mongodb.ServerAddress;

public class UserDAO {
	private static UserDAO instance;
	private final DBCollection usersCollection;
	
	private  DBCollection fbusersCollection;
	private Random random = new SecureRandom();
	
	public UserDAO(final DB projectDB){
		usersCollection = projectDB.getCollection("profiles");
	}
	
	 public static UserDAO getInstance() throws UnknownHostException {
	    	if(instance == null){
	    		MongoClient mongoClient = new MongoClient(new ServerAddress(
	    				"localhost", 27017));
	    		final DB pricekingDB = mongoClient.getDB("priceking");
	    		instance = new UserDAO(pricekingDB);
	    	}
	    	return instance;
	    }
	
	
	 	/**
		 * Adding new user to user collection.
		 * Return true for successful signup else false if username is already exists in the collection.
		 * 
		 * 
		 */
	 public boolean addUser(SignUp newUser) {

		 	/*Password encryption for keeping it secure */
	        String passwordHash = makePasswordHash(newUser.getPassword(), Integer.toString(random.nextInt()));

	        BasicDBObject user = new BasicDBObject();

	        user.append("_id", newUser.getUsername()).append("password", passwordHash).append("firstname", newUser.getFirstname()).append("lastname", newUser.getLastname()).append("email", newUser.getEmail()).append("phone", newUser.getPhone());

	        try {
	            usersCollection.insert(user);
	            return true;
	        } catch (MongoException.DuplicateKey e) {
	            System.out.println("Username already in use: " + newUser.getUsername());
	            return false;
	        }
	    }
	
	
	public LoginResponse validateLogin(String username, String password, LoginResponse result){
		
		
		DBObject profileDoc = usersCollection.findOne(new BasicDBObject("_id",username));

        if (profileDoc == null) {
    		result.setUid(username);
			result.setErrCode(1);
			result.setErrorMessage("Incorrect Username");
			result.setEmail(null);
			System.out.println("Login failed");
        }
        MongoClient mongoClient = MongoConnection.getNewMongoClient();
		
		if (profileDoc!=null) {
		    String hashedAndSalted = profileDoc.get("password").toString();
	        
	        System.out.println(hashedAndSalted);

	        String salt = hashedAndSalted.split(",")[1];
	        
	        String passwd = makePasswordHash(password, salt);
	        
	        System.out.println(passwd);

	        if (!hashedAndSalted.equals(passwd)) {
	        	result.setUid(username);
				result.setErrCode(1);
				result.setErrorMessage("Incorrect Password");
				result.setEmail(null);
				System.out.println("Login failed");
	        }
	        else{
				result.setUid(username);
				result.setErrCode(0);
				result.setErrorMessage("success");
				result.setEmail(profileDoc.get("email").toString());
				System.out.println("Login Successful");
			}
		}
		mongoClient.close();
	

        return result;
	}
	
	public boolean isValidUser(String username){
		
		boolean isValidUser = false;
		DBObject profileDoc = usersCollection.findOne(new BasicDBObject("_id",username));
		if(profileDoc!=null)
			isValidUser = true;
		return isValidUser;
	}
	
	/*Password encryption is done using MD5 hash algorithm. Randomly generated integer gets appended to password. 
	 *Then the resulted string will be encoded using MD5 BASE64Encoder */
	private String makePasswordHash(String password, String salt) {
        try {
            String saltedAndHashed = password + "," + salt;
            MessageDigest digest = MessageDigest.getInstance("MD5");
            digest.update(saltedAndHashed.getBytes());
            BASE64Encoder encoder = new BASE64Encoder();
            byte hashedBytes[] = (new String(digest.digest(), "UTF-8")).getBytes();
            System.out.println(hashedBytes[0]);
            return encoder.encode(hashedBytes) + "," + salt;
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("MD5 is not available", e);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("UTF-8 unavailable?  Not a chance", e);
        }
    }

}
