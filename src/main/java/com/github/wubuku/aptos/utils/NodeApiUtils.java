package com.github.wubuku.aptos.utils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.wubuku.aptos.bean.*;
import com.github.wubuku.aptos.types.RawTransaction;
import com.github.wubuku.aptos.types.SignedUserTransaction;
import com.github.wubuku.aptos.types.TypeTag;
import com.novi.serde.Bytes;
import com.novi.serde.SerializationError;

import java.io.IOException;
import java.math.BigInteger;
import java.util.List;
import java.util.Map;

public class NodeApiUtils {


    private NodeApiUtils() {
    }

    private static ObjectMapper getObjectMapper() {
        return new ObjectMapper();
    }

    private static NodeApiClient getNodeApiClient(String baseUrl) {
        return new NodeApiClient(baseUrl);
    }

    public static RawTransaction newRawTransaction(String baseUrl, String senderAddress,
                                                   String moduleAddress, String moduleName, String functionName,
                                                   Long maxGasAmount,
                                                   List<TypeTag> txnTypeArgs, List<Bytes> trxArgs
    ) throws IOException {
        return getNodeApiClient(baseUrl).newRawTransaction(senderAddress,
                moduleAddress, moduleName, functionName,
                maxGasAmount,
                txnTypeArgs, trxArgs);
    }

    public static Integer getGasUnitPrice(String baseUrl) throws IOException {
        return getNodeApiClient(baseUrl).getGasUnitPrice();
    }

    public static SubmitTransactionRequest toSubmitTransactionRequest(EncodeSubmissionRequest encodeSubmissionRequest) {
        ObjectMapper objectMapper = getObjectMapper();
        Map<String, Object> map = objectMapper.convertValue(encodeSubmissionRequest, new TypeReference<Map<String, Object>>() {
        });
        return objectMapper.convertValue(map, SubmitTransactionRequest.class);
    }

    public static LedgerInfo getLedgerInfo(String baseUrl) throws IOException {
        return getNodeApiClient(baseUrl).getLedgerInfo();
    }

    public static HealthCheckSuccess checkBasicNodeHealth(String baseUrl, Integer durationSecs) throws IOException {
        return getNodeApiClient(baseUrl).checkBasicNodeHealth(durationSecs);
    }

    /**
     * Get transaction by hash.
     *
     * @return If the transaction is not found, return null.
     * @throws IOException
     */
    public static Transaction getTransactionByHash(String baseUrl, String hash) throws IOException {
        return getNodeApiClient(baseUrl).getTransactionByHash(hash);
    }

    public static boolean isTransactionPending(String baseUrl, String hash) throws IOException {
        return getNodeApiClient(baseUrl).isTransactionPending(hash);
    }

    public static void waitForTransaction(String baseUrl, String hash) throws IOException {
        getNodeApiClient(baseUrl).waitForTransaction(hash);
    }

    public static Transaction getTransactionByVersion(String baseUrl, String version) throws IOException {
        return getNodeApiClient(baseUrl).getTransactionByVersion(version);
    }

    public static List<Transaction> getAccountTransactions(String baseUrl, String address, Long start, Integer limit) throws IOException {
        return getNodeApiClient(baseUrl).getAccountTransactions(address, start, limit);
    }


    public static Block getBlockByHeight(String baseUrl, String height, Boolean withTransactions) throws IOException {
        return getNodeApiClient(baseUrl).getBlockByHeight(height, withTransactions);
    }

    public static Block getBlockByVersion(String baseUrl, String version, Boolean withTransactions) throws IOException {
        return getNodeApiClient(baseUrl).getBlockByVersion(version, withTransactions);
    }

    public static List<Event> getEventsByEventHandle(String baseUrl, String accountAddress,
                                                     String eventHandleStruct, String eventHandleFieldName,
                                                     Long start, Integer limit) throws IOException {
        return getNodeApiClient(baseUrl).getEventsByEventHandle(accountAddress,
                eventHandleStruct, eventHandleFieldName, start, limit);
    }

    public static <TData> List<Event<TData>> getEventsByEventHandle(String baseUrl, String accountAddress,
                                                                    String eventHandleStruct, String eventHandleFieldName,
                                                                    Class<TData> eventDataType, Long start, Integer limit) throws IOException {
        return getNodeApiClient(baseUrl).getEventsByEventHandle(accountAddress,
                eventHandleStruct, eventHandleFieldName, eventDataType, start, limit);
    }

    /**
     * Get events by creation number.
     */
    public static <TData> List<Event<TData>> getEventsByCreationNumber(String baseUrl, String accountAddress,
                                                                       String creationNumber,
                                                                       Class<TData> eventDataType, Long start, Integer limit) throws IOException {
        return getNodeApiClient(baseUrl).getEventsByCreationNumber(accountAddress,
                creationNumber, eventDataType, start, limit);
    }

