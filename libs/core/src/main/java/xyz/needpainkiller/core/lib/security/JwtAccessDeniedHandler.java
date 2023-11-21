package xyz.needpainkiller.core.lib.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerExceptionResolver;

/**
 * JWT 권한(authorization) 미달 Handling
 * WebSecurityConfigurerAdapter 구현체에서 accessDeniedHandler 로 등록
 * ControllerAdvice 에서 AccessDeniedException 으로 FORBIDDEN 응답 처리
 *
 * @author ywkang-rbrain
 */

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAccessDeniedHandler implements AccessDeniedHandler {

    private final HandlerExceptionResolver handlerExceptionResolver;

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) {
        log.info("###### JwtAccessDeniedHandler :" + accessDeniedException);
        handlerExceptionResolver.resolveException(request, response, null, accessDeniedException);
    }
}
