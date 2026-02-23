package com.lablink.borrow;

public class BorrowResponse {

    private final String message;
    private final BorrowSummary borrowRecord;

    BorrowResponse(String message, BorrowSummary borrowRecord) {
        this.message      = message;
        this.borrowRecord = borrowRecord;
    }

    public String getMessage()             { return message; }
    public BorrowSummary getBorrowRecord() { return borrowRecord; }
}
