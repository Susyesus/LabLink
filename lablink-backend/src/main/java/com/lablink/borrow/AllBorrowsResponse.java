package com.lablink.borrow;

import java.util.List;

public class AllBorrowsResponse {

    private final List<BorrowRecordDto> borrows;

    AllBorrowsResponse(List<BorrowRecordDto> borrows) {
        this.borrows = borrows;
    }

    public List<BorrowRecordDto> getBorrows() { return borrows; }
}
