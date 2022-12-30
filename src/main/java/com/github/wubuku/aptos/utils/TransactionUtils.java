package com.github.wubuku.aptos.utils;

import com.github.wubuku.aptos.types.*;
import com.novi.serde.Bytes;
import com.novi.serde.SerializationError;

import java.util.List;

public class TransactionUtils {
    public static final byte[] ZERO_PADDED_SIGNATURE = new byte[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,};

    private TransactionUtils() {
    }

    public static SignedUserTransaction newSignedUserTransactionToSimulate(RawTransaction rawTransaction, byte[] publicKey) {
        return newSignedUserTransaction(rawTransaction, publicKey, ZERO_PADDED_SIGNATURE);
    }

    public static SignedUserTransaction newSignedUserTransaction(RawTransaction rawTransaction,
                                                                 byte[] publicKey,
                                                                 byte[] signature) {
        SignedUserTransaction signedTransactionToSimulate = new SignedUserTransaction(rawTransaction,
                new TransactionAuthenticator.Ed25519(
                        new Ed25519PublicKey(Bytes.valueOf(publicKey)),
                        new Ed25519Signature(Bytes.valueOf(signature))
                ));
        return signedTransactionToSimulate;
    }

    public static RawTransaction newRawTransaction(
            ChainId chainId,
            String sender,
            String sequenceNumber,
            String maxGasAmount,
            String gasUnitPrice,
            String expirationTimestampSecs,
            String moduleAddress,
            String moduleName,
            String functionName,
            List<TypeTag> typeArgs,
            List<Bytes> args) {
        ModuleId moduleId = new ModuleId(AccountAddress.valueOf(HexUtils.hexToByteArray(moduleAddress)),
                new Identifier(moduleName));
        Identifier functionId = new Identifier(functionName);
        return newRawTransaction(chainId, sender, sequenceNumber, maxGasAmount, gasUnitPrice,
                expirationTimestampSecs,
                moduleId,
                functionId,
                typeArgs, args);
    }

    public static RawTransaction newRawTransaction(
            ChainId chainId,
            String sender,
            String sequenceNumber,
            String maxGasAmount,
            String gasUnitPrice,
            String expirationTimestampSecs,
            ModuleId moduleId,
            Identifier functionId,
            List<TypeTag> typeArgs,
            List<Bytes> args) {
        EntryFunction entryFunction = new EntryFunction(moduleId, functionId, typeArgs, args);
        com.github.wubuku.aptos.types.TransactionPayload.EntryFunction typesTransactionPayload = new com.github.wubuku.aptos.types.TransactionPayload.EntryFunction(entryFunction);
        RawTransaction rawTransaction = new RawTransaction(
                AccountAddress.valueOf(HexUtils.hexToByteArray(sender)),
                Long.parseLong(sequenceNumber),
                typesTransactionPayload,
                Long.parseLong(maxGasAmount),
                Long.parseLong(gasUnitPrice),
                Long.parseLong(expirationTimestampSecs),
                chainId
        );
        return rawTransaction;
    }

    /**
     * Get bytes to sign from RawTransaction.
     */
    public static byte[] rawTransactionBcsBytesToSign(RawTransaction rawTransaction) throws SerializationError {
        return rawTransactionBcsBytesToSign(rawTransaction.bcsSerialize());
    }

    /**
     * Get bytes to sign from BCS bytes of RawTransaction.
     *
     * @param rawTransaction BCS bytes of {@link RawTransaction RawTransaction}
     * @return Bytes to be signed
     */
    private static byte[] rawTransactionBcsBytesToSign(byte[] rawTransaction) {
        return com.google.common.primitives.Bytes
                .concat(HashUtils.hashWithAptosPrefix("RawTransaction"), rawTransaction);
    }

    /**
     * Compute transaction hash locally.
     *
     * @param signedTransaction Instance of {@link SignedUserTransaction SignedUserTransaction}
     * @return Transaction hash
     * @throws SerializationError if BCS serialization error
     */
    public static byte[] getTransactionHash(SignedUserTransaction signedTransaction) throws SerializationError {
        com.github.wubuku.aptos.types.Transaction t = new com.github.wubuku.aptos.types.Transaction.UserTransaction(signedTransaction);
        return HashUtils.sha3Hash(com.google.common.primitives.Bytes.concat(
                HashUtils.hashWithAptosPrefix("Transaction"), t.bcsSerialize()));
    }

}
