/**
 * 
 */
package com.storm.core.rest;

import java.util.Properties;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import kafka.javaapi.producer.Producer;
import kafka.producer.KeyedMessage;
import kafka.producer.ProducerConfig;

/**
 * @author vinaybhore
 *
 */
public class TestProducer {

	/**
	 * 
	 */

	private static Producer<Integer, String> producer;
	private final Properties props = new Properties();

	public TestProducer() {
		
		props.put("metadata.broker.list", "localhost:9092");
		props.put("serializer.class", "kafka.serializer.StringEncoder");
		props.put("request.required.acks", "1");
		producer = new Producer<Integer, String>(new ProducerConfig(props));
	}

	/**
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		TestProducer sp = new TestProducer();
		String topic = "searchTopic";
		JsonObject object = new JsonObject();
		object.addProperty("keyword", "iphone6");
		object.addProperty("userid", "vinaybhore");

		Gson gson = new Gson();

		while (true) {
			KeyedMessage<Integer, String> data = new KeyedMessage<Integer, String>(topic, gson.toJson(object));
			producer.send(data);
			Thread.sleep(2000);
		}
	}

	public void produce(String data, String userId) {

		if (null != data) {
			Gson gson = new Gson();
			KeyedMessage<Integer, String> response = new KeyedMessage<Integer, String>(userId, gson.toJson(data));
			producer.send(response);
			// producer.close();
		}
	}

}
