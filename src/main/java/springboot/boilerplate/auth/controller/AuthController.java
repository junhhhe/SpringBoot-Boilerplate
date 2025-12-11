package springboot.boilerplate.auth.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import springboot.boilerplate.auth.dto.request.RequestTokenReissueDto;
import springboot.boilerplate.auth.dto.request.RequestUserLoginDto;
import springboot.boilerplate.auth.dto.request.RequestUserSaveDto;
import springboot.boilerplate.auth.dto.response.TokenDto;
import springboot.boilerplate.auth.dto.response.ResponseUserSaveDto;
import springboot.boilerplate.auth.service.AuthService;
import springboot.boilerplate.global.common.BaseResponse;
import springboot.boilerplate.global.exception.ErrorCode;
import springboot.boilerplate.global.swagger.ApiErrorCodeExamples;

@RestController
@RequiredArgsConstructor
@Tag(name = "AuthController", description = "인증 관련 API")
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;

    /**
     * 회원가입 API
     * 
     * @param dto 회원가입 요청 DTO
     * @return 회원가입된 사용자 정보
     */
    @Operation(summary = "회원가입 API")
    @ApiErrorCodeExamples({
            ErrorCode.INVALID_REQUEST,
            ErrorCode.EMAIL_ALREADY_EXISTS,
            ErrorCode.INTERNAL_SERVER_ERROR
    })
    @PostMapping("/save")
    public ResponseEntity<BaseResponse<ResponseUserSaveDto>> save(@Valid @RequestBody RequestUserSaveDto dto) {
        ResponseUserSaveDto data = authService.save(dto);
        return ResponseEntity.ok(BaseResponse.success(data, "회원가입 성공", HttpStatus.CREATED));
    }

    /**
     * 로그인 API
     * 
     * @param dto 로그인 요청 DTO
     * @return Access Token과 Refresh Token
     */
    @Operation(summary = "로그인 API")
    @ApiErrorCodeExamples({
            ErrorCode.INVALID_REQUEST,
            ErrorCode.INVALID_CREDENTIALS,
            ErrorCode.INTERNAL_SERVER_ERROR
    })
    @PostMapping("/login")
    public ResponseEntity<BaseResponse<TokenDto>> login(@Valid @RequestBody RequestUserLoginDto dto) {
        TokenDto tokenDto = authService.login(dto);
        return ResponseEntity.ok(BaseResponse.success(tokenDto, "로그인 성공", HttpStatus.OK));
    }

    /**
     * 토큰 재발급 API
     * 
     * @param dto 토큰 재발급 요청 DTO
     * @return 새로운 Access Token과 기존 Refresh Token
     */
    @Operation(summary = "토큰 재발급 API")
    @ApiErrorCodeExamples({
            ErrorCode.INVALID_REQUEST,
            ErrorCode.TOKEN_EXPIRED,
            ErrorCode.TOKEN_INVALID,
            ErrorCode.INTERNAL_SERVER_ERROR
    })
    @PostMapping("/reissue")
    public ResponseEntity<BaseResponse<TokenDto>> reissue(@Valid @RequestBody RequestTokenReissueDto dto) {
        TokenDto newToken = authService.reissueAccessToken(dto);
        return ResponseEntity.ok(BaseResponse.success(newToken, "토큰 재발급 성공", HttpStatus.OK));
    }
}