package com.lablink.equipment;

import com.lablink.common.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/equipment")
public class EquipmentController {

    private final EquipmentService equipmentService;

    public EquipmentController(EquipmentService equipmentService) {
        this.equipmentService = equipmentService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<EquipmentPageResponse>> getAll(
            @RequestParam(required = false)              String search,
            @RequestParam(required = false)              EquipmentStatus status,
            @RequestParam(required = false)              UUID   categoryId,
            @RequestParam(defaultValue = "1")            int    page,
            @RequestParam(name = "limit", defaultValue = "20") int limit) {
        return ResponseEntity.ok(ApiResponse.ok(
                equipmentService.getAll(search, status, categoryId, page, limit)));
    }

    @GetMapping("/categories")
    public ResponseEntity<ApiResponse<Map<String, List<CategoryDto>>>> getCategories() {
        return ResponseEntity.ok(ApiResponse.ok(
                Map.of("categories", equipmentService.getCategories())));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Map<String, EquipmentDto>>> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.ok(Map.of("item", equipmentService.getById(id))));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<Map<String, EquipmentDto>>> create(
            @Valid @RequestBody CreateEquipmentRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok(Map.of("item", equipmentService.create(request))));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Map<String, EquipmentDto>>> update(
            @PathVariable UUID id,
            @RequestBody UpdateEquipmentRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(Map.of("item", equipmentService.update(id, request))));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable UUID id) {
        equipmentService.delete(id);
        return ResponseEntity.ok(ApiResponse.ok(null));
    }
}
