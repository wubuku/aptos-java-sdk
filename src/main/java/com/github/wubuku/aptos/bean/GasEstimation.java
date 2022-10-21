package com.github.wubuku.aptos.bean;

import com.fasterxml.jackson.annotation.JsonProperty;

public class GasEstimation {
    @JsonProperty("gas_estimate")
    private Integer gasEstimate;

    @JsonProperty("deprioritized_gas_estimate")
    private Integer deprioritizedGasEstimate;

    @JsonProperty("prioritized_gas_estimate")
    private Integer prioritizedGasEstimate;

    public Integer getGasEstimate() {
        return gasEstimate;
    }

    public void setGasEstimate(Integer gasEstimate) {
        this.gasEstimate = gasEstimate;
    }

    public Integer getDeprioritizedGasEstimate() {
        return deprioritizedGasEstimate;
    }

    public void setDeprioritizedGasEstimate(Integer deprioritizedGasEstimate) {
        this.deprioritizedGasEstimate = deprioritizedGasEstimate;
    }

    public Integer getPrioritizedGasEstimate() {
        return prioritizedGasEstimate;
    }

    public void setPrioritizedGasEstimate(Integer prioritizedGasEstimate) {
        this.prioritizedGasEstimate = prioritizedGasEstimate;
    }

    @Override
    public String toString() {
        return "GasEstimation{" +
                "gasEstimate=" + gasEstimate +
                ", deprioritizedGasEstimate=" + deprioritizedGasEstimate +
                ", prioritizedGasEstimate=" + prioritizedGasEstimate +
                '}';
    }
}
