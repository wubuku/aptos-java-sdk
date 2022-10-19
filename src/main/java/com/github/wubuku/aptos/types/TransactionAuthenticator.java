package com.github.wubuku.aptos.types;


import com.novi.serde.DeserializationError;
import com.novi.serde.SerializationError;

public abstract class TransactionAuthenticator {

    public static final byte SCHEME_ID_ED25519 = 0;
    public static final byte SCHEME_ID_MULTI_ED25519 = 1;

    public static byte[] preimage(TransactionAuthenticator transactionAuthenticator) {
        if (transactionAuthenticator instanceof Ed25519) {
            byte[] rawBytes = ((Ed25519) transactionAuthenticator).public_key.value.content();
            return com.google.common.primitives.Bytes.concat(rawBytes, new byte[]{SCHEME_ID_ED25519});
        } else if (transactionAuthenticator instanceof MultiEd25519) {
            byte[] rawBytes = ((MultiEd25519) transactionAuthenticator).public_key.value.content();
            return com.google.common.primitives.Bytes.concat(rawBytes, new byte[]{SCHEME_ID_MULTI_ED25519});
        } else {
            throw new IllegalArgumentException("Unknown authenticator type: " + transactionAuthenticator);
        }
    }

    public static AuthenticationKey authenticationKey(TransactionAuthenticator transactionAuthenticator) throws SerializationError, DeserializationError {
        byte[] preimage = preimage(transactionAuthenticator);
        return new AuthenticationKey(HashValue.sha3Of(preimage).value);
    }

    public static TransactionAuthenticator deserialize(com.novi.serde.Deserializer deserializer) throws DeserializationError {
        int index = deserializer.deserialize_variant_index();
        switch (index) {
            case 0:
                return Ed25519.load(deserializer);
            case 1:
                return MultiEd25519.load(deserializer);
            default:
                throw new DeserializationError("Unknown variant index for TransactionAuthenticator: " + index);
        }
    }

    public static TransactionAuthenticator bcsDeserialize(byte[] input) throws DeserializationError {
        if (input == null) {
            throw new DeserializationError("Cannot deserialize null array");
        }
        com.novi.serde.Deserializer deserializer = new com.novi.bcs.BcsDeserializer(input);
        TransactionAuthenticator value = deserialize(deserializer);
        if (deserializer.get_buffer_offset() < input.length) {
            throw new DeserializationError("Some input bytes were not read");
        }
        return value;
    }

    abstract public void serialize(com.novi.serde.Serializer serializer) throws SerializationError;

    public byte[] bcsSerialize() throws SerializationError {
        com.novi.serde.Serializer serializer = new com.novi.bcs.BcsSerializer();
        serialize(serializer);
        return serializer.get_bytes();
    }

    public static final class Ed25519 extends TransactionAuthenticator {
        public final Ed25519PublicKey public_key;
        public final Ed25519Signature signature;

        public Ed25519(Ed25519PublicKey public_key, Ed25519Signature signature) {
            java.util.Objects.requireNonNull(public_key, "public_key must not be null");
            java.util.Objects.requireNonNull(signature, "signature must not be null");
            this.public_key = public_key;
            this.signature = signature;
        }

        static Ed25519 load(com.novi.serde.Deserializer deserializer) throws DeserializationError {
            deserializer.increase_container_depth();
            Builder builder = new Builder();
            builder.public_key = Ed25519PublicKey.deserialize(deserializer);
            builder.signature = Ed25519Signature.deserialize(deserializer);
            deserializer.decrease_container_depth();
            return builder.build();
        }

        public void serialize(com.novi.serde.Serializer serializer) throws SerializationError {
            serializer.increase_container_depth();
            serializer.serialize_variant_index(0);
            public_key.serialize(serializer);
            signature.serialize(serializer);
            serializer.decrease_container_depth();
        }

        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null) return false;
            if (getClass() != obj.getClass()) return false;
            Ed25519 other = (Ed25519) obj;
            if (!java.util.Objects.equals(this.public_key, other.public_key)) {
                return false;
            }
            return java.util.Objects.equals(this.signature, other.signature);
        }

        public int hashCode() {
            int value = 7;
            value = 31 * value + (this.public_key != null ? this.public_key.hashCode() : 0);
            value = 31 * value + (this.signature != null ? this.signature.hashCode() : 0);
            return value;
        }

        public static final class Builder {
            public Ed25519PublicKey public_key;
            public Ed25519Signature signature;

            public Ed25519 build() {
                return new Ed25519(
                        public_key,
                        signature
                );
            }
        }
    }

    public static final class MultiEd25519 extends TransactionAuthenticator {
        public final MultiEd25519PublicKey public_key;
        public final MultiEd25519Signature signature;

        public MultiEd25519(MultiEd25519PublicKey public_key, MultiEd25519Signature signature) {
            java.util.Objects.requireNonNull(public_key, "public_key must not be null");
            java.util.Objects.requireNonNull(signature, "signature must not be null");
            this.public_key = public_key;
            this.signature = signature;
        }

        static MultiEd25519 load(com.novi.serde.Deserializer deserializer) throws DeserializationError {
            deserializer.increase_container_depth();
            Builder builder = new Builder();
            builder.public_key = MultiEd25519PublicKey.deserialize(deserializer);
            builder.signature = MultiEd25519Signature.deserialize(deserializer);
            deserializer.decrease_container_depth();
            return builder.build();
        }

        public void serialize(com.novi.serde.Serializer serializer) throws SerializationError {
            serializer.increase_container_depth();
            serializer.serialize_variant_index(1);
            public_key.serialize(serializer);
            signature.serialize(serializer);
            serializer.decrease_container_depth();
        }

        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null) return false;
            if (getClass() != obj.getClass()) return false;
            MultiEd25519 other = (MultiEd25519) obj;
            if (!java.util.Objects.equals(this.public_key, other.public_key)) {
                return false;
            }
            return java.util.Objects.equals(this.signature, other.signature);
        }

        public int hashCode() {
            int value = 7;
            value = 31 * value + (this.public_key != null ? this.public_key.hashCode() : 0);
            value = 31 * value + (this.signature != null ? this.signature.hashCode() : 0);
            return value;
        }

        public static final class Builder {
            public MultiEd25519PublicKey public_key;
            public MultiEd25519Signature signature;

            public MultiEd25519 build() {
                return new MultiEd25519(
                        public_key,
                        signature
                );
            }
        }
    }
}

