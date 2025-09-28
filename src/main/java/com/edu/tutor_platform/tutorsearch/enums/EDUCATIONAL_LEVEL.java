package com.edu.tutor_platform.tutorsearch.enums;

public enum EDUCATIONAL_LEVEL {
    PRIMARY_GRADE_1_5("PRIMARY/GRADE 1-5"),
    SECONDARY_GRADE_6_11("SECONDARY/GRADE 6-11"),
    HIGHSCHOOL_ADVANCED_LEVEL("HIGHSCHOOL/ADVANCED_LEVEL"),
    UNDERGRADUATE("UNDERGRADUATE"),
    POSTGRADUATE("POSTGRADUATE"),
    DOCTORATE("DOCTORATE");

    private final String label;

    EDUCATIONAL_LEVEL(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
