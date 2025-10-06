// package com.edu.tutor_platform.booking.repository;

// import org.springframework.data.jpa.repository.Query;
// import org.springframework.data.repository.Repository;
// import org.springframework.data.repository.query.Param;
// import com.edu.tutor_platform.booking.repository.TutorSlotFunctionProjection;
// import org.springframework.data.jpa.repository.Query;
// import org.springframework.data.repository.query.Param;
// import java.util.List;

// public interface MonthlyRecurringSlotRepository extends Repository<TutorSlotFunctionProjection, Long> {

//  @Query(value = """
//         SELECT 
//             slot_id    AS slotId,
//             date       AS date,
//             start_time AS startTime,
//             end_time   AS endTime,
//             status     AS status
//         FROM get_tutor_slots(
//             :tutorId,
//             :recurring,
//             :weekday,
//             :month,
//             :year
//         )
//         """, nativeQuery = true)
//     List<TutorSlotFunctionProjection> findTutorSlots(
//             @Param("tutorId") Long tutorId,
//             @Param("recurring") boolean recurring,
//             @Param("weekday") String weekday,
//             @Param("month") Integer month,
//             @Param("year") Integer year);
// }