package com.lablink.equipment;

import java.util.UUID;

public class EquipmentDto {

    private final UUID id;
    private final String name, description, serialNumber, imageUrl;
    private final EquipmentStatus status;
    private final CategoryDto category;

    EquipmentDto(UUID id, String name, String description, String serialNumber,
                 EquipmentStatus status, CategoryDto category, String imageUrl) {
        this.id = id; this.name = name; this.description = description;
        this.serialNumber = serialNumber; this.status = status;
        this.category = category; this.imageUrl = imageUrl;
    }

    public static EquipmentDto from(Equipment e) {
        return new EquipmentDto(
                e.getId(), e.getName(), e.getDescription(),
                e.getSerialNumber(), e.getStatus(),
                CategoryDto.from(e.getCategory()), e.getImageUrl()
        );
    }

    public UUID getId()               { return id; }
    public String getName()           { return name; }
    public String getDescription()    { return description; }
    public String getSerialNumber()   { return serialNumber; }
    public EquipmentStatus getStatus(){ return status; }
    public CategoryDto getCategory()  { return category; }
    public String getImageUrl()       { return imageUrl; }
}
