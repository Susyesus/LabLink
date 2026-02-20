package com.lablink.equipment;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.UUID;

// ── Request DTOs ──────────────────────────────────────────────

class CreateEquipmentRequest {
    @NotBlank(message = "Equipment name is required")
    private String name;
    private String description;
    private String serialNumber;
    @NotNull(message = "Category is required")
    private UUID categoryId;
    private String imageUrl;

    public String getName()          { return name; }
    public void setName(String v)    { this.name = v; }
    public String getDescription()   { return description; }
    public void setDescription(String v) { this.description = v; }
    public String getSerialNumber()  { return serialNumber; }
    public void setSerialNumber(String v) { this.serialNumber = v; }
    public UUID getCategoryId()      { return categoryId; }
    public void setCategoryId(UUID v){ this.categoryId = v; }
    public String getImageUrl()      { return imageUrl; }
    public void setImageUrl(String v){ this.imageUrl = v; }
}

class UpdateEquipmentRequest {
    private String name;
    private String description;
    private EquipmentStatus status;
    private UUID categoryId;
    private String imageUrl;

    public String getName()              { return name; }
    public void setName(String v)        { this.name = v; }
    public String getDescription()       { return description; }
    public void setDescription(String v) { this.description = v; }
    public EquipmentStatus getStatus()   { return status; }
    public void setStatus(EquipmentStatus v) { this.status = v; }
    public UUID getCategoryId()          { return categoryId; }
    public void setCategoryId(UUID v)    { this.categoryId = v; }
    public String getImageUrl()          { return imageUrl; }
    public void setImageUrl(String v)    { this.imageUrl = v; }
}

// ── Response DTOs ─────────────────────────────────────────────

class EquipmentDto {
    private final UUID id;
    private final String name, description, serialNumber, imageUrl;
    private final EquipmentStatus status;
    private final CategoryDto category;

    private EquipmentDto(UUID id, String name, String description, String serialNumber,
                         EquipmentStatus status, CategoryDto category, String imageUrl) {
        this.id = id; this.name = name; this.description = description;
        this.serialNumber = serialNumber; this.status = status;
        this.category = category; this.imageUrl = imageUrl;
    }

    static EquipmentDto from(Equipment e) {
        return new EquipmentDto(e.getId(), e.getName(), e.getDescription(),
                e.getSerialNumber(), e.getStatus(), CategoryDto.from(e.getCategory()), e.getImageUrl());
    }

    public UUID getId()              { return id; }
    public String getName()          { return name; }
    public String getDescription()   { return description; }
    public String getSerialNumber()  { return serialNumber; }
    public EquipmentStatus getStatus(){ return status; }
    public CategoryDto getCategory() { return category; }
    public String getImageUrl()      { return imageUrl; }
}

class CategoryDto {
    private final UUID id;
    private final String name, description;

    private CategoryDto(UUID id, String name, String description) {
        this.id = id; this.name = name; this.description = description;
    }

    static CategoryDto from(Category c) {
        return new CategoryDto(c.getId(), c.getName(), c.getDescription());
    }

    public UUID getId()            { return id; }
    public String getName()        { return name; }
    public String getDescription() { return description; }
}

class EquipmentPageResponse {
    private final List<EquipmentDto> equipment;
    private final PaginationDto pagination;

    EquipmentPageResponse(List<EquipmentDto> equipment, PaginationDto pagination) {
        this.equipment  = equipment;
        this.pagination = pagination;
    }

    public List<EquipmentDto> getEquipment() { return equipment; }
    public PaginationDto getPagination()     { return pagination; }
}

class PaginationDto {
    private final int page, limit, pages;
    private final long total;

    PaginationDto(int page, int limit, long total, int pages) {
        this.page = page; this.limit = limit; this.total = total; this.pages = pages;
    }

    public int getPage()   { return page; }
    public int getLimit()  { return limit; }
    public long getTotal() { return total; }
    public int getPages()  { return pages; }
}
