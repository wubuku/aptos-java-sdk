package com.github.wubuku.aptos.bean;

import java.util.Objects;

public class Triple<T1, T2, T3> {
    private T1 item1;
    private T2 item2;
    private T3 item3;

    public Triple() {
    }

    public Triple(T1 item1, T2 item2, T3 item3) {
        this.item1 = item1;
        this.item2 = item2;
        this.item3 = item3;
    }

    public T1 getItem1() {
        return item1;
    }

    public void setItem1(T1 item1) {
        this.item1 = item1;
    }

    public T2 getItem2() {
        return item2;
    }

    public void setItem2(T2 item2) {
        this.item2 = item2;
    }

    public T3 getItem3() {
        return item3;
    }

    public void setItem3(T3 item3) {
        this.item3 = item3;
    }

    @Override
    public String toString() {
        return "Triple{" +
                "item1=" + item1 +
                ", item2=" + item2 +
                ", item3=" + item3 +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Triple<?, ?, ?> triple = (Triple<?, ?, ?>) o;
        return Objects.equals(item1, triple.item1) && Objects.equals(item2, triple.item2) && Objects.equals(item3, triple.item3);
    }

    @Override
    public int hashCode() {
        return Objects.hash(item1, item2, item3);
    }
}
