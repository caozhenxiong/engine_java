package com.alibabacloud.polar_race.engine.common.cache;

import com.alibabacloud.polar_race.engine.common.StoreBlock;
import com.alibabacloud.polar_race.engine.common.config.CacheConstants;
import com.alibabacloud.polar_race.engine.common.config.StoreConstants;
import com.alibabacloud.polar_race.engine.common.config.ThreadPoolContext;
import com.alibabacloud.polar_race.engine.common.index.SmartSortIndex;

public class RingBuffer {

    public static volatile RingBuffer instance = null;

    private Sequence[] sequences = new Sequence[CacheConstants.CACHE_SIZE];

    private volatile boolean isProducer = false;

    private RingBuffer() {
        for (int i = 0; i < CacheConstants.CACHE_SIZE; i++) {
            sequences[i] = new Sequence();
        }
    }

    public static RingBuffer getInstance() {
        if (instance == null) {
            instance = new RingBuffer();
        }
        return instance;
    }

    /**
     * @param datas    数据块
     * @param totalNum 数据总个数
     */
    public void startProducer(StoreBlock[] datas, int totalNum) {
        if (!isProducer) {
            synchronized (this) {
                if (!isProducer) {
                    isProducer = true;
                    int[] threadInfo = calcuThreadEn(totalNum);
                    for (int i = 0; i < CacheConstants.SPLIT_COUNT; i++) {
                        final int splitNo = i;
                        ThreadPoolContext.T().get().execute(() -> {
                            int totalReadNum = threadInfo[splitNo];
                            int readCount = 0;
                            int keyIndex = splitNo * CacheConstants.BLOCK_NUM;
                            int batch = 0;
                            int suffixSeq = CacheConstants.BLOCK_NUM * splitNo;
                            int endSeq = CacheConstants.BLOCK_NUM * (splitNo + 1) - 1;
                            int curSeq = suffixSeq;
                            try {
                                while (true) {
                                    if (readCount == totalReadNum) {
                                        isProducer = false;
                                        break;
                                    }
                                    long keyL = SmartSortIndex.getInstance().get(keyIndex);
                                    sequences[curSeq].set(datas[(int) (keyL & (datas.length - 1))], keyL, getBatch(keyIndex));
                                    readCount++;
                                    if (curSeq == endSeq) {
                                        curSeq = suffixSeq;
                                        keyIndex = (++batch) * CacheConstants.CACHE_SIZE + suffixSeq;
                                    } else {
                                        curSeq++;
                                        keyIndex++;
                                    }
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        });
                    }
                }
            }
        }

    }

    private int[] calcuThreadEn(int totalNum) {
        int[] arrs = new int[CacheConstants.SPLIT_COUNT];
        int atLeastCycle = totalNum / CacheConstants.CACHE_SIZE;
        for (int i = 0; i < arrs.length; i++) {
            arrs[i] = atLeastCycle * CacheConstants.BLOCK_NUM;
        }
        int lastCycle = totalNum % CacheConstants.CACHE_SIZE;
        if (lastCycle != 0) {
            int lastBlockSize = lastCycle % CacheConstants.BLOCK_NUM;
            int threadNo = lastCycle / CacheConstants.BLOCK_NUM;
            for (int i = 0; i < threadNo; i++) {
                arrs[i] += CacheConstants.BLOCK_NUM;
            }
            if (lastBlockSize != 0) {
                arrs[threadNo] += lastBlockSize;
            }
        }
        return arrs;


    }

    //获取batch
    private int getBatch(int keyIndexTmp) {
        int keyIndex = keyIndexTmp + 1;
        return keyIndex % CacheConstants.CACHE_SIZE == 0 ? keyIndex / CacheConstants.CACHE_SIZE : keyIndex / CacheConstants.CACHE_SIZE + 1;
    }

    public Sequence get(int keyIndex) {
        return sequences[keyIndex % CacheConstants.CACHE_SIZE].get(getBatch(keyIndex));
    }

}
