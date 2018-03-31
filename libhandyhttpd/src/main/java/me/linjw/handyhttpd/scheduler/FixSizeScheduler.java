package me.linjw.handyhttpd.scheduler;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by linjiawei on 2018/3/30.
 * e-mail : bluesky466@qq.com
 */

@SuppressWarnings("WeakerAccess")
public class FixSizeScheduler implements IScheduler {
    public static final int DEFAULT_SIZE = 4;

    private ExecutorService mExecutorService;

    public FixSizeScheduler() {
        this(DEFAULT_SIZE);
    }

    public FixSizeScheduler(int size) {
        mExecutorService = Executors.newFixedThreadPool(size);
    }

    @Override
    public void schedule(Runnable runnable) {
        mExecutorService.execute(runnable);
    }
}