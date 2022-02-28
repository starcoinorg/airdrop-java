package org.starcoin.airdrop.data.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Version;

@Entity
public class ProposalToBeAirdropped {

    /**
     * Proposal id.(OnChain)
     */
    @Id
    @Column(name = "proposal_id", nullable = false)
    private Long proposalId;

    @Column(name = "status")
    private Integer status;

    @Column
    private String network;

    @Column
    private String title;

    @Column
    private String titleEn;

    @Column(length = 5000)
    private String description;

    @Column(length = 5000)
    private String descriptionEn;

    @Column
    private String creator;

    @Column
    private Long againstVotes;

    @Column
    private Long forVotes;

    @Column
    private Long quorumVotes;

    @Column
    private Long onChainStartTime;

    @Column
    private Long onChainEndTime;

    @Column
    private String link;

    @Column
    private String typeArgs1;

    @Column
    private Long endTime;

    @Column(name = "created_at", nullable = false)
    private Long createdAt;

    @Column(name = "updated_at", nullable = false)
    private Long updatedAt;

    @Column(length = 70, nullable = false)
    private String createdBy;

    @Column(length = 70, nullable = false)
    private String updatedBy;

    /**
     * 是否已不再使用。
     */
    @Column(nullable = false)
    private final Boolean deactived = false;

    @Version
    private Long version;

    public Long getProposalId() {
        return proposalId;
    }

    public void setProposalId(Long proposalId) {
        this.proposalId = proposalId;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getNetwork() {
        return network;
    }

    public void setNetwork(String network) {
        this.network = network;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitleEn() {
        return titleEn;
    }

    public void setTitleEn(String titleEn) {
        this.titleEn = titleEn;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescriptionEn() {
        return descriptionEn;
    }

    public void setDescriptionEn(String descriptionEn) {
        this.descriptionEn = descriptionEn;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public Long getAgainstVotes() {
        return againstVotes;
    }

    public void setAgainstVotes(Long againstVotes) {
        this.againstVotes = againstVotes;
    }

    public Long getForVotes() {
        return forVotes;
    }

    public void setForVotes(Long forVotes) {
        this.forVotes = forVotes;
    }

    public Long getQuorumVotes() {
        return quorumVotes;
    }

    public void setQuorumVotes(Long quorumVotes) {
        this.quorumVotes = quorumVotes;
    }

    public Long getOnChainStartTime() {
        return onChainStartTime;
    }

    public void setOnChainStartTime(Long onChainStartTime) {
        this.onChainStartTime = onChainStartTime;
    }

    public Long getOnChainEndTime() {
        return onChainEndTime;
    }

    public void setOnChainEndTime(Long onChainEndTime) {
        this.onChainEndTime = onChainEndTime;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getTypeArgs1() {
        return typeArgs1;
    }

    public void setTypeArgs1(String typeArgs1) {
        this.typeArgs1 = typeArgs1;
    }

    public Long getEndTime() {
        return endTime;
    }

    public void setEndTime(Long endTime) {
        this.endTime = endTime;
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

    public Boolean getDeactived() {
        return deactived;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }
}
