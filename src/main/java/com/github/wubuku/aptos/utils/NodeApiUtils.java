package com.github.wubuku.aptos.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.wubuku.aptos.bean.*;
import com.github.wubuku.aptos.types.ChainId;
import com.github.wubuku.aptos.types.RawTransaction;
import com.github.wubuku.aptos.types.SignedUserTransaction;
import com.github.wubuku.aptos.types.TypeTag;
import com.novi.serde.Bytes;
import com.novi.serde.SerializationError;
import okhttp3.*;
import okio.ByteString;

import java.io.IOException;
import java.math.BigInteger;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class NodeApiUtils {
    public static final long DEFAULT_MAX_GAS_AMOUNT = 400000L;

    private NodeApiUtils() {
    }

    private static OkHttpClient getOkHttpClient() {
        return new OkHttpClient();
    }

    private static ObjectMapper getObjectMapper() {
        return new ObjectMapper();
    }

    public static RawTransaction newRawTransaction(String baseUrl, String senderAddress,
                                                   String moduleAddress, String moduleName, String functionName,
                                                   Long maxGasAmount,
                                                   List<TypeTag> txnTypeArgs, List<Bytes> trxArgs
    ) throws IOException {
        String expirationTimestampSecs = (System.currentTimeMillis() / 1000L + 600) + "";
        String sequenceNumber = getAccountSequenceNumber(baseUrl,
                senderAddress);
        String gasUnitPrice = getGasUnitPrice(baseUrl).toString();
        LedgerInfo ledgerInfo = getLedgerInfo(baseUrl);
        ChainId chainId = new ChainId((byte) ledgerInfo.getChainId().intValue());
        RawTransaction rawTransaction = TransactionUtils.newRawTransaction(
                chainId,
                senderAddress,
                sequenceNumber,
                maxGasAmount != null ? maxGasAmount.toString() : DEFAULT_MAX_GAS_AMOUNT + "",
                gasUnitPrice,
                expirationTimestampSecs,
                moduleAddress,
                moduleName,
                functionName,
                txnTypeArgs, trxArgs);
        return rawTransaction;
    }

    public static Integer getGasUnitPrice(String baseUrl) throws IOException {
        return estimateGasPrice(baseUrl).getGasEstimate();
    }

    public static SubmitTransactionRequest toSubmitTransactionRequest(EncodeSubmissionRequest encodeSubmissionRequest) {
        ObjectMapper objectMapper = getObjectMapper();
        Map<String, Object> map = objectMapper.convertValue(encodeSubmissionRequest, new TypeReference<Map<String, Object>>() {
        });
        return objectMapper.convertValue(map, SubmitTransactionRequest.class);
    }

    public static LedgerInfo getLedgerInfo(String baseUrl) throws IOException {
        HttpUrl url = HttpUrl.parse(baseUrl);
        Request request = new Request.Builder().url(url).build();
        OkHttpClient client = getOkHttpClient();
        try (Response response = client.newCall(request).execute()) {
            if (response.code() >= 400) {
                throwNodeApiException(response);
            }
            return readResponseBody(response, LedgerInfo.class);
        }
    }

    public static HealthCheckSuccess checkBasicNodeHealth(String baseUrl, Integer durationSecs) throws IOException {
        Request request = newCheckBasicNodeHealthRequest(baseUrl, durationSecs);
        OkHttpClient client = getOkHttpClient();
        try (Response response = client.newCall(request).execute()) {
            if (response.code() >= 400) {
                throwNodeApiException(response);
            }
            return readResponseBody(response, HealthCheckSuccess.class);
        }
    }

    /**
     * Get transaction by hash.
     *
     * @return If the transaction is not found, return null.
     * @throws IOException
     */
    public static Transaction getTransactionByHash(String baseUrl, String hash) throws IOException {
        Request request = newGetTransactionByHashRequest(baseUrl, hash);
        OkHttpClient client = getOkHttpClient();
        try (Response response = client.newCall(request).execute()) {
            if (response.code() == 404) {
                return null;
            }
            if (response.code() >= 400) {
                throw new IOException("Failed to get transaction by hash: " + response.body().string());
            }
            return readResponseBody(response, Transaction.class);
        }
    }

    public static boolean isTransactionPending(String baseUrl, String hash) throws IOException {
        Transaction transaction = getTransactionByHash(baseUrl, hash);
        return transaction == null
                || Transaction.TYPE_PENDING_TRANSACTION.equals(transaction.getType());
    }

    public static void waitForTransaction(String baseUrl, String hash) throws IOException {
        while (isTransactionPending(baseUrl, hash)) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        Transaction transaction = getTransactionByHash(baseUrl, hash);
        if (!(transaction.getSuccess() != null && transaction.getSuccess())) {
            throw new IOException("Transaction is not success.");
        }
    }

    public static Transaction getTransactionByVersion(String baseUrl, String version) throws IOException {
        Request request = newGetTransactionByVersionRequest(baseUrl, version);
        OkHttpClient client = getOkHttpClient();
        try (Response response = client.newCall(request).execute()) {
            if (response.code() >= 400) {
                throwNodeApiException(response);
            }
            return readResponseBody(response, Transaction.class);
        }
    }

    public static List<Transaction> getAccountTransactions(String baseUrl, String address, Long start, Integer limit) throws IOException {
        Request request = newGetAccountTransactionsRequest(baseUrl, address, start, limit);
        OkHttpClient client = getOkHttpClient();
        try (Response response = client.newCall(request).execute()) {
            if (response.code() >= 400) {
                throwNodeApiException(response);
            }
            return readResponseBodyAsList(response, Transaction.class);
        }
    }


    public static Block getBlockByHeight(String baseUrl, String height, Boolean withTransactions) throws IOException {
        Request request = newGetBlockByHeightRequest(baseUrl, height, withTransactions);
        OkHttpClient client = getOkHttpClient();
        try (Response response = client.newCall(request).execute()) {
            if (response.code() >= 400) {
                throwNodeApiException(response);
            }
            return readResponseBody(response, Block.class);
        }
    }

    public static Block getBlockByVersion(String baseUrl, String version, Boolean withTransactions) throws IOException {
        Request request = newGetBlockByVersionRequest(baseUrl, version, withTransactions);
        OkHttpClient client = getOkHttpClient();
        try (Response response = client.newCall(request).execute()) {
            if (response.code() >= 400) {
                throwNodeApiException(response);
            }
            return readResponseBody(response, Block.class);
        }
    }

    public static List<Event> getEventsByEventHandle(String baseUrl, String accountAddress,
                                                     String eventHandleStruct, String eventHandleFieldName,
                                                     Long start, Integer limit) throws IOException {
        Request request = newGetEventsRequest(baseUrl, accountAddress, eventHandleStruct, eventHandleFieldName, start, limit);
        OkHttpClient client = getOkHttpClient();
        try (Response response = client.newCall(request).execute()) {
            if (response.code() >= 400) {
                throwNodeApiException(response);
            }
            return readResponseBodyAsList(response, Event.class);
        }
    }

    public static <TData> List<Event<TData>> getEventsByEventHandle(String baseUrl, String accountAddress,
                                                                    String eventHandleStruct, String eventHandleFieldName,
                                                                    Class<TData> eventDataType, Long start, Integer limit) throws IOException {
        Request request = newGetEventsRequest(baseUrl, accountAddress, eventHandleStruct, eventHandleFieldName, start, limit);
        OkHttpClient client = getOkHttpClient();
        try (Response response = client.newCall(request).execute()) {
            if (response.code() >= 400) {
                throwNodeApiException(response);
            }
            return readEventList(response.body().string(), eventDataType);
        }
    }

    /**
     * Get events by creation number.
     */
    public static <TData> List<Event<TData>> getEventsByCreationNumber(String baseUrl, String accountAddress,
                                                                       String creationNumber,
                                                                       Class<TData> eventDataType, Long start, Integer limit) throws IOException {
        Request request = newGetEventsByCreationNumberRequest(baseUrl, accountAddress, creationNumber, start, limit);
        OkHttpClient client = getOkHttpClient();
        try (Response response = client.newCall(request).execute()) {
            if (response.code() >= 400) {
                throwNodeApiException(response);
            }
            return readEventList(response.body().string(), eventDataType);
        }

    }

    /**
     * Get events by event key.
     */
    public static <TData> List<Event<TData>> getEventsByEventKey(String baseUrl, String eventKey,
                                                                 Class<TData> eventDataType,
                                                                 Long start, Integer limit) throws IOException {
        Request request = newGetEventsRequest(baseUrl, eventKey, start, limit);
        OkHttpClient client = getOkHttpClient();
        try (Response response = client.newCall(request).execute()) {
            if (response.code() >= 400) {
                throwNodeApiException(response);
            }
            return readEventList(response.body().string(), eventDataType);
        }
    }

    private static <TData> List<Event<TData>> readEventList(String json, Class<TData> eventDataType) throws JsonProcessingException {
        ObjectMapper objectMapper = getObjectMapper();
        List<Map<String, Object>> mapList = objectMapper.readValue(json,
                objectMapper.getTypeFactory().constructCollectionType(List.class, Map.class));
        return mapList.stream().map(map -> (Event<TData>) objectMapper.convertValue(map,
                        objectMapper.getTypeFactory().constructParametricType(Event.class, eventDataType)))
                .collect(Collectors.toList());
    }

    /**
     * Retrieves high level information about an account such as its sequence number and authentication key.
     */
    public static Account getAccount(String baseUrl, String accountAddress) {
        Request request = newGetAccountRequest(baseUrl, accountAddress);
        OkHttpClient client = getOkHttpClient();
        try (Response response = client.newCall(request).execute()) {
            if (response.code() >= 400) {
                throwNodeApiException(response);
            }
            return readResponseBody(response, Account.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static BigInteger getAccountBalance(String baseUrl, String accountAddress) throws IOException {
        String resourceType = "0x1::coin::CoinStore<0x1::aptos_coin::AptosCoin>";
        AccountResource<Map> accountResource = getAccountResource(baseUrl, accountAddress, resourceType,
                Map.class, null);
        return new BigInteger(((Map) accountResource.getData().get("coin")).get("value").toString());
    }

    public static <TData> AccountResource<TData> getAccountResource(String baseUrl, String accountAddress, String resourceType,
                                                                    Class<TData> dataType, String ledgerVersion) throws IOException {
        Request request = newGetAccountResourceRequest(baseUrl, accountAddress, resourceType, ledgerVersion);
        OkHttpClient client = getOkHttpClient();
        try (Response response = client.newCall(request).execute()) {
            if (response.code() >= 400) {
                throwNodeApiException(response);
            }
            return (AccountResource<TData>) readResponseBodyAsParametricType(response, AccountResource.class, dataType);
        }
    }

    public static MoveModuleBytecode getAccountModule(String baseUrl, String accountAddress, String moduleName, String ledgerVersion) throws IOException {
        Request request = newGetAccountModuleRequest(baseUrl, accountAddress, moduleName, ledgerVersion);
        OkHttpClient client = getOkHttpClient();
        try (Response response = client.newCall(request).execute()) {
            if (response.code() >= 400) {
                throwNodeApiException(response);
            }
            return readResponseBody(response, MoveModuleBytecode.class);
        }
    }

    public static List<AccountResource> getAccountResources(String baseUrl, String accountAddress,
                                                            String ledgerVersion) throws IOException {
        Request request = newGetAccountResourcesRequest(baseUrl, accountAddress, ledgerVersion);
        OkHttpClient client = getOkHttpClient();
        try (Response response = client.newCall(request).execute()) {
            if (response.code() >= 400) {
                throwNodeApiException(response);
            }
            return readResponseBodyAsList(response, AccountResource.class);
        }
    }

    public static List<MoveModuleBytecode> getAccountModules(String baseUrl, String accountAddress, String ledgerVersion) throws IOException {
        Request request = newGetAccountModulesRequest(baseUrl, accountAddress, ledgerVersion);
        OkHttpClient client = getOkHttpClient();
        try (Response response = client.newCall(request).execute()) {
            if (response.code() >= 400) {
                throwNodeApiException(response);
            }
            return readResponseBodyAsList(response, MoveModuleBytecode.class);
        }
    }

    public static <T> T getTableItem(String baseUrl, String tableHandle, String keyType, String valueType, Object key,
                                     Class<T> valueJavaType, String ledgerVersion) throws IOException {
        Request request = newGetTableItemRequest(baseUrl, tableHandle, keyType, valueType, key, ledgerVersion);
        OkHttpClient client = getOkHttpClient();
        try (Response response = client.newCall(request).execute()) {
            if (response.code() == 404) {
                return null;
            }
            if (response.code() >= 400) {
                throwNodeApiException(response);
            }
            return readResponseBody(response, valueJavaType);
        }
    }

    public static GasEstimation estimateGasPrice(String baseUrl) throws IOException {
        Request request = newEstimateGasPriceRequest(baseUrl);
        OkHttpClient client = getOkHttpClient();
        try (Response response = client.newCall(request).execute()) {
            if (response.code() >= 400) {
                throwNodeApiException(response);
            }
            return readResponseBody(response, GasEstimation.class);
        }
    }

    public static Transaction submitBcsTransaction(String baseUrl, SignedUserTransaction signedTransaction) throws IOException, SerializationError {
        Request httpRequest = newSubmitBcsTransactionHttpRequest(baseUrl, signedTransaction);
        OkHttpClient client = new OkHttpClient.Builder().build();
        try (Response response = client.newCall(httpRequest).execute()) {
            if (response.code() >= 400) {
                throwNodeApiException(response);
            }
            return readResponseBody(response, Transaction.class);
        }
    }

    public static TransactionsBatchSubmissionResult submitBatchBcsTransactions(String baseUrl, List<SignedUserTransaction> signedTransactionList) throws IOException, SerializationError {
        Request httpRequest = newSubmitBatchBcsTransactionsHttpRequest(baseUrl, signedTransactionList);
        OkHttpClient client = new OkHttpClient.Builder().build();
        try (Response response = client.newCall(httpRequest).execute()) {
            if (response.code() >= 400) {
                throwNodeApiException(response);
            }
            return readResponseBody(response, TransactionsBatchSubmissionResult.class);
        }
    }

    public static List<Transaction> simulateBcsTransaction(String baseUrl, SignedUserTransaction signedTransaction,
                                                           Boolean estimateGasUnitPrice, Boolean estimateMaxGasAmount) throws IOException, SerializationError {
        Request httpRequest = newSimulateBcsTransactionHttpRequest(baseUrl, signedTransaction, estimateGasUnitPrice, estimateMaxGasAmount);
        OkHttpClient client = new OkHttpClient.Builder().build();
        try (Response response = client.newCall(httpRequest).execute()) {
            if (response.code() >= 400) {
                throwNodeApiException(response);
            }
            return readResponseBodyAsList(response, Transaction.class);
        }
    }

    public static Transaction submitTransaction(String baseUrl, SubmitTransactionRequest submitTransactionRequest) throws IOException {
        Request httpRequest = newSubmitTransactionHttpRequest(baseUrl, submitTransactionRequest);
        //HttpLoggingInterceptor logInterceptor = new HttpLoggingInterceptor(new HttpLogger());
        //logInterceptor.setLevel(HttpLoggingInterceptor.Level.HEADERS);
        OkHttpClient client = new OkHttpClient.Builder()
                //        .addNetworkInterceptor(logInterceptor)
                .build();
        try (Response response = client.newCall(httpRequest).execute()) {
            if (response.code() >= 400) {
                throwNodeApiException(response);
            }
            return readResponseBody(response, Transaction.class);
        }
    }


    /**
     * Submit batch transactions.
     */
    public static TransactionsBatchSubmissionResult submitBatchTransactions(String baseUrl, List<SubmitTransactionRequest> submitTransactionRequestList) throws IOException {
        Request httpRequest = newSubmitBatchTransactionsHttpRequest(baseUrl, submitTransactionRequestList);
        OkHttpClient client = new OkHttpClient.Builder()
                .build();
        try (Response response = client.newCall(httpRequest).execute()) {
            if (response.code() >= 400) {
                throwNodeApiException(response);
            }
            return readResponseBody(response, TransactionsBatchSubmissionResult.class);
        }
    }

    public static List<Transaction> simulateTransaction(String baseUrl, SubmitTransactionRequest submitTransactionRequest,
                                                        Boolean estimateGasUnitPrice, Boolean estimateMaxGasAmount) throws IOException {
        Request httpRequest = newSimulateTransactionHttpRequest(baseUrl, submitTransactionRequest, estimateGasUnitPrice, estimateMaxGasAmount);
        OkHttpClient client = new OkHttpClient.Builder().build();
        try (Response response = client.newCall(httpRequest).execute()) {
            if (response.code() >= 400) {
                throwNodeApiException(response);
            }
            return readResponseBodyAsList(response, Transaction.class);
        }
    }


    //    public static class HttpLogger implements HttpLoggingInterceptor.Logger {
    //        @Override
    //        public void log(String message) {
    //            System.out.println("HttpLogInfo\t" + message);
    //        }
    //    }

    public static String encodeSubmission(String baseUrl, EncodeSubmissionRequest r) throws IOException {
        Request httpRequest = newEncodeSubmissionHttpRequest(baseUrl, r);
        OkHttpClient client = getOkHttpClient();
        try (Response response = client.newCall(httpRequest).execute()) {
            if (response.code() >= 400) {
                throwNodeApiException(response);
            }
            return readResponseBody(response, String.class);
        }
    }

    public static EncodeSubmissionRequest newEncodeSubmissionRequest(String baseUrl, String sender,
                                                                     Long expirationTimestampSecs,
                                                                     TransactionPayload payload,
                                                                     Long maxGasAmount,
                                                                     String sequenceNumber,
                                                                     String gasUnitPrice) throws IOException {
        EncodeSubmissionRequest r = new EncodeSubmissionRequest();
        r.setSender(sender);
        r.setExpirationTimestampSecs(expirationTimestampSecs.toString());
        r.setMaxGasAmount("" + (maxGasAmount != null ? maxGasAmount : DEFAULT_MAX_GAS_AMOUNT));//todo
        r.setSequenceNumber(sequenceNumber == null ? getAccountSequenceNumber(baseUrl, sender) : sequenceNumber);
        r.setGasUnitPrice(gasUnitPrice == null ? getGasUnitPrice(baseUrl).toString() : gasUnitPrice);
        r.setPayload(payload);
        return r;
    }

    private static Request newEncodeSubmissionHttpRequest(String baseUrl, EncodeSubmissionRequest request) throws JsonProcessingException {
        HttpUrl.Builder builder = HttpUrl.parse(baseUrl).newBuilder()
                .addPathSegment("transactions")
                .addPathSegment("encode_submission");
        HttpUrl url = builder.build();
        ObjectMapper objectMapper = getObjectMapper();
        String json = objectMapper.writeValueAsString(request);
        RequestBody body = RequestBody.create(MediaType.parse("application/json"), ByteString.encodeUtf8(json));
        return new Request.Builder().url(url).post(body).build();
    }

    private static Request newSubmitTransactionHttpRequest(String baseUrl, SubmitTransactionRequest submitTransactionRequest) throws JsonProcessingException {
        HttpUrl.Builder builder = HttpUrl.parse(baseUrl).newBuilder()
                .addPathSegment("transactions");
        HttpUrl url = builder.build();
        ObjectMapper objectMapper = getObjectMapper();
        String json = objectMapper.writeValueAsString(submitTransactionRequest);
        RequestBody body = RequestBody.create(MediaType.parse("application/json"), ByteString.encodeUtf8(json));
        return new Request.Builder().url(url).post(body)
                //.header("Content-Type", "application/json")
                .build();
    }

    private static Request newSubmitBatchTransactionsHttpRequest(String baseUrl, List<SubmitTransactionRequest> submitTransactionRequestList) throws JsonProcessingException {
        HttpUrl.Builder builder = HttpUrl.parse(baseUrl).newBuilder()
                .addPathSegment("transactions")
                .addPathSegment("batch");
        HttpUrl url = builder.build();
        ObjectMapper objectMapper = getObjectMapper();
        String json = objectMapper.writeValueAsString(submitTransactionRequestList);
        RequestBody body = RequestBody.create(MediaType.parse("application/json"), ByteString.encodeUtf8(json));
        return new Request.Builder().url(url).post(body)
                .build();
    }

    private static Request newSimulateTransactionHttpRequest(String baseUrl, SubmitTransactionRequest submitTransactionRequest,
                                                             Boolean estimateGasUnitPrice, Boolean estimateMaxGasAmount) throws JsonProcessingException {

        HttpUrl.Builder builder = HttpUrl.parse(baseUrl).newBuilder()
                .addPathSegment("transactions")
                .addPathSegment("simulate");
        if (estimateGasUnitPrice != null) {
            builder.addQueryParameter("estimate_gas_unit_price", estimateGasUnitPrice.toString());
        }
        if (estimateMaxGasAmount != null) {
            builder.addQueryParameter("estimate_max_gas_amount", estimateMaxGasAmount.toString());
        }
        HttpUrl url = builder.build();
        ObjectMapper objectMapper = getObjectMapper();
        String json = objectMapper.writeValueAsString(submitTransactionRequest);
        RequestBody body = RequestBody.create(MediaType.parse("application/json"), ByteString.encodeUtf8(json));
        return new Request.Builder().url(url).post(body).build();
    }

    private static Request newSubmitBcsTransactionHttpRequest(String baseUrl, SignedUserTransaction signedTransaction) throws SerializationError {
        HttpUrl.Builder builder = HttpUrl.parse(baseUrl).newBuilder()
                .addPathSegment("transactions");
        HttpUrl url = builder.build();
        RequestBody body = RequestBody.create(MediaType.parse("application/x.aptos.signed_transaction+bcs"),
                signedTransaction.bcsSerialize());
        return new Request.Builder().url(url).post(body)
                .build();
    }


    private static Request newSubmitBatchBcsTransactionsHttpRequest(String baseUrl, List<SignedUserTransaction> signedTransactionList) throws SerializationError {
        HttpUrl.Builder builder = HttpUrl.parse(baseUrl).newBuilder()
                .addPathSegment("transactions")
                .addPathSegment("batch");
        HttpUrl url = builder.build();
        RequestBody body = RequestBody.create(MediaType.parse("application/x.aptos.signed_transaction+bcs"),
                bcsSerializeSignedUserTransactionList(signedTransactionList));
        return new Request.Builder().url(url).post(body)
                .build();
    }

    public static byte[] bcsSerializeSignedUserTransactionList(java.util.List<SignedUserTransaction> transactions) throws com.novi.serde.SerializationError {
        com.novi.serde.Serializer serializer = new com.novi.bcs.BcsSerializer();
        serializer.serialize_len(transactions.size());
        for (SignedUserTransaction transaction : transactions) {
            transaction.serialize(serializer);
        }
        return serializer.get_bytes();
    }

    private static Request newSimulateBcsTransactionHttpRequest(String baseUrl, SignedUserTransaction signedTransaction,
                                                                Boolean estimateGasUnitPrice, Boolean estimateMaxGasAmount) throws SerializationError {
        HttpUrl.Builder builder = HttpUrl.parse(baseUrl).newBuilder()
                .addPathSegment("transactions")
                .addPathSegment("simulate");
        if (estimateGasUnitPrice != null) {
            builder.addQueryParameter("estimate_gas_unit_price", estimateGasUnitPrice.toString());
        }
        if (estimateMaxGasAmount != null) {
            builder.addQueryParameter("estimate_max_gas_amount", estimateMaxGasAmount.toString());
        }
        HttpUrl url = builder.build();
        RequestBody body = RequestBody.create(MediaType.parse("application/x.aptos.signed_transaction+bcs"),
                signedTransaction.bcsSerialize());
        return new Request.Builder().url(url).post(body)
                .build();
    }

    public static String getAccountSequenceNumber(String baseUrl, String sender) {
        return getAccount(baseUrl, sender).getSequenceNumber();
    }

    private static Request newEstimateGasPriceRequest(String baseUrl) {
        HttpUrl.Builder builder = HttpUrl.parse(baseUrl).newBuilder()
                .addPathSegment("estimate_gas_price");
        HttpUrl url = builder.build();
        return new Request.Builder().url(url).build();
    }

    private static Request newGetTableItemRequest(String baseUrl, String tableHandle,
                                                  String keyType, String valueType, Object key, String ledgerVersion) throws JsonProcessingException {
        HttpUrl.Builder builder = HttpUrl.parse(baseUrl).newBuilder()
                .addPathSegment("tables")
                .addPathSegment(tableHandle)
                .addPathSegment("item");
        if (ledgerVersion != null) {
            builder.addQueryParameter("ledger_version", ledgerVersion);
        }
        HttpUrl url = builder.build();
        ObjectMapper objectMapper = getObjectMapper();
        Map<String, Object> bodyMap = new LinkedHashMap<>();
        bodyMap.put("key_type", keyType);
        bodyMap.put("value_type", valueType);
        bodyMap.put("key", key);
        RequestBody body = RequestBody.create(MediaType.parse("application/json"), objectMapper.writeValueAsString(bodyMap));
        return new Request.Builder().url(url).post(body).build();
    }

    private static Request newGetAccountResourceRequest(String baseUrl, String accountAddress, String resourceType, String ledgerVersion) {
        HttpUrl.Builder builder = HttpUrl.parse(baseUrl).newBuilder()
                .addPathSegment("accounts")
                .addPathSegment(accountAddress)
                .addPathSegment("resource")
                .addPathSegment(resourceType);
        if (ledgerVersion != null) {
            builder.addQueryParameter("ledger_version", ledgerVersion);
        }
        HttpUrl url = builder.build();
        return new Request.Builder().url(url).build();
    }

    private static Request newGetAccountModuleRequest(String baseUrl, String accountAddress, String moduleName, String ledgerVersion) {
        HttpUrl.Builder builder = HttpUrl.parse(baseUrl).newBuilder()
                .addPathSegment("accounts")
                .addPathSegment(accountAddress)
                .addPathSegment("module")
                .addPathSegment(moduleName);
        if (ledgerVersion != null) {
            builder.addQueryParameter("ledger_version", ledgerVersion);
        }
        HttpUrl url = builder.build();
        return new Request.Builder().url(url).build();
    }

    private static Request newGetAccountResourcesRequest(String baseUrl, String accountAddress, String ledgerVersion) {
        HttpUrl.Builder builder = HttpUrl.parse(baseUrl).newBuilder()
                .addPathSegment("accounts")
                .addPathSegment(accountAddress)
                .addPathSegment("resources");
        if (ledgerVersion != null) {
            builder.addQueryParameter("ledger_version", ledgerVersion);
        }
        HttpUrl url = builder.build();
        return new Request.Builder().url(url).build();
    }

    private static Request newGetAccountModulesRequest(String baseUrl, String accountAddress, String ledgerVersion) {
        HttpUrl.Builder builder = HttpUrl.parse(baseUrl).newBuilder()
                .addPathSegment("accounts")
                .addPathSegment(accountAddress)
                .addPathSegment("modules");
        if (ledgerVersion != null) {
            builder.addQueryParameter("ledger_version", ledgerVersion);
        }
        HttpUrl url = builder.build();
        return new Request.Builder().url(url).build();
    }

    private static Request newGetAccountRequest(String baseUrl, String accountAddress) {
        HttpUrl.Builder builder = HttpUrl.parse(baseUrl).newBuilder()
                .addPathSegment("accounts")
                .addPathSegment(accountAddress);
        HttpUrl url = builder.build();
        return new Request.Builder().url(url).build();
    }

    private static Request newGetEventsRequest(String baseUrl, String accountAddress,
                                               String eventHandleStruct, String eventHandleFieldName,
                                               Long start, Integer limit) {
        HttpUrl.Builder builder = HttpUrl.parse(baseUrl).newBuilder()
                .addPathSegment("accounts")
                .addPathSegment(accountAddress)
                .addPathSegment("events")
                .addPathSegment(eventHandleStruct)
                .addPathSegment(eventHandleFieldName);
        if (start != null) {
            builder.addQueryParameter("start", start.toString());
        }
        if (limit != null) {
            builder.addQueryParameter("limit", limit.toString());
        }
        HttpUrl url = builder.build();
        return new Request.Builder().url(url).build();
    }

    private static Request newGetEventsByCreationNumberRequest(String baseUrl, String accountAddress,
                                                               String creationNumber, Long start, Integer limit) {
        HttpUrl.Builder builder = HttpUrl.parse(baseUrl).newBuilder()
                .addPathSegment("accounts")
                .addPathSegment(accountAddress)
                .addPathSegment("events")
                .addPathSegment(creationNumber);
        if (start != null) {
            builder.addQueryParameter("start", start.toString());
        }
        if (limit != null) {
            builder.addQueryParameter("limit", limit.toString());
        }
        HttpUrl url = builder.build();
        return new Request.Builder().url(url).build();
    }

    private static Request newGetEventsRequest(String baseUrl, String eventKey, Long start, Integer limit) {
        HttpUrl.Builder builder = HttpUrl.parse(baseUrl).newBuilder()
                .addPathSegment("events")
                .addPathSegment(eventKey);
        if (start != null) {
            builder.addQueryParameter("start", start.toString());
        }
        if (limit != null) {
            builder.addQueryParameter("limit", limit.toString());
        }
        HttpUrl url = builder.build();
        return new Request.Builder().url(url).build();
    }


    private static Request newGetTransactionByHashRequest(String baseUrl, String hash) {
        HttpUrl.Builder builder = HttpUrl.parse(baseUrl).newBuilder()
                .addPathSegment("transactions")
                .addPathSegment("by_hash")
                .addPathSegment(hash);
        HttpUrl url = builder.build();
        return new Request.Builder().url(url).build();
    }

    private static Request newGetTransactionByVersionRequest(String baseUrl, String version) {
        HttpUrl.Builder builder = HttpUrl.parse(baseUrl).newBuilder()
                .addPathSegment("transactions")
                .addPathSegment("by_version")
                .addPathSegment(version);
        HttpUrl url = builder.build();
        return new Request.Builder().url(url).build();
    }

    private static Request newGetAccountTransactionsRequest(String baseUrl, String accountAddress, Long start, Integer limit) {
        HttpUrl.Builder builder = HttpUrl.parse(baseUrl).newBuilder()
                .addPathSegment("accounts")
                .addPathSegment(accountAddress)
                .addPathSegment("transactions");
        if (start != null) {
            builder.addQueryParameter("start", start.toString());
        }
        if (limit != null) {
            builder.addQueryParameter("limit", limit.toString());
        }
        HttpUrl url = builder.build();
        return new Request.Builder().url(url).build();
    }


    private static Request newGetBlockByHeightRequest(String baseUrl, String height, Boolean withTransactions) {
        HttpUrl.Builder builder = HttpUrl.parse(baseUrl).newBuilder()
                .addPathSegment("blocks")
                .addPathSegment("by_height")
                .addPathSegment(height);
        if (withTransactions != null) {
            builder.addQueryParameter("with_transactions", withTransactions.toString());
        }
        HttpUrl url = builder.build();
        return new Request.Builder().url(url).build();
    }


    private static Request newGetBlockByVersionRequest(String baseUrl, String height, Boolean withTransactions) {
        HttpUrl.Builder builder = HttpUrl.parse(baseUrl).newBuilder()
                .addPathSegment("blocks")
                .addPathSegment("by_version")
                .addPathSegment(height);
        if (withTransactions != null) {
            builder.addQueryParameter("with_transactions", withTransactions.toString());
        }
        HttpUrl url = builder.build();
        return new Request.Builder().url(url).build();
    }


    private static Request newCheckBasicNodeHealthRequest(String baseUrl, Integer durationSecs) {
        HttpUrl.Builder builder = HttpUrl.parse(baseUrl).newBuilder()
                .addPathSegment("-")
                .addPathSegment("healthy");
        if (durationSecs != null) {
            builder.addQueryParameter("duration_secs", durationSecs.toString());
        }
        HttpUrl url = builder.build();
        return new Request.Builder().url(url).build();
    }

    private static <T> T readResponseBody(Response response, Class<T> clazz) throws IOException {
        ObjectMapper objectMapper = getObjectMapper();
        return objectMapper.readValue(response.body().string(), clazz);
    }

    private static <T> List<T> readResponseBodyAsList(Response response, Class<T> elementType) throws IOException {
        ObjectMapper objectMapper = getObjectMapper();
        return objectMapper.readValue(response.body().string(),
                objectMapper.getTypeFactory().constructCollectionType(List.class, elementType));
    }

    private static Object readResponseBodyAsParametricType(Response response, Class<?> parametrized, Class<?>... parameterClasses) throws IOException {
        ObjectMapper objectMapper = getObjectMapper();
        return objectMapper.readValue(response.body().string(),
                objectMapper.getTypeFactory().constructParametricType(parametrized, parameterClasses));
    }

    private static void throwNodeApiException(Response response) {
        ObjectMapper objectMapper = getObjectMapper();
        AptosError aptosError;
        try {
            aptosError = objectMapper.readValue(response.body().string(), AptosError.class);
        } catch (IOException e) {
            throw new NodeApiException(response.code(), null, response.request().url().toString(), e);
        }
        throw new NodeApiException(response.code(), aptosError, response.request().url().toString(), null);
    }

//    private static String formatPathSegmentAccountAddress(String accountAddress) {
//        return HexUtils.byteArrayToHexWithPrefix(HexUtils.hexToAccountAddressBytes(accountAddress));
//    }

}
