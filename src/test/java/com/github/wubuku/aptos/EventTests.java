package com.github.wubuku.aptos;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.wubuku.aptos.bean.AccountResource;
import com.github.wubuku.aptos.bean.Event;
import com.github.wubuku.aptos.utils.NodeApiUtils;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class EventTests {
    @Test
    void testGetEvents_1() throws IOException {

        String aptosDevnetApiBaseUrl = "https://fullnode.devnet.aptoslabs.com/v1";
        String baseUrl = aptosDevnetApiBaseUrl;
        String accountAddress = "0x427a45134e0bbfc962c576a7804eb6b3f1ad2d3d885f6bee6f27d91aca96df5b";
        String eventHandleStruct = "0x2239450816c09cef0202c090ec15f648a33e3fff0209167cad1ef6830b1d5d1f::day_summary::Events";
        String eventHandleFieldName = "day_summary_created_handle";
        Long start = 0L;//0L;
        Integer limit = 1;
        List<Event> events_1 = NodeApiUtils.getEventsByEventHandle(baseUrl, accountAddress,
                eventHandleStruct, eventHandleFieldName,
                start, limit);
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
        String accountAddress = "0x427a45134e0bbfc962c576a7804eb6b3f1ad2d3d885f6bee6f27d91aca96df5b";
        AccountResource<Map> accountResource = NodeApiUtils.getAccountResource(baseUrl, accountAddress,
                "0x2239450816c09cef0202c090ec15f648a33e3fff0209167cad1ef6830b1d5d1f::day_summary::Tables", Map.class, null);
        System.out.println(toJson(accountResource));
        String handle = (String) ((Map<String, Object>) accountResource.getData().get("day_summary_table")).get("handle");
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
                        "        \"number\": 25,\n" +
                        "        \"time_zone\": \"Beijing\"\n" +
                        "      },\n" +
                        "      \"description\": \"description\",\n" +
                        "      \"meta_data\": \"0x010203\",\n" +
                        "      \"optional_data\": {\n" +
                        "        \"vec\": [\n" +
                        "          \"\\u0012optional_data_item\"\n" +
                        "        ]\n" +
                        "      }",
                Map.class
        );
        Map v = NodeApiUtils.getTableItem(baseUrl, handle,
                "0x2239450816c09cef0202c090ec15f648a33e3fff0209167cad1ef6830b1d5d1f::day::Day",
                "0x2239450816c09cef0202c090ec15f648a33e3fff0209167cad1ef6830b1d5d1f::day_summary::DaySummary",
                key, Map.class, null);
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
}
