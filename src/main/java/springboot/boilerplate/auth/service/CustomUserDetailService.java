package springboot.boilerplate.auth.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import springboot.boilerplate.auth.domain.CustomUserDetails;
import springboot.boilerplate.auth.domain.User;
import springboot.boilerplate.auth.repository.UserRepository;

/**
 * 사용자 인증 정보를 로드하는 서비스 클래스
 * 
 * Spring Security의 UserDetailsService를 구현하여
 * 이메일을 기반으로 사용자 정보를 조회하고 UserDetails 객체로 변환
 * 
 */
@Service
@RequiredArgsConstructor
public class CustomUserDetailService implements UserDetailsService {

    private final UserRepository userRepository;

    /**
     * 이메일을 기반으로 사용자 정보를 로드합니다.
     * 
     * @param email 사용자 이메일
     * @return UserDetails 객체
     * @throws UsernameNotFoundException 사용자를 찾을 수 없는 경우
     */
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("사용자 없음: " + email));
        return new CustomUserDetails(user);
    }
}