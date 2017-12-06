package com.sdl.einvoice;

import com.sdl.einvoice.config.MQConfig;
import com.sdl.einvoice.mq.RocketMQConsumer;
import com.sdl.einvoice.mq.EinvoiceNotifyListener;
import com.sdl.einvoice.mq.RocketMQProducer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class EinvoiceApplication  extends SpringBootServletInitializer {

	@Autowired
	MQConfig mqConfig;
	/**
	 * 启动生产者
	 */
	@Bean
	public RocketMQProducer rocketMQProducer(){
		RocketMQProducer rocketMQProducer = new RocketMQProducer(mqConfig.getNameSrvAddr(),
				mqConfig.getProducerGroupName(), mqConfig.getTopics());
		rocketMQProducer.init();
		return rocketMQProducer;
	}

	/**
	 * 启动消费者
	 */
	@Bean
	public RocketMQConsumer startConsumer(){
		EinvoiceNotifyListener mqListener = new EinvoiceNotifyListener();
		RocketMQConsumer mqConsumer = new RocketMQConsumer(mqListener, mqConfig.getNameSrvAddr(),
				mqConfig.getConsumerGroupName(), mqConfig.getTopics());
		mqConsumer.init();
		return mqConsumer;
	}

	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		return application.sources(EinvoiceApplication.class);
	}

	public static void main(String[] args) {
		SpringApplication.run(EinvoiceApplication.class, args);
	}



}
