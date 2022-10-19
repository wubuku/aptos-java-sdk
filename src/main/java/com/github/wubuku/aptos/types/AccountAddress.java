package com.github.wubuku.aptos.types;


import com.github.wubuku.aptos.utils.HexUtils;

public final class AccountAddress {
    public static final int LENGTH = 32;
    public final java.util.@com.novi.serde.ArrayLen(length = 32) List<@com.novi.serde.Unsigned Byte> value;

    public AccountAddress(java.util.@com.novi.serde.ArrayLen(length = 32) List<@com.novi.serde.Unsigned Byte> value) {
        java.util.Objects.requireNonNull(value, "value must not be null");
        this.value = value;
    }

    public static AccountAddress deserialize(com.novi.serde.Deserializer deserializer) throws com.novi.serde.DeserializationError {
        deserializer.increase_container_depth();
        Builder builder = new Builder();
        builder.value = TraitHelpers.deserialize_array32_u8_array(deserializer);
        deserializer.decrease_container_depth();
        return builder.build();
    }

    public static AccountAddress bcsDeserialize(byte[] input) throws com.novi.serde.DeserializationError {
        if (input == null) {
            throw new com.novi.serde.DeserializationError("Cannot deserialize null array");
        }
        com.novi.serde.Deserializer deserializer = new com.novi.bcs.BcsDeserializer(input);
        AccountAddress value = deserialize(deserializer);
        if (deserializer.get_buffer_offset() < input.length) {
            throw new com.novi.serde.DeserializationError("Some input bytes were not read");
        }
        return value;
    }

    public static AccountAddress valueOf(byte[] values) {
        if (values.length != LENGTH) {
            throw new IllegalArgumentException("Invalid length for AccountAddress");
        }
        java.util.List<Byte> address = new java.util.ArrayList<>(LENGTH);
        for (int i = 0; i < LENGTH; i++) {
            address.add(values[i]);
        }
        return new AccountAddress(address);
    }

    public void serialize(com.novi.serde.Serializer serializer) throws com.novi.serde.SerializationError {
        serializer.increase_container_depth();
        TraitHelpers.serialize_array32_u8_array(value, serializer);
        serializer.decrease_container_depth();
    }

    public byte[] bcsSerialize() throws com.novi.serde.SerializationError {
        com.novi.serde.Serializer serializer = new com.novi.bcs.BcsSerializer();
        serialize(serializer);
        return serializer.get_bytes();
    }

    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        AccountAddress other = (AccountAddress) obj;
        return java.util.Objects.equals(this.value, other.value);
    }

    public int hashCode() {
        int value = 7;
        value = 31 * value + this.value.hashCode();
        return value;
    }

    public byte[] toBytes() {
        byte[] bytes = new byte[LENGTH];
        int i = 0;
        for (Byte item : value) {
            bytes[i++] = item;
        }
        return bytes;
    }

    @Override
    public String toString() {
        return HexUtils.byteListToHexWithPrefix(value);
    }

    public static final class Builder {
        public java.util.@com.novi.serde.ArrayLen(length = 32) List<@com.novi.serde.Unsigned Byte> value;

        public AccountAddress build() {
            return new AccountAddress(
                    value
            );
        }
    }
}
