package com.lablink.borrow;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface BorrowRepository extends JpaRepository<BorrowRecord, UUID> {

    /** All active borrows for a specific user — used by /borrow/my-items */
    @Query("""
        SELECT b FROM BorrowRecord b
        JOIN FETCH b.equipment e
        JOIN FETCH e.category
        WHERE b.user.id = :userId
          AND b.status IN ('ACTIVE', 'OVERDUE')
        ORDER BY b.expectedReturnDate ASC
        """)
    List<BorrowRecord> findActiveByUserId(@Param("userId") UUID userId);

    /** Check for an existing active borrow on a specific equipment item */
    Optional<BorrowRecord> findByEquipmentIdAndStatus(UUID equipmentId, BorrowStatus status);

    /** All records regardless of status — used by admin panel */
    @Query("""
        SELECT b FROM BorrowRecord b
        JOIN FETCH b.user u
        JOIN FETCH b.equipment e
        ORDER BY b.borrowDate DESC
        """)
    List<BorrowRecord> findAllWithDetails();
}
