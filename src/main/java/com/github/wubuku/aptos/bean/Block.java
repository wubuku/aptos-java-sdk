package com.github.wubuku.aptos.bean;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class Block {
    @JsonProperty("block_height")//: "1",
    private String blockHeight;
    @JsonProperty("block_hash")//: "0x66bdfab31d6fed23f1e0afaf714e421f605c2c74840782ceb004165584449e73",
    private String blockHash;
    @JsonProperty("block_timestamp")//: "1663294909795650",
    private String blockTimestamp;
    @JsonProperty("first_version")//: "1",
    private String firstVersion;
    @JsonProperty("last_version")//: "1",
    private String lastVersion;
    @JsonProperty("transactions")//: null
    private List<Transaction> transactions;

    public String getBlockHeight() {
        return blockHeight;
    }

    public void setBlockHeight(String blockHeight) {
        this.blockHeight = blockHeight;
    }

    public String getBlockHash() {
        return blockHash;
    }

    public void setBlockHash(String blockHash) {
        this.blockHash = blockHash;
    }

    public String getBlockTimestamp() {
        return blockTimestamp;
    }

    public void setBlockTimestamp(String blockTimestamp) {
        this.blockTimestamp = blockTimestamp;
    }

    public String getFirstVersion() {
        return firstVersion;
    }

    public void setFirstVersion(String firstVersion) {
        this.firstVersion = firstVersion;
    }

    public String getLastVersion() {
        return lastVersion;
    }

    public void setLastVersion(String lastVersion) {
        this.lastVersion = lastVersion;
    }

    public List<Transaction> getTransactions() {
        return transactions;
    }

    public void setTransactions(List<Transaction> transactions) {
        this.transactions = transactions;
    }

    @Override
    public String toString() {
        return "Block{" +
                "blockHeight='" + blockHeight + '\'' +
                ", blockHash='" + blockHash + '\'' +
                ", blockTimestamp='" + blockTimestamp + '\'' +
                ", firstVersion='" + firstVersion + '\'' +
                ", lastVersion='" + lastVersion + '\'' +
                ", transactions=" + transactions +
                '}';
    }
}
