package com.github.wubuku.aptos.bean;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigInteger;

public class AptosGUID {

    private ID id;

    public ID getId() {
        return id;
    }

    public void setId(ID id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "AptosGUID{" +
                "id=" + id +
                '}';
    }


    public static class ID {

        /// If creation_num is `i`, this is the `i+1`th GUID created by `addr`
        @JsonProperty("creation_num")
        private BigInteger creationNum;//: u64,

        /// Address that created the GUID
        private String addr;//: address

        public BigInteger getCreationNum() {
            return creationNum;
        }

        public void setCreationNum(BigInteger creationNum) {
            this.creationNum = creationNum;
        }

        public String getAddr() {
            return addr;
        }

        public void setAddr(String addr) {
            this.addr = addr;
        }

        @Override
        public String toString() {
            return "ID{" +
                    "creationNum=" + creationNum +
                    ", addr='" + addr + '\'' +
                    '}';
        }
    }
}
