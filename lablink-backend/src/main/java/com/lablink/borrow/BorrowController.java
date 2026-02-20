package com.lablink.borrow;

import com.lablink.auth.User;
import com.lablink.common.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
public class BorrowController {

    private final BorrowService borrowService;

    public BorrowController(BorrowService borrowService) {
        this.borrowService = borrowService;
    }

    @PostMapping("/api/v1/borrow")
    public ResponseEntity<ApiResponse<BorrowResponse>> borrow(
            @Valid @RequestBody BorrowRequest request,
            @AuthenticationPrincipal User currentUser) {
        BorrowResponse response = borrowService.borrow(currentUser.getId(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok(response));
    }

    @GetMapping("/api/v1/borrow/my-items")
    public ResponseEntity<ApiResponse<MyBorrowsResponse>> getMyBorrows(
            @AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(ApiResponse.ok(borrowService.getMyBorrows(currentUser.getId())));
    }

    @GetMapping("/api/v1/admin/borrows")
    public ResponseEntity<ApiResponse<AllBorrowsResponse>> getAllBorrows() {
        return ResponseEntity.ok(ApiResponse.ok(borrowService.getAllBorrows()));
    }

    @PostMapping("/api/v1/admin/return/{recordId}")
    public ResponseEntity<ApiResponse<Map<String, String>>> verifyReturn(
            @PathVariable UUID recordId,
            @RequestBody(required = false) ReturnRequest request) {
        String notes = (request != null) ? request.getConditionNotes() : null;
        borrowService.verifyReturn(recordId, notes);
        return ResponseEntity.ok(ApiResponse.ok(Map.of(
                "message",    "Item marked as returned",
                "itemStatus", "AVAILABLE"
        )));
    }
}
