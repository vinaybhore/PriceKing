/**
 * 
 */
package com.storm.core.rest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import com.google.gson.Gson;

import kafka.consumer.Consumer;
import kafka.consumer.ConsumerConfig;
import kafka.consumer.ConsumerIterator;
import kafka.consumer.KafkaStream;
import kafka.javaapi.consumer.ConsumerConnector;

/**
 * @author vinaybhore
 *
 */
public class MessageConsumer {

	private ConsumerConnector consumer;
	private String topic;

	public MessageConsumer(String zookeeper, String groupId, String topic) {
		Properties props = new Properties();
		props.put("zookeeper.connect", zookeeper);
		props.put("group.id", groupId);
		props.put("zookeeper.session.timeout.ms", "5000");
		props.put("zookeeper.sync.time.ms", "250");
		props.put("auto.commit.interval.ms", "1000");

		consumer = Consumer.createJavaConsumerConnector(new ConsumerConfig(props));
		this.topic = topic;
	}

	public String testConsumer() {
		String result = null;
		Map<String, Integer> topicCount = new HashMap<String, Integer>();
		// Define single thread for topic
		topicCount.put(topic, new Integer(1));

		Map<String, List<KafkaStream<byte[], byte[]>>> consumerStreams = consumer.createMessageStreams(topicCount);

		List<KafkaStream<byte[], byte[]>> streams = consumerStreams.get(topic);
		for (final KafkaStream stream : streams) {
			ConsumerIterator<byte[], byte[]> consumerIte = stream.iterator();
			while (consumerIte.hasNext())
				System.out.println("Message from Single Topic :: " + new String(consumerIte.next().message()));
			Gson gson = new Gson();
			result = gson.toJson(consumerIte.next().message());
			if (consumer != null)
				consumer.shutdown();
		}

		return result;
	}

	public static void consumeMessage(String responseTopic) {

		String topic = responseTopic;
		MessageConsumer simpleHLConsumer = new MessageConsumer("127.0.0.1:2181", "test", topic);
		simpleHLConsumer.testConsumer();
	}
}
