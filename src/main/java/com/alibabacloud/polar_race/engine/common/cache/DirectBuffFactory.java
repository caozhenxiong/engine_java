package com.alibabacloud.polar_race.engine.common.cache;

import com.alibabacloud.polar_race.engine.common.iolib.DirectFileUtils;

import java.lang.reflect.Constructor;
import java.nio.ByteBuffer;

/**
 * DirectBuff工厂类，因为DirectBuffer不能够直接使用，所以我们使用反射，将他实例化
 */
public class DirectBuffFactory {

    public static ByteBuffer allocateAlign(int cap) {
        ByteBuffer byteBuffer = null;
        try {
            Class aClass = Class.forName("java.nio.DirectByteBuffer");
            Constructor declaredConstructor = aClass.getDeclaredConstructor(new Class[]{long.class, int.class, Object.class});
            //使用accessible，可以反射private
            declaredConstructor.setAccessible(true);
            byteBuffer = (ByteBuffer) declaredConstructor.newInstance(DirectFileUtils.consAdrress(cap), cap, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return byteBuffer;
    }


}
