package com.alibabacloud.polar_race.engine.common.config;

/**
 * @author caozhenxiong
 * @version 2018-11-09
 */
public class StoreConstants {

    public static final int VALUE_BYTE = 4096;

    public static final int DATA_FILE_COUNT = 64;

    public static final int MAX_CAPACITY = 64000000;

    public static final boolean isLocal = false;

    /**
     * 索引文件32 + 数据文件32 + offset文件
     */
    public static final String[] FILE_NAMES = new String[DATA_FILE_COUNT * 2];

    static {
        for (int i = 0; i < DATA_FILE_COUNT; i++) {
            FILE_NAMES[i] = i + "";
        }
        int count = 0;
        for (int i = DATA_FILE_COUNT; i < DATA_FILE_COUNT * 2; i++){
            FILE_NAMES[i] = "index" + count;
            count ++;
        }
    }
}
