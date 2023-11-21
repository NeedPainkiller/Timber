package xyz.needpainkiller.core.lib.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import xyz.needpainkiller.core.lib.exceptions.ApiErrorResponse;
import xyz.needpainkiller.core.lib.exceptions.CommonErrorCode;
import xyz.needpainkiller.core.lib.security.error.TokenValidFailedException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.PrintWriter;

/**
 * JWT 값 인증(authorization) 실패 Handling
 * WebSecurityConfigurerAdapter 구현체에서 authenticationEntryPoint 로 등록
 * commence 에서 401 UNAUTHORIZED 응답 처리 (ErrorCode 는 TOKEN_VALIDATION_FAILED)
 *
 * @author ywkang-rbrain
 */

@Slf4j
@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {


    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, TokenValidFailedException {
        log.info("###### JwtAuthenticationEntryPoint > commence:" + authException);

        ApiErrorResponse apiErrorResponse = ApiErrorResponse.of(CommonErrorCode.TOKEN_VALIDATION_FAILED, authException);

        response.setContentType("application/json; charset=UTF-8");
        response.setCharacterEncoding("UTF-8");
        response.setStatus(apiErrorResponse.getStatus().value());
        PrintWriter out = response.getWriter();
        out.write("{ \"message\": \"" + apiErrorResponse.getMessage() + "\"" + ",\"status\": \"" + apiErrorResponse.getStatus() + "\"" + ",\"code\": \"" + apiErrorResponse.getCode() + "\"" + ",\"cause\": \"" + apiErrorResponse.getCause() + "\"" + " }");
    }
}

