package com.alibabacloud.polar_race.engine.common.index;

import com.alibabacloud.polar_race.engine.common.cache.RingBuffer;
import com.alibabacloud.polar_race.engine.common.config.StoreConstants;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class SmartSortIndex {

    private static final SmartSortIndex instance = new SmartSortIndex();

    private volatile AtomicBoolean isShuffled = new AtomicBoolean(true);

    private long[] sortIndexs = new long[StoreConstants.MAX_CAPACITY];

    private int limit;

    private int plusNum = 0;

    private AtomicInteger index = new AtomicInteger(0);

    public static SmartSortIndex getInstance() {
        return instance;
    }

    private SmartSortIndex() {
        for (int i = 0; i < sortIndexs.length; i++) {
            sortIndexs[i] = Long.MAX_VALUE;
        }
    }


    public void sort() {
        if (isShuffled.get()) {
            Arrays.parallelSort(sortIndexs);
            initLimitPoint();
            plusNum = index.get() - limit;
            isShuffled.set(false);
        }
    }

    private void initLimitPoint() {
        for (int i = 0; i < sortIndexs.length; i++) {
            if (sortIndexs[i] >= 0) {
                limit = i;
                break;
            }
        }
    }

    public int[] getRangeIndex(byte[] start, byte[] end) {
        int[] res = new int[2];
        res[0] = 0;
        res[1] = index.get() - 1;
        return res;
    }

    public int calcuIndex(int index) {
        return index + 1 > plusNum ? index - plusNum : limit + index;
    }

    public int getCurrentSize() {
        return index.get();
    }

    public void set(long element) {
        sortIndexs[index.getAndIncrement()] = element;
    }

    public long get(int index) {
        return sortIndexs[calcuIndex(index)];
    }

}
