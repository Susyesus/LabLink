package com.lablink.borrow;

import java.util.List;

public class MyBorrowsResponse {

    private final List<BorrowRecordDto> activeBorrows;

    MyBorrowsResponse(List<BorrowRecordDto> activeBorrows) {
        this.activeBorrows = activeBorrows;
    }

    public List<BorrowRecordDto> getActiveBorrows() { return activeBorrows; }
}
