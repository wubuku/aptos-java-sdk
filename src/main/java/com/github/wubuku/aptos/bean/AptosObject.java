package com.github.wubuku.aptos.bean;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

public class AptosObject {
    @JsonProperty("inner")
    private String inner;

    public String getInner() {
        return inner;
    }

    public void setInner(String inner) {
        this.inner = inner;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AptosObject that = (AptosObject) o;
        return Objects.equals(inner, that.inner);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(inner);
    }

    @Override
    public String toString() {
        return "AptosObject{" +
                "inner='" + inner + '\'' +
                '}';
    }
}
