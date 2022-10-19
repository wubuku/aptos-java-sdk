package com.github.wubuku.aptos.bean;

import com.fasterxml.jackson.annotation.JsonProperty;

public class TypeInfo {
    @JsonProperty("account_address")//=0x2b490841c230a31fe012f3b2a3f3d146316be073e599eb7d7e5074838073ef14,
    private String accountAddress;
    @JsonProperty("module_name")//=0x68656c6c6f5f7461626c65,
    private String moduleName;
    @JsonProperty("struct_name")//=0x4576656e7453746f7265
    private String structName;

    public String getAccountAddress() {
        return accountAddress;
    }

    public void setAccountAddress(String accountAddress) {
        this.accountAddress = accountAddress;
    }

    public String getModuleName() {
        return moduleName;
    }

    public void setModuleName(String moduleName) {
        this.moduleName = moduleName;
    }

    public String getStructName() {
        return structName;
    }

    public void setStructName(String structName) {
        this.structName = structName;
    }

    @Override
    public String toString() {
        return "TypeInfo{" +
                "accountAddress='" + accountAddress + '\'' +
                ", moduleName='" + moduleName + '\'' +
                ", structName='" + structName + '\'' +
                '}';
    }
}
