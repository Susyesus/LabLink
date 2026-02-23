package com.lablink.borrow;

import java.time.LocalDate;
import java.util.UUID;

public class BorrowSummary {

    private final UUID id;
    private final String borrowDate;
    private final LocalDate expectedReturnDate;
    private final BorrowStatus status;

    BorrowSummary(UUID id, String borrowDate, LocalDate expectedReturnDate, BorrowStatus status) {
        this.id = id; this.borrowDate = borrowDate;
        this.expectedReturnDate = expectedReturnDate; this.status = status;
    }

    public UUID getId()                      { return id; }
    public String getBorrowDate()            { return borrowDate; }
    public LocalDate getExpectedReturnDate() { return expectedReturnDate; }
    public BorrowStatus getStatus()          { return status; }
}
