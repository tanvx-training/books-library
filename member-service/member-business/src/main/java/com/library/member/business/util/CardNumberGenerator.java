package com.library.member.business.util;

import org.springframework.stereotype.Component;

import java.security.SecureRandom;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Component
public class CardNumberGenerator {

    private static final String CARD_PREFIX = "LC";
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyMM");
    private static final SecureRandom RANDOM = new SecureRandom();
    private static final int RANDOM_DIGITS = 6;

    public String generateCardNumber() {
        String dateComponent = LocalDate.now().format(DATE_FORMAT);
        String randomComponent = generateRandomDigits();
        
        return CARD_PREFIX + dateComponent + randomComponent;
    }

    private String generateRandomDigits() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < CardNumberGenerator.RANDOM_DIGITS; i++) {
            sb.append(RANDOM.nextInt(10));
        }
        return sb.toString();
    }

    public boolean isValidCardNumberFormat(String cardNumber) {
        if (cardNumber == null || cardNumber.length() != 10) {
            return false;
        }

        // Check prefix
        if (!cardNumber.startsWith(CARD_PREFIX)) {
            return false;
        }

        // Check if the rest are digits
        String numberPart = cardNumber.substring(2);
        return numberPart.matches("\\d{8}");
    }
}