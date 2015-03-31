/**
 * 
 */
package com.storm.core.bolts;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import backtype.storm.topology.BasicOutputCollector;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseBasicBolt;
import backtype.storm.tuple.Tuple;

/**
 * @author vinaybhore
 *
 */
public class MobileRequestBolt extends BaseBasicBolt{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public static final Logger LOG = LoggerFactory.getLogger(MobileRequestBolt.class);
	/**
	 * 
	 */
	public MobileRequestBolt() {
		// TODO Auto-generated constructor stub
	}

	public void execute(Tuple input, BasicOutputCollector collector) {
		LOG.info(input.toString());
		
	}

	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		// TODO Auto-generated method stub
		
	}

}
