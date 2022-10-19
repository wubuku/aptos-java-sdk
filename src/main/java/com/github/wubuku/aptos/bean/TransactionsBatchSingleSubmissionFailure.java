package com.github.wubuku.aptos.bean;

import com.fasterxml.jackson.annotation.JsonProperty;

public class TransactionsBatchSingleSubmissionFailure {
    @JsonProperty("error")
    private AptosError error;
    @JsonProperty("transaction_index")
    private Integer transactionIndex;

    public AptosError getError() {
        return error;
    }

    public void setError(AptosError error) {
        this.error = error;
    }

    public Integer getTransactionIndex() {
        return transactionIndex;
    }

    public void setTransactionIndex(Integer transactionIndex) {
        this.transactionIndex = transactionIndex;
    }

    @Override
    public String toString() {
        return "TransactionsBatchSingleSubmissionFailure{" +
                "error=" + error +
                ", transactionIndex=" + transactionIndex +
                '}';
    }
}
