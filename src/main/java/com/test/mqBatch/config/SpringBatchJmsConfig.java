package com.test.mqBatch.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.jms.JmsItemReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.config.JmsListenerContainerFactory;
import org.springframework.jms.core.JmsTemplate;

import com.test.mqBatch.processor.Processor;


@EnableJms
@Configuration
@EnableBatchProcessing
public class SpringBatchJmsConfig {
	
	public static final Logger logger = LoggerFactory.getLogger(SpringBatchJmsConfig.class.getName());
	 
	 
    @Autowired
    private JmsTemplate jmsTemplate;
 
    @Autowired
    public JobBuilderFactory jobBuilderFactory;
 
    @Autowired
    public StepBuilderFactory stepBuilderFactory;
 
//    @Bean
//    public JmsListenerContainerFactory<?> queueListenerFactory() {
//        DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
////        factory.setMessageConverter(messageConverter());
//        return factory;
//    }
 
//    @Bean
//    public MessageConverter messageConverter() {
//        MappingJackson2MessageConverter converter = new MappingJackson2MessageConverter();
//        converter.setTargetType(MessageType.TEXT);
//        converter.setTypeIdPropertyName("_type");
//        return converter;
//    }
// 
 
    @Bean
    public JmsItemReader personJmsItemReader() {
        JmsItemReader personJmsItemReader = new JmsItemReader();
        personJmsItemReader.setJmsTemplate(jmsTemplate);
        personJmsItemReader.setItemType(String.class);
        logger.info("------------test reader--------------"+personJmsItemReader.read().toString());
        return personJmsItemReader;
    }
 
 
 
    @Bean
    public FlatFileItemWriter<String> personFlatFileItemWriter() {
        FlatFileItemWriter personFlatFileItemWriter = new FlatFileItemWriter<String>();
//        personFlatFileItemWriter.setLineAggregator(person -> person.toString());
//        personFlatFileItemWriter.setLineSeparator(System.lineSeparator());
        personFlatFileItemWriter.setResource(new FileSystemResource("person.txt"));
        return personFlatFileItemWriter;
    }
    

 
    @Bean
    public Job importUserJob() {
        return jobBuilderFactory.get("importUserJob")
                .incrementer(new RunIdIncrementer())
                .listener(jobExecutionListener())
                .flow(step1())
                .end()
                .build();
    }
 
    private Step step1() {
        return stepBuilderFactory.get("step1")
                .<String, String>chunk(10)
                .reader(personJmsItemReader())
                .processor(processor())
                .writer(personFlatFileItemWriter())
                .build();
    }
    
    
    @Bean
    public ItemProcessor<String, String> processor() {
        return new Processor();
    }
 
    @Bean
    public JobExecutionListener jobExecutionListener() {
        return new JobExecutionListener() {
            @Override
            public void beforeJob(JobExecution jobExecution) {
//                IntStream.rangeClosed(1,300).forEach(token->{
//                    Person[] people = {new Person("Jack", "Ryan"), new Person("Raymond", "Red"), new Person("Olivia", "Dunham"),
//                            new Person("Walter", "Bishop"), new Person("Harry", "Bosch")};
//                    for (Person person : people) {
//                        logger.info("sending to queue"+person.toString());
//                        jmsTemplate.convertAndSend(person);
//                    }
//                });
            }
  
 
            @Override
            public void afterJob(JobExecution jobExecution) {
 
            }
        };
    }

}
