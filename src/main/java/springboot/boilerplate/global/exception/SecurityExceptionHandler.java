package springboot.boilerplate.global.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import springboot.boilerplate.global.common.BaseResponse;

import java.io.IOException;

/**
 * 필터 레벨
 * 인증/인가 예외 처리 공통 로직
 */
@Component
@RequiredArgsConstructor
public class SecurityExceptionHandler {

    private final ObjectMapper objectMapper;

    /**
     * 에러 응답을 JSON 형식으로 작성
     * 
     * @param response HTTP 응답
     * @param errorCode 에러 코드
     * @throws IOException IO 예외
     */
    public void writeErrorResponse(HttpServletResponse response, ErrorCode errorCode) throws IOException {
        response.setStatus(errorCode.getHttpStatus().value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        
        BaseResponse<Void> errorResponse = BaseResponse.error(
                errorCode.getMessage(),
                errorCode.getHttpStatus()
        );
        
        objectMapper.writeValue(response.getWriter(), errorResponse);
    }
}

