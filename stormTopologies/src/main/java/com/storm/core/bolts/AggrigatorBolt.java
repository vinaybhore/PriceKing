/**
 * 
 */
package com.storm.core.bolts;

import java.util.Map;

import org.mortbay.log.Log;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import backtype.storm.task.OutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseRichBolt;
import backtype.storm.tuple.Tuple;

import com.google.gson.Gson;
import com.storm.core.config.Constants;
import com.storm.core.rest.APIController;
import com.storm.core.rest.TestProducer;

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
	private OutputCollector collector;
	public APIController controller;
	public TestProducer producer;
	public static final Logger LOG = LoggerFactory.getLogger(AggrigatorBolt.class);
	//ArrayList<String> aggregatorList;
	public AggrigatorBolt() {
		// TODO Auto-generated constructor stub
	}

	@SuppressWarnings("rawtypes")
	public void prepare(Map stormConf, TopologyContext context, OutputCollector collector) {
		// TODO Auto-generated method stub
		this.collector =collector;
		controller = new APIController();
		//aggregatorList = new ArrayList<String>();
		producer = new TestProducer();
		
	}

	public void execute(Tuple input) {
		if(input.getSourceStreamId().equalsIgnoreCase(Constants.AMAZONRESULTSTREAM)){
			this.collector.ack(input);
			Log.info("got message from Amazon bolt:: "+input.toString());
			if(input!=null){
				//ArrayList<String>resultList = new ArrayList<String>();
				Gson gson = new Gson();
				//String result =controller.compareAndSort(input.getValueByField("rs").toString());
				producer.produce(input.getValueByField("rs").toString(), "vinz1234");
				/*for(Object obj:input.getValues()){
					//resultList.add(gson.toJson(obj));
					//aggregatorList.add(gson.toJson(obj));
				}*/
			
			}
		}else if(input.getSourceStreamId().equalsIgnoreCase(Constants.EBAYRESULTSTREAM)){
			this.collector.ack(input);
			Log.info("got message from ebay bolt:: "+input.toString());
			if(input!=null){
				//ArrayList<String>resultList = new ArrayList<String>();
				Gson gson = new Gson();
				//String result = controller.compareAndSort(input.getValueByField("rs").toString());
				producer.produce(input.getValueByField("rs").toString(), "vinz1234");
				/*for(Object obj:input.getValues()){
					//resultList.add(gson.toJson(obj));
					//aggregatorList.add(gson.toJson(obj));
				}
		*/	
			}
		}else if(input.getSourceStreamId().equalsIgnoreCase(Constants.WALMARTRESULTSTREAM)){
			this.collector.ack(input);
			Log.info("got message from walmart bolt:: "+input.toString());
			if(input!=null){
				//ArrayList<String>resultList = new ArrayList<String>();
				Gson gson = new Gson();
				//String result =controller.compareAndSort(input.getValueByField("rs").toString());
				//producer.produce(result, "vinz1234");
				producer.produce(input.getValueByField("rs").toString(), "vinz1234");
		/*		for(Object obj:input.getValues()){
					//resultList.add(gson.toJson(obj));
					//aggregatorList.add(gson.toJson(obj));
				}
		*/	
			}
		}else if(input.getSourceStreamId().equalsIgnoreCase(Constants.SMALLBUSINESSRESULTSTREAM)){
			this.collector.ack(input);
			Log.info("got message from smallbusiness bolt:: "+input.toString());
			if(input!=null){
				//ArrayList<String>resultList = new ArrayList<String>();
				Gson gson = new Gson();
				//String result =controller.compareAndSort(input.getValueByField("rs").toString());
				//producer.produce(result, "vinz1234");
				producer.produce(input.getValueByField("rs").toString(), "vinz1234");
		/*		for(Object obj:input.getValues()){
					//resultList.add(gson.toJson(obj));
					//aggregatorList.add(gson.toJson(obj));
				}
		*/	
			}
		}
		
		Gson gson = new Gson();
		//System.out.println(gson.toJson((aggregatorList)));
		//LOG.info("RESPONSE::"+gson.toJson(controller.compareAndSort(aggregatorList)));
		
		//producer.produce(gson.toJson(controller.compareAndSort(aggregatorList)), "vinz1234");
	}

	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		
		
	}

}
