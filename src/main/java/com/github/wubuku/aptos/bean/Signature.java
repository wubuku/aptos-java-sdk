package com.github.wubuku.aptos.bean;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Signature {
    public static final String TYPE_ED25519_SIGNATURE = "ed25519_signature";

    @JsonProperty("public_key")
    private String publicKey;
    @JsonProperty("signature")
    private String signature;
    @JsonProperty("type")//:"ed25519_signature"
    private String type;

    public String getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(String publicKey) {
        this.publicKey = publicKey;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "Signature{" +
                "publicKey='" + publicKey + '\'' +
                ", signature='" + signature + '\'' +
                ", type='" + type + '\'' +
                '}';
    }
}
