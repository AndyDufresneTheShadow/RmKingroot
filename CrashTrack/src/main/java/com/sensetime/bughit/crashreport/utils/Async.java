package com.sensetime.bughit.crashreport.utils;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Async {

    private static final ExecutorService executor = Executors.newCachedThreadPool();

    public static void run(Runnable task) {
        executor.execute(task);
    }

    public static ExecutorService getExecutor() {
        return executor;
    }
}