package com.github.wubuku.aptos.bean;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class MoveFunctionGenericTypeParam {
    @JsonProperty("constraints")
    private List<String> constraints;

    public List<String> getConstraints() {
        return constraints;
    }

    public void setConstraints(List<String> constraints) {
        this.constraints = constraints;
    }

    @Override
    public String toString() {
        return "MoveFunctionGenericTypeParam{" +
                "constraints=" + constraints +
                '}';
    }
}
