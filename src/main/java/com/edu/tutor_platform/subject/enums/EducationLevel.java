package com.edu.tutor_platform.subject.enums;

public enum EducationLevel {
    DOCTORATE,
    HIGHSCHOOL_ADVANCED_LEVEL,
    POSTGRADUATE,
    PRIMARY_GRADE_1_5,
    SECONDARY_GRADE_6_11,
    UNDERGRADUATE;

    @Override
    public String toString() {
        switch (this) {
            case HIGHSCHOOL_ADVANCED_LEVEL:
                return "HIGHSCHOOL/ADVANCED_LEVEL";
            default:
                return this.name();
        }
    }
}
