package com.github.wubuku.aptos.bean;

import java.util.ArrayList;
import java.util.List;

public class SimpleMap<K, V> {

    private List<Element<K, V>> data = new ArrayList<>(); //: vector<Element<Key, Value>>,

    public List<Element<K, V>> getData() {
        return data;
    }

    public void setData(List<Element<K, V>> data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "SimpleMap{" +
                "data=" + data +
                '}';
    }

    public static class Element<EK, EV> {
        private EK key;
        private EV value;

        public EK getKey() {
            return key;
        }

        public void setKey(EK key) {
            this.key = key;
        }

        public EV getValue() {
            return value;
        }

        public void setValue(EV value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return "Element{" +
                    "key=" + key +
                    ", value=" + value +
                    '}';
        }
    }
}
