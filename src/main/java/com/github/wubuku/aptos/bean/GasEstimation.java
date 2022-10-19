package com.github.wubuku.aptos.bean;

import com.fasterxml.jackson.annotation.JsonProperty;

public class GasEstimation {
    @JsonProperty("gas_estimate")
    private Integer gasEstimate;

    public Integer getGasEstimate() {
        return gasEstimate;
    }

    public void setGasEstimate(Integer gasEstimate) {
        this.gasEstimate = gasEstimate;
    }

    @Override
    public String toString() {
        return "GasEstimation{" +
                "gasEstimate=" + gasEstimate +
                '}';
    }
}
