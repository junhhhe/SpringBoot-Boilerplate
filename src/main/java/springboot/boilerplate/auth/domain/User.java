package springboot.boilerplate.auth.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import springboot.boilerplate.auth.enums.Role;
import springboot.boilerplate.global.common.BaseTimeEntity;

@Entity
@Table(name = "TB_USER")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
public class User extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    @Column(name = "email", length = 100, nullable = false, unique = true)
    private String email;

    @Column(name = "password", nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private Role role;
}