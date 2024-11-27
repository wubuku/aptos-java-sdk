package com.github.wubuku.aptos.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.wubuku.aptos.bean.*;
import com.github.wubuku.aptos.types.ChainId;
import com.github.wubuku.aptos.types.RawTransaction;
import com.github.wubuku.aptos.types.SignedUserTransaction;
import com.github.wubuku.aptos.types.TypeTag;
import com.novi.serde.Bytes;
import com.novi.serde.SerializationError;
import okhttp3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class NodeApiClient {
    public static final long DEFAULT_MAX_GAS_AMOUNT = 400000L;

    private static final Logger LOGGER = LoggerFactory.getLogger(NodeApiClient.class);

    private final String baseUrl;

    private ObjectMapper objectMapper;

    public NodeApiClient(String baseUrl) {
        this(baseUrl, new ObjectMapper());
    }

    public NodeApiClient(String baseUrl, ObjectMapper objectMapper) {
        if (objectMapper == null) throw new NullPointerException("objectMapper is null");
        this.objectMapper = objectMapper;
        this.baseUrl = baseUrl;
    }

    public static byte[] bcsSerializeSignedUserTransactionList(List<SignedUserTransaction> transactions) throws SerializationError {
        com.novi.serde.Serializer serializer = new com.novi.bcs.BcsSerializer();
        serializer.serialize_len(transactions.size());
        for (SignedUserTransaction transaction : transactions) {
            transaction.serialize(serializer);
        }
        return serializer.get_bytes();
    }

    //    public static class HttpLogger implements HttpLoggingInterceptor.Logger {
    //        @Override
    //        public void log(String message) {
    //            System.out.println("HttpLogInfo\t" + message);
    //        }
    //    }

    public ObjectMapper getObjectMapper() {
        return objectMapper;
    }

    public void setObjectMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    protected OkHttpClient getOkHttpClient() {
        //HttpLoggingInterceptor logInterceptor = new HttpLoggingInterceptor(new HttpLogger());
        //logInterceptor.setLevel(HttpLoggingInterceptor.Level.HEADERS);
        return new OkHttpClient.Builder()
                //        .addNetworkInterceptor(logInterceptor)
                .build();
    }

    private HttpUrl newGetAccountResourceHttpUrl(String baseUrl, String accountAddress, String resourceType, String ledgerVersion) {
        HttpUrl.Builder builder = HttpUrl.parse(baseUrl).newBuilder()
                .addPathSegment("accounts")
                .addPathSegment(accountAddress)
                .addPathSegment("resource")
                .addPathSegment(resourceType);
        if (ledgerVersion != null) {
            builder.addQueryParameter("ledger_version", ledgerVersion);
        }
        return builder.build();
    }

    private Request newEncodeSubmissionHttpRequest(String baseUrl, EncodeSubmissionRequest request) throws JsonProcessingException {
        HttpUrl.Builder builder = HttpUrl.parse(baseUrl).newBuilder()
                .addPathSegment("transactions")
                .addPathSegment("encode_submission");
        HttpUrl url = builder.build();
        ObjectMapper objectMapper = getObjectMapper();
        String json = objectMapper.writeValueAsString(request);
        RequestBody body = RequestBody.create(
                json,
                MediaType.get("application/json")
        );
        return new Request.Builder().url(url).post(body).build();
    }

    private Request newSubmitTransactionHttpRequest(String baseUrl, SubmitTransactionRequest submitTransactionRequest) throws JsonProcessingException {
        HttpUrl.Builder builder = HttpUrl.parse(baseUrl).newBuilder()
                .addPathSegment("transactions");
        HttpUrl url = builder.build();
        ObjectMapper objectMapper = getObjectMapper();
        String json = objectMapper.writeValueAsString(submitTransactionRequest);
        RequestBody body = RequestBody.create(
                json,
                MediaType.get("application/json")
        );
        return new Request.Builder().url(url).post(body)
                //.header("Content-Type", "application/json")
                .build();
    }

    private Request newSubmitBatchTransactionsHttpRequest(String baseUrl, List<SubmitTransactionRequest> submitTransactionRequestList) throws JsonProcessingException {
        HttpUrl.Builder builder = HttpUrl.parse(baseUrl).newBuilder()
                .addPathSegment("transactions")
                .addPathSegment("batch");
        HttpUrl url = builder.build();
        ObjectMapper objectMapper = getObjectMapper();
        String json = objectMapper.writeValueAsString(submitTransactionRequestList);
        RequestBody body = RequestBody.create(
                json,
                MediaType.get("application/json")
        );
        return new Request.Builder().url(url).post(body)
                .build();
    }

    private Request newSimulateTransactionHttpRequest(String baseUrl, SubmitTransactionRequest submitTransactionRequest,
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
        RequestBody body = RequestBody.create(
                json,
                MediaType.get("application/json")
        );
        return new Request.Builder().url(url).post(body).build();
    }

    private Request newSubmitBcsTransactionHttpRequest(String baseUrl, SignedUserTransaction signedTransaction) throws SerializationError {
        HttpUrl.Builder builder = HttpUrl.parse(baseUrl).newBuilder()
                .addPathSegment("transactions");
        HttpUrl url = builder.build();
        RequestBody body = RequestBody.create(
                signedTransaction.bcsSerialize(),
                MediaType.get("application/x.aptos.signed_transaction+bcs")
        );
        return new Request.Builder().url(url).post(body)
                .build();
    }

    private Request newSubmitBatchBcsTransactionsHttpRequest(String baseUrl, List<SignedUserTransaction> signedTransactionList) throws SerializationError {
        HttpUrl.Builder builder = HttpUrl.parse(baseUrl).newBuilder()
                .addPathSegment("transactions")
                .addPathSegment("batch");
        HttpUrl url = builder.build();
        RequestBody body = RequestBody.create(
                bcsSerializeSignedUserTransactionList(signedTransactionList),
                MediaType.get("application/x.aptos.signed_transaction+bcs")
        );
        return new Request.Builder().url(url).post(body)
                .build();
    }

    private Request newSimulateBcsTransactionHttpRequest(String baseUrl, SignedUserTransaction signedTransaction,
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
        RequestBody body = RequestBody.create(
                signedTransaction.bcsSerialize(),
                MediaType.get("application/x.aptos.signed_transaction+bcs")
        );
        return new Request.Builder().url(url).post(body)
                .build();
    }

    private Request newEstimateGasPriceRequest(String baseUrl) {
        HttpUrl.Builder builder = HttpUrl.parse(baseUrl).newBuilder()
                .addPathSegment("estimate_gas_price");
        HttpUrl url = builder.build();
        return new Request.Builder().url(url).build();
    }

    private Request newGetTableItemRequest(String baseUrl, String tableHandle,
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
        RequestBody body = RequestBody.create(
                objectMapper.writeValueAsString(bodyMap),
                MediaType.get("application/json")
        );
        return new Request.Builder().url(url).post(body).build();
    }

    private Request newGetRawTableItemRequest(String baseUrl, String tableHandle,
                                              byte[] key, String ledgerVersion) throws JsonProcessingException {
        HttpUrl.Builder builder = HttpUrl.parse(baseUrl).newBuilder()
                .addPathSegment("tables")
                .addPathSegment(tableHandle)
                .addPathSegment("raw_item");
        if (ledgerVersion != null) {
            builder.addQueryParameter("ledger_version", ledgerVersion);
        }
        HttpUrl url = builder.build();
        ObjectMapper objectMapper = getObjectMapper();
        Map<String, Object> bodyMap = new LinkedHashMap<>();
        bodyMap.put("key", HexUtils.byteArrayToHexWithPrefix(key));
        RequestBody body = RequestBody.create(
                objectMapper.writeValueAsString(bodyMap),
                MediaType.get("application/json")
        );
        return new Request.Builder().url(url).post(body)
                .header("Accept", "application/x-bcs")
                .build();
    }

    private Request newGetAccountResourceRequest(String baseUrl, String accountAddress, String resourceType, String ledgerVersion) {
        HttpUrl url = newGetAccountResourceHttpUrl(baseUrl, accountAddress, resourceType, ledgerVersion);
        return new Request.Builder().url(url).build();
    }

    private Request newGetRawAccountResourceRequest(String baseUrl, String accountAddress, String resourceType, String ledgerVersion) {
        HttpUrl url = newGetAccountResourceHttpUrl(baseUrl, accountAddress, resourceType, ledgerVersion);
        return new Request.Builder().url(url)
                .header("Accept", "application/x-bcs")
                .build();
    }

    private Request newGetAccountModuleRequest(String baseUrl, String accountAddress, String moduleName, String ledgerVersion) {
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

    private Request newGetAccountResourcesRequest(String baseUrl, String accountAddress, String ledgerVersion) {
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

    private Request newGetAccountModulesRequest(String baseUrl, String accountAddress, String ledgerVersion) {
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

    private Request newGetAccountRequest(String baseUrl, String accountAddress) {
        HttpUrl.Builder builder = HttpUrl.parse(baseUrl).newBuilder()
                .addPathSegment("accounts")
                .addPathSegment(accountAddress);
        HttpUrl url = builder.build();
        return new Request.Builder().url(url).build();
    }

    private Request newGetEventsRequest(String baseUrl, String accountAddress,
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

    private Request newGetEventsByCreationNumberRequest(String baseUrl, String accountAddress,
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

    private Request newGetEventsRequest(String baseUrl, String eventKey, Long start, Integer limit) {
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

    private Request newGetTransactionByHashRequest(String baseUrl, String hash) {
        HttpUrl.Builder builder = HttpUrl.parse(baseUrl).newBuilder()
                .addPathSegment("transactions")
                .addPathSegment("by_hash")
                .addPathSegment(hash);
        HttpUrl url = builder.build();
        return new Request.Builder().url(url).build();
    }

    private Request newGetTransactionByVersionRequest(String baseUrl, String version) {
        HttpUrl.Builder builder = HttpUrl.parse(baseUrl).newBuilder()
                .addPathSegment("transactions")
                .addPathSegment("by_version")
                .addPathSegment(version);
        HttpUrl url = builder.build();
        return new Request.Builder().url(url).build();
    }

    private Request newGetAccountTransactionsRequest(String baseUrl, String accountAddress, Long start, Integer limit) {
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

    private Request newGetBlockByHeightRequest(String baseUrl, String height, Boolean withTransactions) {
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

    private Request newGetBlockByVersionRequest(String baseUrl, String height, Boolean withTransactions) {
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

    private Request newCheckBasicNodeHealthRequest(String baseUrl, Integer durationSecs) {
        HttpUrl.Builder builder = HttpUrl.parse(baseUrl).newBuilder()
                .addPathSegment("-")
                .addPathSegment("healthy");
        if (durationSecs != null) {
            builder.addQueryParameter("duration_secs", durationSecs.toString());
        }
        HttpUrl url = builder.build();
        return new Request.Builder().url(url).build();
    }

    private <T> T readResponseBody(Response response, Class<T> clazz) throws IOException {
        String responseBody = response.body().string();
        try {
            return objectMapper.readValue(responseBody, clazz);
        } catch (JsonProcessingException e) {
            LOGGER.error("Failed to deserialize response body to class {}: body={}", clazz.getName(), responseBody, e);
            throw e;
        }
    }

    private <T> List<T> readResponseBodyAsList(Response response, Class<T> elementType) throws IOException {
        String responseBody = response.body().string();
        try {
            return objectMapper.readValue(responseBody,
                    objectMapper.getTypeFactory().constructCollectionType(List.class, elementType));
        } catch (JsonProcessingException e) {
            LOGGER.error("Failed to deserialize response body to list of {}: body={}", elementType.getName(), responseBody, e);
            throw e;
        }
    }

    private Object readResponseBodyAsParametricType(Response response, Class<?> parametrized, Class<?>... parameterClasses) throws IOException {
        String responseBody = response.body().string();
        try {
            return objectMapper.readValue(responseBody,
                    objectMapper.getTypeFactory().constructParametricType(parametrized, parameterClasses));
        } catch (JsonProcessingException e) {
            LOGGER.error("Failed to deserialize response body to parametric type {}<{}>: body={}",
                    parametrized.getName(),
                    Arrays.stream(parameterClasses).map(Class::getName).collect(Collectors.joining(",")),
                    responseBody,
                    e);
            throw e;
        }
    }

    private void throwNodeApiException(Response response) {
        String responseBody;
        try {
            responseBody = response.body().string();
            AptosError aptosError = objectMapper.readValue(responseBody, AptosError.class);
            throw new NodeApiException(response.code(), aptosError, response.request().url().toString(), null);
        } catch (IOException e) {
            LOGGER.error("Failed to deserialize error response: url={}, code={}",
                    response.request().url(),
                    response.code(),
                    e);
            throw new NodeApiException(response.code(), null, response.request().url().toString(), e);
        }
    }

    public RawTransaction newRawTransaction(String senderAddress,
                                            String moduleAddress, String moduleName, String functionName,
                                            Long maxGasAmount,
                                            List<TypeTag> txnTypeArgs, List<Bytes> trxArgs
    ) throws IOException {
        String expirationTimestampSecs = String.valueOf(System.currentTimeMillis() / 1000L + 600);
        String sequenceNumber = getAccountSequenceNumber(senderAddress);
        String gasUnitPrice = getGasUnitPrice().toString();
        LedgerInfo ledgerInfo = getLedgerInfo();
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

    public Integer getGasUnitPrice() throws IOException {
        return estimateGasPrice().getGasEstimate();
    }


    public LedgerInfo getLedgerInfo() throws IOException {
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

    public HealthCheckSuccess checkBasicNodeHealth(Integer durationSecs) throws IOException {
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
    public Transaction getTransactionByHash(String hash) throws IOException {
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

    public boolean isTransactionPending(String hash) throws IOException {
        Transaction transaction = getTransactionByHash(hash);
        return transaction == null
                || Transaction.TYPE_PENDING_TRANSACTION.equals(transaction.getType());
    }

    public void waitForTransaction(String hash) throws IOException {
        while (isTransactionPending(hash)) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        Transaction transaction = getTransactionByHash(hash);
        if (!(transaction.getSuccess() != null && transaction.getSuccess())) {
            throw new IOException("Transaction is not success.");
        }
    }

    public Transaction getTransactionByVersion(String version) throws IOException {
        Request request = newGetTransactionByVersionRequest(baseUrl, version);
        OkHttpClient client = getOkHttpClient();
        try (Response response = client.newCall(request).execute()) {
            if (response.code() >= 400) {
                throwNodeApiException(response);
            }
            return readResponseBody(response, Transaction.class);
        }
    }

    public List<Transaction> getAccountTransactions(String address, Long start, Integer limit) throws IOException {
        Request request = newGetAccountTransactionsRequest(baseUrl, address, start, limit);
        OkHttpClient client = getOkHttpClient();
        try (Response response = client.newCall(request).execute()) {
            if (response.code() >= 400) {
                throwNodeApiException(response);
            }
            return readResponseBodyAsList(response, Transaction.class);
        }
    }

    public Block getBlockByHeight(String height, Boolean withTransactions) throws IOException {
        Request request = newGetBlockByHeightRequest(baseUrl, height, withTransactions);
        OkHttpClient client = getOkHttpClient();
        try (Response response = client.newCall(request).execute()) {
            if (response.code() >= 400) {
                throwNodeApiException(response);
            }
            return readResponseBody(response, Block.class);
        }
    }

    public Block getBlockByVersion(String version, Boolean withTransactions) throws IOException {
        Request request = newGetBlockByVersionRequest(baseUrl, version, withTransactions);
        OkHttpClient client = getOkHttpClient();
        try (Response response = client.newCall(request).execute()) {
            if (response.code() >= 400) {
                throwNodeApiException(response);
            }
            return readResponseBody(response, Block.class);
        }
    }

    @SuppressWarnings("rawtypes")
    public List<Event> getEventsByEventHandle(String accountAddress,
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

    public <TData> List<Event<TData>> getEventsByEventHandle(String accountAddress,
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
    public <TData> List<Event<TData>> getEventsByCreationNumber(String accountAddress,
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
    public <TData> List<Event<TData>> getEventsByEventKey(String eventKey,
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

    private <TData> List<Event<TData>> readEventList(String json, Class<TData> eventDataType) throws JsonProcessingException {
        try {
            ObjectMapper objectMapper = getObjectMapper();
            List<Map<String, Object>> mapList = objectMapper.readValue(json,
                    objectMapper.getTypeFactory().constructCollectionType(List.class, Map.class));
            return mapList.stream().map(map -> objectMapper.<Event<TData>>convertValue(map,
                            objectMapper.getTypeFactory().constructParametricType(Event.class, eventDataType)))
                    .collect(Collectors.toList());
        } catch (JsonProcessingException e) {
            LOGGER.error("Failed to deserialize event list for type {}: json={}", eventDataType.getName(), json, e);
            throw e;
        }
    }

    /**
     * Retrieves high level information about an account such as its sequence number and authentication key.
     */
    public Account getAccount(String accountAddress) {
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

    public BigInteger getAccountBalance(String accountAddress) throws IOException {
        String resourceType = "0x1::coin::CoinStore<0x1::aptos_coin::AptosCoin>";
        AccountResource<Map> accountResource = getAccountResource(accountAddress, resourceType,
                Map.class, null);
        return new BigInteger(((Map) accountResource.getData().get("coin")).get("value").toString());
    }

    public <TData> AccountResource<TData> getAccountResource(String accountAddress, String resourceType,
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

    public byte[] getRawAccountResource(String accountAddress, String resourceType,
                                        String ledgerVersion) throws IOException {
        Request request = newGetRawAccountResourceRequest(baseUrl, accountAddress, resourceType, ledgerVersion);
        OkHttpClient client = getOkHttpClient();
        try (Response response = client.newCall(request).execute()) {
            if (response.code() >= 400) {
                throwNodeApiException(response);
            }
            return response.body() != null ? response.body().bytes() : new byte[0];
        }
    }

    public MoveModuleBytecode getAccountModule(String accountAddress, String moduleName, String ledgerVersion) throws IOException {
        Request request = newGetAccountModuleRequest(baseUrl, accountAddress, moduleName, ledgerVersion);
        OkHttpClient client = getOkHttpClient();
        try (Response response = client.newCall(request).execute()) {
            if (response.code() >= 400) {
                throwNodeApiException(response);
            }
            return readResponseBody(response, MoveModuleBytecode.class);
        }
    }

    /**
     * Retrieves account resources whose type matches the given regular expression pattern.
     *
     * @param typeRegex a regular expression to match resource types
     * @return list of matching account resources
     */
    @SuppressWarnings("rawtypes")
    public <TData> List<AccountResource<TData>> getAccountResourcesMatchingType(
            String accountAddress, String typeRegex, Class<TData> dataType,
            String ledgerVersion) throws IOException {
        Request request = newGetAccountResourcesRequest(baseUrl, accountAddress, ledgerVersion);
        OkHttpClient client = getOkHttpClient();
        try (Response response = client.newCall(request).execute()) {
            if (response.code() >= 400) {
                throwNodeApiException(response);
            }
            List<AccountResource> rawResources = readResponseBodyAsList(response, AccountResource.class);
            return rawResources.stream().filter(resource -> resource.getType().matches(typeRegex))
                    .map(r -> new AccountResource<>(
                            r.getType(), objectMapper.convertValue(r.getData(), dataType)
                    ))
                    .collect(Collectors.toList());
        }
    }

    @SuppressWarnings("rawtypes")
    public List<AccountResource> getAccountResources(String accountAddress,
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

    public List<MoveModuleBytecode> getAccountModules(String accountAddress, String ledgerVersion) throws IOException {
        Request request = newGetAccountModulesRequest(baseUrl, accountAddress, ledgerVersion);
        OkHttpClient client = getOkHttpClient();
        try (Response response = client.newCall(request).execute()) {
            if (response.code() >= 400) {
                throwNodeApiException(response);
            }
            return readResponseBodyAsList(response, MoveModuleBytecode.class);
        }
    }

    public <T> T getTableItem(String tableHandle, String keyType, String valueType, Object key,
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

    public byte[] getRawTableItem(String tableHandle, byte[] key,
                                  String ledgerVersion) throws IOException {
        Request request = newGetRawTableItemRequest(baseUrl, tableHandle, key, ledgerVersion);
        OkHttpClient client = getOkHttpClient();
        try (Response response = client.newCall(request).execute()) {
            if (response.code() == 404) {
                return null;
            }
            if (response.code() >= 400) {
                throwNodeApiException(response);
            }
            return response.body() != null ? response.body().bytes() : new byte[0];
        }
    }

    public GasEstimation estimateGasPrice() throws IOException {
        Request request = newEstimateGasPriceRequest(baseUrl);
        OkHttpClient client = getOkHttpClient();
        try (Response response = client.newCall(request).execute()) {
            if (response.code() >= 400) {
                throwNodeApiException(response);
            }
            return readResponseBody(response, GasEstimation.class);
        }
    }

    public Transaction submitBcsTransaction(SignedUserTransaction signedTransaction) throws IOException, SerializationError {
        Request httpRequest = newSubmitBcsTransactionHttpRequest(baseUrl, signedTransaction);
        OkHttpClient client = getOkHttpClient();
        try (Response response = client.newCall(httpRequest).execute()) {
            if (response.code() >= 400) {
                throwNodeApiException(response);
            }
            return readResponseBody(response, Transaction.class);
        }
    }

    public TransactionsBatchSubmissionResult submitBatchBcsTransactions(List<SignedUserTransaction> signedTransactionList) throws IOException, SerializationError {
        Request httpRequest = newSubmitBatchBcsTransactionsHttpRequest(baseUrl, signedTransactionList);
        OkHttpClient client = getOkHttpClient();
        try (Response response = client.newCall(httpRequest).execute()) {
            if (response.code() >= 400) {
                throwNodeApiException(response);
            }
            return readResponseBody(response, TransactionsBatchSubmissionResult.class);
        }
    }

    public List<Transaction> simulateBcsTransaction(SignedUserTransaction signedTransaction,
                                                    Boolean estimateGasUnitPrice, Boolean estimateMaxGasAmount) throws IOException, SerializationError {
        Request httpRequest = newSimulateBcsTransactionHttpRequest(baseUrl, signedTransaction, estimateGasUnitPrice, estimateMaxGasAmount);
        OkHttpClient client = getOkHttpClient();
        try (Response response = client.newCall(httpRequest).execute()) {
            if (response.code() >= 400) {
                throwNodeApiException(response);
            }
            return readResponseBodyAsList(response, Transaction.class);
        }
    }

    public Transaction submitTransaction(SubmitTransactionRequest submitTransactionRequest) throws IOException {
        Request httpRequest = newSubmitTransactionHttpRequest(baseUrl, submitTransactionRequest);
        OkHttpClient client = getOkHttpClient();
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
    public TransactionsBatchSubmissionResult submitBatchTransactions(List<SubmitTransactionRequest> submitTransactionRequestList) throws IOException {
        Request httpRequest = newSubmitBatchTransactionsHttpRequest(baseUrl, submitTransactionRequestList);
        OkHttpClient client = getOkHttpClient();
        try (Response response = client.newCall(httpRequest).execute()) {
            if (response.code() >= 400) {
                throwNodeApiException(response);
            }
            return readResponseBody(response, TransactionsBatchSubmissionResult.class);
        }
    }

    public List<Transaction> simulateTransaction(SubmitTransactionRequest submitTransactionRequest,
                                                 Boolean estimateGasUnitPrice, Boolean estimateMaxGasAmount) throws IOException {
        Request httpRequest = newSimulateTransactionHttpRequest(baseUrl, submitTransactionRequest, estimateGasUnitPrice, estimateMaxGasAmount);
        OkHttpClient client = getOkHttpClient();
        try (Response response = client.newCall(httpRequest).execute()) {
            if (response.code() >= 400) {
                throwNodeApiException(response);
            }
            return readResponseBodyAsList(response, Transaction.class);
        }
    }

    public String encodeSubmission(EncodeSubmissionRequest r) throws IOException {
        Request httpRequest = newEncodeSubmissionHttpRequest(baseUrl, r);
        OkHttpClient client = getOkHttpClient();
        try (Response response = client.newCall(httpRequest).execute()) {
            if (response.code() >= 400) {
                throwNodeApiException(response);
            }
            return readResponseBody(response, String.class);
        }
    }

    public EncodeSubmissionRequest newEncodeSubmissionRequest(String sender,
                                                              Long expirationTimestampSecs,
                                                              TransactionPayload payload,
                                                              Long maxGasAmount,
                                                              String sequenceNumber,
                                                              String gasUnitPrice) throws IOException {
        EncodeSubmissionRequest r = new EncodeSubmissionRequest();
        r.setSender(sender);
        r.setExpirationTimestampSecs(expirationTimestampSecs.toString());
        r.setMaxGasAmount(String.valueOf(maxGasAmount != null ? maxGasAmount : DEFAULT_MAX_GAS_AMOUNT));//todo
        r.setSequenceNumber(sequenceNumber == null ? getAccountSequenceNumber(sender) : sequenceNumber);
        r.setGasUnitPrice(gasUnitPrice == null ? getGasUnitPrice().toString() : gasUnitPrice);
        r.setPayload(payload);
        return r;
    }

    public String getAccountSequenceNumber(String sender) {
        return getAccount(sender).getSequenceNumber();
    }

//    private static String formatPathSegmentAccountAddress(String accountAddress) {
//        return HexUtils.byteArrayToHexWithPrefix(HexUtils.hexToAccountAddressBytes(accountAddress));
//    }

}
