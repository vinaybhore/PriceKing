/**
 * 
 */
package com.storm.core.bolts;

import java.util.Map;

import org.mortbay.log.Log;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.storm.core.config.Constants;

import backtype.storm.task.OutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseRichBolt;
import backtype.storm.tuple.Tuple;

/**
 * @author vinaybhore
 *
 */
public class AggrigatorBolt extends BaseRichBolt{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * 
	 */
	public static final Logger LOG = LoggerFactory.getLogger(AggrigatorBolt.class);
	public AggrigatorBolt() {
		// TODO Auto-generated constructor stub
	}

	@SuppressWarnings("rawtypes")
	public void prepare(Map stormConf, TopologyContext context, OutputCollector collector) {
		// TODO Auto-generated method stub
		
	}

	public void execute(Tuple input) {
		if(input.getSourceStreamId().equalsIgnoreCase(Constants.AMAZONRESULTSTREAM)){
			Log.info("got message from Amazon bolt:: "+input.toString());
		}else if(input.getSourceStreamId().equalsIgnoreCase(Constants.EBAYRESULTSTREAM)){
			Log.info("got message from ebay bolt:: "+input.toString());
		}else if(input.getSourceStreamId().equalsIgnoreCase(Constants.WALMARTRESULTSTREAM)){
			Log.info("got message from walmart bolt:: "+input.toString());
		}
		
		//aggrigate the result.. put the result in a list serialize it and boom.. push it on kafka again!!
		
	}

	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		// TODO Auto-generated method stub
		
	}

}
