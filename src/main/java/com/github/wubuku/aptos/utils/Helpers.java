package com.github.wubuku.aptos.utils;

import com.github.wubuku.aptos.types.AccountAddress;
import com.novi.bcs.BcsSerializer;
import com.novi.serde.Bytes;
import com.novi.serde.SerializationError;

import java.math.BigInteger;

public final class Helpers {
    private Helpers() {
    }


    public static Bytes encodeU8VectorArgument(Bytes arg) {
        try {
            BcsSerializer s = new BcsSerializer();
            s.serialize_bytes(arg);
            return Bytes.valueOf(s.get_bytes());
        } catch (SerializationError e) {
            throw new IllegalArgumentException("Unable to serialize argument of type u8vector");
        }
    }

    public static Bytes encodeU64Argument(Long u) {
        try {
            BcsSerializer s = new BcsSerializer();
            s.serialize_u64(u);
            return Bytes.valueOf(s.get_bytes());
        } catch (SerializationError e) {
            throw new IllegalArgumentException("Unable to serialize argument of type u64");
        }
    }

    public static Bytes encodeBoolArgument(Boolean arg) {
        try {
            BcsSerializer s = new BcsSerializer();
            s.serialize_bool(arg);
            return Bytes.valueOf(s.get_bytes());
        } catch (SerializationError e) {
            throw new IllegalArgumentException("Unable to serialize argument of type bool");
        }
    }

    public static Bytes encodeU128Argument(BigInteger u) {
        try {
            BcsSerializer s = new BcsSerializer();
            s.serialize_u128(u);
            return Bytes.valueOf(s.get_bytes());
        } catch (SerializationError e) {
            throw new IllegalArgumentException("Unable to serialize argument of type u128");
        }
    }

    public static Bytes encodeAccountAddressArgument(AccountAddress address) {
        try {
            BcsSerializer s = new BcsSerializer();
            address.serialize(s);
            return Bytes.valueOf(s.get_bytes());
        } catch (SerializationError e) {
            throw new IllegalArgumentException("Unable to serialize argument of type u8vector");
        }
    }

}
