package com.alibabacloud.polar_race.engine.common.index;

import com.alibabacloud.polar_race.engine.common.ByteUtil;
import com.alibabacloud.polar_race.engine.common.config.StoreConstants;
import com.alibabacloud.polar_race.engine.common.exceptions.EngineException;
import com.alibabacloud.polar_race.engine.common.exceptions.RetCodeEnum;
import net.smacke.jaydio.DirectRandomAccessFile;
import sun.misc.Contended;

import java.io.EOFException;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * 索引持久化
 *
 * @author caozhenxiong
 * @version 2018-11-10
 */
@Contended
public class IndexFile {

    private FileChannel fileChannel;
    /**
     * 文件路径
     */
    private String path;

    private int no;

    private long writePosition = 0;

    private int perIndexByte = 8;

    private int startOffset;

    private DirectRandomAccessFile directRandomAccessFile;

    private ByteBuffer byteBuffer = ByteBuffer.allocate(perIndexByte);

    public int getStartOffset() {
        return startOffset;
    }


    public IndexFile(String path, int no) throws EngineException {

        try {
            this.path = path;
            this.no = no;
            directRandomAccessFile = new DirectRandomAccessFile(new File(path + File.separator + "index" + no), "r", 6 * 1024 * 1024);
            RandomAccessFile randomAccessFile = new RandomAccessFile(path + File.separator + "index" + no, "rw");
            fileChannel = randomAccessFile.getChannel();
        } catch (IOException e) {
            throw new EngineException(RetCodeEnum.IO_ERROR, "index file i/o error");
        }
    }

    public void write(byte[] key) throws EngineException {
        try {
            byteBuffer.put(key);
            byteBuffer.flip();
            fileChannel.write(byteBuffer, writePosition);
            writePosition += (perIndexByte);
        } catch (Exception e) {
            throw new EngineException(RetCodeEnum.IO_ERROR, "write index failed");
        } finally {
            byteBuffer.clear();
        }
    }


    public IndexMemory loadToMap() throws EngineException {
        IndexMemory indexMemory = new IndexMemory();
        int offset = 0;
        try {
//            while (true) {
//                ByteBuffer byteBuffer = ByteBuffer.allocate(perIndexByte);
//                if (fileChannel.read(byteBuffer) == -1) break;
//                byte[] key = byteBuffer.array();
//                indexMemory.put(ByteUtil.bytes2Long(key), offset++);
//            }
            // dio
            while (directRandomAccessFile.length() - directRandomAccessFile.getFilePointer() >= perIndexByte) {
                byte[] bytes = new byte[perIndexByte];
                directRandomAccessFile.read(bytes, 0, perIndexByte);
                indexMemory.put(ByteUtil.bytes2Long(bytes), offset++);
            }

        } catch (EOFException e) {
            System.out.println("文件读到末尾");
        } catch (IOException e) {
            e.printStackTrace();
            throw new EngineException(RetCodeEnum.IO_ERROR, "read index file i/o error");
        } finally {
            try {
                directRandomAccessFile.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        this.startOffset = offset;
        writePosition = (long) offset * (perIndexByte);
        return indexMemory;
    }

    public void close() {
        try {
            fileChannel.close();
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
    }


}
