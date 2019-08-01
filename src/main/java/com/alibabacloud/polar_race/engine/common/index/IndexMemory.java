package com.alibabacloud.polar_race.engine.common.index;

import com.alibabacloud.polar_race.engine.common.config.StoreConstants;
import com.carrotsearch.hppc.LongIntHashMap;

/**
 * @author caozhenxiong
 * @version 2018-11-10
 */
public class IndexMemory {

    private LongIntHashMap memory;

    public IndexMemory() {
        memory = new LongIntHashMap(StoreConstants.MAX_CAPACITY / StoreConstants.DATA_FILE_COUNT + 20000,
                0.9900000095367432D);
    }

    public long[] getKeyArr() {
        return memory.keys;
    }

    public int get(long key) {
        return memory.getOrDefault(key, -1);
    }

    public void put(long key, int offset) {
        memory.put(key, offset);
    }


}
