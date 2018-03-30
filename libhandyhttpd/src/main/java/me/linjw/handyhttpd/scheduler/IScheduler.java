package me.linjw.handyhttpd.scheduler;

/**
 * Created by linjiawei on 2018/3/30.
 * e-mail : bluesky466@qq.com
 */

public interface IScheduler {
    void schedule(Runnable runnable);
}
