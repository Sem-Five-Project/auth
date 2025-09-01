// // package com.edu.tutor_platform.tutorprofile.repository;

// // import com.edu.tutor_platform.tutorprofile.entity.TutorProfile;
// // import org.springframework.data.jpa.repository.JpaRepository;

// // import java.util.Optional;

// // public interface TutorProfileRepository extends JpaRepository<TutorProfile, Long> {
// //     Optional<TutorProfile> findByUserId(Long userId);
// // }
// package com.edu.tutor_platform.tutorprofile.repository;

// import com.edu.tutor_platform.tutorprofile.entity.TutorProfile;
// import org.springframework.data.jpa.repository.JpaRepository;
// import org.springframework.data.jpa.repository.JpaSpecificationExecutor; // <-- ADD THIS IMPORT

// public interface TutorProfileRepository extends JpaRepository<TutorProfile, Long>, JpaSpecificationExecutor<TutorProfile> { // <-- AND EXTEND HERE
// }
package com.edu.tutor_platform.tutorprofile.repository;

import com.edu.tutor_platform.tutorprofile.entity.TutorProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor; // 1. ADD THIS IMPORT

// 2. EXTEND THE JpaSpecificationExecutor INTERFACE
public interface TutorProfileRepository extends JpaRepository<TutorProfile, Long>, JpaSpecificationExecutor<TutorProfile> {
}
