package com.github.wubuku.aptos.bean;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.util.Collections;
import java.util.List;

@JsonDeserialize(using = OptionDeserializer.class)
public class Option<T> {
    @JsonProperty("vec")
    private List<T> vec;

    public Option() {
        this.vec = Collections.emptyList();
    }

    public Option(List<T> vec) {
        this.vec = vec;
    }

    public List<T> getVec() {
        return vec;
    }

    public void setVec(List<T> vec) {
        this.vec = vec;
    }

    @Override
    public String toString() {
        return "Option{" +
                "vec=" + vec +
                '}';
    }
}
