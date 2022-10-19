package com.github.wubuku.aptos.bean;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class TransactionsBatchSubmissionResult {
    @JsonProperty("transaction_failures")
    private List<TransactionsBatchSingleSubmissionFailure> transactionFailures;

    public List<TransactionsBatchSingleSubmissionFailure> getTransactionFailures() {
        return transactionFailures;
    }

    public void setTransactionFailures(List<TransactionsBatchSingleSubmissionFailure> transactionFailures) {
        this.transactionFailures = transactionFailures;
    }

    @Override
    public String toString() {
        return "TransactionsBatchSubmissionResult{" +
                "transactionFailures=" + transactionFailures +
                '}';
    }
}
