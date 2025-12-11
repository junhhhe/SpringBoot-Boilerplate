package springboot.boilerplate.auth.domain;

import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;

/**
 * 사용자 인증 정보 클래스
 * 
 * UserDetails 인터페이스를 구현하여 Spring Security에서 사용하는 사용자 인증 정보를 제공
 * User 엔티티를 래핑하여 Spring Security의 인증 메커니즘과 통합
 * 
 */
@Getter
public class CustomUserDetails implements UserDetails {

    private final User user;

    public CustomUserDetails(User user) {
        this.user = user;
    }

    /**
     * 사용자의 권한 반환
     * 
     * @return 권한 컬렉션
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Collection<GrantedAuthority> collection = new ArrayList<>();
        collection.add(new GrantedAuthority() {
            @Override
            public String getAuthority() {
                return "ROLE_" + user.getRole();
            }
        });

        return collection;
    }

    /**
     * 사용자의 비밀번호 반환
     * 
     * @return 암호화된 비밀번호
     */
    @Override
    public String getPassword() {
        return user.getPassword();
    }

    /**
     * 사용자의 아이디 반환
     * 
     * @return 사용자 아이디
     */
    @Override
    public String getUsername() {
        return user.getEmail();
    }

    /**
     * 계정이 만료되지 않았는지 확인
     * 
     * @return 항상 true
     */
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    /**
     * 계정이 잠겨있지 않은지 확인
     * 
     * @return 항상 true
     */
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    /**
     * 자격 증명이 만료되지 않았는지 확인
     * 
     * @return 항상 true
     */
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    /**
     * 계정이 활성화되어 있는지 확인
     * 
     * @return 항상 true
     */
    @Override
    public boolean isEnabled() {
        return true;
    }

}