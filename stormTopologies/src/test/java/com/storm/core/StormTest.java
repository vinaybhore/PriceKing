/*package com.storm.core;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;

import backtype.storm.Config;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.BasicOutputCollector;
import backtype.storm.tuple.Tuple;

import com.storm.core.bolts.WalmartBolt;
import com.storm.core.config.Constants;

public class StormTest {

	@Test
	public void test() {
		fail("Not yet implemented");
	}
		  private WalmartBolt bolt;

		  //@Mock
		  private TopologyContext topologyContext;

		  @Before
		  public void before() {
		    MockitoAnnotations.initMocks(this);

		    // The factory will return our mock `notifier`
		    bolt = new WalmartBolt();
		    // Now the bolt is holding on to our mock and is under our control!
		    bolt.prepare(new Config(), topologyContext, null);
		  }

		  @Test
		  public void testExecute() {
		    long userId = 24;
		    Tuple tuple = mock(Tuple.class);
		    when(tuple.getLongByField(Constants.WALMART_BOLT_ID)).thenReturn(userId);
		    BasicOutputCollector collector = mock(BasicOutputCollector.class);

		    bolt.execute(tuple, collector);

		    // Here we just verify a call on `notifier`, but we could have stubbed out behavior befor
		    //  the call to execute, too.
		    verify(notifier).notifyUser(userId);
		    verify(collector).emit(new Values(userId));
		  }
		}

*/