package com.alibabacloud.polar_race.engine.common;

import com.alibabacloud.polar_race.engine.common.cache.RingBuffer;
import com.alibabacloud.polar_race.engine.common.cache.Sequence;
import com.alibabacloud.polar_race.engine.common.config.StoreConstants;
import com.alibabacloud.polar_race.engine.common.config.ThreadPoolContext;
import com.alibabacloud.polar_race.engine.common.exceptions.EngineException;
import com.alibabacloud.polar_race.engine.common.exceptions.RetCodeEnum;
import com.alibabacloud.polar_race.engine.common.index.IndexFile;
import com.alibabacloud.polar_race.engine.common.index.IndexMemory;
import com.alibabacloud.polar_race.engine.common.index.SmartSortIndex;
import com.alibabacloud.polar_race.engine.common.store.DataFile;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;

/**
 * @author caozhenxiong
 * @version 2018-11-10
 */
public class Flow {

    private String path;

    private StoreBlock[] datas = new StoreBlock[StoreConstants.DATA_FILE_COUNT];

    private boolean isFirstStart = true;

    private SmartSortIndex sortIndex = SmartSortIndex.getInstance();

    private RingBuffer ringBuffer = null;

    private volatile boolean isBlank = true;

    private void initFile() throws EngineException {
        try {
            for (String fileName : StoreConstants.FILE_NAMES) {
                File file = new File(path + File.separator + fileName);
                if (!file.exists()) {
                    file.createNewFile();
                } else {
                    if ("0".equals(fileName)) {
                        isFirstStart = false;
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw new EngineException(RetCodeEnum.IO_ERROR, "init file error");
        }
    }

    private void initAbstractFile() throws InterruptedException {
        ExecutorService executorSerivce = ThreadPoolContext.T().get();
        CountDownLatch countDownLatch = new CountDownLatch(StoreConstants.DATA_FILE_COUNT);
        for (int i = 0; i < StoreConstants.DATA_FILE_COUNT; i++) {
            final int a = i;
            executorSerivce.execute(() -> {
                try {
                    IndexFile indexFile = new IndexFile(path, a);
                    IndexMemory indexMemory = indexFile.loadToMap();
                    int offset = isFirstStart ? -1 : indexFile.getStartOffset();
                    DataFile dataFile = new DataFile(a, path, offset);
                    datas[a] = new StoreBlock(a, indexFile, dataFile, indexMemory);
                    countDownLatch.countDown();
                } catch (EngineException e) {
                    e.printStackTrace();
                }
            });
        }
        countDownLatch.await();
    }

    public void open(String path) throws EngineException, InterruptedException {
        this.path = path;
        File dir = new File(path);
        if (!dir.exists()) {
            dir.mkdir();
        }
        initFile();
        initAbstractFile();

        System.out.println("open complete ...........");
    }

    public void write(byte[] key, byte[] value) throws EngineException {
        long keyL = ByteUtil.bytes2Long(key);
        datas[hashKey(keyL)].write(key, keyL, value);
    }

    public byte[] read(byte[] key) throws EngineException {
        long keyL = ByteUtil.bytes2Long(key);
        StoreBlock storeBlock = datas[hashKey(keyL)];
        return storeBlock.read(keyL);
    }

    private void load() {
        if (isBlank) {
            synchronized (this) {
                if (isBlank) {
                    this.loadSortIndex();
                    sortIndex.sort();
                    ringBuffer = RingBuffer.getInstance();
                    isBlank = false;
                }
            }
        }
    }

    private void loadSortIndex() {
        CountDownLatch countDownLatch = new CountDownLatch(64);
        for (StoreBlock storeBlock : datas) {
            ThreadPoolContext.T().get().execute(() -> {
                long[] keyArr = storeBlock.getIndexMemory().getKeyArr();
                for (int i = 0; i < keyArr.length; i++) {
                    if (keyArr[i] == 0l) continue;
                    SmartSortIndex.getInstance().set(keyArr[i]);
                }
                countDownLatch.countDown();
            });
        }
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void range(byte[] start, byte[] end, AbstractVisitor visitor) throws EngineException {
        this.load();
        ringBuffer.startProducer(datas, sortIndex.getCurrentSize());
        int[] range = sortIndex.getRangeIndex(start, end);
        int startI = range[0];
        int endI = range[1];
        for (int i = startI; i <= endI; i++) {
            long key = sortIndex.get(i);
            if (key == Long.MAX_VALUE) {
                break;
            }
            Sequence sequence = ringBuffer.get(i);
            visitor.visit(ByteUtil.long2Bytes(key), sequence.getData());
            sequence.increment();
        }
    }

    public void close() {
        for (StoreBlock storeBlock : datas) {
            storeBlock.close();
        }
        ThreadPoolContext.T().get().shutdownNow();
    }

    private int hashKey(long key) {
//        HashCode hashCode = hashFunction.newHasher().putBytes(key).hash();
        return (int) (key & (datas.length - 1));
    }


}
