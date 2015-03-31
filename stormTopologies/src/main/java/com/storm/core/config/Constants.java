/**
 * 
 */
package com.storm.core.config;

/**
 * @author vinaybhore
 *
 */
public class Constants {
	
	public Constants(){
		
	}
	//Streams
	public static final String SEARCH_ITEM_STREAM = "search_item_stream"; 

	
	//Kafka topic
	public static final String KAFKA_SEARCH_BUS = "searchTopic";
	
	//Spout and Bolt
	public static final String MOBILE_REQUEST_SPOUT_ID ="mobile_request_spout";
	public static final String MOBILE_REQUEST_BOLT_ID ="mobile_request_bolt";
	
	//tick frequency
	
	//Number of workers
	public static final int SEARCH_TOPOLOGY_WORKERS = 2;
	
	//NUmber of threads for Search topology
	public static final int SEARCH_BOLT_THREAD_COUNT =1;
}
