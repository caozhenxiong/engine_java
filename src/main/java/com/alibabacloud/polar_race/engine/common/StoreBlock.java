package com.alibabacloud.polar_race.engine.common;

import com.alibabacloud.polar_race.engine.common.exceptions.EngineException;
import com.alibabacloud.polar_race.engine.common.exceptions.RetCodeEnum;
import com.alibabacloud.polar_race.engine.common.index.IndexFile;
import com.alibabacloud.polar_race.engine.common.index.IndexMemory;
import com.alibabacloud.polar_race.engine.common.store.DataFile;


/**
 * 存储系统组合块
 *
 * @author caozhenxiong
 * @version 2018-11-10
 */
public class StoreBlock {

    private int no;

    private IndexFile indexFile;

    private DataFile dataFile;

    private IndexMemory indexMemory;


    public IndexMemory getIndexMemory() {
        return indexMemory;
    }

    public StoreBlock(int no, IndexFile indexFile, DataFile dataFile, IndexMemory indexMemory) {
        this.no = no;
        this.indexFile = indexFile;
        this.dataFile = dataFile;
        this.indexMemory = indexMemory;
    }

    public int write(byte[] key, long keyL, byte[] value) throws EngineException {
        int offset;
        synchronized (this) {
            offset = dataFile.write(value);
            indexFile.write(key);
            indexMemory.put(keyL, offset);
        }
        return offset;
    }

    public byte[] read(long keyL) throws EngineException {
        int offset = indexMemory.get(keyL);
        if (offset == -1) {
            throw new EngineException(RetCodeEnum.NOT_FOUND, "not found " + " offset = " + keyL);
        }
        return dataFile.read(offset);
    }

    public byte[] readForRange(long keyL, byte[] data) throws EngineException {
        int offset = indexMemory.get(keyL);
        if (offset == -1) {
            throw new EngineException(RetCodeEnum.NOT_FOUND, "not found " + " offset = " + keyL);
        }
        return dataFile.readForRange(offset, data);
    }

    public void close() {
        indexFile.close();
        dataFile.close();
        indexMemory = null;
    }

    public int getNo() {
        return no;
    }

}
