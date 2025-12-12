package springboot.boilerplate.global.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;
import springboot.boilerplate.global.exception.ErrorCode;
import springboot.boilerplate.global.exception.SecurityExceptionHandler;

import java.io.IOException;

/**
 * 필터레벨
 * 권한이 없을 때 발생하는 AccessDeniedException을 처리하는 핸들러
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    private final SecurityExceptionHandler securityExceptionHandler;

    /**
     * 권한이 없을 때 호출되는 메서드
     * 
     * @param request HTTP 요청
     * @param response HTTP 응답
     * @param accessDeniedException 발생한 AccessDeniedException
     * @throws IOException IO 예외
     */
    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException {
        log.warn("[AccessDeniedException] {} - {}", request.getRequestURI(), accessDeniedException.getMessage());
        securityExceptionHandler.writeErrorResponse(response, ErrorCode.FORBIDDEN);
    }
}

