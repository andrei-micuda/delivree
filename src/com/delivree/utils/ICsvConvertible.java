package com.delivree.utils;

public interface ICsvConvertible<T> {
    String[] stringify();
    static <T>T parse(String csv) { return null; }
}
