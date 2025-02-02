package com.github.wubuku.aptos;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.wubuku.aptos.bean.AptosObject;
import com.github.wubuku.aptos.bean.Option;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class OptionDeserializerTests {
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    public void testEmptyOptionDeserialization() throws Exception {
        // Test empty option with "0x"
        String json1 = "{\"vec\":\"0x\"}";
        Option<Integer> option1 = objectMapper.readValue(json1, objectMapper.getTypeFactory()
                .constructParametricType(Option.class, Integer.class));
        assertTrue(option1.getVec().isEmpty());

        // Test empty option with empty array
        String json2 = "{\"vec\":[]}";
        Option<Integer> option2 = objectMapper.readValue(json2, objectMapper.getTypeFactory()
                .constructParametricType(Option.class, Integer.class));
        assertTrue(option2.getVec().isEmpty());
    }

    @Test
    public void testNonEmptyOptionDeserialization() throws Exception {
        // Test hex string number
        String json1 = "{\"vec\":[\"0x12\"]}";
        Option<Integer> option1 = objectMapper.readValue(json1, objectMapper.getTypeFactory()
                .constructParametricType(Option.class, Integer.class));
        assertFalse(option1.getVec().isEmpty());
        assertEquals(18, option1.getVec().get(0)); // 0x12 = 18 in decimal

        // Test decimal number
        String json2 = "{\"vec\":[18]}";
        Option<Integer> option2 = objectMapper.readValue(json2, objectMapper.getTypeFactory()
                .constructParametricType(Option.class, Integer.class));
        assertFalse(option2.getVec().isEmpty());
        assertEquals(18, option2.getVec().get(0));
    }

    @Test
    public void testLongValueOptionDeserialization() throws Exception {
        // Test large hex number
        String json1 = "{\"vec\":[\"0xFF\"]}";
        Option<Long> option1 = objectMapper.readValue(json1, objectMapper.getTypeFactory()
                .constructParametricType(Option.class, Long.class));
        assertFalse(option1.getVec().isEmpty());
        assertEquals(255L, option1.getVec().get(0));

        // Test large decimal number
        String json2 = "{\"vec\":[255]}";
        Option<Long> option2 = objectMapper.readValue(json2, objectMapper.getTypeFactory()
                .constructParametricType(Option.class, Long.class));
        assertFalse(option2.getVec().isEmpty());
        assertEquals(255L, option2.getVec().get(0));

        // Test very large number that requires long
        String json3 = "{\"vec\":[\"0xFFFFFFFF\"]}";
        Option<Long> option3 = objectMapper.readValue(json3, objectMapper.getTypeFactory()
                .constructParametricType(Option.class, Long.class));
        assertFalse(option3.getVec().isEmpty());
        assertEquals(4294967295L, option3.getVec().get(0));
    }

    @Test
    public void testStringOptionDeserialization() throws Exception {
        // Test simple string
        String json1 = "{\"vec\":[\"hello\"]}";
        Option<String> option1 = objectMapper.readValue(json1, objectMapper.getTypeFactory()
                .constructParametricType(Option.class, String.class));
        assertFalse(option1.getVec().isEmpty());
        assertEquals("hello", option1.getVec().get(0));

        // Test hex string that should remain as string
        String json2 = "{\"vec\":[\"0xhello\"]}";
        Option<String> option2 = objectMapper.readValue(json2, objectMapper.getTypeFactory()
                .constructParametricType(Option.class, String.class));
        assertFalse(option2.getVec().isEmpty());
        assertEquals("0xhello", option2.getVec().get(0));
    }

    @Test
    public void testComplexObjectOptionDeserialization() throws Exception {
        // Test with a complex object
        String json = "{\"vec\":[{\"name\":\"test\",\"value\":123}]}";
        Option<TestObject> option = objectMapper.readValue(json,
                objectMapper.getTypeFactory().constructParametricType(Option.class, TestObject.class));

        assertFalse(option.getVec().isEmpty());
        assertEquals("test", option.getVec().get(0).getName());
        assertEquals(123, option.getVec().get(0).getValue());
    }

    @Test
    public void testListOptionDeserialization() throws Exception {
        // Test with a list of strings
        String json = "{\"vec\":[[\"item1\", \"item2\"]]}";
        Option<List> option = objectMapper.readValue(json,
                objectMapper.getTypeFactory().constructParametricType(Option.class, List.class));

        assertFalse(option.getVec().isEmpty());
        List<?> list = option.getVec().get(0);
        assertEquals(2, list.size());
        assertEquals("item1", list.get(0));
        assertEquals("item2", list.get(1));
    }

    @Test
    public void testMultipleValuesOptionDeserialization() throws Exception {
        // Test option with multiple values
        String json = "{\"vec\":[\"value1\", \"value2\"]}";
        Option<String> option = objectMapper.readValue(json,
                objectMapper.getTypeFactory().constructParametricType(Option.class, String.class));

        assertEquals(2, option.getVec().size());
        assertEquals("value1", option.getVec().get(0));
        assertEquals("value2", option.getVec().get(1));
    }

    @Test
    public void testComplexCustomClassOptionDeserialization() throws Exception {
        String json = "{\"vec\":[{" +
                "\"address\":\"0x123\"," +
                "\"coins\":{\"vec\":[{\"value\":\"100\",\"denom\":\"APT\"}]}," +
                "\"numbers\":[1,2,3]," +
                "\"metadata\":{\"key1\":\"value1\",\"key2\":\"value2\"}," +
                "\"amount\":\"1000000000000000000\"" +
                "}]}";

        Option<ComplexCustomClass> option = objectMapper.readValue(json,
                objectMapper.getTypeFactory().constructParametricType(Option.class, ComplexCustomClass.class));

        assertFalse(option.getVec().isEmpty());
        ComplexCustomClass obj = option.getVec().get(0);

        // Verify address field
        assertEquals("0x123", obj.getAddress());

        // Verify nested Option field
        assertFalse(obj.getCoins().getVec().isEmpty());
        assertEquals("100", obj.getCoins().getVec().get(0).getValue());
        assertEquals("APT", obj.getCoins().getVec().get(0).getDenom());

        // Verify array field
        assertEquals(3, obj.getNumbers().size());
        assertEquals(1, obj.getNumbers().get(0));
        assertEquals(2, obj.getNumbers().get(1));
        assertEquals(3, obj.getNumbers().get(2));

        // Verify Map field
        assertEquals(2, obj.getMetadata().size());
        assertEquals("value1", obj.getMetadata().get("key1"));
        assertEquals("value2", obj.getMetadata().get("key2"));

        // Verify BigInteger field
        assertEquals(new BigInteger("1000000000000000000"), obj.getAmount());
    }

    @Test
    public void testBigIntegerOptionDeserialization() throws Exception {
        // Test decimal number
        String json1 = "{\"vec\":[\"1000000000000000000\"]}";
        Option<BigInteger> option1 = objectMapper.readValue(json1,
                objectMapper.getTypeFactory().constructParametricType(Option.class, BigInteger.class));
        assertFalse(option1.getVec().isEmpty());
        assertEquals(new BigInteger("1000000000000000000"), option1.getVec().get(0));

        // Test hex number
        String json2 = "{\"vec\":[\"0xFF\"]}";
        Option<BigInteger> option2 = objectMapper.readValue(json2,
                objectMapper.getTypeFactory().constructParametricType(Option.class, BigInteger.class));
        assertFalse(option2.getVec().isEmpty());
        assertEquals(new BigInteger("FF", 16), option2.getVec().get(0));

        // Test very large hex number
        String json3 = "{\"vec\":[\"0xFFFFFFFFFFFFFFFF\"]}";
        Option<BigInteger> option3 = objectMapper.readValue(json3,
                objectMapper.getTypeFactory().constructParametricType(Option.class, BigInteger.class));
        assertFalse(option3.getVec().isEmpty());
        assertEquals(new BigInteger("FFFFFFFFFFFFFFFF", 16), option3.getVec().get(0));

        // Test numeric value
        String json4 = "{\"vec\":[1000000000000000000]}";
        Option<BigInteger> option4 = objectMapper.readValue(json4,
                objectMapper.getTypeFactory().constructParametricType(Option.class, BigInteger.class));
        assertFalse(option4.getVec().isEmpty());
        assertEquals(new BigInteger("1000000000000000000"), option4.getVec().get(0));
    }

    @Test
    public void testDecimalNumberOptionDeserialization() throws Exception {
        // Test decimal point number as string
        String json1 = "{\"vec\":[\"123.456\"]}";
        Option<BigDecimal> option1 = objectMapper.readValue(json1,
                objectMapper.getTypeFactory().constructParametricType(Option.class, BigDecimal.class));
        assertFalse(option1.getVec().isEmpty());
        assertEquals(new BigDecimal("123.456"), option1.getVec().get(0));

        // Test decimal point number as number
        String json2 = "{\"vec\":[123.456]}";
        Option<BigDecimal> option2 = objectMapper.readValue(json2,
                objectMapper.getTypeFactory().constructParametricType(Option.class, BigDecimal.class));
        assertFalse(option2.getVec().isEmpty());
        assertEquals(new BigDecimal("123.456"), option2.getVec().get(0));

        // Test scientific notation
        String json3 = "{\"vec\":[\"1.23456E+2\"]}";
        Option<BigDecimal> option3 = objectMapper.readValue(json3,
                objectMapper.getTypeFactory().constructParametricType(Option.class, BigDecimal.class));
        assertFalse(option3.getVec().isEmpty());
        assertEquals(new BigDecimal("123.456"), option3.getVec().get(0));

        // Test very precise decimal
        String json4 = "{\"vec\":[\"123.4567890123456789\"]}";
        Option<BigDecimal> option4 = objectMapper.readValue(json4,
                objectMapper.getTypeFactory().constructParametricType(Option.class, BigDecimal.class));
        assertFalse(option4.getVec().isEmpty());
        assertEquals(new BigDecimal("123.4567890123456789"), option4.getVec().get(0));

        // Test hex with decimal (should throw exception)
        String json5 = "{\"vec\":[\"0xFF.FF\"]}";
        assertThrows(IOException.class, () -> {
            objectMapper.readValue(json5,
                    objectMapper.getTypeFactory().constructParametricType(Option.class, BigDecimal.class));
        });
    }

    @Test
    public void testFloatOptionDeserialization() throws Exception {
        // Test float as number
        String json1 = "{\"vec\":[123.456]}";
        Option<Float> option1 = objectMapper.readValue(json1,
                objectMapper.getTypeFactory().constructParametricType(Option.class, Float.class));
        assertFalse(option1.getVec().isEmpty());
        assertEquals(123.456f, option1.getVec().get(0), 0.0001);

        // Test float as string
        String json2 = "{\"vec\":[\"123.456\"]}";
        Option<Float> option2 = objectMapper.readValue(json2,
                objectMapper.getTypeFactory().constructParametricType(Option.class, Float.class));
        assertFalse(option2.getVec().isEmpty());
        assertEquals(123.456f, option2.getVec().get(0), 0.0001);

        // Test scientific notation
        String json3 = "{\"vec\":[\"1.23456E+2\"]}";
        Option<Float> option3 = objectMapper.readValue(json3,
                objectMapper.getTypeFactory().constructParametricType(Option.class, Float.class));
        assertFalse(option3.getVec().isEmpty());
        assertEquals(123.456f, option3.getVec().get(0), 0.0001);

        // Test hex string (should throw exception)
        String json4 = "{\"vec\":[\"0xFF\"]}";
        assertThrows(IOException.class, () -> {
            objectMapper.readValue(json4,
                    objectMapper.getTypeFactory().constructParametricType(Option.class, Float.class));
        });
    }

    @Test
    public void testDoubleOptionDeserialization() throws Exception {
        // Test double as number
        String json1 = "{\"vec\":[123.4567890123]}";
        Option<Double> option1 = objectMapper.readValue(json1,
                objectMapper.getTypeFactory().constructParametricType(Option.class, Double.class));
        assertFalse(option1.getVec().isEmpty());
        assertEquals(123.4567890123, option1.getVec().get(0), 0.0000000001);

        // Test double as string
        String json2 = "{\"vec\":[\"123.4567890123\"]}";
        Option<Double> option2 = objectMapper.readValue(json2,
                objectMapper.getTypeFactory().constructParametricType(Option.class, Double.class));
        assertFalse(option2.getVec().isEmpty());
        assertEquals(123.4567890123, option2.getVec().get(0), 0.0000000001);

        // Test scientific notation
        String json3 = "{\"vec\":[\"1.234567890123E+2\"]}";
        Option<Double> option3 = objectMapper.readValue(json3,
                objectMapper.getTypeFactory().constructParametricType(Option.class, Double.class));
        assertFalse(option3.getVec().isEmpty());
        assertEquals(123.4567890123, option3.getVec().get(0), 0.0000000001);

        // Test very precise double
        String json4 = "{\"vec\":[\"123.45678901234567890\"]}";
        Option<Double> option4 = objectMapper.readValue(json4,
                objectMapper.getTypeFactory().constructParametricType(Option.class, Double.class));
        assertFalse(option4.getVec().isEmpty());
        assertEquals(123.45678901234567890, option4.getVec().get(0), 0.0000000000000001);

        // Test hex string (should throw exception)
        String json5 = "{\"vec\":[\"0xFF\"]}";
        assertThrows(IOException.class, () -> {
            objectMapper.readValue(json5,
                    objectMapper.getTypeFactory().constructParametricType(Option.class, Double.class));
        });
    }

    @Test
    public void testOptionObjectDeserialization() throws JsonProcessingException {
        String json = "{\n" +
                "            \"vec\": [\n" +
                "              {\n" +
                "                \"inner\": \"0x71332984cb209500001bb4b4f741630f86adaaaf40614eb5b3d2dbf47d07064\"\n" +
                "              }\n" +
                "            ]\n" +
                "          }";
        Option<AptosObject> x = objectMapper.readValue(json,
                objectMapper.getTypeFactory().constructParametricType(Option.class, AptosObject.class)
        );
        System.out.println(x);
    }


    static class TestObject {
        private String name;
        private int value;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getValue() {
            return value;
        }

        public void setValue(int value) {
            this.value = value;
        }
    }

    // Complex custom class with various field types
    static class ComplexCustomClass {
        private String address;
        private Option<Coin> coins;
        private List<Integer> numbers;
        private Map<String, String> metadata;
        private BigInteger amount;

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public Option<Coin> getCoins() {
            return coins;
        }

        public void setCoins(Option<Coin> coins) {
            this.coins = coins;
        }

        public List<Integer> getNumbers() {
            return numbers;
        }

        public void setNumbers(List<Integer> numbers) {
            this.numbers = numbers;
        }

        public Map<String, String> getMetadata() {
            return metadata;
        }

        public void setMetadata(Map<String, String> metadata) {
            this.metadata = metadata;
        }

        public BigInteger getAmount() {
            return amount;
        }

        public void setAmount(BigInteger amount) {
            this.amount = amount;
        }
    }

    // Nested custom class
    static class Coin {
        @JsonProperty("value")
        private String value;

        @JsonProperty("denom")
        private String denom;

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public String getDenom() {
            return denom;
        }

        public void setDenom(String denom) {
            this.denom = denom;
        }
    }
} 