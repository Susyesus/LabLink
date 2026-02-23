package com.lablink.borrow;

import java.util.UUID;

public class BorrowRecordDto {

    private final UUID id;
    private final String itemName, imageUrl, borrowDate, expectedReturnDate, actualReturnDate;
    private final UUID equipmentId;
    private final BorrowStatus status;
    private final String purpose, remarks;

    BorrowRecordDto(UUID id, String itemName, UUID equipmentId, String imageUrl,
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

    public UUID getId()                   { return id; }
    public String getItemName()           { return itemName; }
    public UUID getEquipmentId()          { return equipmentId; }
    public String getImageUrl()           { return imageUrl; }
    public String getBorrowDate()         { return borrowDate; }
    public String getExpectedReturnDate() { return expectedReturnDate; }
    public String getActualReturnDate()   { return actualReturnDate; }
    public BorrowStatus getStatus()       { return status; }
    public String getPurpose()            { return purpose; }
    public String getRemarks()            { return remarks; }
}
