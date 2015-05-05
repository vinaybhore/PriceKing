/**
 * 
 */
package com.storm.core.bolts;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.apache.http.client.ClientProtocolException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.storm.core.config.Constants;
import com.storm.core.rest.APIController;

import backtype.storm.task.OutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.BasicOutputCollector;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseBasicBolt;
import backtype.storm.topology.base.BaseRichBolt;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Tuple;
import backtype.storm.tuple.Values;

/**
 * @author vinaybhore
 *
 */
public class SmallBusinessBolt extends BaseRichBolt{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private OutputCollector collector;
	public APIController controller;
	public static final Logger LOG = LoggerFactory.getLogger(SmallBusinessBolt.class);
	/**
	 * 
	 */
	public SmallBusinessBolt() {
		// TODO Auto-generated constructor stub
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
			List<String> results = controller.smallBusinessesCall(searchKey);
			if(!results.isEmpty()){
				//String responseString = controller.compareAndSort(results.toString());
				//System.out.println("responseString:: from ebay::"+responseString);
			}
			this.collector.emit(Constants.SMALLBUSINESSRESULTSTREAM, new Values(results));
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		declarer.declareStream(Constants.SMALLBUSINESSRESULTSTREAM, new Fields("rs"));
		
	}

}
