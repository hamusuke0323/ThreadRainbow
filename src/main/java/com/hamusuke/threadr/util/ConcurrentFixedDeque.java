package com.hamusuke.threadr.util;

import java.util.Collection;
import java.util.concurrent.ConcurrentLinkedDeque;

public class ConcurrentFixedDeque<E> extends ConcurrentLinkedDeque<E> {
    private int sizeLimit = Integer.MAX_VALUE;

    public ConcurrentFixedDeque() {
    }

    public ConcurrentFixedDeque(Collection<? extends E> c) {
        super(c);
    }

    public ConcurrentFixedDeque(int sizeLimit) {
        if (sizeLimit < 0) {
            sizeLimit = 0;
        }

        this.sizeLimit = sizeLimit;
    }

    public ConcurrentFixedDeque(Collection<? extends E> c, int sizeLimit) {
        super(c);
        if (sizeLimit < 0) {
            sizeLimit = 0;
        }

        this.sizeLimit = sizeLimit;
    }

    public int getSizeLimit() {
        return this.sizeLimit;
    }

    public void setSizeLimit(int sizeLimit) {
        this.sizeLimit = sizeLimit;
    }

    @Override
    public void addFirst(E e) {
        while (this.size() >= this.sizeLimit) {
            this.pollLast();
        }

        super.addFirst(e);
    }

    @Override
    public void addLast(E e) {
        while (this.size() >= this.sizeLimit) {
            this.pollFirst();
        }

        super.addLast(e);
    }
}
