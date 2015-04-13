package Config;

import java.net.UnknownHostException;

import com.mongodb.MongoClient;


public class MongoConnection {
	public static MongoClient getNewMongoClient()

	{

	MongoClient mongoClient  = null;

	try {

	mongoClient = new MongoClient(PricekingConfig.SERVER, PricekingConfig.PORT);

	} catch (UnknownHostException e) {

	// TODO Auto-generated catch block

	e.printStackTrace();

	}

	return mongoClient;

	}

	public static void closeMongoClient(MongoClient mongoClient)

	{

	mongoClient.close();

	}



}
