package com.github.wubuku.aptos.bean;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Token {
    private TokenId id;
    /**
     * The amount of tokens. Only property_version = 0 can have a value bigger than 1.
     * Server will return string for u64.
     */
    private String amount;//: U64;

    /**
     * The property map of the token.
     * When property_version = 0, the token_properties are the same as default_properties in TokenData, we don't store it.
     * When the property_map mutates, a new property_version is assigned to the token.
     */
    @JsonProperty("token_properties")
    private TokenPropertyMap tokenProperties;//: AnyObject;

    public Token() {
    }

    public Token(TokenId id, String amount, TokenPropertyMap tokenProperties) {
        this.id = id;
        this.amount = amount;
        this.tokenProperties = tokenProperties;
    }

    public TokenId getId() {
        return id;
    }

    public void setId(TokenId id) {
        this.id = id;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public TokenPropertyMap getTokenProperties() {
        return tokenProperties;
    }

    public void setTokenProperties(TokenPropertyMap tokenProperties) {
        this.tokenProperties = tokenProperties;
    }

    @Override
    public String toString() {
        return "Token{" +
                "id=" + id +
                ", amount='" + amount + '\'' +
                ", tokenProperties=" + tokenProperties +
                '}';
    }
}
