package com.github.wubuku.aptos.bean;

public class TokenPropertyMap {
    private SimpleMap<String, PropertyValue> map = new SimpleMap<>();

    public SimpleMap<String, PropertyValue> getMap() {
        return map;
    }

    public void setMap(SimpleMap<String, PropertyValue> map) {
        this.map = map;
    }

    @Override
    public String toString() {
        return "TokenPropertyMap{" +
                "map=" + map +
                '}';
    }

    public static class PropertyValue {
        private String value;//: vector<u8>,
        private String type;//: String,

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        @Override
        public String toString() {
            return "PropertyValue{" +
                    "value='" + value + '\'' +
                    ", type='" + type + '\'' +
                    '}';
        }
    }
}
