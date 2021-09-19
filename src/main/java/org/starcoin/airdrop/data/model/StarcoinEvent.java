package org.starcoin.airdrop.data.model;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.starcoin.airdrop.DomainError;

import javax.persistence.*;
import java.math.BigInteger;

import static org.starcoin.airdrop.DomainError.INVALID_STATUS;


@Entity
@DynamicInsert
@DynamicUpdate
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "event_type")
@Table(uniqueConstraints = {
        @UniqueConstraint(name = "UniqueStarcoinEvent", columnNames = {"block_hash", "transaction_index", "event_key", "event_sequence_number"})
}, indexes = {
        @Index(name = "StcEvnStatusCreatedAt", columnList = "status, created_at"),
        @Index(name = "StcEvnStatusUpdatedAt", columnList = "status, updated_at")
})
public abstract class StarcoinEvent {

    public static final String STATUS_CREATED = "CREATED";

    public static final String STATUS_CONFIRMED = "CONFIRMED";

    public static final String STATUS_DROPPED = "DROPPED";

    public static final String STATUS_DEACTIVED = "DEACTIVED";

    @Id
    @Column(length = 66, nullable = false)
    private String eventId;

    //0xe25b5e486b293cc4ad80e19c71e85b25fc679dc05cd89e030d28c37ea6d41fe2
    @Column(name = "block_hash", length = 66, nullable = false)
    private String blockHash;

    @Column(precision = 50, scale = 0)
    private BigInteger blockNumber;

    //0xb5e98b1e4607f12f97e942c9d5610919519d47601c31dc5fb96d681cf7209c2d
    @Column(length = 66, nullable = false)
    private String transactionHash;

    @Column(name = "transaction_index", precision = 50, scale = 0, nullable = false)
    private BigInteger transactionIndex;

    //0x010000000000000007fa08a855753f0ff7292fdcbe871216
    @Column(name = "event_key", length = 50, nullable = false)
    private String eventKey;

    @Column(name = "event_sequence_number", precision = 50, scale = 0, nullable = false)
    private BigInteger eventSequenceNumber;

    private String typeTag;

    private String data;

    @Column(name = "status", length = 20, nullable = false)
    private String status = STATUS_CREATED;

    @Column(length = 70, nullable = false)
    private String createdBy;

    @Column(length = 70, nullable = false)
    private String updatedBy;

    @Column(name = "created_at", nullable = false)
    private Long createdAt;

    @Column(name = "updated_at", nullable = false)
    private Long updatedAt;

    @Version
    private Long version;

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public String getBlockHash() {
        return blockHash;
    }

    public void setBlockHash(String blockHash) {
        this.blockHash = blockHash;
    }

    public BigInteger getBlockNumber() {
        return blockNumber;
    }

    public void setBlockNumber(BigInteger blockNumber) {
        this.blockNumber = blockNumber;
    }

    public String getTransactionHash() {
        return transactionHash;
    }

    public void setTransactionHash(String transactionHash) {
        this.transactionHash = transactionHash;
    }

    public BigInteger getTransactionIndex() {
        return transactionIndex;
    }

    public void setTransactionIndex(BigInteger transactionIndex) {
        this.transactionIndex = transactionIndex;
    }

    public String getEventKey() {
        return eventKey;
    }

    public void setEventKey(String eventKey) {
        this.eventKey = eventKey;
    }

    public BigInteger getEventSequenceNumber() {
        return eventSequenceNumber;
    }

    public void setEventSequenceNumber(BigInteger eventSequenceNumber) {
        this.eventSequenceNumber = eventSequenceNumber;
    }

    public String getTypeTag() {
        return typeTag;
    }

    public void setTypeTag(String typeTag) {
        this.typeTag = typeTag;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getStatus() {
        return status;
    }

    protected void setStatus(String status) {
        this.status = status;
    }

    public void confirmed() {
        if (!STATUS_CREATED.equals(this.status)) {
            throw DomainError.named(INVALID_STATUS,
                    "Can not confirm, invalid status of event '%1$s', '%2$s'.", this.eventId, this.status);
        }
        this.setStatus(STATUS_CONFIRMED);
    }

    public void resetStatus() {
        this.setStatus(STATUS_CREATED);
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }

    public Long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Long createdAt) {
        this.createdAt = createdAt;
    }

    public Long getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Long updatedAt) {
        this.updatedAt = updatedAt;
    }

    public boolean isDeactived() {
        return STATUS_DEACTIVED.equalsIgnoreCase(this.getStatus());
    }

    @Override
    public String toString() {
        return "StarcoinEvent{" +
                "eventId='" + eventId + '\'' +
                ", blockHash='" + blockHash + '\'' +
                ", blockNumber=" + blockNumber +
                ", transactionHash='" + transactionHash + '\'' +
                ", transactionIndex=" + transactionIndex +
                ", eventKey='" + eventKey + '\'' +
                ", eventSequenceNumber='" + eventSequenceNumber + '\'' +
                ", typeTag='" + typeTag + '\'' +
                ", data='" + data + '\'' +
                ", status='" + status + '\'' +
                ", createdBy='" + createdBy + '\'' +
                ", updatedBy='" + updatedBy + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                ", version=" + version +
                '}';
    }

}
