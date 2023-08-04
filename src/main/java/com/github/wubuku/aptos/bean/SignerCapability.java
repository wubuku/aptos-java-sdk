package com.github.wubuku.aptos.bean;

public class SignerCapability {
    private String account;

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public SignerCapability() {
    }

    public SignerCapability(String account) {
        this.account = account;
    }

    @Override
    public String toString() {
        return "SignerCapability{" +
                "account='" + account + '\'' +
                '}';
    }
}
