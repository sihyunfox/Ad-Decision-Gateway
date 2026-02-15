package com.adg.shared.adapter.web;

import com.adg.config.AppAuthProperties;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * API Key 기반 인증 필터.
 * <p>public path(/v1/decision, /v1/events, /mock/, /actuator/ 등): 인증 생략, /v1/decision·/v1/events는 ATTR_CLIENT_ID=openrtb 설정.
 * 그 외: X-API-Key 헤더 검증. Admin Key면 ATTR_IS_ADMIN=true. 아니면 api-keys에서 clientId 조회 후 401 또는 통과.</p>
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE + 1)
public class AuthFilter extends OncePerRequestFilter {

    public static final String ATTR_CLIENT_ID = "auth.clientId";
    public static final String ATTR_IS_ADMIN = "auth.isAdmin";

    private static final String API_KEY_HEADER = "X-API-Key";
    private static final String DEFAULT_DEV_CLIENT_ID = "default";

    private final AppAuthProperties authProperties;

    public AuthFilter(AppAuthProperties authProperties) {
        this.authProperties = authProperties;
    }

    private Map<String, String> getApiKeys() {
        return authProperties.getApiKeys();
    }

    private String getAdminKey() {
        return authProperties.getAdminKey();
    }

    private List<String> getPublicPaths() {
        return authProperties.getPublicPaths();
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String path = request.getRequestURI();
        /* Public path: 인증 없이 통과. Decision/Event는 clientId=openrtb로 설정 */
        if (isPublicPath(path)) {
            if (path.startsWith("/v1/decision") || path.startsWith("/v1/events")) {
                request.setAttribute(ATTR_CLIENT_ID, "openrtb");
                request.setAttribute(ATTR_IS_ADMIN, false);
            }
            filterChain.doFilter(request, response);
            return;
        }

        if (authProperties.isDevMode()) {
            request.setAttribute(ATTR_CLIENT_ID, DEFAULT_DEV_CLIENT_ID);
            request.setAttribute(ATTR_IS_ADMIN, false);
            filterChain.doFilter(request, response);
            return;
        }

        String apiKey = request.getHeader(API_KEY_HEADER);
        if (apiKey == null || apiKey.isBlank()) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        if (getAdminKey() != null && !getAdminKey().isBlank() && getAdminKey().equals(apiKey)) {
            request.setAttribute(ATTR_IS_ADMIN, true);
            request.setAttribute(ATTR_CLIENT_ID, "admin");
            filterChain.doFilter(request, response);
            return;
        }

        String clientId = findClientId(apiKey);
        if (clientId == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        request.setAttribute(ATTR_CLIENT_ID, clientId);
        request.setAttribute(ATTR_IS_ADMIN, false);
        filterChain.doFilter(request, response);
    }

    /** /v1/decision, /v1/events 또는 설정된 public-paths에 포함되면 인증 생략 */
    private boolean isPublicPath(String path) {
        if (path.startsWith("/v1/decision") || path.startsWith("/v1/events")) {
            return true;
        }
        List<String> publicPaths = getPublicPaths();
        if (publicPaths == null || publicPaths.isEmpty()) {
            return path.startsWith("/mock/") || path.startsWith("/actuator/") || path.startsWith("/docs")
                    || path.startsWith("/v3/api-docs") || path.startsWith("/swagger-ui");
        }
        return publicPaths.stream().anyMatch(path::startsWith);
    }

    private String findClientId(String key) {
        for (Map.Entry<String, String> e : getApiKeys().entrySet()) {
            if (key.equals(e.getValue())) {
                return e.getKey();
            }
        }
        return null;
    }
}
