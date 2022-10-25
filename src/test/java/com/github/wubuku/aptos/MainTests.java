package com.github.wubuku.aptos;

import com.github.wubuku.aptos.bean.Transaction;
import com.github.wubuku.aptos.bean.*;
import com.github.wubuku.aptos.types.*;
import com.github.wubuku.aptos.utils.HexUtils;
import com.github.wubuku.aptos.utils.NodeApiUtils;
import com.github.wubuku.aptos.utils.SignatureUtils;
import com.github.wubuku.aptos.utils.StructTagUtils;
import com.novi.bcs.BcsSerializer;
import com.novi.serde.Bytes;
import com.novi.serde.SerializationError;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;

public class MainTests {

    public static void main(String[] args) throws IOException {
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
            long maxGasAmount = 400000L;
            long expirationTimestampSecs = System.currentTimeMillis() / 1000L + 600;

            byte[] publicKey = HexUtils.hexToByteArray("0xa76e9dd1a2d9101de47e69e52e0232060b95cd7d80265d61c3fa25e406389b75");
            byte[] privateKey = HexUtils.hexToByteArray("");

            com.github.wubuku.aptos.bean.TransactionPayload transactionPayload = new com.github.wubuku.aptos.bean.TransactionPayload();
            transactionPayload.setType(com.github.wubuku.aptos.bean.TransactionPayload.TYPE_ENTRY_FUNCTION_PAYLOAD);
            transactionPayload.setFunction("0x2b490841c230a31fe012f3b2a3f3d146316be073e599eb7d7e5074838073ef14::message::set_message");
            List<Object> transactionArgs = Collections.singletonList("hello world!");
            transactionPayload.setArguments(transactionArgs);
            //transactionPayload.setTypeArguments();
            EncodeSubmissionRequest encodeSubmissionRequest = NodeApiUtils.newEncodeSubmissionRequest(baseUrl, accountAddress,
                    expirationTimestampSecs, transactionPayload, maxGasAmount, null, null);
            String toSign = NodeApiUtils.encodeSubmission(baseUrl, encodeSubmissionRequest);
            System.out.println(toSign);
            //if (true) return;

            ModuleId module = new ModuleId(AccountAddress.valueOf(HexUtils.hexToByteArray(accountAddress)), new Identifier("message"));
            Identifier function = new Identifier("set_message");
            List<TypeTag> typeArgs = Collections.emptyList();
            List<Bytes> set_message_args = Collections.singletonList(encode_u8vector_argument(Bytes.valueOf("hello world!".getBytes(StandardCharsets.UTF_8))));
            EntryFunction entryFunction = new EntryFunction(module, function, typeArgs, set_message_args);
            com.github.wubuku.aptos.types.TransactionPayload.EntryFunction typesTransactionPayload = new com.github.wubuku.aptos.types.TransactionPayload.EntryFunction(entryFunction);
            RawTransaction rawTransaction = new RawTransaction(
                    AccountAddress.valueOf(HexUtils.hexToByteArray(accountAddress)),
                    Long.parseLong(encodeSubmissionRequest.getSequenceNumber()),
                    typesTransactionPayload,
                    Long.parseLong(encodeSubmissionRequest.getMaxGasAmount()),
                    Long.parseLong(encodeSubmissionRequest.getGasUnitPrice()),
                    Long.parseLong(encodeSubmissionRequest.getExpirationTimestampSecs()),
                    chainId
            );
            try {
                byte[] rawTxnToSign = NodeApiUtils.rawTransactionToSign(rawTransaction.bcsSerialize());
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
                SignedUserTransaction signedTransactionToSimulate = new SignedUserTransaction(rawTransaction,
                        new TransactionAuthenticator.Ed25519(
                                new Ed25519PublicKey(Bytes.valueOf(publicKey)),
                                new Ed25519Signature(Bytes.valueOf(NodeApiUtils.ZERO_PADDED_SIGNATURE))
                        ));
                try {
                    List<com.github.wubuku.aptos.bean.Transaction> simulatedTransactions = NodeApiUtils.simulateBcsTransaction(baseUrl,
                            signedTransactionToSimulate, null, null);
                    //System.out.println(simulatedTransactions);
                    System.out.println("Simulated transaction: " + simulatedTransactions.get(0));
                } catch (SerializationError e) {
                    throw new RuntimeException(e);
                }
                // /////////////////////////////////////////////////////////////////////////
                SignedUserTransaction signedTransaction = new SignedUserTransaction(rawTransaction,
                        new TransactionAuthenticator.Ed25519(
                                new Ed25519PublicKey(Bytes.valueOf(publicKey)),
                                new Ed25519Signature(Bytes.valueOf(signature))
                        ));
                try {
                    System.out.println("Client got transaction_hash: " + HexUtils.byteArrayToHexWithPrefix(
                            NodeApiUtils.getTransactionHash(signedTransaction)));
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
                    submitTransactionRequest.getSignature().setSignature(HexUtils.byteArrayToHexWithPrefix(NodeApiUtils.ZERO_PADDED_SIGNATURE));
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

        GasEstimation gasEstimation = NodeApiUtils.estimateGasPrice(baseUrl);
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

    private static Bytes encode_u8vector_argument(Bytes arg) {
        try {
            BcsSerializer s = new BcsSerializer();
            s.serialize_bytes(arg);
            return Bytes.valueOf(s.get_bytes());
        } catch (SerializationError e) {
            throw new IllegalArgumentException("Unable to serialize argument of type u8vector");
        }
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
