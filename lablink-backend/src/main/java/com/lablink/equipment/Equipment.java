package com.lablink.equipment;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "equipment")
public class Equipment {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @Column(nullable = false, length = 255)
    private String name;

    @Column(columnDefinition = "text")
    private String description;

    @Column(name = "serial_number", unique = true, length = 100)
    private String serialNumber;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EquipmentStatus status = EquipmentStatus.AVAILABLE;

    @Column(name = "image_url", length = 500)
    private String imageUrl;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt = Instant.now();

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt = Instant.now();

    public Equipment() {}

    @PreUpdate
    public void onUpdate() { this.updatedAt = Instant.now(); }

    public UUID getId()                  { return id; }
    public Category getCategory()        { return category; }
    public void setCategory(Category v)  { this.category = v; }
    public String getName()              { return name; }
    public void setName(String v)        { this.name = v; }
    public String getDescription()       { return description; }
    public void setDescription(String v) { this.description = v; }
    public String getSerialNumber()      { return serialNumber; }
    public void setSerialNumber(String v){ this.serialNumber = v; }
    public EquipmentStatus getStatus()   { return status; }
    public void setStatus(EquipmentStatus v) { this.status = v; }
    public String getImageUrl()          { return imageUrl; }
    public void setImageUrl(String v)    { this.imageUrl = v; }
    public Instant getCreatedAt()        { return createdAt; }
    public Instant getUpdatedAt()        { return updatedAt; }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private Category category;
        private String name, description, serialNumber, imageUrl;
        private EquipmentStatus status = EquipmentStatus.AVAILABLE;

        public Builder category(Category v)        { this.category = v; return this; }
        public Builder name(String v)              { this.name = v; return this; }
        public Builder description(String v)       { this.description = v; return this; }
        public Builder serialNumber(String v)      { this.serialNumber = v; return this; }
        public Builder imageUrl(String v)          { this.imageUrl = v; return this; }
        public Builder status(EquipmentStatus v)   { this.status = v; return this; }

        public Equipment build() {
            Equipment e = new Equipment();
            e.category     = this.category;
            e.name         = this.name;
            e.description  = this.description;
            e.serialNumber = this.serialNumber;
            e.imageUrl     = this.imageUrl;
            e.status       = this.status != null ? this.status : EquipmentStatus.AVAILABLE;
            return e;
        }
    }
}
