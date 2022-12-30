package com.github.wubuku.aptos;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.wubuku.aptos.bean.Transaction;
import com.github.wubuku.aptos.bean.*;
import com.github.wubuku.aptos.types.*;
import com.github.wubuku.aptos.utils.*;
import com.novi.serde.Bytes;
import com.novi.serde.SerializationError;

import java.io.IOException;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.github.wubuku.aptos.utils.Helpers.*;
import static com.github.wubuku.aptos.utils.NodeApiClient.DEFAULT_MAX_GAS_AMOUNT;
import static com.github.wubuku.aptos.utils.NodeApiUtils.estimateGasPrice;
import static com.github.wubuku.aptos.utils.StructTagUtils.toTypeTag;
import static com.github.wubuku.aptos.utils.TransactionUtils.*;

public class MainTests {

    public static void main(String[] args) throws IOException {

        if (false) {
            String aptosDevnetApiBaseUrl = "https://fullnode.devnet.aptoslabs.com/v1";
            // //////////////////////////////////
            mobiusQueryTest_1(aptosDevnetApiBaseUrl);
            if (true) return;
            // //////////////////////////////////

            nftTest_1(aptosDevnetApiBaseUrl);
            if (true) return;

            Map testOrderState = NodeApiUtils.getTableItem(
                    aptosDevnetApiBaseUrl,
                    "0x361bf11399a0f5b89b32d9a0a1079dd674d7a4402b63df0ed6834075b0f6872d",
                    "vector<u8>",
                    "0xccf1b0a1053878b4ec1ec094fe50adbe47e9be326208210233132df3ddaf9030::order::OrderState",//"vector<u8>",
                    HexUtils.byteArrayToHexWithPrefix("test_order_id_1".getBytes()),
                    Map.class, null);
            System.out.println(testOrderState);

            java.util.Map<String, Object> testOrderItemId = new HashMap<>();
            testOrderItemId.put("order_id", HexUtils.byteArrayToHexWithPrefix("test_order_id_1".getBytes()));
            testOrderItemId.put("product_id", HexUtils.byteArrayToHexWithPrefix("test_product_id_1".getBytes()));
            Map testOrderItemState = NodeApiUtils.getTableItem(
                    aptosDevnetApiBaseUrl,
                    "0x36f48f56e3265945691686c05abe765af78be8f3fbb7b39ef3017a39e25bdff1",
                    "0xccf1b0a1053878b4ec1ec094fe50adbe47e9be326208210233132df3ddaf9030::order::OrderItemId",
                    "0xccf1b0a1053878b4ec1ec094fe50adbe47e9be326208210233132df3ddaf9030::order::OrderItemState",//"vector<u8>",
                    testOrderItemId,
                    Map.class, null);
            System.out.println(testOrderItemState);
            //if (true) return;
        }

        testFormatStructTags();
        //if (true) return;

        //String baseUrl = "https://fullnode.devnet.aptoslabs.com/v1";
        //ChainId chainId = new ChainId((byte) 32); // devnet chain Id.
        ChainId chainId = new ChainId((byte) 2); // testnet chain Id.
        String baseUrl = "https://testnet.aptoslabs.com/v1";

//        String hex = NodeApiUtils.getTableItem("https://testnet.aptoslabs.com/v1",
//                "0x1b854694ae746cdbd8d44186ca4929b2b337df21d1c74633be19b2710552fdca",
//                "vector<u8>", "vector<u8>",
//                "0x619dc29a0aac8fa146714058e8dd6d2d0f3bdf5f6331907bf91f3acd81e6935", String.class, null);
//        System.out.println(hex);
//        System.out.println(new String(HexUtils.hexToByteArray(hex)));
//        if (true) return;
//        //0x1b854694ae746cdbd8d44186ca4929b2b337df21d1c74633be19b2710552fdca
//        //0x619dc29a0aac8fa146714058e8dd6d2d0f3bdf5f6331907bf91f3acd81e6935

//        AccountResource<CoinInfo> coinInfo = NodeApiUtils.getAccountResource(baseUrl, "0x1",
//                "0x1::coin::CoinInfo<0x1::aptos_coin::AptosCoin>", CoinInfo.class, null);
//        System.out.println(coinInfo);
//        if (true) return;

        String accountAddress = "0x2b490841c230a31fe012f3b2a3f3d146316be073e599eb7d7e5074838073ef14";
        String eventHandleStruct = "0x2b490841c230a31fe012f3b2a3f3d146316be073e599eb7d7e5074838073ef14::message::MessageHolder";
        String eventHandleFieldName = "message_change_events";

        List<com.github.wubuku.aptos.bean.Transaction> transactions = NodeApiUtils.getAccountTransactions(baseUrl, accountAddress, null, 100);
        System.out.println(transactions);
        transactions.forEach(t -> {
            System.out.println(t);
        });
        HealthCheckSuccess healthCheckSuccess = NodeApiUtils.checkBasicNodeHealth(baseUrl, 100);
        System.out.println(healthCheckSuccess);
        //if (true) return;

        LedgerInfo ledgerInfo = NodeApiUtils.getLedgerInfo(baseUrl);
        System.out.println(ledgerInfo);
        //if (true) return;
        String blockHeight = ledgerInfo.getBlockHeight();
        String ledgerVersion = ledgerInfo.getLedgerVersion();

        java.math.BigInteger balance = NodeApiUtils.getAccountBalance(baseUrl, accountAddress);
        System.out.println(balance);
        //if (true) return;

        boolean testSubmitTransaction = false;
        if (testSubmitTransaction) {
            long maxGasAmount = DEFAULT_MAX_GAS_AMOUNT;
            long expirationTimestampSecs = System.currentTimeMillis() / 1000L + 600;
            byte[] publicKey = HexUtils.hexToByteArray("0xa76e9dd1a2d9101de47e69e52e0232060b95cd7d80265d61c3fa25e406389b75");
            byte[] privateKey = HexUtils.hexToByteArray(""); //TODO fill in private key here.

            com.github.wubuku.aptos.bean.TransactionPayload txnPayloadBean = new com.github.wubuku.aptos.bean.TransactionPayload();
            txnPayloadBean.setType(com.github.wubuku.aptos.bean.TransactionPayload.TYPE_ENTRY_FUNCTION_PAYLOAD);
            txnPayloadBean.setFunction("0x2b490841c230a31fe012f3b2a3f3d146316be073e599eb7d7e5074838073ef14::message::set_message");
            List<Object> transactionArgs = Collections.singletonList("hello world!");
            txnPayloadBean.setArguments(transactionArgs);
            //transactionPayload.setTypeArguments();
            EncodeSubmissionRequest encodeSubmissionRequest = NodeApiUtils.newEncodeSubmissionRequest(baseUrl,
                    accountAddress,
                    expirationTimestampSecs,
                    txnPayloadBean,
                    maxGasAmount,
                    null, null);
            String toSign = NodeApiUtils.encodeSubmission(baseUrl, encodeSubmissionRequest);
            System.out.println(toSign);
            //if (true) return;

            List<TypeTag> txnTypeArgs = Collections.emptyList();
            List<Bytes> trxArgs = Collections.singletonList(
                    encodeU8VectorArgument(Bytes.valueOf("hello world!".getBytes(StandardCharsets.UTF_8)))
            );
            RawTransaction rawTransaction = newRawTransaction(
                    chainId,
                    accountAddress,
                    encodeSubmissionRequest.getSequenceNumber(),
                    encodeSubmissionRequest.getMaxGasAmount(),
                    encodeSubmissionRequest.getGasUnitPrice(),
                    encodeSubmissionRequest.getExpirationTimestampSecs(),
                    accountAddress, "message", "set_message",
                    txnTypeArgs, trxArgs);
            try {
                byte[] rawTxnToSign = rawTransactionBcsBytesToSign(rawTransaction);
                //System.out.println(HexUtils.byteArrayToHexWithPrefix(HashUtils.sha3Hash(rawTxnToSign)));
                String rawTxnToSignHex = HexUtils.byteArrayToHexWithPrefix(rawTxnToSign);
                System.out.println(rawTxnToSignHex);
                if (toSign.equals(rawTxnToSignHex)) {
                    System.out.println("Node encoded toSign equals rawTxnToSignHex");
                } else {
                    System.out.println("Node encoded toSign not equals rawTxnToSignHex");
                    System.out.println(toSign);
                    System.out.println(rawTxnToSignHex);
                    throw new RuntimeException("Node encoded toSign not equals rawTxnToSignHex");
                }
                //System.out.println(HexUtils.byteArrayToHexWithPrefix(HashUtils.sha3Hash(rawTxnToSign)));
            } catch (SerializationError e) {
                throw new RuntimeException(e);
            }
            //if (true) return;
            byte[] signature = SignatureUtils.ed25519Sign(privateKey, HexUtils.hexToByteArray(toSign));

            boolean submitBcsTxn = true;
            com.github.wubuku.aptos.bean.Transaction submitTransactionResult;
            if (submitBcsTxn) {
                // ///////////////////////// Simulate transaction //////////////////////////
                SignedUserTransaction signedTransactionToSimulate = newSignedUserTransactionToSimulate(rawTransaction, publicKey);
                try {
                    List<com.github.wubuku.aptos.bean.Transaction> simulatedTransactions = NodeApiUtils.simulateBcsTransaction(baseUrl,
                            signedTransactionToSimulate, null, null);
                    //System.out.println(simulatedTransactions);
                    System.out.println("Simulated transaction: " + simulatedTransactions.get(0));
                } catch (SerializationError e) {
                    throw new RuntimeException(e);
                }
                // /////////////////////////////////////////////////////////////////////////
                SignedUserTransaction signedTransaction = newSignedUserTransaction(rawTransaction, publicKey, signature);
                try {
                    System.out.println("Client got transaction_hash: " + HexUtils.byteArrayToHexWithPrefix(
                            getTransactionHash(signedTransaction)));
                    // //////////////// Test submit batch transactions ///////////////////////
                    if (false) {
                        TransactionsBatchSubmissionResult batchSubmissionResult =
                                NodeApiUtils.submitBatchBcsTransactions(baseUrl, Collections.singletonList(signedTransaction));
                        System.out.println("Client got batch_submission_result: " + batchSubmissionResult);
                        if (true) return;
                    }
                    submitTransactionResult = NodeApiUtils.submitBcsTransaction(baseUrl, signedTransaction);
                } catch (SerializationError e) {
                    throw new RuntimeException(e);
                }
            } else {
                Signature s = new Signature();
                s.setType(Signature.TYPE_ED25519_SIGNATURE);
                s.setPublicKey(HexUtils.byteArrayToHexWithPrefix(publicKey));
                s.setSignature(HexUtils.byteArrayToHexWithPrefix(signature));
                SubmitTransactionRequest submitTransactionRequest = NodeApiUtils.toSubmitTransactionRequest(encodeSubmissionRequest);
                submitTransactionRequest.setSignature(s);
                System.out.println(submitTransactionRequest);
                if (false) {
                    // ////////////////////// Simulate transaction //////////////////////
                    submitTransactionRequest.getSignature().setSignature(
                            HexUtils.byteArrayToHexWithPrefix(TransactionUtils.ZERO_PADDED_SIGNATURE));
                    List<com.github.wubuku.aptos.bean.Transaction> simulatedTransactions = NodeApiUtils.simulateTransaction(baseUrl, submitTransactionRequest, true, true);
                    System.out.println(simulatedTransactions.get(0));
                    submitTransactionResult = simulatedTransactions.get(0);
                    // //////////////////////////////////////////////////////////////////
                } else {
                    if (false) {
                        // ///////// Test submit batch transactions ////////
                        TransactionsBatchSubmissionResult transactionsBatchSubmissionResult = NodeApiUtils.submitBatchTransactions(baseUrl, Collections.singletonList(submitTransactionRequest));
                        System.out.println(transactionsBatchSubmissionResult);
                        if (true) return;
                    }
                    // /////////////////////////////////////////////////
                    submitTransactionResult = NodeApiUtils.submitTransaction(baseUrl, submitTransactionRequest);
                }
            }
            System.out.println(submitTransactionResult);
            NodeApiUtils.waitForTransaction(baseUrl, submitTransactionResult.getHash());
            Transaction transaction = NodeApiUtils.getTransactionByHash(baseUrl, submitTransactionResult.getHash());
            System.out.println(transaction);
            System.out.println(transaction.getSuccess());
            System.out.println(transaction.getVmStatus());
            //        List<Event<?>> events_0 = NodeApiUtils.getEvents(baseUrl, accountAddress, eventHandleStruct, eventHandleFieldName, null, null);
            //        System.out.println(events_0);
        }


//        String eventHandleStruct_3 = "0x2b490841c230a31fe012f3b2a3f3d146316be073e599eb7d7e5074838073ef14::hello_table::EventStore";
//        String eventHandleFieldName_3 = "add_liquidity_event_handle";
//        List<Event<AddLiquidityEvent>> events_3 = NodeApiUtils.getEventsByEventHandle(baseUrl, accountAddress,
//                eventHandleStruct_3, eventHandleFieldName_3, AddLiquidityEvent.class, null, null);
//        System.out.println(events_3);
//        if (true) return;

        List<Event> events_1 = NodeApiUtils.getEventsByEventHandle(baseUrl, accountAddress, eventHandleStruct, eventHandleFieldName, null, null);
        System.out.println(events_1);
        events_1.forEach(event -> {
            //java.util.LinkedHashMap cannot be cast to dev.aptos.bean.EventTests$HelloBlockchainMessageChangeEvent!
            //System.out.println(event.getData().getClass());
            System.out.println(event.getData().getClass());
        });
        List<Event<HelloBlockchainMessageChangeEvent>> events_2 = NodeApiUtils.getEventsByEventHandle(baseUrl, accountAddress, eventHandleStruct, eventHandleFieldName, HelloBlockchainMessageChangeEvent.class, 1L, 1);
        System.out.println(events_2);
//        String creationNumber = "4";
//        List<Event<HelloBlockchainMessageChangeEvent>> events_3 = NodeApiUtils.getEventsByCreationNumber(baseUrl, accountAddress, creationNumber, HelloBlockchainMessageChangeEvent.class, 0L, 1);
//        System.out.println(events_3);
        //if (true) return;

        Block block = NodeApiUtils.getBlockByHeight(baseUrl, blockHeight, true);
        System.out.println(block);
        Block block2 = NodeApiUtils.getBlockByVersion(baseUrl, ledgerVersion, true);
        System.out.println(block2);
        //if (true) return;

        GasEstimation gasEstimation = estimateGasPrice(baseUrl);
        System.out.println(gasEstimation);
        //if (true) return;

//        Transaction transaction2 = NodeApiUtils.getTransactionByVersion(baseUrl, "1");
//        System.out.println(transaction2);
//        //if (true) return;

        Account account = NodeApiUtils.getAccount(baseUrl, accountAddress);
        System.out.println(account);
        //if (true) return;
        List<MoveModuleBytecode> moveModuleBytecodeList = NodeApiUtils.getAccountModules(baseUrl, accountAddress, null);
        System.out.println(moveModuleBytecodeList);
        //if (true) return;

        MoveModuleBytecode moveModuleBytecode = NodeApiUtils.getAccountModule(baseUrl, accountAddress, "message", null);
        System.out.println(moveModuleBytecode);
        //if (true) return;

//        List<Event<HelloBlockchainMessageChangeEvent>> eventList = NodeApiUtils.getEvents(baseUrl,
//                "0x04000000000000002b490841c230a31fe012f3b2a3f3d146316be073e599eb7d7e5074838073ef14",
//                HelloBlockchainMessageChangeEvent.class, null, null);
//        System.out.println(eventList);
//        //if (true) return;

        List<AccountResource> resources = NodeApiUtils.getAccountResources(baseUrl, accountAddress, null);
        System.out.println(resources);
        //if (true) return;

//        String hex = NodeApiUtils.getTableItem(baseUrl,
//                "0xb0239bb1d99e33fd9897f219b9767fd68b7b486f1fda4628765ab91e3851b364",
//                "vector<u8>", "vector<u8>",
//                HexUtils.byteArrayToHexWithPrefix("hello".getBytes()), String.class, null);
//        System.out.println(hex);
//        System.out.println(new String(HexUtils.hexToByteArray(hex)));
//        //if (true) return;

//        AccountResource<TestTableHolder> resource = NodeApiUtils.getAccountResource(baseUrl, accountAddress,
//                "0x2b490841c230a31fe012f3b2a3f3d146316be073e599eb7d7e5074838073ef14::hello_table::TableHolder",
//                TestTableHolder.class, null);
//        System.out.println(resource.getData().getTable().getHandle());
//        //if (true) return;

        System.out.println("Seem all Ok.");
    }

