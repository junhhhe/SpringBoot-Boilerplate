package springboot.boilerplate.auth.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ResponseUserSaveDto {
    private Long id;
    private String email;
    private String role;
}
