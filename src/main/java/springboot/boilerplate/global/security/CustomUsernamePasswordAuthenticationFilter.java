package springboot.boilerplate.global.security;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.util.StreamUtils;
import springboot.boilerplate.auth.domain.CustomUserDetails;
import springboot.boilerplate.auth.dto.response.TokenDto;
import springboot.boilerplate.auth.dto.request.RequestUserLoginDto;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * 커스텀 사용자명/비밀번호 인증 필터
 * 
 * JSON 형식의 로그인 요청을 처리하고, 인증 성공 시 JWT 토큰을 생성하여 반환
 * Spring Security의 UsernamePasswordAuthenticationFilter를 확장하여 구현
 */
public class CustomUsernamePasswordAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;


    public CustomUsernamePasswordAuthenticationFilter(AuthenticationManager authenticationManager, JwtUtil jwtUtil) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
    }

    /**
     * 인증 시도 처리
     * 
     * JSON 데이터를 읽어 RequestUserLoginDto로 파싱한 후,
     * AuthenticationManager를 통해 인증을 수행
     * 
     * @param request HTTP 요청
     * @param response HTTP 응답
     * @return 인증 결과
     * @throws AuthenticationException 인증 실패 시
     */
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        // 요청 본문 읽기
        ServletInputStream inputStream;
        String requestBody;
        try {
            inputStream = request.getInputStream();
            requestBody = StreamUtils.copyToString(inputStream, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException("요청 본문을 읽을 수 없음", e);
        }

        // JSON 데이터 파싱
        RequestUserLoginDto requestUserLoginDto;
        try {
            requestUserLoginDto = objectMapper.readValue(requestBody, RequestUserLoginDto.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("JSON 파싱 실패", e);
        }

        // 인증 토큰 생성 및 인증 수행
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                requestUserLoginDto.getEmail(), 
                requestUserLoginDto.getPassword(), 
                null
        );
        return authenticationManager.authenticate(authToken);
    }

    /**
     * 인증된 사용자 정보를 기반으로 JWT 토큰을 생성하고,
     * JSON 형식으로 응답
     * 
     * @param request HTTP 요청
     * @param response HTTP 응답
     * @param chain 필터 체인
     * @param authResult 인증 결과
     * @throws IOException IO 예외
     * @throws ServletException 서블릿 예외
     */
    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {
        // 인증된 사용자 정보 추출
        CustomUserDetails userDetails = (CustomUserDetails) authResult.getPrincipal();
        
        // 토큰 생성
        String accessToken = jwtUtil.createAccessToken(userDetails.getUser().getEmail(), userDetails.getUser().getRole().name());
        String refreshToken = jwtUtil.createRefreshToken(userDetails.getUser().getEmail(), userDetails.getUser().getRole().name());
        TokenDto tokenDto = TokenDto.of(accessToken, refreshToken);

        // JSON 형식으로 응답
        String jsonResponse = objectMapper.writeValueAsString(tokenDto);
        response.setContentType("application/json");
        response.getWriter().write(jsonResponse);
    }
}
