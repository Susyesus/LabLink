package com.lablink;

import com.lablink.auth.User;
import com.lablink.auth.UserRepository;
import com.lablink.auth.UserRole;
import com.lablink.borrow.*;
import com.lablink.equipment.*;
import com.lablink.exception.BusinessException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for BorrowService core transaction logic.
 * How to run: mvn test
 */
@ExtendWith(MockitoExtension.class)
class BorrowServiceTest {

    @Mock private BorrowRepository    borrowRepository;
    @Mock private EquipmentRepository equipmentRepository;
    @Mock private UserRepository      userRepository;

    @InjectMocks private BorrowService borrowService;

    private User      testUser;
    private Equipment testEquipment;
    private UUID      userId;
    private UUID      equipmentId;

    @BeforeEach
    void setUp() {
        userId      = UUID.randomUUID();
        equipmentId = UUID.randomUUID();

        // Build User manually (no Lombok builder .id() — set via reflection workaround)
        testUser = User.builder()
                .email("student@cit.edu")
                .fullName("Test Student")
                .passwordHash("hash")
                .idNumber("21-1234-567")
                .role(UserRole.STUDENT)
                .build();

        // Build Category via constructor
        Category cat = new Category();
        cat.setName("Microcontrollers");

        // Build Equipment
        testEquipment = Equipment.builder()
                .name("Arduino Uno R4")
                .status(EquipmentStatus.AVAILABLE)
                .category(cat)
                .build();
    }

    @Test
    void borrow_successfulTransaction_createsRecordAndMarksUnavailable() {
        BorrowRequest req = new BorrowRequest();
        req.setEquipmentId(equipmentId);
        req.setExpectedReturnDate(LocalDate.now().plusDays(3));

        when(equipmentRepository.findById(equipmentId)).thenReturn(Optional.of(testEquipment));
        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(borrowRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(equipmentRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        BorrowResponse result = borrowService.borrow(userId, req);

        assertThat(result.getMessage()).contains("Reservation successful");
        assertThat(testEquipment.getStatus()).isEqualTo(EquipmentStatus.UNAVAILABLE);
        verify(borrowRepository).save(any(BorrowRecord.class));
        verify(equipmentRepository).save(testEquipment);
    }

    @Test
    void borrow_unavailableItem_throwsConflict() {
        testEquipment.setStatus(EquipmentStatus.UNAVAILABLE);
        when(equipmentRepository.findById(equipmentId)).thenReturn(Optional.of(testEquipment));

        BorrowRequest req = new BorrowRequest();
        req.setEquipmentId(equipmentId);
        req.setExpectedReturnDate(LocalDate.now().plusDays(2));

        assertThatThrownBy(() -> borrowService.borrow(userId, req))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("no longer available");

        verify(borrowRepository, never()).save(any());
    }

    @Test
    void borrow_returnDateExceeds7Days_throwsBadRequest() {
        when(equipmentRepository.findById(equipmentId)).thenReturn(Optional.of(testEquipment));

        BorrowRequest req = new BorrowRequest();
        req.setEquipmentId(equipmentId);
        req.setExpectedReturnDate(LocalDate.now().plusDays(10));

        assertThatThrownBy(() -> borrowService.borrow(userId, req))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("7 days");
    }

    @Test
    void verifyReturn_validActiveRecord_marksReturnedAndResetsAvailable() {
        BorrowRecord record = BorrowRecord.builder()
                .user(testUser)
                .equipment(testEquipment)
                .status(BorrowStatus.ACTIVE)
                .expectedReturnDate(LocalDate.now().plusDays(1))
                .build();

        UUID recordId = UUID.randomUUID();

        when(borrowRepository.findById(recordId)).thenReturn(Optional.of(record));
        when(borrowRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(equipmentRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        borrowService.verifyReturn(recordId, "Good condition");

        assertThat(record.getStatus()).isEqualTo(BorrowStatus.RETURNED);
        assertThat(record.getActualReturnDate()).isNotNull();
        assertThat(testEquipment.getStatus()).isEqualTo(EquipmentStatus.AVAILABLE);
    }

    @Test
    void verifyReturn_alreadyReturnedRecord_throwsBadRequest() {
        BorrowRecord record = BorrowRecord.builder()
                .user(testUser)
                .equipment(testEquipment)
                .status(BorrowStatus.RETURNED)
                .expectedReturnDate(LocalDate.now().minusDays(1))
                .build();

        UUID recordId = UUID.randomUUID();
        when(borrowRepository.findById(recordId)).thenReturn(Optional.of(record));

        assertThatThrownBy(() -> borrowService.verifyReturn(recordId, null))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("already closed");
    }
}
