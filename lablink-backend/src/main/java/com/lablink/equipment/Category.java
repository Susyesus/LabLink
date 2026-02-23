package com.lablink.equipment;

import jakarta.persistence.*;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "categories")
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, unique = true, length = 100)
    private String name;

    @Column(columnDefinition = "text")
    private String description;

    @OneToMany(mappedBy = "category", fetch = FetchType.LAZY)
    private List<Equipment> equipment;

    public Category() {}

    public UUID getId()                  { return id; }
    public String getName()              { return name; }
    public void setName(String v)        { this.name = v; }
    public String getDescription()       { return description; }
    public void setDescription(String v) { this.description = v; }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private String name, description;

        public Builder name(String v)        { this.name = v; return this; }
        public Builder description(String v) { this.description = v; return this; }

        public Category build() {
            Category c = new Category();
            c.name        = this.name;
            c.description = this.description;
            return c;
        }
    }
}