    /**
     * Get events by event key.
     */
    public static <TData> List<Event<TData>> getEventsByEventKey(String baseUrl, String eventKey,
                                                                 Class<TData> eventDataType,
                                                                 Long start, Integer limit) throws IOException {
        return getNodeApiClient(baseUrl).getEventsByEventKey(eventKey, eventDataType, start, limit);
    }


    /**
     * Retrieves high level information about an account such as its sequence number and authentication key.
     */
    public static Account getAccount(String baseUrl, String accountAddress) {
        return getNodeApiClient(baseUrl).getAccount(accountAddress);
    }

    public static BigInteger getAccountBalance(String baseUrl, String accountAddress) throws IOException {
        return getNodeApiClient(baseUrl).getAccountBalance(accountAddress);
    }

    public static <TData> AccountResource<TData> getAccountResource(String baseUrl, String accountAddress, String resourceType,
                                                                    Class<TData> dataType, String ledgerVersion) throws IOException {
        return getNodeApiClient(baseUrl).getAccountResource(accountAddress, resourceType, dataType, ledgerVersion);
    }

    public static MoveModuleBytecode getAccountModule(String baseUrl, String accountAddress, String moduleName, String ledgerVersion) throws IOException {
        return getNodeApiClient(baseUrl).getAccountModule(accountAddress, moduleName, ledgerVersion);
    }

    public static List<AccountResource> getAccountResources(String baseUrl, String accountAddress,
                                                            String ledgerVersion) throws IOException {

        return getNodeApiClient(baseUrl).getAccountResources(accountAddress, ledgerVersion);
    }

    public static List<MoveModuleBytecode> getAccountModules(String baseUrl, String accountAddress, String ledgerVersion) throws IOException {
        return getNodeApiClient(baseUrl).getAccountModules(accountAddress, ledgerVersion);
    }

    public static <T> T getTableItem(String baseUrl, String tableHandle, String keyType, String valueType, Object key,
                                     Class<T> valueJavaType, String ledgerVersion) throws IOException {
        return getNodeApiClient(baseUrl).getTableItem(tableHandle, keyType, valueType, key, valueJavaType, ledgerVersion);
    }

    public static GasEstimation estimateGasPrice(String baseUrl) throws IOException {
        return getNodeApiClient(baseUrl).estimateGasPrice();
    }

    public static Transaction submitBcsTransaction(String baseUrl, SignedUserTransaction signedTransaction) throws IOException, SerializationError {
        return getNodeApiClient(baseUrl).submitBcsTransaction(signedTransaction);
    }

    public static TransactionsBatchSubmissionResult submitBatchBcsTransactions(String baseUrl, List<SignedUserTransaction> signedTransactionList) throws IOException, SerializationError {
        return getNodeApiClient(baseUrl).submitBatchBcsTransactions(signedTransactionList);
    }

    public static List<Transaction> simulateBcsTransaction(String baseUrl, SignedUserTransaction signedTransaction,
                                                           Boolean estimateGasUnitPrice, Boolean estimateMaxGasAmount) throws IOException, SerializationError {
        return getNodeApiClient(baseUrl).simulateBcsTransaction(signedTransaction, estimateGasUnitPrice, estimateMaxGasAmount);
    }

    public static Transaction submitTransaction(String baseUrl, SubmitTransactionRequest submitTransactionRequest) throws IOException {
        return getNodeApiClient(baseUrl).submitTransaction(submitTransactionRequest);
    }


    /**
     * Submit batch transactions.
     */
    public static TransactionsBatchSubmissionResult submitBatchTransactions(String baseUrl, List<SubmitTransactionRequest> submitTransactionRequestList) throws IOException {
        return getNodeApiClient(baseUrl).submitBatchTransactions(submitTransactionRequestList);
    }

    public static List<Transaction> simulateTransaction(String baseUrl, SubmitTransactionRequest submitTransactionRequest,
                                                        Boolean estimateGasUnitPrice, Boolean estimateMaxGasAmount) throws IOException {

        return getNodeApiClient(baseUrl).simulateTransaction(submitTransactionRequest, estimateGasUnitPrice, estimateMaxGasAmount);
    }


    public static String encodeSubmission(String baseUrl, EncodeSubmissionRequest r) throws IOException {
        return getNodeApiClient(baseUrl).encodeSubmission(r);
    }

    public static EncodeSubmissionRequest newEncodeSubmissionRequest(String baseUrl, String sender,
                                                                     Long expirationTimestampSecs,
                                                                     TransactionPayload payload,
                                                                     Long maxGasAmount,
                                                                     String sequenceNumber,
                                                                     String gasUnitPrice) throws IOException {
        return getNodeApiClient(baseUrl).newEncodeSubmissionRequest(sender,
                expirationTimestampSecs, payload, maxGasAmount, sequenceNumber, gasUnitPrice);
    }


    public static String getAccountSequenceNumber(String baseUrl, String sender) {
        return getAccount(baseUrl, sender).getSequenceNumber();
    }

}
