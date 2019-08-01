package com.alibabacloud.polar_race.engine.common.config;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ThreadPoolContext {

    private static final ThreadPoolContext instance = new ThreadPoolContext();

    private ThreadPoolContext() {

    }

    public static ThreadPoolContext T() {
        return instance;
    }

    private ExecutorService producerThreadPool = Executors.newFixedThreadPool(CacheConstants.SPLIT_COUNT);

    public ExecutorService get() {
        return producerThreadPool;
    }

}
