package com.edu.tutor_platform.faq.controller;

import com.edu.tutor_platform.faq.Dto.FaqDto;
import com.edu.tutor_platform.faq.Dto.FaqStatsDto;
import com.edu.tutor_platform.faq.service.FaqService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/faq")
public class FaqController {

    private final FaqService faqService;

    public FaqController(FaqService faqService) {
        this.faqService = faqService;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/stats")
    public ResponseEntity<FaqStatsDto> getFaqStats() {
        return ResponseEntity.ok(faqService.getFaqStats());
    }

    @GetMapping
    public ResponseEntity<List<FaqDto>> getAllFaqs() {
        return ResponseEntity.ok(faqService.getFaqs());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<FaqDto> createFaq(@RequestBody FaqDto faqDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(faqService.createFaq(faqDto));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<FaqDto> updateFaq(@PathVariable Long id, @RequestBody FaqDto faqDto) {
        faqDto.setFaqId(id);
        return ResponseEntity.ok(faqService.updateFaq(faqDto));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFaq(@PathVariable Long id) {
        faqService.deleteFaq(id);
        return ResponseEntity.noContent().build();
    }
}
