package com.lablink.equipment;

import com.lablink.exception.BusinessException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class EquipmentService {

    private final EquipmentRepository equipmentRepository;
    private final CategoryRepository  categoryRepository;

    public EquipmentService(EquipmentRepository equipmentRepository,
                            CategoryRepository categoryRepository) {
        this.equipmentRepository = equipmentRepository;
        this.categoryRepository  = categoryRepository;
    }

    public EquipmentPageResponse getAll(String search, String statusStr, UUID categoryId, int page, int limit) {
        EquipmentStatus status = (statusStr != null) ? EquipmentStatus.valueOf(statusStr) : null;
        PageRequest pageable = PageRequest.of(page - 1, limit, Sort.by("name").ascending());

        Page<Equipment> result = equipmentRepository.findAllFiltered(
                (search != null && !search.isBlank()) ? search : null,
                status, categoryId, pageable
        );

        List<EquipmentDto> dtos = result.getContent().stream().map(EquipmentDto::from).toList();
        return new EquipmentPageResponse(dtos,
                new PaginationDto(page, limit, result.getTotalElements(), result.getTotalPages()));
    }

    public EquipmentDto getById(UUID id) {
        Equipment equipment = equipmentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Equipment not found: " + id));
        return EquipmentDto.from(equipment);
    }

    public List<CategoryDto> getCategories() {
        return categoryRepository.findAll().stream().map(CategoryDto::from).toList();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public EquipmentDto create(CreateEquipmentRequest request) {
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> BusinessException.notFound("DB-001", "Category not found"));

        Equipment equipment = Equipment.builder()
                .name(request.getName())
                .description(request.getDescription())
                .serialNumber(request.getSerialNumber())
                .category(category)
                .imageUrl(request.getImageUrl())
                .status(EquipmentStatus.AVAILABLE)
                .build();

        return EquipmentDto.from(equipmentRepository.save(equipment));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public EquipmentDto update(UUID id, UpdateEquipmentRequest request) {
        Equipment equipment = equipmentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Equipment not found: " + id));

        if (request.getName()        != null) equipment.setName(request.getName());
        if (request.getDescription() != null) equipment.setDescription(request.getDescription());
        if (request.getStatus()      != null) equipment.setStatus(request.getStatus());
        if (request.getImageUrl()    != null) equipment.setImageUrl(request.getImageUrl());
        if (request.getCategoryId()  != null) {
            Category cat = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> BusinessException.notFound("DB-001", "Category not found"));
            equipment.setCategory(cat);
        }

        return EquipmentDto.from(equipmentRepository.save(equipment));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public void delete(UUID id) {
        if (!equipmentRepository.existsById(id)) {
            throw new EntityNotFoundException("Equipment not found: " + id);
        }
        equipmentRepository.deleteById(id);
    }
}
