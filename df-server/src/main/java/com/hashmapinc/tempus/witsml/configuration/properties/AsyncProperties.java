package com.hashmapinc.tempus.witsml.configuration.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "async")
public class AsyncProperties {

  /** CorePoolSize is the initial number of threads that will be running tasks in the pool */
  private int corePoolSize = 20;

  /**
   * The max pool size is the maximum number of workers that can be in the pool. If the max pool
   * size is greater than the core pool size, it means that the pool can grow in size, i.e. more
   * workers can be added to the pool. Workers are added to the pool when a task is submitted but
   * the work queue is full. Every time this happens, a new worker is added until the max pool size
   * is reached. If the max pool size has already been reached and the work queue is full, then the
   * next task will be rejected.
   */
  private int maxPoolSize = 200;

  /**
   * QueueCapacity is the number of tasks that will be “waiting” in the queue while all the threads
   * are in use.
   */
  private int queueCapacity = 50;

  /**
   * If the pool currently has more than corePoolSize threads, excess threads will be terminated if
   * they have been idle for more than the keepAliveTime
   */
  private int keepAliveTimeInSeconds = 60;

  /** Async thread name */
  private String asyncThreadName = "Async-Witsml-";
}
