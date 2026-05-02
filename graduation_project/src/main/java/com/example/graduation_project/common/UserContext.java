package com.example.graduation_project.common;

public class UserContext {

    private static final ThreadLocal<Long> userIdThreadLocal = new ThreadLocal<>();
    
    private static final ThreadLocal<Integer> roleThreadLocal = new ThreadLocal<>();

    private UserContext() {
    }

    public static void setUserId(Long userId) {
        userIdThreadLocal.set(userId);
    }

    public static Long getUserId() {
        return userIdThreadLocal.get();
    }

    public static void setRole(Integer role) {
        roleThreadLocal.set(role);
    }

    public static Integer getRole() {
        return roleThreadLocal.get();
    }

    public static void clear() {
        userIdThreadLocal.remove();
        roleThreadLocal.remove();
    }

    public static boolean hasLogin() {
        return userIdThreadLocal.get() != null;
    }
}
