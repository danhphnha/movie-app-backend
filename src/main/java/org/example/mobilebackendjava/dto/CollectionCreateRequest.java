package org.example.mobilebackendjava.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CollectionCreateRequest {
    @NotBlank(message = "Collection name cannot be blank")
    private String collectionName;

    public CollectionCreateRequest() {}

    public String getCollectionName() { return collectionName; }
    public void setCollectionName(String collectionName) { this.collectionName = collectionName; }
}
