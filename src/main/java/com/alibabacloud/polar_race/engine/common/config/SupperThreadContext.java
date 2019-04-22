package com.alibabacloud.polar_race.engine.common.config;

public class SupperThreadContext {

    private static ThreadLocal<byte[]> threadLocal = ThreadLocal.withInitial(() -> new byte[StoreConstants.VALUE_BYTE]);

    public static byte[] get(){
        return threadLocal.get();
    }

}
