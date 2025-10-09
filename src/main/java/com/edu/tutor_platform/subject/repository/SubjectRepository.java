package com.edu.tutor_platform.subject.repository;

import com.edu.tutor_platform.subject.entity.Subject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.edu.tutor_platform.tutorsearch.filter.enums.EDUCATION_LEVEL;
import com.edu.tutor_platform.tutorsearch.filter.enums.STREAM_TYPE;
import java.util.List;
import java.util.Optional;

@Repository
public interface SubjectRepository extends JpaRepository<Subject, Long> {

    /**
     * Find subject by name (case insensitive)
     */
    @Query("SELECT s FROM Subject s WHERE LOWER(s.name) = LOWER(:name)")
    Optional<Subject> findByNameIgnoreCase(@Param("name") String name);

    /**
     * Find subjects by partial name match
     */
    @Query("SELECT s FROM Subject s WHERE LOWER(s.name) LIKE LOWER(CONCAT('%', :name, '%'))")
    List<Subject> findByNameContainingIgnoreCase(@Param("name") String name);

    /**
     * Find all subjects ordered by name
     */
    @Query("SELECT s FROM Subject s ORDER BY s.name ASC")
    List<Subject> findAllOrderByName();

    /**
     * Check if subject exists by name
     */
    boolean existsByNameIgnoreCase(String name);


    List<Subject> findByEducationLevelAndStreamTypeOrderByNameAsc(EDUCATION_LEVEL educationLevel, STREAM_TYPE streamType);
    List<Subject> findByEducationLevelOrderByNameAsc(EDUCATION_LEVEL educationLevel);
    List<Subject> findByStreamTypeOrderByNameAsc(STREAM_TYPE streamType);

}
