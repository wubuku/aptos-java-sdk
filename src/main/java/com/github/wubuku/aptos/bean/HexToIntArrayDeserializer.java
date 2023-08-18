package com.github.wubuku.aptos.bean;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.github.wubuku.aptos.utils.HexUtils;

import java.io.IOException;

public class HexToIntArrayDeserializer extends JsonDeserializer<Integer[]> {
    @Override
    public Integer[] deserialize(JsonParser jsonParser, DeserializationContext ctx) throws IOException {
        JsonToken currentToken = jsonParser.getCurrentToken();
        if (JsonToken.VALUE_STRING.equals(currentToken)) {
            String hexString = jsonParser.getText();
            byte[] bytes = HexUtils.hexToByteArray(hexString);
            Integer[] result = new Integer[bytes.length];
            for (int i = 0; i < bytes.length; i++) {
                result[i] = (int) bytes[i];
            }
            return result;
        } else if (JsonToken.VALUE_NULL.equals(currentToken)) {
            return null;
        } else if (JsonToken.START_ARRAY.equals(currentToken)) {
            return jsonParser.readValueAs(Integer[].class);
            /*
            List<Integer> list = new ArrayList<>();
            while (true) {
                JsonToken n = jsonParser.nextToken();
                if (!n.isNumeric()) break;
                list.add(jsonParser.readValueAs(Integer.class));
            }
            if (!JsonToken.END_ARRAY.equals(jsonParser.currentToken()))
                throw new InvalidFormatException(jsonParser, "HexStringToIntArrayDeserializer.deserialize() error.", jsonParser.currentToken(), HexStringToIntArrayDeserializer.class);
            return list.toArray(new Integer[0]);
             */
        }
        return null;
    }
}
