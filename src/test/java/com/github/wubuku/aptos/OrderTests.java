package com.github.wubuku.aptos;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.wubuku.aptos.bean.AccountResource;
import com.github.wubuku.aptos.utils.HexUtils;
import com.github.wubuku.aptos.utils.NodeApiClient;
import com.github.wubuku.aptos.utils.NodeApiUtils;
import com.novi.bcs.BcsSerializer;
import com.novi.serde.SerializationError;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Map;

public class OrderTests {
    @Test
    void testGetOrder_1() throws IOException, SerializationError {
        String aptosDevnetApiBaseUrl = "https://fullnode.devnet.aptoslabs.com/v1";
        String baseUrl = aptosDevnetApiBaseUrl;
        String resourceAccountAddress = "0x427a45134e0bbfc962c576a7804eb6b3f1ad2d3d885f6bee6f27d91aca96df5b";
        AccountResource<Map> accountResource = NodeApiUtils.getAccountResource(baseUrl, resourceAccountAddress,
                "0x2239450816c09cef0202c090ec15f648a33e3fff0209167cad1ef6830b1d5d1f::order::Tables", Map.class, null);
        System.out.println(accountResource);
        String handle = (String) ((Map<String, Object>) accountResource.getData().get("order_table")).get("handle");
        System.out.println("# order table handle:");
        System.out.println(handle);
        String key = "test_order_1";
        Map v = NodeApiUtils.getTableItem(baseUrl, handle,
                "0x1::string::String",
                "0x2239450816c09cef0202c090ec15f648a33e3fff0209167cad1ef6830b1d5d1f::order::Order",
                key, Map.class, null);
        System.out.printf("# get order by order_id %s:%n", key);
        System.out.println(toJson(v));

        BcsSerializer s = new BcsSerializer();
        s.serialize_str(key);
        byte[] keyBytes = s.get_bytes();
        System.out.println("# get order by raw key:");
        System.out.println(HexUtils.byteArrayToHexWithPrefix(keyBytes));
        byte[] bcsData = new NodeApiClient(baseUrl).getRawTableItem(handle, keyBytes, null);
        System.out.println("# get order raw data:");
        System.out.println(HexUtils.byteArrayToHexWithPrefix(bcsData));
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
