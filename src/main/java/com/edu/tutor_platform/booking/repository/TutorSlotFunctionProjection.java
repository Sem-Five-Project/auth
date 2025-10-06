package com.edu.tutor_platform.booking.repository;

import java.time.LocalDate;
import java.time.LocalTime;

public interface TutorSlotFunctionProjection {
    Long getSlotId();
    LocalDate getDate();
    LocalTime getStartTime();
    LocalTime getEndTime();
}