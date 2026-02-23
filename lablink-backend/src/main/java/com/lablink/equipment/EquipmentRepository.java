package com.lablink.equipment;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface EquipmentRepository extends JpaRepository<Equipment, UUID> {

    /**
     * Paginated search with optional filters.
     * Uses LOWER() for case-insensitive name/description search.
     */
    @Query("""
        SELECT e FROM Equipment e
        JOIN FETCH e.category c
        WHERE (:search   IS NULL OR LOWER(e.name) LIKE LOWER(CONCAT('%', :search, '%'))
                                 OR LOWER(e.description) LIKE LOWER(CONCAT('%', :search, '%')))
          AND (:status     IS NULL OR e.status     = :status)
          AND (:categoryId IS NULL OR c.id          = :categoryId)
        """)
    Page<Equipment> findAllFiltered(
            @Param("search")     String search,
            @Param("status")     EquipmentStatus status,
            @Param("categoryId") UUID categoryId,
            Pageable pageable
    );
}
