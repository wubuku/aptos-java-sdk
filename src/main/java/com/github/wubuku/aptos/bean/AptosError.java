package com.github.wubuku.aptos.bean;

import com.fasterxml.jackson.annotation.JsonProperty;

public class AptosError {
    @JsonProperty("message")
    private String message;
    @JsonProperty("error_code")
    private String errorCode;
    @JsonProperty("vm_error_code")
    private Integer vmErrorCode;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public Integer getVmErrorCode() {
        return vmErrorCode;
    }

    public void setVmErrorCode(Integer vmErrorCode) {
        this.vmErrorCode = vmErrorCode;
    }

    @Override
    public String toString() {
        return "AptosError{" +
                "message='" + message + '\'' +
                ", errorCode='" + errorCode + '\'' +
                ", vmErrorCode=" + vmErrorCode +
                '}';
    }
}
