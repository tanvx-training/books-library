package com.library.common.utils.logging;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.library.common.constants.LoggingConstants;

import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Utility class for sanitizing sensitive data in logs
 */
public final class DataSanitizer {
    
    private static final String MASKED_VALUE = "***MASKED***";
    private static final String MASKED_PARTIAL_PREFIX = "***";
    private static final String MASKED_PARTIAL_SUFFIX = "***";
    private static final ObjectMapper objectMapper = new ObjectMapper();
    
    // Patterns for detecting sensitive data
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
        "\\b[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Z|a-z]{2,}\\b"
    );
    private static final Pattern PHONE_PATTERN = Pattern.compile(
        "\\b(?:\\+?1[-.\s]?)?\\(?[0-9]{3}\\)?[-.\s]?[0-9]{3}[-.\s]?[0-9]{4}\\b"
    );
    private static final Pattern CREDIT_CARD_PATTERN = Pattern.compile(
        "\\b(?:4[0-9]{12}(?:[0-9]{3})?|5[1-5][0-9]{14}|3[47][0-9]{13}|3[0-9]{13}|6(?:011|5[0-9]{2})[0-9]{12})\\b"
    );
    
    private DataSanitizer() {
        // Utility class
    }
    
    /**
     * Sanitize object for logging by masking sensitive fields
     */
    public static Object sanitizeForLogging(Object obj) {
        if (obj == null) {
            return null;
        }
        
        try {
            if (obj instanceof String) {
                return sanitizeString((String) obj);
            }
            
            if (obj instanceof Map) {
                return sanitizeMap((Map<?, ?>) obj);
            }
            
            // Convert object to JSON and sanitize
            String json = objectMapper.writeValueAsString(obj);
            JsonNode jsonNode = objectMapper.readTree(json);
            JsonNode sanitizedNode = sanitizeJsonNode(jsonNode);
            return objectMapper.writeValueAsString(sanitizedNode);
            
        } catch (Exception e) {
            // If sanitization fails, return a safe representation
            return "Unable to sanitize object: " + obj.getClass().getSimpleName();
        }
    }
    
    /**
     * Sanitize method arguments for logging
     */
    public static Object[] sanitizeMethodArguments(Object[] args) {
        if (args == null || args.length == 0) {
            return args;
        }
        
        Object[] sanitizedArgs = new Object[args.length];
        for (int i = 0; i < args.length; i++) {
            sanitizedArgs[i] = sanitizeForLogging(args[i]);
        }
        return sanitizedArgs;
    }
    
    /**
     * Sanitize string content
     */
    private static String sanitizeString(String str) {
        if (str == null || str.isEmpty()) {
            return str;
        }
        
        // Check if string contains sensitive patterns
        String result = str;
        
        // Mask email addresses partially
        result = EMAIL_PATTERN.matcher(result).replaceAll(m -> {
            String email = m.group();
            int atIndex = email.indexOf('@');
            if (atIndex > 2) {
                return email.substring(0, 2) + MASKED_PARTIAL_PREFIX + 
                       email.substring(atIndex);
            }
            return MASKED_VALUE;
        });
        
        // Mask phone numbers
        result = PHONE_PATTERN.matcher(result).replaceAll(MASKED_VALUE);
        
        // Mask credit card numbers
        result = CREDIT_CARD_PATTERN.matcher(result).replaceAll(MASKED_VALUE);
        
        return result;
    }
    
    /**
     * Sanitize map content
     */
    private static Map<String, Object> sanitizeMap(Map<?, ?> map) {
        return map.entrySet().stream()
            .collect(java.util.stream.Collectors.toMap(
                entry -> String.valueOf(entry.getKey()),
                entry -> {
                    String key = String.valueOf(entry.getKey()).toLowerCase();
                    if (isSensitiveField(key)) {
                        return maskSensitiveValue(entry.getValue());
                    }
                    return sanitizeForLogging(entry.getValue());
                }
            ));
    }
    
    /**
     * Sanitize JSON node recursively
     */
    private static JsonNode sanitizeJsonNode(JsonNode node) {
        if (node.isObject()) {
            ObjectNode objectNode = objectMapper.createObjectNode();
            Iterator<Map.Entry<String, JsonNode>> fields = node.fields();
            
            while (fields.hasNext()) {
                Map.Entry<String, JsonNode> field = fields.next();
                String fieldName = field.getKey().toLowerCase();
                
                if (isSensitiveField(fieldName)) {
                    objectNode.put(field.getKey(), MASKED_VALUE);
                } else {
                    objectNode.set(field.getKey(), sanitizeJsonNode(field.getValue()));
                }
            }
            return objectNode;
            
        } else if (node.isArray()) {
            ArrayNode arrayNode = objectMapper.createArrayNode();
            for (JsonNode element : node) {
                arrayNode.add(sanitizeJsonNode(element));
            }
            return arrayNode;
            
        } else if (node.isTextual()) {
            return objectMapper.getNodeFactory().textNode(sanitizeString(node.asText()));
        }
        
        return node;
    }
    
    /**
     * Check if field name indicates sensitive data
     */
    private static boolean isSensitiveField(String fieldName) {
        return Arrays.stream(LoggingConstants.SENSITIVE_FIELDS)
            .anyMatch(sensitive -> fieldName.contains(sensitive.toLowerCase()));
    }
    
    /**
     * Mask sensitive value appropriately
     */
    private static String maskSensitiveValue(Object value) {
        if (value == null) {
            return null;
        }
        
        String strValue = String.valueOf(value);
        if (strValue.length() <= 4) {
            return MASKED_VALUE;
        }
        
        // Show first and last character for longer values
        return strValue.charAt(0) + MASKED_PARTIAL_PREFIX + 
               strValue.charAt(strValue.length() - 1);
    }
    
    /**
     * Sanitize exception for logging (remove sensitive stack trace info)
     */
    public static String sanitizeException(Throwable throwable) {
        if (throwable == null) {
            return null;
        }
        
        StringBuilder sb = new StringBuilder();
        sb.append(throwable.getClass().getSimpleName()).append(": ");
        
        String message = throwable.getMessage();
        if (message != null) {
            sb.append(sanitizeString(message));
        }
        
        return sb.toString();
    }
} 