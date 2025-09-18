package com.edu.tutor_platform.booking.entity;
import com.edu.tutor_platform.booking.enums.SlotStatus;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.hibernate.annotations.JdbcType;
import org.hibernate.dialect.PostgreSQLEnumJdbcType;

@Entity
@Table(name = "slot_instance")
@Data
@ToString(exclude = {"tutorAvailability", "bookings"})
@EqualsAndHashCode(exclude = {"tutorAvailability", "bookings"})
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SlotInstance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "slot_id")
    private Long slotId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "availability_id", nullable = false)
    private TutorAvailability tutorAvailability;

    @Column(name = "slot_date", nullable = false)
    private LocalDate slotDate;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @JdbcType(PostgreSQLEnumJdbcType.class)
    @Column(name = "status", nullable = false, columnDefinition = "slot_status")
    private SlotStatus status = SlotStatus.AVAILABLE;

    @Column(name = "locked_until")
    private LocalDateTime lockedUntil;

    @Column(name = "last_reserved_student_id")
    private Long lastReservedStudentId;

    @Builder.Default
    @OneToMany(mappedBy = "slotInstance", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Booking> bookings = new ArrayList<>();
}