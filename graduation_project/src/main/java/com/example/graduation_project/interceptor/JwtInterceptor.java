package com.example.graduation_project.interceptor;

import com.example.graduation_project.common.UserContext;
import com.example.graduation_project.utils.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtInterceptor implements HandlerInterceptor {

    private final JwtUtil jwtUtil;
    private final ObjectMapper objectMapper;

    private static final List<String> PUBLIC_PATHS = Arrays.asList(
        "/api/auth/login",
        "/api/auth/register",
        "/api/products",
        "/api/products/search",
        "/api/banners",
        "/api/categories",
        "/api/notices",
        "/api/upload",
        "/uploads/"
    );

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String requestURI = request.getRequestURI();

        if (isPublicPath(requestURI)) {
            String token = request.getHeader("Authorization");
            if (token != null && token.startsWith("Bearer ")) {
                token = token.substring(7);
                if (jwtUtil.validateToken(token)) {
                    Long userId = jwtUtil.getUserId(token);
                    Integer role = jwtUtil.getRole(token);
                    UserContext.setUserId(userId);
                    UserContext.setRole(role);
                    log.info("User authenticated for public path: userId={}, role={}", userId, role);
                }
            }
            return true;
        }

        String token = request.getHeader("Authorization");
        
        if (token == null) {
            sendErrorResponse(response, 401, "未授权，请先登录");
            return false;
        }
        
        if (!token.startsWith("Bearer ")) {
            sendErrorResponse(response, 401, "Authorization格式错误，应为: Bearer {token}");
            return false;
        }
        
        token = token.substring(7);
        log.debug("JWT Token received: {}", token.substring(0, Math.min(token.length(), 20)) + "...");
        
        if (!jwtUtil.validateToken(token)) {
            sendErrorResponse(response, 401, "Token无效或已过期");
            return false;
        }
        
        Long userId = jwtUtil.getUserId(token);
        Integer role = jwtUtil.getRole(token);
        
        UserContext.setUserId(userId);
        UserContext.setRole(role);
        
        log.info("User authenticated: userId={}, role={}", userId, role);
        return true;
    }

    private boolean isPublicPath(String requestURI) {
        for (String publicPath : PUBLIC_PATHS) {
            if (requestURI.startsWith(publicPath)) {
                return true;
            }
            if (requestURI.matches("^/api/products/\\d+$")) {
                return true;
            }
            if (requestURI.matches("^/api/notices/\\d+$")) {
                return true;
            }
            if (requestURI.matches("^/api/categories.*")) {
                return true;
            }
        }
        return false;
    }

    private void sendErrorResponse(HttpServletResponse response, int code, String message) throws Exception {
        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType("application/json;charset=UTF-8");
        Map<String, Object> errorResponse = Map.of(
            "code", code,
            "message", message,
            "data", Map.of()
        );
        response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        UserContext.clear();
        log.debug("UserContext cleared after request completion");
    }
}
