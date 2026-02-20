package com.lablink.borrow;

import com.lablink.auth.User;
import com.lablink.auth.UserRepository;
import com.lablink.equipment.Equipment;
import com.lablink.equipment.EquipmentRepository;
import com.lablink.equipment.EquipmentStatus;
import com.lablink.exception.BusinessException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
public class BorrowService {

    private final BorrowRepository    borrowRepository;
    private final EquipmentRepository equipmentRepository;
    private final UserRepository      userRepository;

    public BorrowService(BorrowRepository borrowRepository,
                         EquipmentRepository equipmentRepository,
                         UserRepository userRepository) {
        this.borrowRepository    = borrowRepository;
        this.equipmentRepository = equipmentRepository;
        this.userRepository      = userRepository;
    }

    @Transactional
    public BorrowResponse borrow(UUID userId, BorrowRequest request) {
        LocalDate maxReturn = LocalDate.now().plusDays(7);
        if (request.getExpectedReturnDate().isAfter(maxReturn)) {
            throw BusinessException.badRequest("TRANS-001", "Return date cannot exceed 7 days from today");
        }

        Equipment equipment = equipmentRepository.findById(request.getEquipmentId())
                .orElseThrow(() -> new EntityNotFoundException("Equipment not found"));

        if (equipment.getStatus() != EquipmentStatus.AVAILABLE) {
            throw BusinessException.conflict("TRANS-002", "This item is no longer available");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        BorrowRecord record = BorrowRecord.builder()
                .user(user)
                .equipment(equipment)
                .expectedReturnDate(request.getExpectedReturnDate())
                .purpose(request.getPurpose())
                .status(BorrowStatus.ACTIVE)
                .build();

        borrowRepository.save(record);

        equipment.setStatus(EquipmentStatus.UNAVAILABLE);
        equipmentRepository.save(equipment);

        return new BorrowResponse(
                "Reservation successful. Please pick up at the Lab Admin desk.",
                new BorrowSummary(record.getId(), record.getBorrowDate().toString(),
                        record.getExpectedReturnDate(), record.getStatus())
        );
    }

    public MyBorrowsResponse getMyBorrows(UUID userId) {
        List<BorrowRecordDto> dtos = borrowRepository.findActiveByUserId(userId)
                .stream().map(BorrowRecordDto::from).toList();
        return new MyBorrowsResponse(dtos);
    }

    @PreAuthorize("hasRole('ADMIN')")
    public AllBorrowsResponse getAllBorrows() {
        List<BorrowRecordDto> dtos = borrowRepository.findAllWithDetails()
                .stream().map(BorrowRecordDto::from).toList();
        return new AllBorrowsResponse(dtos);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public void verifyReturn(UUID recordId, String conditionNotes) {
        BorrowRecord record = borrowRepository.findById(recordId)
                .orElseThrow(() -> new EntityNotFoundException("Borrow record not found"));

        if (record.getStatus() != BorrowStatus.ACTIVE && record.getStatus() != BorrowStatus.OVERDUE) {
            throw BusinessException.badRequest("TRANS-003", "This record is already closed");
        }

        record.setStatus(BorrowStatus.RETURNED);
        record.setActualReturnDate(Instant.now());
        record.setRemarks(conditionNotes);
        borrowRepository.save(record);

        Equipment equipment = record.getEquipment();
        equipment.setStatus(EquipmentStatus.AVAILABLE);
        equipmentRepository.save(equipment);
    }
}
