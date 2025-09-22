package co.com.bancolombia.dynamodb.entities;

import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbAttribute;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;

@DynamoDbBean
public class DedupEventEntity {
    private String eventId;
    private String createdAt;

    public DedupEventEntity() {
    }

    public DedupEventEntity(String eventId, String createdAt) {
        this.eventId = eventId;
        this.createdAt = createdAt;
    }

    @DynamoDbPartitionKey
    @DynamoDbAttribute(value = "event_id")
    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    @DynamoDbAttribute(value = "created_at")
    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
}
