package org.jimsey.projects.turbine.spring.service;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

@Service
@Profile("consumer")
//@PropertySource("classpath:conf/turbine.consumer.properties")
//@PropertySource("classpath:conf/${environment}/turbine.consumer.properties")
public class Consumer extends BaseService {
		
	@PostConstruct
	public void init() {
		super.init();
		logger.info(String.format("default.property=%s", environment.getProperty("default.property")));
		logger.info(String.format("consumer what.environment=%s", environment.getProperty("what.environment")));
		logger.info(String.format("@Value default.property=%s", defaultProperty));
		logger.info(String.format("consumer.property=%s", environment.getProperty("consumer.property")));
		logger.info("consumer initialised");
	}
	
	public void consume(String message) {
		logger.info(String.format("consumed message: %s", message)); 
	}

}