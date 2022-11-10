package com.github.wubuku.aptos.bean;

import java.util.Objects;

public class TokenDataId {
    /**
     * Token creator address
     */
    private String creator;

    /**
     * Unique name within this creator's account for this Token's collection
     */
    private String collection;

    /**
     * Name of Token
     */
    private String name;

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public String getCollection() {
        return collection;
    }

    public void setCollection(String collection) {
        this.collection = collection;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public TokenDataId() {
    }

    public TokenDataId(String creator, String collection, String name) {
        this.creator = creator;
        this.collection = collection;
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TokenDataId that = (TokenDataId) o;
        return Objects.equals(creator, that.creator)
                && Objects.equals(collection, that.collection) && Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(creator, collection, name);
    }

    @Override
    public String toString() {
        return "TokenDataId{" +
                "creator='" + creator + '\'' +
                ", collection='" + collection + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}
