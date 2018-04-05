package net.chaosworship.topuslibtest.benchmark;


import android.os.SystemClock;

public abstract class TimedRunner {

    abstract void run();

    // return ms
    public long timedRun() {
        long start = SystemClock.uptimeMillis();
        run();
        return SystemClock.uptimeMillis() - start;
    }
}
