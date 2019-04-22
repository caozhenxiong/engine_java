import com.alibabacloud.polar_race.engine.common.AbstractVisitor;
import com.alibabacloud.polar_race.engine.common.ByteUtil;
import com.alibabacloud.polar_race.engine.common.EngineRace;
import com.alibabacloud.polar_race.engine.common.exceptions.EngineException;

import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

public class Test2 {
    private static EngineRace engineRace;
//
    public static void main(String[] args) throws IOException, EngineException, InterruptedException {
        long startTime = System.currentTimeMillis();
        engineRace = new EngineRace();
        AbstractVisitor visitor = new VisitorImpl();
        engineRace.open("/Users/caozhenxiong/ssym/store");

//        write(engineRace);
        CountDownLatch countDownLatch1 = new CountDownLatch(64);

        for (int i = 0; i < 64; i++) {
            new RangeThread(engineRace, visitor,countDownLatch1).start();
        }
        countDownLatch1.await();
        System.out.println("读取完成，关闭，耗时 = " + (System.currentTimeMillis() - startTime));
        engineRace.close();
        System.out.println("已关闭");


    }

    public static void write(EngineRace engineRace) throws EngineException {
        long tmp = 1;
        for (int i = 1; i <= 640000; i++) {
            byte[] key = ByteUtil.long2Bytes(tmp);
            engineRace.write(key, makeValue(key));
            tmp += 1;
        }
    }

    public static byte[] makeValue(byte[] key) {
        byte[] bytes = new byte[4096];
        for (int i = 0; i < bytes.length; i += key.length) {
            System.arraycopy(key, 0, bytes, i, key.length);
        }
        return bytes;
    }


    static class VisitorImpl extends AbstractVisitor {

        private AtomicInteger integer = new AtomicInteger(0);

        @Override
        public void visit(byte[] key, byte[] value) {
            byte[] prefixValue = new byte[8];
            System.arraycopy(value, 0, prefixValue, 0, 8);
            String result = Arrays.equals(key, prefixValue)?"true":"false=======================false";
//            System.out.println("thread name:" + Thread.currentThread().getName() + " key:" + ByteUtil
//                    .bytes2Long(key) +","+ ByteUtil.bytes2Long(prefixValue) + " " + result + " " + Arrays.equals(key, prefixValue) + " " + integer.getAndIncrement());
        }
    }
}
