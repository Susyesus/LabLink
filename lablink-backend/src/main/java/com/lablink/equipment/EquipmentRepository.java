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
     * Paginated equipment search with optional filters.
     *
     * Why native SQL instead of JPQL:
     *   Hibernate 6 infers NULL JPQL parameters as bytea inside LOWER(), causing
     *   "function lower(bytea) does not exist". Native SQL + ILIKE avoids this.
     *
     * Why no :: PostgreSQL cast operator:
     *   Hibernate's native query parser intercepts any token starting with ':'
     *   and treats it as a named parameter — including the second ':' in '::'.
     *   After substituting :categoryId with ?, the remaining ::uuid becomes :uuid,
     *   which PostgreSQL receives as invalid syntax.
     *   Solution: use only ANSI CAST(x AS type) — never :: in @Query strings.
     *
     * Why CAST(:categoryId AS varchar):
     *   Spring Data passes UUID as java.util.UUID. With nativeQuery=true, Hibernate
     *   cannot infer the SQL type, so we cast to varchar first, then to uuid.
     *   CAST(CAST(:categoryId AS varchar) AS uuid) is verbose but unambiguous.
     */
    @Query(
        value = """
            SELECT e.*
            FROM   equipment e
            JOIN   categories c ON c.id = e.category_id
            WHERE  (CAST(:search AS text) IS NULL
                    OR e.name        ILIKE '%' || CAST(:search AS text) || '%'
                    OR e.description ILIKE '%' || CAST(:search AS text) || '%')
              AND  (CAST(:status AS text) IS NULL
                    OR e.status = CAST(:status AS text))
              AND  (:categoryId IS NULL
                    OR e.category_id = CAST(CAST(:categoryId AS varchar) AS uuid))
            ORDER  BY e.name
            """,
        countQuery = """
            SELECT COUNT(*)
            FROM   equipment e
            JOIN   categories c ON c.id = e.category_id
            WHERE  (CAST(:search AS text) IS NULL
                    OR e.name        ILIKE '%' || CAST(:search AS text) || '%'
                    OR e.description ILIKE '%' || CAST(:search AS text) || '%')
              AND  (CAST(:status AS text) IS NULL
                    OR e.status = CAST(:status AS text))
              AND  (:categoryId IS NULL
                    OR e.category_id = CAST(CAST(:categoryId AS varchar) AS uuid))
            """,
        nativeQuery = true
    )
    Page<Equipment> findAllFiltered(
            @Param("search")     String search,
            @Param("status")     String status,
            @Param("categoryId") UUID   categoryId,
            Pageable pageable
    );
}