    private static void mobiusQueryTest_1(String aptosDevnetApiBaseUrl) throws IOException {
        String senderAddress = "0xbebaf664c81aa143a87105a5144cc8c0f9ee6b222adb7b2d2a5265ec0ae71f4e";
        byte[] publicKey = HexUtils.hexToByteArray("0x8a22072b1f2161052ead92fb03fc61354d189974cac300065fa237a16bf96e0c");
        TypeTag txnTypeTag_PT = toTypeTag("0xbebaf664c81aa143a87105a5144cc8c0f9ee6b222adb7b2d2a5265ec0ae71f4e"
                + "::" + "management"
                + "::" + "StandardPosition"
        );
        String moduleAddress = "0xbebaf664c81aa143a87105a5144cc8c0f9ee6b222adb7b2d2a5265ec0ae71f4e";
        String moduleName = "query";//"treasury";
        String functionName = "get_current_rate_list";
        List<TypeTag> txnTypeArgs = Collections.singletonList(txnTypeTag_PT);
        List<Bytes> trxArgs = Collections.emptyList();
        RawTransaction rawTransaction = NodeApiUtils.newRawTransaction(aptosDevnetApiBaseUrl, senderAddress,
                moduleAddress, moduleName, functionName, DEFAULT_MAX_GAS_AMOUNT,
                txnTypeArgs, trxArgs);
        SignedUserTransaction signedUserTransaction = TransactionUtils.newSignedUserTransactionToSimulate(rawTransaction, publicKey);
        try {
            System.out.println(moduleAddress + "::" + moduleName + "::" + functionName);
            List<Transaction> result = NodeApiUtils.simulateBcsTransaction(aptosDevnetApiBaseUrl, signedUserTransaction, false, false);
            System.out.println(result);
            String json = new ObjectMapper().writeValueAsString(result);
            System.out.println(json);
        } catch (SerializationError e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        // ////////////////
        TypeTag txnTypeTag_METH = toTypeTag("0xbebaf664c81aa143a87105a5144cc8c0f9ee6b222adb7b2d2a5265ec0ae71f4e"
                + "::" + "METH" + "::" + "METH"
        );
        moduleAddress = "0xbebaf664c81aa143a87105a5144cc8c0f9ee6b222adb7b2d2a5265ec0ae71f4e";
        moduleName = "query";
        functionName = "get_current_to_usd_value";
        txnTypeArgs = Collections.singletonList(txnTypeTag_METH);
        trxArgs = Collections.singletonList(encodeU128Argument(BigInteger.valueOf(1000000000)));
        rawTransaction = NodeApiUtils.newRawTransaction(aptosDevnetApiBaseUrl, senderAddress,
                moduleAddress, moduleName, functionName, DEFAULT_MAX_GAS_AMOUNT,
                txnTypeArgs, trxArgs);
        signedUserTransaction = TransactionUtils.newSignedUserTransactionToSimulate(rawTransaction, publicKey);
        try {
            System.out.println(moduleAddress + "::" + moduleName + "::" + functionName);
            List<Transaction> result = NodeApiUtils.simulateBcsTransaction(aptosDevnetApiBaseUrl, signedUserTransaction, false, false);
            System.out.println(result);
            String json = new ObjectMapper().writeValueAsString(result);
            System.out.println(json);
        } catch (SerializationError e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }

        moduleAddress = "0xbebaf664c81aa143a87105a5144cc8c0f9ee6b222adb7b2d2a5265ec0ae71f4e";
        moduleName = "query";
        functionName = "get_current_to_usd_price_list";
        txnTypeArgs = Collections.emptyList();
        trxArgs = Collections.emptyList();
        rawTransaction = NodeApiUtils.newRawTransaction(aptosDevnetApiBaseUrl, senderAddress,
                moduleAddress, moduleName, functionName, DEFAULT_MAX_GAS_AMOUNT,
                txnTypeArgs, trxArgs);
        signedUserTransaction = TransactionUtils.newSignedUserTransactionToSimulate(rawTransaction, publicKey);
        try {
            System.out.println(moduleAddress + "::" + moduleName + "::" + functionName);
            List<Transaction> result = NodeApiUtils.simulateBcsTransaction(aptosDevnetApiBaseUrl, signedUserTransaction, false, false);
            System.out.println(result);
            String json = new ObjectMapper().writeValueAsString(result);
            System.out.println(json);
        } catch (SerializationError e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }

        moduleAddress = "0xbebaf664c81aa143a87105a5144cc8c0f9ee6b222adb7b2d2a5265ec0ae71f4e";
        moduleName = "query";
        functionName = "get_current_assets_overview";
        txnTypeArgs = Collections.singletonList(txnTypeTag_PT);
        trxArgs = Collections.emptyList();
        rawTransaction = NodeApiUtils.newRawTransaction(aptosDevnetApiBaseUrl, senderAddress,
                moduleAddress, moduleName, functionName, DEFAULT_MAX_GAS_AMOUNT,
                txnTypeArgs, trxArgs);
        signedUserTransaction = TransactionUtils.newSignedUserTransactionToSimulate(rawTransaction, publicKey);
        try {
            System.out.println(moduleAddress + "::" + moduleName + "::" + functionName);
            List<Transaction> result = NodeApiUtils.simulateBcsTransaction(aptosDevnetApiBaseUrl, signedUserTransaction, false, false);
            System.out.println(result);
            String json = new ObjectMapper().writeValueAsString(result);
            System.out.println(json);
        } catch (SerializationError e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }

        moduleAddress = "0xbebaf664c81aa143a87105a5144cc8c0f9ee6b222adb7b2d2a5265ec0ae71f4e";
        moduleName = "query";
        functionName = "get_current_borrowable_amounts_list";
        txnTypeArgs = Collections.singletonList(txnTypeTag_PT);
        trxArgs = Collections.singletonList(encodeAccountAddressArgument(AccountAddress.valueOf(
                HexUtils.hexToAccountAddressBytes("0xbebaf664c81aa143a87105a5144cc8c0f9ee6b222adb7b2d2a5265ec0ae71f4e")
        )));
        rawTransaction = NodeApiUtils.newRawTransaction(aptosDevnetApiBaseUrl, senderAddress,
                moduleAddress, moduleName, functionName, DEFAULT_MAX_GAS_AMOUNT,
                txnTypeArgs, trxArgs);
        signedUserTransaction = TransactionUtils.newSignedUserTransactionToSimulate(rawTransaction, publicKey);
        try {
            System.out.println(moduleAddress + "::" + moduleName + "::" + functionName);
            List<Transaction> result = NodeApiUtils.simulateBcsTransaction(aptosDevnetApiBaseUrl, signedUserTransaction, false, false);
            System.out.println(result);
            String json = new ObjectMapper().writeValueAsString(result);
            System.out.println(json);
        } catch (SerializationError e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    private static void nftTest_1(String aptosDevnetApiBaseUrl) throws IOException {
        //String tokenCollectionDataTableHandle = "0x670f034a262b791164fd95c774b53f9b8333e34ae01fb00a2ea1083d0d26ca15";
        String tokenCollectionName = "Alice's";
        TokenCollectionData tokenCollectionData = TokenUtils.getCollectionData(aptosDevnetApiBaseUrl,
                "0xbe6b0076a63c7dc86e033dcbad89b1882c45fdd9a3a8d421aca86e969d4d9397",
                tokenCollectionName);
        System.out.println(tokenCollectionData);

        String tokenName = "Alice's first token";
        TokenData tokenData = TokenUtils.getTokenData(aptosDevnetApiBaseUrl,
                "0xbe6b0076a63c7dc86e033dcbad89b1882c45fdd9a3a8d421aca86e969d4d9397",
                tokenCollectionName, tokenName);
        System.out.println(tokenData);

        Token token_1 = TokenUtils.getToken(aptosDevnetApiBaseUrl,
                "0xbe6b0076a63c7dc86e033dcbad89b1882c45fdd9a3a8d421aca86e969d4d9397",
                tokenCollectionName, tokenName, null);
        System.out.println(token_1);

        Token token_2 = TokenUtils.getTokenForAccount(aptosDevnetApiBaseUrl,
                "0x62c18bb90ca44c738f7236ff6a543331f905c3ddd8a598b50f30ea19f848b7ec",
                //new TokenId(new TokenDataId(
                "0xbe6b0076a63c7dc86e033dcbad89b1882c45fdd9a3a8d421aca86e969d4d9397",
                tokenCollectionName,
                tokenName,//),
                "0"
        );
        System.out.println(token_2);
    }


    private static void testFormatStructTags() {
        String a1 = StructTagUtils.format(StructTagUtils.parseStructTag(
                "0x00101::coin::CoinInfo<" +
                        "0x1111::coin::LP<0x1::coin::CoinInfo<u8, u64>, u8, vector<vector<u8>>,0x99911::test::D>" +
                        ">"));
        System.out.println(a1);
        //if (true) return;
        String a = StructTagUtils.format(StructTagUtils.parseStructTag(
                "0xc3dbe4f07390f05b19ccfc083fc6aa5bc5d75621d131fc49557c8f4bbc11716::test::S"));
        System.out.println(a);
        a = StructTagUtils.format(StructTagUtils.parseStructTag(
                "0x0c3dbe4f07390f05b19ccfc083fc6aa5bc5d75621d131fc49557c8f4bbc11716::test::Foo<u8>"));
        System.out.println(a);
        a = StructTagUtils.format(StructTagUtils.parseStructTag(
                "0x0c3dbe4f07390f05b19ccfc083fc6aa5bc5d75621d131fc49557c8f4bbc11716::test::Foo<0x00101::coin::CoinInfo>"));
        System.out.println(a);
        a = StructTagUtils.format(StructTagUtils.parseStructTag(
                "0x01::coin::CoinInfo<0x1111::coin::LP<u8, u64,u128>>"));
        System.out.println(a);
        a = StructTagUtils.format(StructTagUtils.parseStructTag(
                "0x01::coin::CoinInfo<0x1111::coin::LP<0x00101::coin::CoinInfo, 0x00101::coin::CoinInfo,0x00101::coin::CoinInfo>>"));
        System.out.println(a);
        a = StructTagUtils.format(StructTagUtils.parseStructTag(
                "0x0101::coin::CoinInfo<0x1111::coin::LP<u8,0x99911::test::D,u128>>"));
        System.out.println(a);
        a = StructTagUtils.format(StructTagUtils.parseStructTag(
                "0x0101::coin::CoinInfo<0x1111::coin::LP<0x00101::coin::CoinInfo,0x99911::test::D,0x00101::coin::CoinInfo<0x00101::coin::CoinInfo>>>"));
        System.out.println(a);
        a = StructTagUtils.format(StructTagUtils.parseStructTag(
                "0x00101::coin::CoinInfo<0x1111::coin::LP<vector<u8>,0x99911::test::D>>"));
        System.out.println(a);
        a = StructTagUtils.format(StructTagUtils.parseStructTag(
                "0x00101::coin::CoinInfo<" +
                        "0x1111::coin::LP<0x00101::coin::CoinInfo<0x00101::coin::CoinInfo>,0x99911::test::D>" +
                        ">"));
        System.out.println(a);

//        String formattedAddress = NodeApiUtils.formatPathSegmentAccountAddress(
//                "0xc3dbe4f07390f05b19ccfc083fc6aa5bc5d75621d131fc49557c8f4bbc11716");
//        System.out.println(formattedAddress);
//        TypeInfo typeInfo = StructTagUtils.toTypeInfo(StructTagUtils.parseStructTag("0x1::aptos_coin::AptosCoin"));
//        System.out.println(typeInfo);
    }

    public static class TestTableHolder {
        private Table table;

        public Table getTable() {
            return table;
        }

        public void setTable(Table table) {
            this.table = table;
        }

        @Override
        public String toString() {
            return "TestTableHolder{" +
                    "table=" + table +
                    '}';
        }
    }

    public static class HelloBlockchainMessageChangeEvent {
        public String from_message;
        public String to_message;

        @Override
        public String toString() {
            return "HelloBlockchainMessageChangeEvent{" +
                    "from_message='" + from_message + '\'' +
                    ", to_message='" + to_message + '\'' +
                    '}';
        }
    }
}
