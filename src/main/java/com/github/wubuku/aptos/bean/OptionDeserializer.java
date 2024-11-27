package com.github.wubuku.aptos.bean;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.deser.ContextualDeserializer;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.TextNode;

import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class OptionDeserializer extends JsonDeserializer<Option<?>> implements ContextualDeserializer {
    private JavaType valueType;

    public OptionDeserializer() {
        // Default constructor required by Jackson
    }

    private OptionDeserializer(JavaType valueType) {
        this.valueType = valueType;
    }

    @Override
    public JsonDeserializer<?> createContextual(DeserializationContext ctxt, BeanProperty property)
            throws JsonMappingException {
        JavaType wrapperType = (property == null) ?
                ctxt.getContextualType() : property.getType();
        JavaType valueType = wrapperType.containedType(0);
        return new OptionDeserializer(valueType);
    }

    @Override
    public Option<?> deserialize(JsonParser p, DeserializationContext ctxt)
            throws IOException {
        JsonNode node = p.getCodec().readTree(p);
        JsonNode vecNode = node.get("vec");

        // Handle special case: "0x" represents empty Option for numbers
        if (vecNode instanceof TextNode && "0x".equals(vecNode.asText())) {
            return new Option<>(Collections.emptyList());
        }

        // Handle empty array case
        if (vecNode == null || (vecNode instanceof ArrayNode && vecNode.size() == 0)) {
            return new Option<>(Collections.emptyList());
        }

        // Handle non-empty array cases
        if (vecNode instanceof ArrayNode) {
            ArrayNode arrayNode = (ArrayNode) vecNode;
            List<Object> result = new ArrayList<>();

            for (JsonNode elementNode : arrayNode) {
                Object value;
                // Special handling for hex strings that represent numbers
                if (elementNode.isTextual() && 
                    elementNode.asText().startsWith("0x") && 
                    (Number.class.isAssignableFrom(valueType.getRawClass()) || 
                     valueType.getRawClass() == BigInteger.class)) {
                    String hexValue = elementNode.asText().substring(2);
                    try {
                        if (valueType.getRawClass() == BigInteger.class) {
                            value = new BigInteger(hexValue, 16);
                        } else if (valueType.getRawClass() == Long.class) {
                            value = Long.parseLong(hexValue, 16);
                        } else if (valueType.getRawClass() == Integer.class) {
                            value = Integer.parseInt(hexValue, 16);
                        } else if (valueType.getRawClass() == Byte.class) {
                            value = Byte.parseByte(hexValue, 16);
                        } else {
                            // For other number types, fallback to default Jackson behavior
                            value = p.getCodec().treeToValue(elementNode, valueType.getRawClass());
                        }
                    } catch (NumberFormatException e) {
                        // If hex parsing fails, try default Jackson behavior
                        value = p.getCodec().treeToValue(elementNode, valueType.getRawClass());
                    }
                } else if (elementNode.isTextual() && valueType.getRawClass() == BigInteger.class) {
                    // Handle BigInteger without "0x" prefix
                    try {
                        value = new BigInteger(elementNode.asText());
                    } catch (NumberFormatException e) {
                        // If parsing fails, try default Jackson behavior
                        value = p.getCodec().treeToValue(elementNode, valueType.getRawClass());
                    }
                } else {
                    // Use Jackson's default behavior for all other cases
                    value = p.getCodec().treeToValue(elementNode, valueType.getRawClass());
                }
                result.add(value);
            }
            return new Option<>(result);
        }

        throw new IOException("Unexpected JSON format for Option type");
    }
} 