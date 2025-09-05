package com.edu.tutor_platform.clazz.entity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "class_doc")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClassDoc {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "doc_id")
    private Long docId;

    @Column(name = "class_id")
    private Long classId;

    @Column(name = "doc_type", nullable = false)
    private String docType;

    @Column(name = "link", nullable = false, columnDefinition = "text")
    private String link;
}
