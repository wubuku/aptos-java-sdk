package com.github.wubuku.aptos.bean;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

public class TokenId {
    @JsonProperty("token_data_id")
    private TokenDataId tokenDataId;

    /**
     * version number of the property map
     */
    @JsonProperty("property_version")
    private String propertyVersion;

    public TokenId() {
    }

    public TokenId(TokenDataId tokenDataId, String propertyVersion) {
        this.tokenDataId = tokenDataId;
        this.propertyVersion = propertyVersion;
    }

    public TokenDataId getTokenDataId() {
        return tokenDataId;
    }

    public void setTokenDataId(TokenDataId tokenDataId) {
        this.tokenDataId = tokenDataId;
    }

    public String getPropertyVersion() {
        return propertyVersion;
    }

    public void setPropertyVersion(String propertyVersion) {
        this.propertyVersion = propertyVersion;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TokenId tokenId = (TokenId) o;
        return Objects.equals(tokenDataId, tokenId.tokenDataId) && Objects.equals(propertyVersion, tokenId.propertyVersion);
    }

    @Override
    public int hashCode() {
        return Objects.hash(tokenDataId, propertyVersion);
    }

    @Override
    public String toString() {
        return "TokenId{" +
                "tokenDataId=" + tokenDataId +
                ", propertyVersion='" + propertyVersion + '\'' +
                '}';
    }
}
