package com.swk.springboot.rocketmq;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.rocketmq.client.consumer.DefaultMQPullConsumer;
import org.apache.rocketmq.client.consumer.PullResult;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.common.message.MessageQueue;

public class MQPullConsumer {

	private static final Map<MessageQueue,Long> OFFSE_TABLE = new HashMap<MessageQueue,Long>();
	
	public static void main(String[] args) throws MQClientException {
		DefaultMQPullConsumer consumer = new DefaultMQPullConsumer("groupName");
		consumer.setNamesrvAddr("name-serverl-ip:9876;name-server2-ip:9876");
		consumer.start();
		// 从指定topic中拉取所有消息队列
		Set<MessageQueue> mqs = consumer.fetchSubscribeMessageQueues("order-topic");
		for(MessageQueue mq:mqs){
			try {
				// 获取消息的offset，指定从store中获取
				long offset = consumer.fetchConsumeOffset(mq,true);
				System.out.println("consumer from the queue:"+mq+":"+offset);
				while(true){
					PullResult pullResult = consumer.pullBlockIfNotFound(mq, null, 
							getMessageQueueOffset(mq), 32);
					putMessageQueueOffset(mq,pullResult.getNextBeginOffset());
					switch(pullResult.getPullStatus()){
					case FOUND:
						List<MessageExt> messageExtList = pullResult.getMsgFoundList();
                        for (MessageExt m : messageExtList) {
                            System.out.println(new String(m.getBody()));
                        }
						break;
					case NO_MATCHED_MSG:
						break;
					case NO_NEW_MSG:
						break;
					case OFFSET_ILLEGAL:
						break;
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		consumer.shutdown();
	}

	// 保存上次消费的消息下标
	private static void putMessageQueueOffset(MessageQueue mq,
			long nextBeginOffset) {
		OFFSE_TABLE.put(mq, nextBeginOffset);
	}
	
	// 获取上次消费的消息的下表
	private static Long getMessageQueueOffset(MessageQueue mq) {
		Long offset = OFFSE_TABLE.get(mq);
		if(offset != null){
			return offset;
		}
		return 0l;
	}
	

}
