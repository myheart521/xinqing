package com.hpu.xinqing.TimeTask.warning;



import java.util.concurrent.ArrayBlockingQueue;

public class JiangBlockingQueue<E> extends ArrayBlockingQueue<E> {
    public JiangBlockingQueue(int capacity) {
        super(capacity);
    }
    /**
     * 如果o为null，则不放入队列
     * 原版的put方法会抛出异常，这里重写了put方法，使其不抛出异常
     */
    @Override
    public void put(E e) throws InterruptedException {
        if (e==null) return;
        super.put(e);
    }

}
