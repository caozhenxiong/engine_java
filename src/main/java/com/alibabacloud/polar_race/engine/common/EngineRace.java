package com.alibabacloud.polar_race.engine.common;

import com.alibabacloud.polar_race.engine.common.exceptions.EngineException;


public class EngineRace extends AbstractEngine {

    private Flow flow;

    @Override
    public void open(String path) throws EngineException {
        flow = new Flow();
        try {
            flow.open(path);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void write(byte[] key, byte[] value) throws EngineException {
        flow.write(key,value);
    }

    @Override
    public byte[] read(byte[] key) throws EngineException {
        return flow.read(key);
    }

    @Override
    public void range(byte[] lower, byte[] upper, AbstractVisitor visitor) throws EngineException {
        flow.range(lower,upper,visitor);
    }

    @Override
    public void close() {
        flow.close();
    }

}
