package com.lablink.equipment;

import java.util.List;

public class EquipmentPageResponse {

    private final List<EquipmentDto> equipment;
    private final PaginationDto pagination;

    EquipmentPageResponse(List<EquipmentDto> equipment, PaginationDto pagination) {
        this.equipment  = equipment;
        this.pagination = pagination;
    }

    public List<EquipmentDto> getEquipment() { return equipment; }
    public PaginationDto getPagination()     { return pagination; }
}
