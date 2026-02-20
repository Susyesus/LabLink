package com.lablink.borrow;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

// ── Request DTOs ──────────────────────────────────────────────

class BorrowRequest {
    @NotNull(message = "Equipment ID is required")
    private UUID equipmentId;

    @NotNull(message = "Expected return date is required")
    @Future(message = "Return date must be in the future")
    private LocalDate expectedReturnDate;

    private String purpose;

    public UUID getEquipmentId()              { return equipmentId; }
    public void setEquipmentId(UUID v)        { this.equipmentId = v; }
    public LocalDate getExpectedReturnDate()  { return expectedReturnDate; }
    public void setExpectedReturnDate(LocalDate v) { this.expectedReturnDate = v; }
    public String getPurpose()               { return purpose; }
    public void setPurpose(String v)         { this.purpose = v; }
}

class ReturnRequest {
    private String conditionNotes;
    public String getConditionNotes()         { return conditionNotes; }
    public void setConditionNotes(String v)   { this.conditionNotes = v; }
}

// ── Response DTOs ─────────────────────────────────────────────

class BorrowRecordDto {
    private final UUID id;
    private final String itemName, imageUrl, borrowDate, expectedReturnDate, actualReturnDate;
    private final UUID equipmentId;
    private final BorrowStatus status;
    private final String purpose, remarks;

    private BorrowRecordDto(UUID id, String itemName, UUID equipmentId, String imageUrl,
                            String borrowDate, String expectedReturnDate, String actualReturnDate,
                            BorrowStatus status, String purpose, String remarks) {
        this.id = id; this.itemName = itemName; this.equipmentId = equipmentId;
        this.imageUrl = imageUrl; this.borrowDate = borrowDate;
        this.expectedReturnDate = expectedReturnDate; this.actualReturnDate = actualReturnDate;
        this.status = status; this.purpose = purpose; this.remarks = remarks;
    }

    static BorrowRecordDto from(BorrowRecord b) {
        return new BorrowRecordDto(
                b.getId(),
                b.getEquipment().getName(),
                b.getEquipment().getId(),
                b.getEquipment().getImageUrl(),
                b.getBorrowDate().toString(),
                b.getExpectedReturnDate().toString(),
                b.getActualReturnDate() != null ? b.getActualReturnDate().toString() : null,
                b.getStatus(),
                b.getPurpose(),
                b.getRemarks()
        );
    }

    public UUID getId()                  { return id; }
    public String getItemName()          { return itemName; }
    public UUID getEquipmentId()         { return equipmentId; }
    public String getImageUrl()          { return imageUrl; }
    public String getBorrowDate()        { return borrowDate; }
    public String getExpectedReturnDate(){ return expectedReturnDate; }
    public String getActualReturnDate()  { return actualReturnDate; }
    public BorrowStatus getStatus()      { return status; }
    public String getPurpose()           { return purpose; }
    public String getRemarks()           { return remarks; }
}

class BorrowResponse {
    private final String message;
    private final BorrowSummary borrowRecord;

    BorrowResponse(String message, BorrowSummary borrowRecord) {
        this.message      = message;
        this.borrowRecord = borrowRecord;
    }

    public String getMessage()          { return message; }
    public BorrowSummary getBorrowRecord() { return borrowRecord; }
}

class BorrowSummary {
    private final UUID id;
    private final String borrowDate;
    private final LocalDate expectedReturnDate;
    private final BorrowStatus status;

    BorrowSummary(UUID id, String borrowDate, LocalDate expectedReturnDate, BorrowStatus status) {
        this.id = id; this.borrowDate = borrowDate;
        this.expectedReturnDate = expectedReturnDate; this.status = status;
    }

    public UUID getId()                       { return id; }
    public String getBorrowDate()             { return borrowDate; }
    public LocalDate getExpectedReturnDate()  { return expectedReturnDate; }
    public BorrowStatus getStatus()           { return status; }
}

class MyBorrowsResponse {
    private final List<BorrowRecordDto> activeBorrows;
    MyBorrowsResponse(List<BorrowRecordDto> activeBorrows) { this.activeBorrows = activeBorrows; }
    public List<BorrowRecordDto> getActiveBorrows() { return activeBorrows; }
}

class AllBorrowsResponse {
    private final List<BorrowRecordDto> borrows;
    AllBorrowsResponse(List<BorrowRecordDto> borrows) { this.borrows = borrows; }
    public List<BorrowRecordDto> getBorrows() { return borrows; }
}
