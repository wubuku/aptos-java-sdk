package com.github.wubuku.aptos.bean;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Account {
    @JsonProperty("sequence_number")
    private String sequenceNumber;

    @JsonProperty("authentication_key")
    private String authenticationKey;

    public String getSequenceNumber() {
        return sequenceNumber;
    }

    public void setSequenceNumber(String sequenceNumber) {
        this.sequenceNumber = sequenceNumber;
    }

    public String getAuthenticationKey() {
        return authenticationKey;
    }

    public void setAuthenticationKey(String authenticationKey) {
        this.authenticationKey = authenticationKey;
    }

    @Override
    public String toString() {
        return "Account{" +
                "sequenceNumber='" + sequenceNumber + '\'' +
                ", authenticationKey='" + authenticationKey + '\'' +
                '}';
    }
}
