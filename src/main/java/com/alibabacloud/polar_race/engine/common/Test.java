package com.alibabacloud.polar_race.engine.common;

import com.alibabacloud.polar_race.engine.common.exceptions.EngineException;
import com.alibabacloud.polar_race.engine.common.index.SmartSortIndex;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * @author dongxu.xiu
 * @since 2018-10-17 下午2:24
 */
public class Test {

    private static EngineRace engineRace;

//    public static void main(String[] args) throws EngineException, InterruptedException {

//        for(long i = -1000; i <= 1000; i ++){
//            SmartSortIndex.getInstance().set(i);
//        }
//        SmartSortIndex.getInstance().sort();
//        for (int i = 0; i <= 2000; i ++){
//            System.out.println(SmartSortIndex.getInstance().get(i));
//        }
////
//        engineRace = new EngineRace();
//        engineRace.open("/Users/caozhenxiong/ssym/store");
//        long[] nums = new long[]{
//                3087112465381965360l,
//                -6470943323061382924l,
//                584748141746001723l,
//                624530100976059866l,
//                1048116476840818498l,
//                1424912650558293059l,
//                1648824752144209550l,
//                1838051949958021646l,
//                2361036503014997493l,
//                2628517971918735080l,
//                2693725856866596883l,
//                2840914168447638663l,
//                3087112465381965360l,
//                3718600275114582565l,
//                3728486439726222621l,
//                3924078446937338322l,
//                4489202429059799454l,
//                4889480237003760046l,
//                5041088028333947706l,
//                5485681537384102836l,
//                5609992358059837518l,
//                5634668986059900467l,
//                5638954339590629545l,
//                5824687112438304394l,
//                5938458310327615322l,
//                6320444192145160427l,
//                6400643057217093246l,
//                7113962218366754688l,
//                7211569362017098468l,
//                7364013977160132707l,
//                7663600017727571625l,
//                7666963986696865078l,
//                8415802289839700778l,
//                8680426843284184325l,
//                9156338186430727676l
//
//        };
////        System.out.println(nums.length);
////        for (int i = 0; i < nums.length; i ++){
////            System.out.println(nums[i]);
////        }
//        byte[] byte3 = new byte[]{
//                1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25,26,27,28,29,30,31,32,33,34,35
//        };
//        byte[][] bytes = new byte[35][4096];
//        for (int i = 0; i < bytes.length; i++){
//            bytes[i] = makeValue(byte3[i]);
//        }
//
//        ExecutorService executorService = Executors.newFixedThreadPool(15);
//        final CountDownLatch countDownLatch = new CountDownLatch(1);
//        for (int i = 0; i < 15; i++){
//            final byte[] key = ByteUtil.long2Bytes(nums[i]);
//            final byte[] value = bytes[i];
//            executorService.submit(new Runnable() {
//                @Override
//                public void run() {
//                    try {
//                        visitor visitor = new visitor();
//                        engineRace.range(null,null,visitor);
//                        visitor visitor2 = new visitor();
//                        engineRace.range(null,null,visitor2);
//                        countDownLatch.countDown();
//                    } catch (EngineException e) {
//
//                    }
//
//                }
//            });
//        }
//        executorService.shutdown();
//        countDownLatch.await(5,TimeUnit.SECONDS);
////
////        for (int i = 0; i < nums.length; i++){
////                System.out.println(Arrays.equals(engineRace.read(ByteUtil.long2Bytes(nums[i])), bytes[i]));
////
////        }
//    }

//    static class visitor extends AbstractVisitor{
//
//        @Override
//        public void visit(byte[] key, byte[] value) {
//            System.out.println(value[0]);
//        }
//    }

//    public static void main(String[] args) {
//        //FFFFFFBAF3C73296
//        System.out.println(ByteUtil.bytes2Long(new byte[]{30,30,31,30,20,20,20,20}));
//        System.out.println(Long.parseLong("3030313033382020",16));
//        System.out.println(Long.toHexString(3472329326750343200l));
//
//    }


////    测试步骤
//    // 1.从0开始创建文件，读写正常
    // 2.继续写入文件

    public static byte[] makeValue(byte a) {
        byte[] bytes = new byte[4096];
        for (int i = 0; i < bytes.length; i++) {
            bytes[i] = (byte)a;
        }
        return bytes;
    }

    public static byte[] makeKey(byte b) {
        byte[] bytes = new byte[8];
        for (int i = 0; i < bytes.length; i++) {
            bytes[i] = b;
        }
        return bytes;
    }
}
