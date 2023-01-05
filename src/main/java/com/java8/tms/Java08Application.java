package com.java8.tms;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
@SpringBootApplication
@EnableScheduling
@EnableCaching
public class Java08Application {
	private static final Logger logger = LoggerFactory.getLogger(Java08Application.class);
	public static void main(String[] args) {

		SpringApplication.run(Java08Application.class, args);
	}

	@Bean
	public TaskScheduler taskScheduler() {
		logger.info("Start schedule success!");
		final ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
		scheduler.setPoolSize(10);
		return scheduler;
	}
}
