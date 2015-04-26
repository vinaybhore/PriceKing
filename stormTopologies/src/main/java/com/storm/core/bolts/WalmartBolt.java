/**
 * 
 */
package com.storm.core.bolts;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import backtype.storm.task.OutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseRichBolt;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Tuple;
import backtype.storm.tuple.Values;

import com.storm.core.config.Constants;
import com.storm.core.rest.APIController;

/**
 * @author vinaybhore
 *
 */
public class WalmartBolt extends BaseRichBolt {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * 
	 */
	private OutputCollector collector;
	public APIController controller;
	public static final Logger LOG = LoggerFactory.getLogger(WalmartBolt.class);
	public WalmartBolt() {
	}

	public void prepare(Map stormConf, TopologyContext context, OutputCollector collector) {
		this.collector = collector;
		controller = new APIController();

	}

	public void execute(Tuple input) {
		this.collector.ack(input);
		JSONObject obj = new JSONObject(input.getValue(0).toString());
		String searchKey = obj.get("keyword").toString();
		try {
			List<String> results = controller.walmartCall(searchKey);
			if(!results.isEmpty() && results!=null){
				//String responseString = controller.compareAndSort(results.toString());
				//System.out.println("responseString:: from walmart::"+responseString);
			}
			this.collector.emit(Constants.WALMARTRESULTSTREAM, new Values(results));

		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		declarer.declareStream(Constants.WALMARTRESULTSTREAM, new Fields("rs"));

	}

}
