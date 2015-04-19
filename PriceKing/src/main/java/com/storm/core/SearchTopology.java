/**
 * 
 */
package com.storm.core;

import java.util.Arrays;

import com.priceking.*;
import com.storm.core.bolts.AggrigatorBolt;
import com.storm.core.bolts.AmazonBolt;
import com.storm.core.bolts.EbayBolt;
import com.storm.core.bolts.MobileRequestBolt;
import com.storm.core.bolts.WalmartBolt;
import com.storm.core.config.Constants;
import com.storm.core.spouts.MobileRequestSpout;

import storm.kafka.ZkHosts;
import backtype.storm.Config;
import backtype.storm.LocalCluster;
import backtype.storm.StormSubmitter;
import backtype.storm.generated.StormTopology;
import backtype.storm.topology.TopologyBuilder;
import backtype.storm.tuple.Fields;


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
	            config.setNumWorkers(2);
	            config.setMaxTaskParallelism(5);
	            config.put(Config.NIMBUS_HOST, dockerIp);
	            config.put(Config.NIMBUS_THRIFT_PORT, 6627);
	            config.put(Config.STORM_ZOOKEEPER_PORT, 2181);
	            config.put(Config.STORM_ZOOKEEPER_SERVERS, Arrays.asList(dockerIp));
	            StormSubmitter.submitTopology("searchTopology", config, stormTopology);
	        }
	        else {
	            config.setNumWorkers(2);
	            config.setMaxTaskParallelism(2);
	            LocalCluster cluster = new LocalCluster();
	            cluster.submitTopology("searchTopology", config, stormTopology);
	        }
	    }
	
	public static StormTopology buildTopology() {
       
        TopologyBuilder builder = new TopologyBuilder();
        builder.setSpout(Constants.MOBILE_REQUEST_SPOUT_ID, new MobileRequestSpout(), 10);
        builder.setBolt(Constants.MOBILE_REQUEST_BOLT_ID, new MobileRequestBolt()).shuffleGrouping(Constants.MOBILE_REQUEST_SPOUT_ID);
        builder.setBolt(Constants.AMAZON_BOLT_ID, new AmazonBolt(),1).shuffleGrouping(Constants.MOBILE_REQUEST_SPOUT_ID);
        builder.setBolt(Constants.EBAY_BOLT_ID, new EbayBolt(),1).shuffleGrouping(Constants.MOBILE_REQUEST_SPOUT_ID);
        builder.setBolt(Constants.WALMART_BOLT_ID, new WalmartBolt(),1).shuffleGrouping(Constants.MOBILE_REQUEST_SPOUT_ID);
        builder.setBolt(Constants.AGGRIGATOR_BOLT_ID, new AggrigatorBolt(),3).fieldsGrouping(Constants.AMAZON_BOLT_ID, Constants.AMAZONRESULTSTREAM, new Fields("rs")).fieldsGrouping(Constants.EBAY_BOLT_ID, Constants.EBAYRESULTSTREAM, new Fields("rs")).fieldsGrouping(Constants.WALMART_BOLT_ID, Constants.WALMARTRESULTSTREAM, new Fields("rs"));
        //builder.setBolt(Constants.AGGRIGATOR_BOLT_ID, new AggrigatorBolt(),3).fieldsGrouping(Constants.EBAY_BOLT_ID, Constants.EBAYRESULTSTREAM, new Fields("rs"));
        //builder.setBolt(Constants.AGGRIGATOR_BOLT_ID, new AggrigatorBolt(),3).fieldsGrouping(Constants.WALMART_BOLT_ID, Constants.WALMARTRESULTSTREAM, new Fields("rs"));
        
        //builder.setBolt(AGGREGATE_BOLT_ID, aggregateBolt, 5).fieldsGrouping((COUNT_BOLT_ID), "stream1",  new Fields("ts"));
//builder.setBolt(AGGREGATE_BOLT_ID, aggregateBolt, 5).fieldsGrouping((HTTP_RES_CODE_COUNT_BOLT_ID), "stream2", new Fields("ts"));
       // shuffleGrouping(Constants.AMAZON_BOLT_ID).shuffleGrouping(Constants.EBAY_BOLT_ID).shuffleGrouping(Constants.WALMART_BOLT_ID);
        return builder.createTopology();
    }

}
