package com.github.wubuku.aptos.utils;

import com.github.wubuku.aptos.bean.AptosError;

public class NodeApiException extends RuntimeException {
    private Integer httpStatusCode;
    private AptosError aptosError;

    private String requestUrl;

    public NodeApiException(Integer httpStatusCode, Throwable cause) {
        super(getString(httpStatusCode, null, null), cause);
        this.httpStatusCode = httpStatusCode;
    }

    public NodeApiException(Integer httpStatusCode, AptosError aptosError, Throwable cause) {
        super(getString(httpStatusCode, aptosError, null), cause);
        this.httpStatusCode = httpStatusCode;
        this.aptosError = aptosError;
    }


    public NodeApiException(Integer httpStatusCode, AptosError aptosError, String requestUrl, Throwable cause) {
        super(getString(httpStatusCode, aptosError, requestUrl), cause);
        this.httpStatusCode = httpStatusCode;
        this.aptosError = aptosError;
        this.requestUrl = requestUrl;
    }

    private static String getString(Integer httpStatusCode, AptosError aptosError, String requestUrl) {
        return "NodeApiException{" +
                "httpStatusCode=" + httpStatusCode +
                ", aptosError=" + aptosError +
                ", requestUrl=" + requestUrl +
                '}';
    }

    public Integer getHttpStatusCode() {
        return httpStatusCode;
    }

    public void setHttpStatusCode(Integer httpStatusCode) {
        this.httpStatusCode = httpStatusCode;
    }

    public AptosError getAptosError() {
        return aptosError;
    }

    public void setAptosError(AptosError aptosError) {
        this.aptosError = aptosError;
    }

    public String getRequestUrl() {
        return requestUrl;
    }

    public void setRequestUrl(String requestUrl) {
        this.requestUrl = requestUrl;
    }

    @Override
    public String toString() {
        return getString(httpStatusCode, aptosError, requestUrl);
    }
}
