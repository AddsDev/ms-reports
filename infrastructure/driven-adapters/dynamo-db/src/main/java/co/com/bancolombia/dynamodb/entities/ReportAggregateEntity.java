package co.com.bancolombia.dynamodb.entities;

import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.*;

import java.math.BigDecimal;

@DynamoDbBean
public class ReportAggregateEntity {

    private String pk; //REPORT#APPROVED
    private String sk; //TOTAL
    private Long approvedCount;
    private BigDecimal approvedAmountSum;
    private String updatedAt;

    public ReportAggregateEntity() {
    }

    @DynamoDbPartitionKey
    @DynamoDbAttribute("pk")
    public String getPk() {
        return pk;
    }
    public void setPk(String pk) {
        this.pk = pk;
    }

    @DynamoDbSortKey
    @DynamoDbAttribute("sk")
    public String getSk() {
        return sk;
    }

    public void setSk(String sk) {
        this.sk = sk;
    }
    @DynamoDbAttribute("approved_count")
    public Long getApprovedCount() {
        return approvedCount;
    }

    public void setApprovedCount(Long approvedCount) {
        this.approvedCount = approvedCount;
    }
    @DynamoDbAttribute("approved_amount_sum")
    public BigDecimal getApprovedAmountSum() {
        return approvedAmountSum;
    }

    public void setApprovedAmountSum(BigDecimal approvedAmountSum) {
        this.approvedAmountSum = approvedAmountSum;
    }

    @DynamoDbAttribute("updated_at")
    public String getUpdatedAt() {
        return updatedAt;
    }
    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }
}
