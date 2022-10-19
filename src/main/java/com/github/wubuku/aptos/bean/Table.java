package com.github.wubuku.aptos.bean;

public class Table {

    private String handle;

    public String getHandle() {
        return handle;
    }

    public void setHandle(String handle) {
        this.handle = handle;
    }

    @Override
    public String toString() {
        return "Table{" +
                "handle='" + handle + '\'' +
                '}';
    }
}
