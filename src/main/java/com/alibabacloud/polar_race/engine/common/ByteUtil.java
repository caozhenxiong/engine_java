package com.alibabacloud.polar_race.engine.common;

/**
 * @author dongxu.xiu
 * @since 2018-10-15 下午5:19
 */
public class ByteUtil {

    public static byte[] short2byte(short s) {
        byte[] b = new byte[2];
        for (int i = 0; i < 2; i++) {
            int offset = 16 - (i + 1) * 8;
            b[i] = (byte) ((s >> offset) & 0xff);
        }
        return b;
    }

    public static short byte2short(byte[] b) {
        short l = 0;
        for (int i = 0; i < 2; i++) {
            l <<= 8;
            l |= (b[i] & 0xff);
        }
        return l;
    }

    public static byte[] int2byte(int num) {
        byte[] n = new byte[4];
        for (int ix = 0; ix < 4; ++ix) {
            int offset = 32 - (ix + 1) * 8;
            n[ix] = (byte) ((num >> offset) & 0xff);
        }
        return n;
    }

    public static int byte2int(byte[] byteNum) {
        int n = 0;
        for (int ix = 0; ix < 4; ++ix) {
            n <<= 8;
            n |= (byteNum[ix] & 0xff);
        }
        return n;
    }

    public static byte[] long2Bytes(long num) {
        byte[] byteNum = new byte[8];
        for (int ix = 0; ix < 8; ++ix) {
            int offset = 64 - (ix + 1) * 8;
            byteNum[ix] = (byte) ((num >> offset) & 0xff);
        }
        return byteNum;
    }

    public static long bytes2Long(byte[] byteNum) {
        long num = 0;
        for (int ix = 0; ix < 8; ++ix) {
            num <<= 8;
            num |= (byteNum[ix] & 0xff);
        }
        return num;
    }

    public static byte[] toBytes(String str) {
        if (str == null || str.trim().equals("")) {
            return new byte[0];
        }

        byte[] bytes = new byte[str.length() / 2];
        for (int i = 0; i < str.length() / 2; i++) {
            String subStr = str.substring(i * 2, i * 2 + 2);
            bytes[i] = (byte) Integer.parseInt(subStr, 16);
        }

        return bytes;
    }

//    public static void main(String[] args) {
//        byte[] bytes = toBytes("CAAC96FBDACE7D98");
//        for (int i = 0; i < bytes.length; i++){
//            System.out.println(bytes[i]);
//        }
//    }


}
