package com.edu.tutor_platform.studentprofile.repository;

import com.edu.tutor_platform.studentprofile.dto.StudentProfilePaymentRespondDTO;
import com.edu.tutor_platform.studentprofile.entity.StudentProfile;
import com.edu.tutor_platform.studentprofile.enums.StudentProfileStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface StudentProfileRepository extends JpaRepository<StudentProfile, Long> {

    Optional<StudentProfile> findByUserId(Long userId);

    Optional<StudentProfile> findByUser_Id(Long userId);

    Optional<StudentProfile> findByUserIdAndStatus(Long userId, StudentProfileStatus status);

    @Query("SELECT sp FROM StudentProfile sp JOIN sp.user u WHERE u.email = :email")
    Optional<StudentProfile> findByUser_Email(@Param("email") String email);

    List<StudentProfile> findByStatus(StudentProfileStatus status);

    @Query("SELECT sp FROM StudentProfile sp WHERE sp.status = com.edu.tutor_platform.studentprofile.enums.StudentProfileStatus.ACTIVE")
    List<StudentProfile> findActiveStudents();

    @Query("SELECT sp FROM StudentProfile sp WHERE sp.membership = :membership")
    List<StudentProfile> findByMembership(@Param("membership") String membership);

    boolean existsByUserId(Long userId);

    Long countByStatus(StudentProfileStatus studentProfileStatus);

    @Query("SELECT sp FROM StudentProfile sp JOIN sp.user u WHERE " +
            "(:name IS NULL OR LOWER(CONCAT(u.firstName, ' ', u.lastName)) LIKE LOWER(CONCAT('%', :name, '%'))) AND " +
            "(:username IS NULL OR LOWER(u.username) LIKE LOWER(CONCAT('%', :username, '%'))) AND " +
            "(:email IS NULL OR LOWER(u.email) LIKE LOWER(CONCAT('%', :email, '%'))) AND " +
            "(:studentId IS NULL OR sp.studentId = :studentId) AND " +
            "(:studentProfileStatus IS NULL OR sp.status = :studentProfileStatus)")
    Page<StudentProfile> searchByAdmin(
            @Param("name") String name,
            @Param("username") String username,
            @Param("email") String email,
            @Param("studentId") Long studentId,
            @Param("studentProfileStatus") StudentProfileStatus studentProfileStatus,
            Pageable pageable
    );

    @Query("SELECT COUNT(sp) FROM StudentProfile sp JOIN sp.user u " +
            "WHERE YEAR(u.createdAt) = YEAR(CURRENT_DATE) " +
            "AND MONTH(u.createdAt) = MONTH(CURRENT_DATE)")
    Long countNewStudentsThisMonth();

    @Query(nativeQuery = true, value = "SELECT * FROM public.get_student_payment_history(:studentId, :period)")
    List<StudentProfilePaymentRespondDTO> findPaymentHistoryByStudentId(@Param("studentId") Long studentId, @Param("period") String period);

        // Call Supabase/PostgreSQL function that returns nested JSONB, cast to text for transport
        @Query(value = "SELECT public.get_student_classes_with_details2(:studentId)::text", nativeQuery = true)
        String getStudentClassesWithDetailsJson(@Param("studentId") Long studentId);

        @Query(value = "SELECT public.get_student_upcoming_classes(:studentId)::text", nativeQuery = true)
        String getStudentUpcomingClassesJson(@Param("studentId") Long studentId);
}
