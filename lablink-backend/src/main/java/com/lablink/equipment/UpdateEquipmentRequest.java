package com.lablink.equipment;

import java.util.UUID;

public class UpdateEquipmentRequest {

    private String name;
    private String description;
    private EquipmentStatus status;
    private UUID categoryId;
    private String imageUrl;

    public String getName()               { return name; }
    public void setName(String v)         { this.name = v; }
    public String getDescription()        { return description; }
    public void setDescription(String v)  { this.description = v; }
    public EquipmentStatus getStatus()    { return status; }
    public void setStatus(EquipmentStatus v) { this.status = v; }
    public UUID getCategoryId()           { return categoryId; }
    public void setCategoryId(UUID v)     { this.categoryId = v; }
    public String getImageUrl()           { return imageUrl; }
    public void setImageUrl(String v)     { this.imageUrl = v; }
}
