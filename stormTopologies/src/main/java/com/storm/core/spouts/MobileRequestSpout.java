/**
 * 
 */
package com.storm.core.spouts;

import java.util.ArrayList;
import java.util.List;

import backtype.storm.spout.SchemeAsMultiScheme;

import com.storm.core.config.Constants;

import storm.kafka.BrokerHosts;
import storm.kafka.KafkaSpout;
import storm.kafka.SpoutConfig;
import storm.kafka.StringScheme;
import storm.kafka.ZkHosts;


/**
 * @author vinaybhore
 *
 */
public class MobileRequestSpout extends KafkaSpout{
	
	
	private static final long serialVersionUID = 1L;
	public static SpoutConfig kafkaConfig = null;
	static{
		BrokerHosts brokerHosts = new ZkHosts("brokerZkStr");
		kafkaConfig = new SpoutConfig(brokerHosts, Constants.KAFKA_SEARCH_BUS, "", "uniqueIdentifier");
		kafkaConfig.scheme = new SchemeAsMultiScheme(new StringScheme());
		List<String>zkList = new ArrayList<String>();
		zkList.add("localhost");
		kafkaConfig.zkPort= 2181;
		kafkaConfig.zkServers = zkList;
		
	}
	
	public MobileRequestSpout() {
		super(kafkaConfig);
		
	}
	
	

}
