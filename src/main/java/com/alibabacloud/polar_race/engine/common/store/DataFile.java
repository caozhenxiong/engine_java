package com.alibabacloud.polar_race.engine.common.store;

import com.alibabacloud.polar_race.engine.common.config.StoreConstants;
import com.alibabacloud.polar_race.engine.common.config.SupperThreadContext;
import com.alibabacloud.polar_race.engine.common.exceptions.EngineException;
import com.alibabacloud.polar_race.engine.common.exceptions.RetCodeEnum;
import net.smacke.jaydio.DirectRandomAccessFile;
import sun.misc.Contended;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * 数据文件
 *
 * @author caozhenxiong
 * @version 2018-11-09
 */
@Contended
public class DataFile {

    /**
     * 文件no，文件名
     */
    private int no;
    /**
     * path
     */
    private String path;
    /**
     * offset 初始大小为-1
     */
    private int offset;
    /**
     * 写位置信息
     */
    private long writePosition;
    /**
     * 读信道
     */
    private DirectRandomAccessFile dReadRandomAccesssFile;

//    private RandomAccessFile readRandomAccesssFile;

//    private DirectRandomAccessFile writeRandomAccessFile;
    /**
     * 写信道
     */
    private FileChannel writeChannel;

    private ByteBuffer byteBuffer = ByteBuffer.allocateDirect(StoreConstants.VALUE_BYTE);


    public DataFile(int no, String path, int offset) throws EngineException {
        this.no = no;
        this.path = path;
        this.offset = offset + 1;
        this.writePosition = (long) (this.offset) << 12;
        try {
            init();
        } catch (Exception e) {
            e.printStackTrace();
            throw new EngineException(RetCodeEnum.IO_ERROR, "data file 初始化分配失败");
        }
    }

    private void init() throws Exception {
        dReadRandomAccesssFile = new DirectRandomAccessFile(path + File.separator + no, "r");
        RandomAccessFile randomAccessFile = new RandomAccessFile(path + File.separator + no, "rw");
        writeChannel = randomAccessFile.getChannel();
    }

    public byte[] read(int offset) throws EngineException {
        try {
            byte[] data = SupperThreadContext.get();
            synchronized (this) {
                dReadRandomAccesssFile.seek((long) offset << 12);
                dReadRandomAccesssFile.read(data);
            }
            return data;
        } catch (Exception e) {
            throw new EngineException(RetCodeEnum.IO_ERROR, "read io error");
        }
    }

    public byte[] readForRange(int offset, byte[] data) throws EngineException {
        try {
            synchronized (this) {
                dReadRandomAccesssFile.seek((long) offset << 12);
                dReadRandomAccesssFile.read(data);
            }
            return data;
        } catch (Exception e) {
            throw new EngineException(RetCodeEnum.IO_ERROR, "read io error");
        }
    }

    public int write(byte[] data) throws EngineException {
        try {
            byteBuffer.put(data);
            byteBuffer.flip();
            writeChannel.write(byteBuffer, writePosition);
        } catch (IOException e) {
            throw new EngineException(RetCodeEnum.IO_ERROR, "read io error");
        } finally {
            byteBuffer.clear();
        }
        writePosition += StoreConstants.VALUE_BYTE;
        return offset++;
    }

    public void close() {
        try {
            dReadRandomAccesssFile.close();
            writeChannel.close();
        } catch (Exception e) {
            System.out.println("data file 关闭失败");
            e.printStackTrace();
        }

    }


}
