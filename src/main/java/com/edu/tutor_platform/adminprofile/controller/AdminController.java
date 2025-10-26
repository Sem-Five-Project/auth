package com.edu.tutor_platform.adminprofile.controller;


import com.edu.tutor_platform.adminprofile.dto.HomePageDto;
import com.edu.tutor_platform.adminprofile.service.AdminService;
import com.edu.tutor_platform.studentprofile.dto.StudentStatsDto;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin")
public class AdminController {

    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/homepage")
    public ResponseEntity<HomePageDto> getHomepage() {
        HomePageDto homepage = adminService.getHomePageData();
        return ResponseEntity.ok(homepage);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/faqs")
    public ResponseEntity<String> manageFaqs() {
        return ResponseEntity.ok("FAQs managed");
    }



}