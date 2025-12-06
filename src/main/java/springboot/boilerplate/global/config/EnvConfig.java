package springboot.boilerplate.global.config;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;

public class EnvConfig implements ApplicationContextInitializer<ConfigurableApplicationContext> {

    @Override
    public void initialize(ConfigurableApplicationContext applicationContext) {
        // .env 파일 로드
        Dotenv dotenv = Dotenv.configure()
                .ignoreIfMissing()  // .env 파일이 없어도 에러 발생 안 함
                .load();
        
        // 환경 변수를 시스템 프로퍼티로 설정
        dotenv.entries().forEach(entry -> {
            System.setProperty(entry.getKey(), entry.getValue());
        });
    }
}

