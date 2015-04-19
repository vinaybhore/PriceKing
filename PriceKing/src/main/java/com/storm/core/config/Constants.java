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
	public static final String EBAYRESULTSTREAM ="ebay_result_stream";
	public static final String AMAZONRESULTSTREAM ="amazon_result_stream";
	public static final String WALMARTRESULTSTREAM ="walmart_result_stream";

	
	//Kafka topic
	public static final String KAFKA_SEARCH_BUS = "searchTopic";
	
	//Spout and Bolt
	public static final String MOBILE_REQUEST_SPOUT_ID ="mobile_request_spout";
	public static final String MOBILE_REQUEST_BOLT_ID ="mobile_request_bolt";
	public static final String AMAZON_BOLT_ID ="amazon_bolt";
	public static final String EBAY_BOLT_ID ="ebay_bolt";
	public static final String WALMART_BOLT_ID ="walmart_bolt";
	public static final String AGGRIGATOR_BOLT_ID ="aggrigator_bolt";
	
	//tick frequency
	
	//Number of workers
	public static final int SEARCH_TOPOLOGY_WORKERS = 2;
	
	//NUmber of threads for Search topology
	public static final int SEARCH_BOLT_THREAD_COUNT =1;
}
