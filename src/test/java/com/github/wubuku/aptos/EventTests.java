package com.github.wubuku.aptos;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.wubuku.aptos.bean.AccountResource;
import com.github.wubuku.aptos.bean.AptosEventHandle;
import com.github.wubuku.aptos.bean.Event;
import com.github.wubuku.aptos.bean.SignerCapability;
import com.github.wubuku.aptos.tests.DaySummary;
import com.github.wubuku.aptos.tests.DaySummaryCreated;
import com.github.wubuku.aptos.utils.HexUtils;
import com.github.wubuku.aptos.utils.NodeApiClient;
import com.github.wubuku.aptos.utils.NodeApiUtils;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class EventTests {

    @Test
    void testGetResourceAccountAddress_1() throws IOException {
        String aptosDevnetApiBaseUrl = "https://fullnode.devnet.aptoslabs.com/v1";
        String baseUrl = aptosDevnetApiBaseUrl;
        String accountAddress = "0x2239450816c09cef0202c090ec15f648a33e3fff0209167cad1ef6830b1d5d1f";
        String resourceType = accountAddress + "::resource_account::ResourceAccount";
        AccountResource<TestResourceAccount> resource = NodeApiUtils.getAccountResource(baseUrl, accountAddress,
                resourceType, TestResourceAccount.class, null);
        System.out.println(resource);
        System.out.println(resource.getData().getCap().getAccount());

        byte[] bcsData = new NodeApiClient(baseUrl).getRawAccountResource(accountAddress, resourceType, null);
        System.out.println(HexUtils.byteArrayToHexWithPrefix(bcsData));
    }

    @Test
    void testGetEventHandle_1() throws IOException {
        String aptosDevnetApiBaseUrl = "https://fullnode.devnet.aptoslabs.com/v1";
        String baseUrl = aptosDevnetApiBaseUrl;
        String resourceAccountAddress = "0x427a45134e0bbfc962c576a7804eb6b3f1ad2d3d885f6bee6f27d91aca96df5b";
        String eventHandleStruct = "0x2239450816c09cef0202c090ec15f648a33e3fff0209167cad1ef6830b1d5d1f::day_summary::Events";
        //String eventHandleFieldName = "day_summary_created_handle";

        AccountResource<DaySummaryEvents> resource = NodeApiUtils.getAccountResource(baseUrl, resourceAccountAddress,
                eventHandleStruct, DaySummaryEvents.class, null);
        System.out.println(resource.getData().day_summary_created_handle.getGuid().getId().getAddr());
        System.out.println("Creation number:");
        System.out.println(resource.getData().day_summary_created_handle.getGuid().getId().getCreationNum());
    }

    @Test
    void testGetEvents_1() throws IOException {

        String aptosDevnetApiBaseUrl = "https://fullnode.devnet.aptoslabs.com/v1";
        String baseUrl = aptosDevnetApiBaseUrl;
        String resourceAccountAddress = "0x427a45134e0bbfc962c576a7804eb6b3f1ad2d3d885f6bee6f27d91aca96df5b";
        String eventHandleStruct = "0x2239450816c09cef0202c090ec15f648a33e3fff0209167cad1ef6830b1d5d1f::day_summary::Events";
        String eventHandleFieldName = "day_summary_created_handle";
        Long start = 0L;//0L;
        Integer limit = 1;
        List<Event<DaySummaryCreated>> events_1 = NodeApiUtils.getEventsByEventHandle(baseUrl, resourceAccountAddress,
                eventHandleStruct, eventHandleFieldName,
                DaySummaryCreated.class,
                start, limit
        );
        System.out.println(toJson(events_1));
        //System.out.println(events_1);
        events_1.forEach(event -> {
            //java.util.LinkedHashMap cannot be cast to dev.aptos.bean.EventTests$HelloBlockchainMessageChangeEvent!
            //System.out.println(event.getData().getClass());
            System.out.println(event.getData().getClass());
        });
    }

    @Test
    void testGetAccountResource_1() throws IOException {
        String aptosDevnetApiBaseUrl = "https://fullnode.devnet.aptoslabs.com/v1";
        String baseUrl = aptosDevnetApiBaseUrl;
        String resourceAccountAddress = "0x427a45134e0bbfc962c576a7804eb6b3f1ad2d3d885f6bee6f27d91aca96df5b";
        AccountResource<Map> accountResource = NodeApiUtils.getAccountResource(baseUrl, resourceAccountAddress,
                "0x2239450816c09cef0202c090ec15f648a33e3fff0209167cad1ef6830b1d5d1f::day_summary::Tables", Map.class, null);
        System.out.println(toJson(accountResource));
        String handle = (String) ((Map<String, Object>) accountResource.getData().get("day_summary_table")).get("handle");
        System.out.println("# day_summary_table handle:");
        System.out.println(handle);
        Map<String, Object> key = new ObjectMapper().readValue(
                "{\n" +
                        "        \"month\": {\n" +
                        "          \"is_leap\": false,\n" +
                        "          \"number\": 4,\n" +
                        "          \"year\": {\n" +
                        "            \"calendar\": \"ChineseLunar\",\n" +
                        "            \"number\": 2022\n" +
                        "          }\n" +
                        "        },\n" +
                        //"        \"number\": 25,\n" +
                        "        \"number\": 21,\n" +
                        "        \"time_zone\": \"Beijing\"\n" +
                        "}",
                Map.class
        );
        DaySummary v = NodeApiUtils.getTableItem(baseUrl, handle,
                "0x2239450816c09cef0202c090ec15f648a33e3fff0209167cad1ef6830b1d5d1f::day::Day",
                "0x2239450816c09cef0202c090ec15f648a33e3fff0209167cad1ef6830b1d5d1f::day_summary::DaySummary",
                key,
                DaySummary.class,//Map.class,
                null
        );
        System.out.println("# getTableItem:");
        System.out.println(toJson(v));
    }

    String toJson(Object obj) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    static class DaySummaryEvents {
        public AptosEventHandle day_summary_created_handle;

        @Override
        public String toString() {
            return "DaySummaryEvents{" +
                    "day_summary_created_handle=" + day_summary_created_handle +
                    '}';
        }
    }

    static class TestResourceAccount {
        private SignerCapability cap;

        public SignerCapability getCap() {
            return cap;
        }

        public void setCap(SignerCapability cap) {
            this.cap = cap;
        }

        @Override
        public String toString() {
            return "TestResourceAccount{" +
                    "cap=" + cap +
                    '}';
        }
    }
}
