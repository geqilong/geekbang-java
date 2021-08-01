package org.geektimes.microprofile.faulttolerance;

public class BuzServiceImpl implements BuzService{
    @Override
    public void task() {
        try {
            Thread.sleep(100L);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public void fallbackTask() {
        System.out.println("fallbackTask");
    }
}
