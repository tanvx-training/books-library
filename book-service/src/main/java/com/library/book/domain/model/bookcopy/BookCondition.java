package com.library.book.domain.model.bookcopy;

public enum BookCondition {
    NEW("Brand new condition"),
    EXCELLENT("Excellent condition"),
    GOOD("Good condition with minor wear"),
    FAIR("Fair condition with noticeable wear"),
    POOR("Poor condition, significant damage");

    private final String description;

    BookCondition(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public boolean isAcceptableForLending() {
        return this != POOR;
    }

    public boolean requiresReplacement() {
        return this == POOR;
    }
}