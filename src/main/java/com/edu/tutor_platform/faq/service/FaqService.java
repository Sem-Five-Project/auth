package com.edu.tutor_platform.faq.service;

import com.edu.tutor_platform.faq.Dto.FaqDto;
import com.edu.tutor_platform.faq.Dto.FaqStatsDto;
import com.edu.tutor_platform.faq.entity.Faq;
import com.edu.tutor_platform.faq.repository.FaqRepository;
import org.springframework.stereotype.Service;
import com.edu.tutor_platform.faq.execption.FaqNotFoundException;

import java.util.List;

@Service
public class FaqService {
    private final FaqRepository faqRepository;
    public FaqService(FaqRepository faqRepository) {
        this.faqRepository = faqRepository;
    }

    public FaqStatsDto getFaqStats() {
        long totalFaqs = faqRepository.count();
        long activeFaqs = faqRepository.findAll()
                .stream()
                .filter(Faq::getIsActive)
                .count();
        long categories =  2;

        return new FaqStatsDto(totalFaqs, activeFaqs, categories);
    }

    public List<FaqDto> getFaqs() {
        return faqRepository.findAll()
                .stream()
                .map(faq -> new FaqDto(
                        faq.getFaqId(),
                        faq.getQuestion(),
                        faq.getAnswer(),
                        faq.getCategory(),
                        faq.getIsActive(),
                        faq.getCreatedAt()
                ))
                .toList();
    }

    public FaqDto createFaq(FaqDto faqDto) {
        Faq faq = Faq.builder()
                .question(faqDto.getQuestion())
                .answer(faqDto.getAnswer())
                .category(faqDto.getCategory())
                .isActive(faqDto.getIsActive())
                .createdAt(faqDto.getCreatedAt())
                .build();

        Faq savedFaq = faqRepository.save(faq);

        return new FaqDto(
                savedFaq.getFaqId(),
                savedFaq.getQuestion(),
                savedFaq.getAnswer(),
                savedFaq.getCategory(),
                savedFaq.getIsActive(),
                savedFaq.getCreatedAt()
        );
    }

    public FaqDto updateFaq(FaqDto faqDto) {
        Faq faq = faqRepository.findById(faqDto.getFaqId())
                .orElseThrow(() -> new FaqNotFoundException("FAQ not found with id: " + faqDto.getFaqId()));

        faq.setQuestion(faqDto.getQuestion());
        faq.setAnswer(faqDto.getAnswer());
        faq.setCategory(faqDto.getCategory());
        faq.setIsActive(faqDto.getIsActive());

        Faq updatedFaq = faqRepository.save(faq);

        return new FaqDto(
                updatedFaq.getFaqId(),
                updatedFaq.getQuestion(),
                updatedFaq.getAnswer(),
                updatedFaq.getCategory(),
                updatedFaq.getIsActive(),
                updatedFaq.getCreatedAt()
        );
    }

    public void deleteFaq(Long id) {
        faqRepository.deleteById(id);
    }

}
