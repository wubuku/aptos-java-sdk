package com.github.wubuku.aptos.bean;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CoinInfo {
    @JsonProperty("decimals")
    private Integer decimals;
    @JsonProperty("name")//: "Aptos Coin",
    private String name;
    @JsonProperty("supply")
    private Option<OptionalAggregator> supply;
    @JsonProperty("symbol")//: "APT"
    private String symbol;

    public Integer getDecimals() {
        return decimals;
    }

    public void setDecimals(Integer decimals) {
        this.decimals = decimals;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Option<OptionalAggregator> getSupply() {
        return supply;
    }

    public void setSupply(Option<OptionalAggregator> supply) {
        this.supply = supply;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    @Override
    public String toString() {
        return "CoinInfo{" +
                "decimals=" + decimals +
                ", name='" + name + '\'' +
                ", supply=" + supply +
                ", symbol='" + symbol + '\'' +
                '}';
    }
}
