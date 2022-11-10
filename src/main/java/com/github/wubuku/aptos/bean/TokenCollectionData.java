package com.github.wubuku.aptos.bean;

import com.fasterxml.jackson.annotation.JsonProperty;

public class TokenCollectionData {
    // A description for the token collection Eg: "Aptos Toad Overload"
    private String description;

    // The collection name, which should be unique among all collections by the creator; the name should also be smaller than 128 characters, eg: "Animal Collection"
    private String name;

    // The URI for the collection; its length should be smaller than 512 characters
    private String uri;

    // The number of different TokenData entries in this collection
    private String supply;//: u64,

    // If maximal is a non-zero value, the number of created TokenData entries should be smaller or equal to this maximum
    // If maximal is 0, Aptos doesn't track the supply of this collection, and there is no limit
    private String maximum;//: u64,

    // control which collectionData field is mutable
    @JsonProperty("mutability_config")
    private CollectionMutabilityConfig mutabilityConfig;//: CollectionMutabilityConfig,

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getSupply() {
        return supply;
    }

    public void setSupply(String supply) {
        this.supply = supply;
    }

    public String getMaximum() {
        return maximum;
    }

    public void setMaximum(String maximum) {
        this.maximum = maximum;
    }

    public CollectionMutabilityConfig getMutabilityConfig() {
        return mutabilityConfig;
    }

    public void setMutabilityConfig(CollectionMutabilityConfig mutabilityConfig) {
        this.mutabilityConfig = mutabilityConfig;
    }

    @Override
    public String toString() {
        return "TokenCollectionData{" +
                "description='" + description + '\'' +
                ", name='" + name + '\'' +
                ", uri='" + uri + '\'' +
                ", supply='" + supply + '\'' +
                ", maximum='" + maximum + '\'' +
                ", mutabilityConfig=" + mutabilityConfig +
                '}';
    }

    /// This config specifies which fields in the Collection are mutable
    public static class CollectionMutabilityConfig {
        // control if description is mutable
        private Boolean description;//: bool,
        // control if uri is mutable
        private Boolean uri;//: bool,
        // control if collection maxium is mutable
        private Boolean maximum;//: bool,

        public Boolean getDescription() {
            return description;
        }

        public void setDescription(Boolean description) {
            this.description = description;
        }

        public Boolean getUri() {
            return uri;
        }

        public void setUri(Boolean uri) {
            this.uri = uri;
        }

        public Boolean getMaximum() {
            return maximum;
        }

        public void setMaximum(Boolean maximum) {
            this.maximum = maximum;
        }

        @Override
        public String toString() {
            return "CollectionMutabilityConfig{" +
                    "description=" + description +
                    ", uri=" + uri +
                    ", maximum=" + maximum +
                    '}';
        }
    }
}
