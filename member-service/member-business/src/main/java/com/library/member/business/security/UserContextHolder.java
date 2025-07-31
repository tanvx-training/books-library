package com.library.member.business.security;

public class UserContextHolder {
    
    private static final ThreadLocal<UserContext> contextHolder = new ThreadLocal<>();

    public static void setContext(UserContext userContext) {
        contextHolder.set(userContext);
    }

    public static UserContext getContext() {
        return contextHolder.get();
    }

    public static void clearContext() {
        contextHolder.remove();
    }

    public static boolean hasContext() {
        return contextHolder.get() != null;
    }
}