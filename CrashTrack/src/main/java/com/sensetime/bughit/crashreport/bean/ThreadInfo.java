package com.sensetime.bughit.crashreport.bean;

/**
 * Created by inx on 2016/8/31.
 */
public class ThreadInfo {
    private Long id;
    private String name;
    private StackTraceElement[] stackTraceElements;

    public ThreadInfo(Thread thread) {
        this.id = thread.getId();
        this.name = thread.getName();
        this.stackTraceElements = thread.getStackTrace();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public StackTraceElement[] getStackTraceElements() {
        return stackTraceElements;
    }

    public void setStackTraceElements(StackTraceElement[] stackTraceElements) {
        this.stackTraceElements = stackTraceElements;
    }
}
