package com.github.wubuku.aptos.types;


public final class AuthenticationKey {
    public static final int LENGTH = 32;
    public static final AuthenticationKey DUMMY_KEY = new AuthenticationKey(com.novi.serde.Bytes.valueOf(new byte[LENGTH]));

    public final com.novi.serde.Bytes value;

    public AuthenticationKey(com.novi.serde.Bytes value) {
        java.util.Objects.requireNonNull(value, "value must not be null");
        this.value = value;
    }

    public static AuthenticationKey deserialize(com.novi.serde.Deserializer deserializer) throws com.novi.serde.DeserializationError {
        deserializer.increase_container_depth();
        Builder builder = new Builder();
        builder.value = deserializer.deserialize_bytes();
        deserializer.decrease_container_depth();
        return builder.build();
    }

    public static AuthenticationKey bcsDeserialize(byte[] input) throws com.novi.serde.DeserializationError {
        if (input == null) {
            throw new com.novi.serde.DeserializationError("Cannot deserialize null array");
        }
        com.novi.serde.Deserializer deserializer = new com.novi.bcs.BcsDeserializer(input);
        AuthenticationKey value = deserialize(deserializer);
        if (deserializer.get_buffer_offset() < input.length) {
            throw new com.novi.serde.DeserializationError("Some input bytes were not read");
        }
        return value;
    }

//    public AccountAddress derivedAddress() {
//        byte[] addressBytes = Arrays.copyOfRange(this.value.content(), LENGTH - AccountAddress.LENGTH, LENGTH);
//        return AccountAddress.valueOf(addressBytes);
//    }

    public void serialize(com.novi.serde.Serializer serializer) throws com.novi.serde.SerializationError {
        serializer.increase_container_depth();
        serializer.serialize_bytes(value);
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
        AuthenticationKey other = (AuthenticationKey) obj;
        return java.util.Objects.equals(this.value, other.value);
    }

    public int hashCode() {
        int value = 7;
        value = 31 * value + (this.value != null ? this.value.hashCode() : 0);
        return value;
    }

    public static final class Builder {
        public com.novi.serde.Bytes value;

        public AuthenticationKey build() {
            return new AuthenticationKey(
                    value
            );
        }
    }
}
