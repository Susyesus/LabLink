package com.lablink.equipment;

import java.util.UUID;

public class CategoryDto {

    private final UUID id;
    private final String name, description;

    CategoryDto(UUID id, String name, String description) {
        this.id = id; this.name = name; this.description = description;
    }

    public static CategoryDto from(Category c) {
        return new CategoryDto(c.getId(), c.getName(), c.getDescription());
    }

    public UUID getId()            { return id; }
    public String getName()        { return name; }
    public String getDescription() { return description; }
}
