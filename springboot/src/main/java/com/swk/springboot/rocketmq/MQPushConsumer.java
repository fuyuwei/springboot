package com.swk.springboot.rocketmq;

import java.util.List;

import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.common.consumer.ConsumeFromWhere;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.common.protocol.heartbeat.MessageModel;

public class MQPushConsumer {

	public static void main(String[] args) throws MQClientException {
		String groupName = "rocketMqGroup1";
		// 用于把多个Consumer组织到一起，提高并发处理能力
		DefaultMQPushConsumer consumer = new DefaultMQPushConsumer(groupName);
		// 设置nameServer地址，多个以;分隔
		consumer.setNamesrvAddr("name-serverl-ip:9876;name-server2-ip:9876");
		/**
		 * 1. CONSUME_FROM_LAST_OFFSET：第一次启动从队列最后位置消费，后续再启动接着上次消费的进度开始消费 
		   2. CONSUME_FROM_FIRST_OFFSET：第一次启动从队列初始位置消费，后续再启动接着上次消费的进度开始消费 
		   3. CONSUME_FROM_TIMESTAMP：第一次启动从指定时间点位置消费，后续再启动接着上次消费的进度开始消费 
		        以上所说的第一次启动是指从来没有消费过的消费者，如果该消费者消费过，那么会在broker端记录该消费者的消费位置，如果该消费者挂了再启动，那么自动从上次消费的进度开始
		 */
		consumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_FIRST_OFFSET);
		/**
		 * CLUSTERING：默认模式，同一个ConsumerGroup(groupName相同)每个consumer只消费所订阅消息的一部分内容，同一个ConsumerGroup里所有的Consumer消息加起来才是所
		 * 	订阅topic整体，从而达到负载均衡的目的
		 * BROADCASTING：同一个ConsumerGroup每个consumer都消费到所订阅topic所有消息，也就是一个消费会被多次分发，被多个consumer消费。
		 * 
		 */
		consumer.setMessageModel(MessageModel.BROADCASTING);
		// 订阅topic，可以对指定消息进行过滤，例如："TopicTest","tagl||tag2||tag3",*或null表示topic所有消息
		consumer.subscribe("order-topic", "*");
		consumer.registerMessageListener(new MessageListenerConcurrently() {
			
			@Override
			public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> mgs,
					ConsumeConcurrentlyContext consumeconcurrentlycontext) {
				System.out.println(Thread.currentThread().getName()+"Receive New Messages:"+mgs);
				// ConsumeConcurrentlyStatus.RECONSUME_LATER boker会根据设置的messageDelayLevel发起重试，默认16次
				return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
			}
		});
		consumer.start();
	}
	
}
