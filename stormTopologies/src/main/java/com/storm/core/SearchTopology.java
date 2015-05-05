/**
 * 
 */
package com.storm.core;

import java.util.Arrays;

import storm.kafka.KafkaSpout;
import storm.kafka.SpoutConfig;
import storm.kafka.ZkHosts;
import backtype.storm.Config;
import backtype.storm.LocalCluster;
import backtype.storm.StormSubmitter;
import backtype.storm.generated.StormTopology;
import backtype.storm.topology.TopologyBuilder;
import backtype.storm.tuple.Fields;

import com.storm.core.bolts.AggrigatorBolt;
import com.storm.core.bolts.AmazonBolt;
import com.storm.core.bolts.EbayBolt;
import com.storm.core.bolts.SmallBusinessBolt;
import com.storm.core.bolts.WalmartBolt;
import com.storm.core.config.Constants;
import com.storm.core.spouts.MobileRequestSpout;

/**
 * @author vinaybhore
 *
 */
public class SearchTopology {

	public SearchTopology(String kafkaZookeeper) {
		 new ZkHosts(kafkaZookeeper);
	}

	/**
	 * @param args
	 */
	  public static void main(String[] args) throws Exception {

	        Config config = new Config();
	       config.put(Config.TOPOLOGY_TRIDENT_BATCH_EMIT_INTERVAL_MILLIS, 2000);
	        
	        StormTopology stormTopology = SearchTopology.buildTopology();
	        if (args != null && args.length > 1) {
	            String dockerIp = "127.0.0.1";
	            config.setNumWorkers(1);
	            config.setMaxTaskParallelism(1);
	            config.put(Config.NIMBUS_HOST, dockerIp);
	            config.put(Config.NIMBUS_THRIFT_PORT, 6627);
	            config.put(Config.STORM_ZOOKEEPER_PORT, 2181);
	            config.put(Config.STORM_ZOOKEEPER_SERVERS, Arrays.asList(dockerIp));
	            StormSubmitter.submitTopology("searchTopology", config, stormTopology);
	        }
	        else {
	            config.setNumWorkers(1);
	            config.setMaxTaskParallelism(1);
	            LocalCluster cluster = new LocalCluster();
	            cluster.submitTopology("searchTopology", config, stormTopology);
	        }
	    }
	
	public static StormTopology buildTopology() {
        TopologyBuilder builder = new TopologyBuilder();
       // builder.setSpout(Constants.MOBILE_REQUEST_SPOUT_ID, new KafkaSpout(new SpoutConfig(new ZkHosts("127.0.0.1"),"walmartTopic", "", "id")), 1);
        builder.setSpout(Constants.MOBILE_REQUEST_SPOUT_ID, new MobileRequestSpout(), 1);
        builder.setBolt(Constants.AMAZON_BOLT_ID, new AmazonBolt(),1).shuffleGrouping(Constants.MOBILE_REQUEST_SPOUT_ID);
        builder.setBolt(Constants.EBAY_BOLT_ID, new EbayBolt(),1).shuffleGrouping(Constants.MOBILE_REQUEST_SPOUT_ID);
        builder.setBolt(Constants.WALMART_BOLT_ID, new WalmartBolt(),1).shuffleGrouping(Constants.MOBILE_REQUEST_SPOUT_ID);
        builder.setBolt(Constants.SMALLBUSINESS_BOLT_ID, new SmallBusinessBolt(),1).shuffleGrouping(Constants.MOBILE_REQUEST_SPOUT_ID);
        builder.setBolt(Constants.AGGRIGATOR_BOLT_ID, new AggrigatorBolt(),4).fieldsGrouping(Constants.AMAZON_BOLT_ID, Constants.AMAZONRESULTSTREAM, new Fields("rs")).fieldsGrouping(Constants.EBAY_BOLT_ID, Constants.EBAYRESULTSTREAM, new Fields("rs")).fieldsGrouping(Constants.WALMART_BOLT_ID, Constants.WALMARTRESULTSTREAM, new Fields("rs")).fieldsGrouping(Constants.SMALLBUSINESS_BOLT_ID, Constants.SMALLBUSINESSRESULTSTREAM, new Fields("rs"));
        
        return builder.createTopology();
    }

}
