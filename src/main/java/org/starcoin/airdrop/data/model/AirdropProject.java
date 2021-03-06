package org.starcoin.airdrop.data.model;

import javax.persistence.*;
import java.math.BigInteger;
import java.util.Date;

@Entity
@Table(name = "airdrop_projects", uniqueConstraints = {
        @UniqueConstraint(name = "UniqueName", columnNames = {"name"})
})
public class AirdropProject {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // int NOT NULL AUTO_INCREMENT,

    @Column
    private String token = "0x1::STC::STC"; // varchar(45) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL DEFAULT '0x1::STC::STC',

    /**
     * Chain Id. localhost=254, main=1, barnard=251
     */
    @Column(name = "network_version")
    private Integer networkVersion = 0; // int DEFAULT '0',

    @Column(name = "token_precision")
    private Integer tokenPrecision = 9;// int DEFAULT '9',

    @Column(nullable = false)
    private String name; // varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL DEFAULT '',

    @Column(name = "name_en", length = 100)//varchar(100)
    private String nameEn;

    @Column(name = "token_icon")
    private String tokenIcon = ""; // varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT '',

    @Column(name = "token_symbol")
    private String tokenSymbol = "STC"; // varchar(45) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL DEFAULT 'STC',

    @Column(name = "total_amount")
    private BigInteger totalAmount = BigInteger.ZERO; // bigint DEFAULT '0',

    @Column(name = "valid_amount")
    private BigInteger validAmount = BigInteger.ZERO; // bigint DEFAULT '0',

    @Column(name = "start_at")
    private Date startAt; // datetime DEFAULT CURRENT_TIMESTAMP,

    @Column(name = "end_at")
    private Date endAt; // datetime DEFAULT CURRENT_TIMESTAMP,

    @Column(name = "owner_address")
    private String ownerAddress; // varchar(45) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL,

    @Column
    private String root; // varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL,

    @Column(name = "create_at")
    private Date createAt; // datetime DEFAULT CURRENT_TIMESTAMP,

    @Column(name = "update_at")
    private Date updateAt; // datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Integer getNetworkVersion() {
        return networkVersion;
    }

    public void setNetworkVersion(Integer networkVersion) {
        this.networkVersion = networkVersion;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTokenIcon() {
        return tokenIcon;
    }

    public void setTokenIcon(String tokenIcon) {
        this.tokenIcon = tokenIcon;
    }

    public String getTokenSymbol() {
        return tokenSymbol;
    }

    public void setTokenSymbol(String tokenSymbol) {
        this.tokenSymbol = tokenSymbol;
    }

    public Integer getTokenPrecision() {
        return tokenPrecision;
    }

    public void setTokenPrecision(Integer tokenPrecision) {
        this.tokenPrecision = tokenPrecision;
    }

    public BigInteger getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigInteger totalAmount) {
        this.totalAmount = totalAmount;
    }

    public BigInteger getValidAmount() {
        return validAmount;
    }

    public void setValidAmount(BigInteger validAmount) {
        this.validAmount = validAmount;
    }

    public Date getStartAt() {
        return startAt;
    }

    public void setStartAt(Date startAt) {
        this.startAt = startAt;
    }

    public Date getEndAt() {
        return endAt;
    }

    public void setEndAt(Date endAt) {
        this.endAt = endAt;
    }

    public String getOwnerAddress() {
        return ownerAddress;
    }

    public void setOwnerAddress(String ownerAddress) {
        this.ownerAddress = ownerAddress;
    }

    public String getRoot() {
        return root;
    }

    public void setRoot(String root) {
        this.root = root;
    }

    public Date getCreateAt() {
        return createAt;
    }

    public void setCreateAt(Date createAt) {
        this.createAt = createAt;
    }

    public Date getUpdateAt() {
        return updateAt;
    }

    public void setUpdateAt(Date updateAt) {
        this.updateAt = updateAt;
    }


    public String getNameEn() {
        return nameEn;
    }

    public void setNameEn(String nameEn) {
        this.nameEn = nameEn;
    }

    @Override
    public String toString() {
        return "AirdropProject{" +
                "id=" + id +
                ", token='" + token + '\'' +
                ", networkVersion=" + networkVersion +
                ", name='" + name + '\'' +
                ", nameEn='" + nameEn + '\'' +
                ", tokenIcon='" + tokenIcon + '\'' +
                ", tokenSymbol='" + tokenSymbol + '\'' +
                ", tokenPrecision=" + tokenPrecision +
                ", totalAmount=" + totalAmount +
                ", validAmount=" + validAmount +
                ", startAt=" + startAt +
                ", endAt=" + endAt +
                ", ownerAddress='" + ownerAddress + '\'' +
                ", root='" + root + '\'' +
                ", createAt=" + createAt +
                ", updateAt=" + updateAt +
                '}';
    }

}
