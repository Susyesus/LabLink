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

    @Query(value = """
        SELECT e.* FROM equipment e
        LEFT JOIN categories c ON e.category_id = c.id
        WHERE (?1 IS NULL OR ?1 = '' 
               OR e.name ILIKE CONCAT('%', CAST(?1 AS TEXT), '%') 
               OR e.description ILIKE CONCAT('%', CAST(?1 AS TEXT), '%'))
          AND (?2 IS NULL OR e.status = CAST(CAST(?2 AS TEXT) AS equipment_status))
          AND (?3 IS NULL OR e.category_id = CAST(?3 AS UUID))
        """, 
        countQuery = """
        SELECT count(*) FROM equipment e
        WHERE (?1 IS NULL OR ?1 = '' 
               OR e.name ILIKE CONCAT('%', CAST(?1 AS TEXT), '%') 
               OR e.description ILIKE CONCAT('%', CAST(?1 AS TEXT), '%'))
          AND (?2 IS NULL OR e.status = CAST(CAST(?2 AS TEXT) AS equipment_status))
          AND (?3 IS NULL OR e.category_id = CAST(?3 AS UUID))
        """,
        nativeQuery = true)
    Page<Equipment> findAllFiltered(
            String search, 
            String status, 
            UUID categoryId, 
            Pageable pageable
    );
}