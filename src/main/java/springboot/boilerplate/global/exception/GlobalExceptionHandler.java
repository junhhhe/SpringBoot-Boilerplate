package springboot.boilerplate.global.exception;

import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import springboot.boilerplate.global.common.BaseResponse;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * CustomException 처리
     * 
     * @param e 발생한 CustomException
     * @return 에러 응답
     */
    @ExceptionHandler(CustomException.class)
    public ResponseEntity<BaseResponse<Void>> handleCustomException(CustomException e) {
        log.error("[CustomException] {}", e.getMessage());
        return ResponseEntity
                .status(e.getErrorCode().getHttpStatus())
                .body(BaseResponse.error(e.getErrorCode().getMessage(), e.getErrorCode().getHttpStatus()));
    }

    /**
     *
     * @Valid 어노테이션으로 검증 실패 시 발생하는 예외 처리
     * 
     * @param e 발생한 MethodArgumentNotValidException
     * @return 에러 응답
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<BaseResponse<Void>> handleValidationException(MethodArgumentNotValidException e) {
        log.warn("[ValidationException] {}", e.getMessage());
        String errorMessage = e.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(fieldError -> fieldError.getField() + ": " + fieldError.getDefaultMessage())
                .findFirst()
                .orElse(ErrorCode.INVALID_REQUEST.getMessage());
        return ResponseEntity
                .status(ErrorCode.INVALID_REQUEST.getHttpStatus())
                .body(BaseResponse.error(errorMessage, ErrorCode.INVALID_REQUEST.getHttpStatus()));
    }

    /**
     * 
     * 메서드 파라미터나 반환값의 제약 조건 위반 시 발생하는 예외 처리
     * 
     * @param e 발생한 ConstraintViolationException
     * @return 에러 응답
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<BaseResponse<Void>> handleConstraintViolationException(ConstraintViolationException e) {
        log.warn("[ConstraintViolationException] {}", e.getMessage());
        String errorMessage = e.getConstraintViolations()
                .stream()
                .map(v -> v.getPropertyPath() + ": " + v.getMessage())
                .findFirst()
                .orElse(ErrorCode.INVALID_REQUEST.getMessage());
        return ResponseEntity
                .status(ErrorCode.INVALID_REQUEST.getHttpStatus())
                .body(BaseResponse.error(errorMessage, ErrorCode.INVALID_REQUEST.getHttpStatus()));
    }

    /**
     * 예상치 못한 예외 처리
     * 
     * @param e 발생한 예외
     * @return 에러 응답
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<BaseResponse<Void>> handleUnexpectedException(Exception e) {
        log.error("[UnexpectedException]", e);
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(BaseResponse.error("내부 서버 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR));
    }
}