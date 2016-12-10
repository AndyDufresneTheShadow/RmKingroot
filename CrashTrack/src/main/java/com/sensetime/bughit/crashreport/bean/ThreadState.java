package com.sensetime.bughit.crashreport.bean;

import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

/**
 * Created by inx on 2016/8/31.
 */
public class ThreadState{

    private ArrayList<ThreadInfo> threadState;

    public ThreadState() {
        Map<Thread, StackTraceElement[]> liveThreads = Thread.getAllStackTraces();
        threadState = new ArrayList<ThreadInfo>();
        Set<Thread> threads = liveThreads.keySet();
        for (Thread thread : threads) {
            threadState.add(new ThreadInfo(thread));
        }

    }

}
