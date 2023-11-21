package xyz.needpainkiller.core.lib.security;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

/**
 * JWT 이중 로그인 방지
 *
 * @author ywkang-rbrain
 */

@Component(value = "JwtDoubleChecker")
@Service
@Primary
public class CommonJwtDoubleChecker implements JwtDoubleChecker {


    public void validationTokenMatch(String userId, String sourceToken) {
        return;
    }

    public String getToken(String userId) {
        return null;
    }

    public void putToken(String userId, String token) {
        return;
    }


    public void deleteToken(String userId) {
        return;
    }
}
