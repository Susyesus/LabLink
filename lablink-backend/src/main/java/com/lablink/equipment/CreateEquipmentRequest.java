package com.lablink.equipment;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public class CreateEquipmentRequest {

    @NotBlank(message = "Equipment name is required")
    private String name;
    private String description;
    private String serialNumber;
    @NotNull(message = "Category is required")
    private UUID categoryId;
    private String imageUrl;

    public String getName()           { return name; }
    public void setName(String v)     { this.name = v; }
    public String getDescription()    { return description; }
    public void setDescription(String v) { this.description = v; }
    public String getSerialNumber()   { return serialNumber; }
    public void setSerialNumber(String v) { this.serialNumber = v; }
    public UUID getCategoryId()       { return categoryId; }
    public void setCategoryId(UUID v) { this.categoryId = v; }
    public String getImageUrl()       { return imageUrl; }
    public void setImageUrl(String v) { this.imageUrl = v; }
}
