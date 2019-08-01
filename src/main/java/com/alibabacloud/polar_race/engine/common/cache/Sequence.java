package com.alibabacloud.polar_race.engine.common.cache;

import com.alibabacloud.polar_race.engine.common.StoreBlock;
import com.alibabacloud.polar_race.engine.common.config.CacheConstants;
import com.alibabacloud.polar_race.engine.common.config.StoreConstants;
import com.alibabacloud.polar_race.engine.common.config.SupperThreadContext;
import sun.misc.Contended;

import java.util.concurrent.atomic.AtomicInteger;

@Contended
public class Sequence {

    private static final int readyCount = CacheConstants.readyCount;

    private AtomicInteger readCount = new AtomicInteger(0);

    private byte[] data = new byte[StoreConstants.VALUE_BYTE];

    private volatile int batch = 0;
    /**
     * 就绪状态:就绪就可以消费，未就绪需要等待
     */
    private volatile boolean isReady = false;

    public void set(StoreBlock storeBlock, long keyL, int batch) {
        try {
            byte[] threadBytes = SupperThreadContext.get();
            storeBlock.readForRange(keyL, threadBytes);
            int turnCount = 0;
            while (isReady) {
                turnCount++;
                if (turnCount == 3) {
                    Thread.sleep(1);
                    turnCount = 0;
                }
            }
            System.arraycopy(threadBytes, 0, this.data, 0, StoreConstants.VALUE_BYTE);
        } catch (Exception e) {
            e.printStackTrace();
        }

        this.batch = batch;
        isReady = true;
    }

    public Sequence get(int batch) {
        try {
            int turnCount = 0;
            while (!isReady || this.batch != batch) {
                turnCount++;
                if (turnCount == 3) {
                    Thread.sleep(1);
                    turnCount = 0;
                }

            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return this;
    }

    public byte[] getData() {
        return this.data;
    }

    public void increment() {
        if (readCount.incrementAndGet() == readyCount) {
            readCount.set(0);
            isReady = false;
        }
    }


}
