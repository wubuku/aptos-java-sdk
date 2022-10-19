package com.github.wubuku.aptos.bean;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

public class Transaction {
    public static String TYPE_PENDING_TRANSACTION = "pending_transaction";

    @JsonProperty("version")//: "11742804",
    private String version;
    @JsonProperty("hash")//:"0xbcaac6583ecd9ce75ed65b1fbef6f530d4d40c57b0d6c1672d5f2584e7ca9752",
    private String hash;
    @JsonProperty("state_change_hash")//:"0x7ca103fa1a8546a897a2239b31e477b1af070493bc6c8faf3585d385ada2eaf8",
    private String stateChangeHash;
    @JsonProperty("event_root_hash")//:"0xeef0cb80b791c5644b9142802ad211ec128989dbf2147fbdb2dcd0ed03f4461b",
    private String eventRootHash;
    @JsonProperty("state_checkpoint_hash")//:null,
    private String stateCheckpointHash;
    @JsonProperty("gas_used")//:"19",
    private String gasUsed;
    @JsonProperty("success")//:true,
    private Boolean success;
    @JsonProperty("vm_status")//:"Executed successfully",
    private String vmStatus;
    @JsonProperty("accumulator_root_hash")//:"0x773d18998e224ceddbe3c9c12248fffcdfb2e62920339ba3795a09fe449c8920",
    private String accumulatorRootHash;
    @JsonProperty("changes")//:[
    private List<Map<String, Object>> changes;
    @JsonProperty("sender")//:"0x2b490841c230a31fe012f3b2a3f3d146316be073e599eb7d7e5074838073ef14",
    private String sender;
    @JsonProperty("sequence_number")//:"5",
    private String sequenceNumber;
    @JsonProperty("max_gas_amount")//:"50000",
    private String maxGasAmount;
    @JsonProperty("gas_unit_price")//:"1",
    private String gasUnitPrice;
    @JsonProperty("expiration_timestamp_secs")//:"1663596231",
    private String expirationTimestampSecs;
    @JsonProperty("payload")//:{
    private TransactionPayload payload;
    @JsonProperty("signature")//:[
    private Signature signature;
    @JsonProperty("events")
    private List<Event<Object>> events;
    @JsonProperty("timestamp")//:"1663596202076366",
    private String timestamp;
    @JsonProperty("type")//:"user_transaction"
    private String type;

    @JsonProperty("id")//:"0x66bdfab31d6fed23f1e0afaf714e421f605c2c74840782ceb004165584449e73",
    private String id;
    @JsonProperty("epoch")//:"1",
    private String epoch;
    @JsonProperty("round")//:"2",
    private String round;

    @JsonProperty("previous_block_votes_bitvec")
    private List<Integer> previousBlockVotesBitvec;

    @JsonProperty("proposer")
    private String proposer;

    @JsonProperty("failed_proposer_indices")
    private List<Integer> failedProposerIndices;


    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public String getStateChangeHash() {
        return stateChangeHash;
    }

    public void setStateChangeHash(String stateChangeHash) {
        this.stateChangeHash = stateChangeHash;
    }

    public String getEventRootHash() {
        return eventRootHash;
    }

    public void setEventRootHash(String eventRootHash) {
        this.eventRootHash = eventRootHash;
    }

    public String getStateCheckpointHash() {
        return stateCheckpointHash;
    }

    public void setStateCheckpointHash(String stateCheckpointHash) {
        this.stateCheckpointHash = stateCheckpointHash;
    }

    public String getGasUsed() {
        return gasUsed;
    }

    public void setGasUsed(String gasUsed) {
        this.gasUsed = gasUsed;
    }

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public String getVmStatus() {
        return vmStatus;
    }

    public void setVmStatus(String vmStatus) {
        this.vmStatus = vmStatus;
    }

    public String getAccumulatorRootHash() {
        return accumulatorRootHash;
    }

    public void setAccumulatorRootHash(String accumulatorRootHash) {
        this.accumulatorRootHash = accumulatorRootHash;
    }

    public List<Map<String, Object>> getChanges() {
        return changes;
    }

    public void setChanges(List<Map<String, Object>> changes) {
        this.changes = changes;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getSequenceNumber() {
        return sequenceNumber;
    }

    public void setSequenceNumber(String sequenceNumber) {
        this.sequenceNumber = sequenceNumber;
    }

    public String getMaxGasAmount() {
        return maxGasAmount;
    }

    public void setMaxGasAmount(String maxGasAmount) {
        this.maxGasAmount = maxGasAmount;
    }

    public String getGasUnitPrice() {
        return gasUnitPrice;
    }

    public void setGasUnitPrice(String gasUnitPrice) {
        this.gasUnitPrice = gasUnitPrice;
    }

    public String getExpirationTimestampSecs() {
        return expirationTimestampSecs;
    }

    public void setExpirationTimestampSecs(String expirationTimestampSecs) {
        this.expirationTimestampSecs = expirationTimestampSecs;
    }

    public TransactionPayload getPayload() {
        return payload;
    }

    public void setPayload(TransactionPayload payload) {
        this.payload = payload;
    }

    public Signature getSignature() {
        return signature;
    }

    public void setSignature(Signature signature) {
        this.signature = signature;
    }

    public List<Event<Object>> getEvents() {
        return events;
    }

    public void setEvents(List<Event<Object>> events) {
        this.events = events;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEpoch() {
        return epoch;
    }

    public void setEpoch(String epoch) {
        this.epoch = epoch;
    }

    public String getRound() {
        return round;
    }

    public void setRound(String round) {
        this.round = round;
    }

    public List<Integer> getPreviousBlockVotesBitvec() {
        return previousBlockVotesBitvec;
    }

    public void setPreviousBlockVotesBitvec(List<Integer> previousBlockVotesBitvec) {
        this.previousBlockVotesBitvec = previousBlockVotesBitvec;
    }

    public String getProposer() {
        return proposer;
    }

    public void setProposer(String proposer) {
        this.proposer = proposer;
    }

    public List<Integer> getFailedProposerIndices() {
        return failedProposerIndices;
    }

    public void setFailedProposerIndices(List<Integer> failedProposerIndices) {
        this.failedProposerIndices = failedProposerIndices;
    }

    @Override
    public String toString() {
        return "Transaction{" +
                "version='" + version + '\'' +
                ", hash='" + hash + '\'' +
                ", stateChangeHash='" + stateChangeHash + '\'' +
                ", eventRootHash='" + eventRootHash + '\'' +
                ", stateCheckpointHash='" + stateCheckpointHash + '\'' +
                ", gasUsed='" + gasUsed + '\'' +
                ", success=" + success +
                ", vmStatus='" + vmStatus + '\'' +
                ", accumulatorRootHash='" + accumulatorRootHash + '\'' +
                ", changes=" + changes +
                ", sender='" + sender + '\'' +
                ", sequenceNumber='" + sequenceNumber + '\'' +
                ", maxGasAmount='" + maxGasAmount + '\'' +
                ", gasUnitPrice='" + gasUnitPrice + '\'' +
                ", expirationTimestampSecs='" + expirationTimestampSecs + '\'' +
                ", payload=" + payload +
                ", signature=" + signature +
                ", events=" + events +
                ", timestamp='" + timestamp + '\'' +
                ", type='" + type + '\'' +
                ", id='" + id + '\'' +
                ", epoch='" + epoch + '\'' +
                ", round='" + round + '\'' +
                ", previousBlockVotesBitvec=" + previousBlockVotesBitvec +
                ", proposer='" + proposer + '\'' +
                ", failedProposerIndices=" + failedProposerIndices +
                '}';
    }
}
