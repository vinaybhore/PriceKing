package dao;

import java.net.UnknownHostException;
import java.security.SecureRandom;

import sun.misc.BASE64Encoder;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.ServerAddress;

public class SessionDAO {

	private static DBCollection sessionsCollection;

 	private static SessionDAO instance;
 	
    public SessionDAO(final DB projectDB) {
        sessionsCollection = projectDB.getCollection("session");
    }
    
    public static SessionDAO getInstance() throws UnknownHostException {
    	if(instance == null){
    		MongoClient mongoClient = new MongoClient(new ServerAddress(
    				"localhost", 27017));
    		final DB pricekingDB = mongoClient.getDB("priceking");
    		instance = new SessionDAO(pricekingDB);
    	}
    	return instance;
    }
    
    
    
    public  String startSession(String username) {

        // get 32 byte random number. that's a lot of bits.
        SecureRandom generator = new SecureRandom();
        byte randomBytes[] = new byte[32];
        generator.nextBytes(randomBytes);

        BASE64Encoder encoder = new BASE64Encoder();

        String sessionID = encoder.encode(randomBytes);
        
        System.out.println("SessioId: "+ sessionID);

        // build the BSON object
        System.out.println("Username"+username);
        BasicDBObject session = new BasicDBObject("username", username);

        session.append("_id", sessionID);
        sessionsCollection.insert(session);

        return session.getString("_id");
    }
    
    public void endSession(String username) {
        sessionsCollection.remove(new BasicDBObject("username", username));
    }

    // retrieves the session from the sessions table
    public DBObject getSession(String username) {
        return sessionsCollection.findOne(new BasicDBObject("username", username));
    }
}
