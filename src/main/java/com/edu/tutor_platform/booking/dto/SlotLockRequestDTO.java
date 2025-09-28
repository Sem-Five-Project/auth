package com.edu.tutor_platform.booking.dto;

import lombok.Data;
import java.util.List;

@Data
public class SlotLockRequestDTO {
    private List<Long> slotIds;
}