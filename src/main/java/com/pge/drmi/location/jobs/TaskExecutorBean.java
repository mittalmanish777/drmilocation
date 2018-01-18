package com.pge.drmi.location.jobs;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
public class TaskExecutorBean {

	@Value("${drmi.taskexecutor.corepoolsize}")
	private int corePoolSize;

	@Value("${drmi.taskexecutor.maxpoolsize}")
	private int maxPoolSize;

	@Bean
	public TaskExecutor getTaskExecutor() {
		ThreadPoolTaskExecutor threadPoolTaskExecutor = new ThreadPoolTaskExecutor();
		threadPoolTaskExecutor.setCorePoolSize(corePoolSize);
		threadPoolTaskExecutor.setMaxPoolSize(maxPoolSize);
		return threadPoolTaskExecutor;
	}

}
