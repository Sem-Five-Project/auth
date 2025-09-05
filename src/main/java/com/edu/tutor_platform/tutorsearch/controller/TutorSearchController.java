// // package com.edu.tutor_platform.tutorsearch.controller;

// // import com.edu.tutor_platform.tutorsearch.dto.TutorSearchDTO;
// // import com.edu.tutor_platform.tutorsearch.service.TutorSearchService;
// // import org.springframework.beans.factory.annotation.Autowired;
// // import org.springframework.data.domain.Page;
// // import org.springframework.data.domain.Pageable;
// // import org.springframework.http.ResponseEntity;
// // import org.springframework.web.bind.annotation.GetMapping;
// // import org.springframework.web.bind.annotation.RequestMapping;
// // import org.springframework.web.bind.annotation.RequestParam;
// // import org.springframework.web.bind.annotation.RestController;

// // @RestController
// // @RequestMapping("/api/tutors")
// // public class TutorSearchController {

// //     @Autowired
// //     private TutorSearchService tutorSearchService;

// //     @GetMapping("/search")
// //     public ResponseEntity<Page<TutorSearchDTO>> searchTutors(
// //             @RequestParam(required = false) Double minRating,
// //             @RequestParam(required = false) Integer minExperience,
// //             @RequestParam(required = false) Double minHourlyRate,
// //             Pageable pageable) { // Spring automatically provides this

// //         Page<TutorSearchDTO> results = tutorSearchService.findTutors(minRating, minExperience, minHourlyRate, pageable);
// //         return ResponseEntity.ok(results);
// //     }
// // }
// package com.edu.tutor_platform.tutorsearch.controller;

// import com.edu.tutor_platform.tutorsearch.dto.TutorSearchDTO;
// import com.edu.tutor_platform.tutorsearch.service.TutorSearchService;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.data.domain.Page;
// import org.springframework.data.domain.Pageable;
// import org.springframework.http.ResponseEntity;
// import org.springframework.web.bind.annotation.GetMapping;
// import org.springframework.web.bind.annotation.RequestMapping;
// import org.springframework.web.bind.annotation.RequestParam;
// import org.springframework.web.bind.annotation.RestController;

// @RestController
// @RequestMapping("/tutors")
// public class TutorSearchController {

//     @Autowired
//     private TutorSearchService tutorSearchService;

//     public TutorSearchController() {
//         System.out.println("TutorSearchController initialized with RequestMapping(/api/tutors)");
//     }

//     @GetMapping("/search")
//     public ResponseEntity<Page<TutorSearchDTO>> searchTutors(
//             @RequestParam(required = false) Double minRating,
//             @RequestParam(required = false) Integer minExperience,
//             @RequestParam(required = false) Double minHourlyRate,
//             Pageable pageable) {

//         System.out.println("Received search request with filters: " +
//                 "minRating=" + minRating +
//                 ", minExperience=" + minExperience +
//                 ", minHourlyRate=" + minHourlyRate +
//                 ", page=" + pageable.getPageNumber() +
//                 ", size=" + pageable.getPageSize());

//         Page<TutorSearchDTO> results = tutorSearchService.findTutors(minRating, minExperience, minHourlyRate, pageable);

//         System.out.println("Search results returned with total elements: " + results.getTotalElements());

//         return ResponseEntity.ok(results);
//     }
// }



package com.edu.tutor_platform.tutorsearch.controller;

import com.edu.tutor_platform.tutorsearch.dto.TutorCardDTO;
import com.edu.tutor_platform.tutorsearch.service.TutorSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/tutors")
public class TutorSearchController {

    @Autowired
    private TutorSearchService tutorSearchService;

    @GetMapping("/search")
    public ResponseEntity<Page<TutorCardDTO>> searchTutors(
            @RequestParam(required = false) Double minRating,
            @RequestParam(required = false) Integer minExperience,
            @RequestParam(required = false) Double minHourlyRate,
            @RequestParam(required = false) Double maxHourlyRate,
            @RequestParam(required = false) Double minClassCompletionRate,
            @RequestParam(required = false) String subjectName,
            @RequestParam(required = false) String tutorName,
            @PageableDefault(size = 10, sort = "rating") Pageable pageable) {

        Page<TutorCardDTO> results = tutorSearchService.searchTutors(
                minRating, minExperience, minHourlyRate, maxHourlyRate,
                minClassCompletionRate, subjectName, tutorName, pageable);
                
        return ResponseEntity.ok(results);
    }
}
