package com.edu.tutor_platform.rating.enums;

public enum RatingEnum {
    ONE(1),
    TWO(2),
    THREE(3),
    FOUR(4),
    FIVE(5);

    private final int value;

    RatingEnum(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static RatingEnum fromValue(int value) {
        for (RatingEnum rating : RatingEnum.values()) {
            if (rating.getValue() == value) {
                return rating;
            }
        }
        throw new IllegalArgumentException("Invalid rating value: " + value);
    }
}