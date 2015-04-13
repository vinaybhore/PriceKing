/**
 * 
 */
package com.storm.core.bolts;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.storm.core.config.Constants;

import backtype.storm.task.OutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseRichBolt;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Tuple;
import backtype.storm.tuple.Values;

/**
 * @author vinaybhore
 *
 */
public class EbayBolt extends BaseRichBolt {

	/**
	 * 
	 */
	
	private OutputCollector collector;
	public static final Logger LOG = LoggerFactory.getLogger(EbayBolt.class);
	public EbayBolt() {
		// TODO Auto-generated constructor stub
	}

	public void prepare(Map stormConf, TopologyContext context, OutputCollector collector) {
		// TODO Auto-generated method stub
		this.collector =collector;
		
	}

	public void execute(Tuple input) {
		// TODO Auto-generated method stub
		this.collector.emit(Constants.EBAYRESULTSTREAM,new Values(input.getValue(0)));
		LOG.info(input.toString());
		
	}

	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		declarer.declareStream(Constants.EBAYRESULTSTREAM, new Fields("rs"));
		
	}

}
