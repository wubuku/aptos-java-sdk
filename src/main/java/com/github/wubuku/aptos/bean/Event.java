package com.github.wubuku.aptos.bean;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Event<TData> {

    @JsonProperty("version")
    private String version;

    @JsonProperty("key")
    private String key;

    @JsonProperty("sequence_number")
    private String sequenceNumber;

    @JsonProperty("type")
    private String type;

    @JsonProperty("data")
    private TData data;

    /*
    "guid": {
      "creation_number": "4",
      "account_address": "0x2b490841c230a31fe012f3b2a3f3d146316be073e599eb7d7e5074838073ef14"
    },
     */
    @JsonProperty("guid")
    private Guid guid;

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getSequenceNumber() {
        return sequenceNumber;
    }

    public void setSequenceNumber(String sequenceNumber) {
        this.sequenceNumber = sequenceNumber;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public TData getData() {
        return data;
    }

    public void setData(TData data) {
        this.data = data;
    }

    public Guid getGuid() {
        return guid;
    }

    public void setGuid(Guid guid) {
        this.guid = guid;
    }

    @Override
    public String toString() {
        return "Event{" +
                "version='" + version + '\'' +
                ", key='" + key + '\'' +
                ", sequenceNumber='" + sequenceNumber + '\'' +
                ", type='" + type + '\'' +
                ", data=" + data +
                ", guid=" + guid +
                '}';
    }

    public static class Guid {
        @JsonProperty("creation_number")
        private String creationNumber;
        @JsonProperty("account_address")
        private String accountAddress;

        public String getCreationNumber() {
            return creationNumber;
        }

        public void setCreationNumber(String creationNumber) {
            this.creationNumber = creationNumber;
        }

        public String getAccountAddress() {
            return accountAddress;
        }

        public void setAccountAddress(String accountAddress) {
            this.accountAddress = accountAddress;
        }

        @Override
        public String toString() {
            return "Guid{" +
                    "creationNumber='" + creationNumber + '\'' +
                    ", accountAddress='" + accountAddress + '\'' +
                    '}';
        }
    }
}
