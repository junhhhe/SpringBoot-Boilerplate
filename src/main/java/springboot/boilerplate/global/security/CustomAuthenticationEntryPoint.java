package springboot.boilerplate.global.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import springboot.boilerplate.global.exception.ErrorCode;
import springboot.boilerplate.global.exception.SecurityExceptionHandler;

import java.io.IOException;

/**
 * 필터레벨
 * 인증이 필요할 때 발생하는 AuthenticationException을 처리하는 핸들러
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final SecurityExceptionHandler securityExceptionHandler;

    /**
     * 인증이 필요할 때 호출되는 메서드
     * 
     * @param request HTTP 요청
     * @param response HTTP 응답
     * @param authException 발생한 AuthenticationException
     * @throws IOException IO 예외
     */
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException {
        log.warn("[AuthenticationException] {} - {}", request.getRequestURI(), authException.getMessage());
        securityExceptionHandler.writeErrorResponse(response, ErrorCode.UNAUTHORIZED);
    }
}

