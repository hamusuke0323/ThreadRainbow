package com.hamusuke.threadr.util;

import com.google.common.base.Suppliers;

import java.util.Objects;
import java.util.function.Supplier;

public class Lazy<T> {
    private final Supplier<T> supplier;

    public Lazy(Supplier<T> delegate) {
        Objects.requireNonNull(delegate);
        this.supplier = Suppliers.memoize(delegate::get);
    }

    public T get() {
        return this.supplier.get();
    }
}
