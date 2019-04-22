package com.alibabacloud.polar_race.engine.common.config;

public class CacheConstants {

    public static final int CACHE_SIZE = 8192;

    public static final int SPLIT_COUNT = 64;

    public static final int BLOCK_NUM = CACHE_SIZE / SPLIT_COUNT;

    public static final int readyCount = 64;

}
