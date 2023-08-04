package com.github.wubuku.aptos.bean;

public class TableWithLength {
    private Table inner;
    private int length;

    public TableWithLength() {
    }

    public TableWithLength(Table inner, int length) {
        this.inner = inner;
        this.length = length;
    }

    public Table getInner() {
        return inner;
    }

    public void setInner(Table inner) {
        this.inner = inner;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    @Override
    public String toString() {
        return "TableWithLength{" +
                "inner=" + inner +
                ", length=" + length +
                '}';
    }
}
