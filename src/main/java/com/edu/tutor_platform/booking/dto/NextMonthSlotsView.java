package com.edu.tutor_platform.booking.dto;

import lombok.Builder;
import lombok.Value;

import java.util.List;

@Value
@Builder
public class NextMonthSlotsView {
    List<Long> allSlotIds;
    List<NextMonthSlotRespondDTO> slots;
}
