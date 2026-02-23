package com.lablink.borrow;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.UUID;

public class BorrowRequest {

    @NotNull(message = "Equipment ID is required")
    private UUID equipmentId;

    @NotNull(message = "Expected return date is required")
    @Future(message = "Return date must be in the future")
    private LocalDate expectedReturnDate;

    private String purpose;

    public UUID getEquipmentId()                  { return equipmentId; }
    public void setEquipmentId(UUID v)            { this.equipmentId = v; }
    public LocalDate getExpectedReturnDate()       { return expectedReturnDate; }
    public void setExpectedReturnDate(LocalDate v) { this.expectedReturnDate = v; }
    public String getPurpose()                     { return purpose; }
    public void setPurpose(String v)               { this.purpose = v; }
}
