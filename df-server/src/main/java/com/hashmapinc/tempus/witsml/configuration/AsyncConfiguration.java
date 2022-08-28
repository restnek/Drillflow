package com.hashmapinc.tempus.witsml.configuration;

import com.hashmapinc.tempus.witsml.configuration.properties.AsyncProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
@EnableAsync(proxyTargetClass = true)
public class AsyncConfiguration {

  @Bean("asyncCustomTaskExecutor")
  public TaskExecutor getAsyncExecutor(AsyncProperties asyncProperties) {
    ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
    executor.setCorePoolSize(asyncProperties.getCorePoolSize());
    executor.setMaxPoolSize(asyncProperties.getMaxPoolSize());
    executor.setQueueCapacity(asyncProperties.getQueueCapacity());
    executor.setKeepAliveSeconds(asyncProperties.getKeepAliveTimeInSeconds());
    executor.setWaitForTasksToCompleteOnShutdown(true);
    executor.setThreadNamePrefix(asyncProperties.getAsyncThreadName());
    executor.initialize();
    return executor;
  }
}
