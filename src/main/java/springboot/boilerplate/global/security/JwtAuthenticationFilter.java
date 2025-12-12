package springboot.boilerplate.global.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;
import springboot.boilerplate.auth.domain.CustomUserDetails;
import springboot.boilerplate.auth.domain.User;
import springboot.boilerplate.auth.enums.Role;

import java.io.IOException;

/**
 * JWT 인증 필터
 * 
 * 요청 헤더에서 JWT 토큰을 추출하여 검증하고, 인증 정보를 SecurityContext에 설정
 * OncePerRequestFilter를 상속받아 요청당 한 번만 실행
 */
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    public JwtAuthenticationFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    /**
     * 필터 내부 로직 처리
     * 
     * 요청 헤더에서 JWT 토큰을 추출하고, 토큰의 유효성을 검증한 후
     * SecurityContext에 인증 정보를 설정
     * 
     * @param request HTTP 요청
     * @param response HTTP 응답
     * @param filterChain 필터 체인
     * @throws ServletException 서블릿 예외
     * @throws IOException IO 예외
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // 인증이 필요 없는 경로는 통과
        String requestURI = request.getRequestURI();
        if (requestURI.startsWith("/auth")) {
            filterChain.doFilter(request, response);
            return;
        }

        // 요청 헤더에서 토큰 추출
        String token = request.getHeader("Authorization");
        if (token == null || !token.startsWith("Bearer ")){
            filterChain.doFilter(request, response);
            return;
        }

        // "Bearer " 접두사 제거
        token = token.split(" ")[1];

        // 토큰 만료 검증
        if (jwtUtil.isExpired(token)) {
            filterChain.doFilter(request, response);
            return;
        }

        // 토큰에서 사용자 정보 추출
        String email = jwtUtil.getUsername(token);
        String role = jwtUtil.getRole(token);

        // 임시 User 객체 생성 (인증 정보만 포함)
        User user = User.builder()
                .email(email)
                .password("")
                .role(Role.valueOf(role))
                .build();

        // CustomUserDetails 생성 및 인증 정보 설정
        CustomUserDetails customUserDetails = new CustomUserDetails(user);
        Authentication authToken = new UsernamePasswordAuthenticationToken(
                customUserDetails, 
                null, 
                customUserDetails.getAuthorities()
        );
        SecurityContextHolder.getContext().setAuthentication(authToken);

        filterChain.doFilter(request, response);
    }
}
