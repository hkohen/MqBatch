package com.test.mqBatch.processor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;

public class Processor implements ItemProcessor<String, String> {
	public static final Logger logger = LoggerFactory.getLogger(ItemProcessor.class.getName());
	
	
	@Override
	public String process(String item) throws Exception {
		
		
		logger.info("==================test processor ==================="+ item);
		return item;
	}

}
