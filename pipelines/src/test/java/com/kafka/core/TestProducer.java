/**
 * 
 */
package com.kafka.core;

import java.util.Properties;

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
		// TODO Auto-generated constructor stub
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
		  // for(int i=1;i<20;i++){
		    while(true){
			   KeyedMessage<Integer, String> data = new KeyedMessage<Integer, String>(topic, "hello there");
			    producer.send(data);  
			    //System.out.println("sending data"+ i);
			    Thread.sleep(2000);
		   }
		    //producer.close();
		  }
		
	}


