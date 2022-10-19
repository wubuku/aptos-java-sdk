package com.github.wubuku.aptos.bean;

import com.fasterxml.jackson.annotation.JsonProperty;

public class SubmitTransactionRequest extends SubmitTransactionRequestBase {

    @JsonProperty("signature")
    private Signature signature;

    public Signature getSignature() {
        return signature;
    }

    public void setSignature(Signature signature) {
        this.signature = signature;
    }

    @Override
    public String toString() {
        return "SubmitTransactionRequest{" +
                super.toString() +
                "signature=" + signature +
                '}';
    }
}
