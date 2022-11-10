package com.github.wubuku.aptos.bean;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

/**
 * The shared TokenData by tokens with different property_version.
 */
public class TokenData {

    // The maximal number of tokens that can be minted under this TokenData; if the maximum is 0, there is no limit
    @JsonProperty("maximum")
    private String maximum;//: u64,

    // The current largest property version of all tokens with this TokenData
    @JsonProperty("largest_property_version")
    private String largestPropertyVersion;//: u64,

    // The number of tokens with this TokenData. Supply is only tracked for the limited token whose maximum is not 0
    private String supply;//: u64,

    // The Uniform Resource Identifier (uri) pointing to the JSON file stored in off-chain storage; the URL length should be less than 512 characters, eg: https://arweave.net/Fmmn4ul-7Mv6vzm7JwE69O-I-vd6Bz2QriJO1niwCh4
    private String uri;//: String,

    // The denominator and numerator for calculating the royalty fee; it also contains payee account address for depositing the Royalty
    private Royalty royalty;//: Royalty,

    // The name of the token, which should be unique within the collection; the length of name should be smaller than 128, characters, eg: "Aptos Animal #1234"
    private String name;//: String,

    // Describes this Token
    private String description;//: String,

    // The properties are stored in the TokenData that are shared by all tokens
    @JsonProperty("default_properties")
    private TokenPropertyMap defaultProperties;//: PropertyMap,

    // Control the TokenData field mutability
    @JsonProperty("mutability_config")
    private Map<String, Object> mutabilityConfig;//: TokenMutabilityConfig,

    public String getMaximum() {
        return maximum;
    }

    public void setMaximum(String maximum) {
        this.maximum = maximum;
    }

    public String getLargestPropertyVersion() {
        return largestPropertyVersion;
    }

    public void setLargestPropertyVersion(String largestPropertyVersion) {
        this.largestPropertyVersion = largestPropertyVersion;
    }

    public String getSupply() {
        return supply;
    }

    public void setSupply(String supply) {
        this.supply = supply;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public Royalty getRoyalty() {
        return royalty;
    }

    public void setRoyalty(Royalty royalty) {
        this.royalty = royalty;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public TokenPropertyMap getDefaultProperties() {
        return defaultProperties;
    }

    public void setDefaultProperties(TokenPropertyMap defaultProperties) {
        this.defaultProperties = defaultProperties;
    }

    public Map<String, Object> getMutabilityConfig() {
        return mutabilityConfig;
    }

    public void setMutabilityConfig(Map<String, Object> mutabilityConfig) {
        this.mutabilityConfig = mutabilityConfig;
    }

    @Override
    public String toString() {
        return "TokenData{" +
                "maximum='" + maximum + '\'' +
                ", largest_property_version='" + largestPropertyVersion + '\'' +
                ", supply='" + supply + '\'' +
                ", uri='" + uri + '\'' +
                ", royalty=" + royalty +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", default_properties=" + defaultProperties +
                ", mutability_config=" + mutabilityConfig +
                '}';
    }

    /// The royalty of a token
    public static class Royalty {
        @JsonProperty("royalty_points_numerator")
        private String royaltyPointsNumerator;//: u64,

        @JsonProperty("royalty_points_denominator")
        private String royaltyPointsDenominator;//: u64,

        // if the token is jointly owned by multiple creators, the group of creators should create a shared account.
        // the payee_address will be the shared account address.
        @JsonProperty("payee_address")
        private String payeeAddress;//: address,

        public String getRoyaltyPointsNumerator() {
            return royaltyPointsNumerator;
        }

        public void setRoyaltyPointsNumerator(String royaltyPointsNumerator) {
            this.royaltyPointsNumerator = royaltyPointsNumerator;
        }

        public String getRoyaltyPointsDenominator() {
            return royaltyPointsDenominator;
        }

        public void setRoyaltyPointsDenominator(String royaltyPointsDenominator) {
            this.royaltyPointsDenominator = royaltyPointsDenominator;
        }

        public String getPayeeAddress() {
            return payeeAddress;
        }

        public void setPayeeAddress(String payeeAddress) {
            this.payeeAddress = payeeAddress;
        }

        @Override
        public String toString() {
            return "Royalty{" +
                    "royaltyPointsNumerator='" + royaltyPointsNumerator + '\'' +
                    ", royaltyPointsDenominator='" + royaltyPointsDenominator + '\'' +
                    ", payeeAddress='" + payeeAddress + '\'' +
                    '}';
        }
    }
}
