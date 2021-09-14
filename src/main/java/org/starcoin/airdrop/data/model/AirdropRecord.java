package org.starcoin.airdrop.data.model;

import javax.persistence.*;
import java.math.BigInteger;
import java.util.Date;

@Entity
@Table(name = "airdrop_records")
public class AirdropRecord {

    //PRIMARY KEY (`id`),
    //KEY `fk_airdrop_id` (`airdrop_id`),
    //KEY `idx_records_address` (`address`),
    //CONSTRAINT `fk_airdrop_id` FOREIGN KEY (`airdrop_id`) REFERENCES `airdrop_projects` (`id`)

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // int NOT NULL AUTO_INCREMENT,

    @Column
    private String address; // varchar(45) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL,

    @Column
    private BigInteger amount = BigInteger.ZERO; // bigint unsigned DEFAULT '0',

    @Column
    private Integer idx = 0; // int DEFAULT '0',

    @Column
    private String proof; // varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL,

    @Column(name = "airdrop_id")
    private Long airdropId; // int NOT NULL DEFAULT '0',

    @Column
    private Integer status = 0; // int DEFAULT '0',

    @Column(name = "created_at")
    private Date createdAt; // datetime DEFAULT CURRENT_TIMESTAMP,

    @Column(name = "updated_at")
    private Date updatedAt; // datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public BigInteger getAmount() {
        return amount;
    }

    public void setAmount(BigInteger amount) {
        this.amount = amount;
    }

    public Integer getIdx() {
        return idx;
    }

    public void setIdx(Integer idx) {
        this.idx = idx;
    }

    public String getProof() {
        return proof;
    }

    public void setProof(String proof) {
        this.proof = proof;
    }

    public Long getAirdropId() {
        return airdropId;
    }

    public void setAirdropId(Long airdropId) {
        this.airdropId = airdropId;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public String toString() {
        return "AirdropRecord{" +
                "id=" + id +
                ", address='" + address + '\'' +
                ", amount=" + amount +
                ", idx=" + idx +
                ", proof='" + proof + '\'' +
                ", airdropId=" + airdropId +
                ", status=" + status +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}
