package com.github.wubuku.aptos.bean;

import com.fasterxml.jackson.annotation.JsonProperty;

public class OptionalAggregator {

    @JsonProperty("aggregator")
    private Option<Aggregator> aggregator;
    @JsonProperty("integer")
    private Option<Integer> integer;

    public Option<Aggregator> getAggregator() {
        return aggregator;
    }

    public void setAggregator(Option<Aggregator> aggregator) {
        this.aggregator = aggregator;
    }

    public Option<Integer> getInteger() {
        return integer;
    }

    public void setInteger(Option<Integer> integer) {
        this.integer = integer;
    }

    @Override
    public String toString() {
        return "OptionalAggregator{" +
                "aggregator=" + aggregator +
                ", integer=" + integer +
                '}';
    }

    public static class Aggregator {
        @JsonProperty("handle")
        private String handle;
        @JsonProperty("key")
        private String key;
        @JsonProperty("limit")
        private String limit;

        public String getHandle() {
            return handle;
        }

        public void setHandle(String handle) {
            this.handle = handle;
        }

        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }

        public String getLimit() {
            return limit;
        }

        public void setLimit(String limit) {
            this.limit = limit;
        }

        @Override
        public String toString() {
            return "Aggregator{" +
                    "handle='" + handle + '\'' +
                    ", key='" + key + '\'' +
                    ", limit='" + limit + '\'' +
                    '}';
        }
    }

    public static class Integer {
        @JsonProperty("value")
        private String value;//: u128,
        @JsonProperty("limit")
        private String limit;//: u128,

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public String getLimit() {
            return limit;
        }

        public void setLimit(String limit) {
            this.limit = limit;
        }

        @Override
        public String toString() {
            return "Integer{" +
                    "value='" + value + '\'' +
                    ", limit='" + limit + '\'' +
                    '}';
        }
    }
}
