package com.github.wubuku.aptos.bean;

import java.math.BigInteger;

public class AptosEventHandle {
    /// Total number of events emitted to this event stream.
    private BigInteger counter;//: u64,

    /// A globally unique ID for this event stream.
    private AptosGUID guid;

    public BigInteger getCounter() {
        return counter;
    }

    public void setCounter(BigInteger counter) {
        this.counter = counter;
    }

    public AptosGUID getGuid() {
        return guid;
    }

    public void setGuid(AptosGUID guid) {
        this.guid = guid;
    }

    @Override
    public String toString() {
        return "AptosEventHandle{" +
                "counter=" + counter +
                ", guid=" + guid +
                '}';
    }
}
