package com.delivree.utils;

public class Tuple<T, U, V> {
    public T first;
    public U second;
    public V third;

    public Tuple(T first, U second, V third) {
        this.first = first;
        this.second = second;
        this.third = third;
    }
}
