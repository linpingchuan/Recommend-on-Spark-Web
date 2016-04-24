package com.moon.kafka;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import kafka.api.FetchRequest;
import kafka.api.FetchRequestBuilder;
import kafka.javaapi.FetchResponse;
import kafka.javaapi.consumer.SimpleConsumer;
import kafka.javaapi.message.ByteBufferMessageSet;
import kafka.message.MessageAndOffset;

public class SimpleConsumerTest {
	private static void generateData(){
		
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	private static void printMessage(ByteBufferMessageSet byteBufferMessageSet) throws UnsupportedEncodingException{
		for(MessageAndOffset messageAndOffset:byteBufferMessageSet){
			ByteBuffer payload=messageAndOffset.message().payload();
			byte[] bytes=new byte[payload.limit()];
			payload.get(bytes);
			System.out.println(new String(bytes,"utf-8"));
		}
	}
	@Test
	public void testConsume() throws UnsupportedEncodingException {
		generateData();
		
		SimpleConsumer simpleConsumer=new SimpleConsumer(MoonKafkaProperties.KAFKA_SERVER_URL,MoonKafkaProperties.KAFKA_SERVER_PORT,MoonKafkaProperties.CONNECTION_TIMEOUT,MoonKafkaProperties.KAFKA_PRODUCER_BUFFER_SIZE,MoonKafkaProperties.CLIENT_ID);
		
		System.out.println("Testing single fetch");
		FetchRequest req=new FetchRequestBuilder()
				.clientId(MoonKafkaProperties.CLIENT_ID)
				.addFetch(MoonKafkaProperties.TOPIC2,0,0L,100)
				.build();
		
		FetchResponse fetchResponse=simpleConsumer.fetch(req);
		printMessage(fetchResponse.messageSet(MoonKafkaProperties.TOPIC2, 0));
		
		System.out.println("Testing single multi-fetch");
		Map<String,List<Integer>> topicMap=new HashMap<>();
		topicMap.put(MoonKafkaProperties.TOPIC2, Collections.singletonList(0));
		topicMap.put(MoonKafkaProperties.TOPIC3, Collections.singletonList(0));
		
		req=new FetchRequestBuilder()
				.clientId(MoonKafkaProperties.CLIENT_ID)
				.addFetch(MoonKafkaProperties.TOPIC2, 0, 0L, 100)
				.addFetch(MoonKafkaProperties.TOPIC3,0,0L,100)
				.build();
		fetchResponse=simpleConsumer.fetch(req);
		int fetchReq=0;
		for(Map.Entry<String, List<Integer>> entry:topicMap.entrySet()){
			String topic=entry.getKey();
			for(Integer offset:entry.getValue()){
				System.out.println("Response from fetch request no: "+ ++fetchReq);
				printMessage(fetchResponse.messageSet(topic, offset));
			}
		}
	}
	@Test
	public void testConsumerProducer(){
	}
}