package com.github.wubuku.aptos.bean;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Move module bytecode along with its ABI.
 */
public class MoveModuleBytecode {
    @JsonProperty("bytecode")
    private String bytecode;

    @JsonProperty("abi")
    private MoveModule abi;

    public String getBytecode() {
        return bytecode;
    }

    public void setBytecode(String bytecode) {
        this.bytecode = bytecode;
    }

    public MoveModule getAbi() {
        return abi;
    }

    public void setAbi(MoveModule abi) {
        this.abi = abi;
    }

    @Override
    public String toString() {
        return "MoveModuleBytecode{" +
                "bytecode='" + bytecode + '\'' +
                ", abi=" + abi +
                '}';
    }
}
