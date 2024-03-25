package com.hamusuke.threadr.client.network.spider;

import com.hamusuke.threadr.network.Spider;

public abstract class AbstractClientSpider extends Spider {
    protected AbstractClientSpider(String name) {
        super(name);
    }

    public void setId(int id) {
        this.id = id;
    }
}
