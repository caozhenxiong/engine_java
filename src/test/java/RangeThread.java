import com.alibabacloud.polar_race.engine.common.AbstractVisitor;
import com.alibabacloud.polar_race.engine.common.EngineRace;
import com.alibabacloud.polar_race.engine.common.exceptions.EngineException;

import java.util.concurrent.CountDownLatch;

public class RangeThread extends Thread{
    private EngineRace engineRace;
    private AbstractVisitor visitor;
    private CountDownLatch countDownLatch;

    public RangeThread(EngineRace engineRace, AbstractVisitor visitor, CountDownLatch countDownLatch) {
        this.visitor = visitor;
        this.engineRace = engineRace;
        this.countDownLatch = countDownLatch;
    }

    @Override
    public void run() {
        long tmp = 0;
        byte[] lower = null;
        tmp += (64000 - 1);
        byte[] upper = null;
        try {
            engineRace.range(lower, upper, visitor);
            engineRace.range(lower, upper, visitor);
            countDownLatch.countDown();
        } catch (EngineException e) {
            e.printStackTrace();
        }
    }
}
