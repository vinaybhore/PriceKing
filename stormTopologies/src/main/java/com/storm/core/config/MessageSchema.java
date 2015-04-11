/**
 * 
 */
package com.storm.core.config;

import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;
import java.util.List;

import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Values;

/**
 * @author vinaybhore
 *
 */
public class MessageSchema {

	/**
	 * 
	 */
	public MessageSchema() {
		super();
	}
	public static final String MESSAGE_STREAM_KEY = "str";
	
	public List<Object>deserialize(byte[] bytes){
		return new Values(deserializeString(bytes));
	}

	public static String deserializeString(byte[] string) {

		try{
			ByteArrayInputStream bin = new ByteArrayInputStream(string);
			ObjectInputStream in = new ObjectInputStream(bin);
			Object obj = in.readObject();
			return (String) obj;
		}catch(Exception e){
			throw new RuntimeException(e);
		}
	}

	public Fields getOutFields(){
		return new Fields(MESSAGE_STREAM_KEY);
	}
}
