package com.github.wubuku.aptos.bean;

import com.fasterxml.jackson.annotation.JsonProperty;

public class AccountResource<TData> {

    @JsonProperty("type")
    private String type;

    @JsonProperty("data")
    private TData data;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public TData getData() {
        return data;
    }

    public void setData(TData data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "AccountResource{" +
                "type='" + type + '\'' +
                ", data=" + data +
                '}';
    }
}
