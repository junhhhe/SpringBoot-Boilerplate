package springboot.boilerplate.auth.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import springboot.boilerplate.auth.domain.User;
import springboot.boilerplate.auth.dto.request.RequestTokenReissueDto;
import springboot.boilerplate.auth.dto.request.RequestUserLoginDto;
import springboot.boilerplate.auth.dto.request.RequestUserSaveDto;
import springboot.boilerplate.auth.dto.response.TokenDto;
import springboot.boilerplate.auth.dto.response.ResponseUserSaveDto;
import springboot.boilerplate.auth.enums.Role;
import springboot.boilerplate.auth.repository.UserRepository;
import springboot.boilerplate.global.exception.CustomException;
import springboot.boilerplate.global.exception.ErrorCode;
import springboot.boilerplate.global.redis.RedisService;
import springboot.boilerplate.global.security.JwtUtil;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final JwtUtil jwtUtil;
    private final RedisService redisService;

    /**
     * 회원가입
     * 
     * @param dto 회원가입 요청 DTO
     * @return 회원가입된 사용자 정보
     * @throws CustomException 이메일이 이미 존재하는 경우
     */
    @Transactional
    public ResponseUserSaveDto save(RequestUserSaveDto dto) {
        // 이메일 중복 검사
        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new CustomException(ErrorCode.EMAIL_ALREADY_EXISTS);
        }
        
        // 비밀번호 암호화 후 사용자 생성
        User user = User.builder()
                .email(dto.getEmail())
                .password(bCryptPasswordEncoder.encode(dto.getPassword()))
                .role(Role.USER)
                .build();

        userRepository.save(user);

        return new ResponseUserSaveDto(user.getId(), user.getEmail(), user.getRole().name());
    }

    /**
     * 로그인
     * 
     * @param dto 로그인 요청 DTO
     * @return TokenDto
     * @throws CustomException 사용자를 찾을 수 없거나 비밀번호가 일치하지 않는 경우
     */
    public TokenDto login(RequestUserLoginDto dto) {
        // 사용자 조회
        User user = userRepository.findByEmail(dto.getEmail())
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        // 비밀번호 검증
        if (!bCryptPasswordEncoder.matches(dto.getPassword(), user.getPassword())) {
            throw new CustomException(ErrorCode.INVALID_CREDENTIALS);
        }

        // 토큰 생성
        String accessToken = jwtUtil.createAccessToken(user.getEmail(), user.getRole().name());
        String refreshToken = jwtUtil.createRefreshToken(user.getEmail(), user.getRole().name());
        TokenDto tokenDto = TokenDto.of(accessToken, refreshToken);

        // 기존 Refresh Token 삭제 후 새로 저장
        redisService.deleteRefreshToken(user.getId());
        redisService.saveRefreshToken(
                user.getId(), 
                tokenDto.getRefreshToken(), 
                jwtUtil.getRefreshTokenExpirationMillis()
        );

        return tokenDto;
    }

    /**
     * Access Token 재발급
     * 
     * @param dto 토큰 재발급 요청 DTO
     * @return 새로운 Access Token과 기존 Refresh Token을 포함한 TokenDto
     * @throws CustomException 토큰이 만료되었거나 유효하지 않은 경우
     */
    public TokenDto reissueAccessToken(RequestTokenReissueDto dto) {
        // Refresh Token 만료 검증
        if (jwtUtil.isExpired(dto.getRefreshToken())) {
            throw new CustomException(ErrorCode.TOKEN_EXPIRED);
        }

        // Redis에 저장된 Refresh Token과 비교
        String storedToken = redisService.getRefreshToken(dto.getUserId());
        if (storedToken == null || !storedToken.equals(dto.getRefreshToken())) {
            throw new CustomException(ErrorCode.TOKEN_INVALID);
        }

        // 사용자 조회
        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        // 새로운 Access Token 생성
        String newAccessToken = jwtUtil.createAccessToken(user.getEmail(), user.getRole().name());
        return TokenDto.of(newAccessToken, dto.getRefreshToken());
    }
}
