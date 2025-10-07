package com.edu.tutor_platform.faq.repository;

import com.edu.tutor_platform.faq.entity.Faq;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface FaqRepository extends JpaRepository<Faq, Long> {
}