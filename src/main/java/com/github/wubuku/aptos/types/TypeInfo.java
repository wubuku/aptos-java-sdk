package com.github.wubuku.aptos.types;


public final class TypeInfo {
    public final AccountAddress accountAddress;
    public final String moduleName;
    public final String structName;

    public TypeInfo(AccountAddress accountAddress, String moduleName, String structName) {
        java.util.Objects.requireNonNull(accountAddress, "address must not be null");
        java.util.Objects.requireNonNull(moduleName, "module must not be null");
        java.util.Objects.requireNonNull(structName, "name must not be null");
        this.accountAddress = accountAddress;
        this.moduleName = moduleName;
        this.structName = structName;
    }

    public static TypeInfo deserialize(com.novi.serde.Deserializer deserializer) throws com.novi.serde.DeserializationError {
        deserializer.increase_container_depth();
        Builder builder = new Builder();
        builder.accountAddress = AccountAddress.deserialize(deserializer);
        builder.moduleName = deserializer.deserialize_str();
        builder.structName = deserializer.deserialize_str();
        deserializer.decrease_container_depth();
        return builder.build();
    }

    public static TypeInfo bcsDeserialize(byte[] input) throws com.novi.serde.DeserializationError {
        if (input == null) {
            throw new com.novi.serde.DeserializationError("Cannot deserialize null array");
        }
        com.novi.serde.Deserializer deserializer = new com.novi.bcs.BcsDeserializer(input);
        TypeInfo value = deserialize(deserializer);
        if (deserializer.get_buffer_offset() < input.length) {
            throw new com.novi.serde.DeserializationError("Some input bytes were not read");
        }
        return value;
    }

    public void serialize(com.novi.serde.Serializer serializer) throws com.novi.serde.SerializationError {
        serializer.increase_container_depth();
        accountAddress.serialize(serializer);
        serializer.serialize_str(moduleName);
        serializer.serialize_str(structName);
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
        TypeInfo other = (TypeInfo) obj;
        if (!java.util.Objects.equals(this.accountAddress, other.accountAddress)) {
            return false;
        }
        if (!java.util.Objects.equals(this.moduleName, other.moduleName)) {
            return false;
        }
        if (!java.util.Objects.equals(this.structName, other.structName)) {
            return false;
        }
        return true;
    }

    public int hashCode() {
        int value = 7;
        value = 31 * value + (this.accountAddress != null ? this.accountAddress.hashCode() : 0);
        value = 31 * value + (this.moduleName != null ? this.moduleName.hashCode() : 0);
        value = 31 * value + (this.structName != null ? this.structName.hashCode() : 0);
        return value;
    }

    public static final class Builder {
        public AccountAddress accountAddress;
        public String moduleName;
        public String structName;

        public TypeInfo build() {
            return new TypeInfo(
                    accountAddress,
                    moduleName,
                    structName
            );
        }
    }

}
