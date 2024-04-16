package xyz.needpainkiller.lib.security.secret;

import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Profile("!vault")
@Component
public class CommonJsonWebTokenSecretKey implements JsonWebTokenSecretKeyManager {

    private final SecretKey secretKey;

    public CommonJsonWebTokenSecretKey(
            @Value("${jwt.secret-key}") String secretKeyStr) {
        if (secretKeyStr == null || secretKeyStr.isEmpty()) {
            throw new IllegalArgumentException("jwt.secret-key is empty");
        }
        String base64EncodedSecretKey = Base64.getEncoder().encodeToString(secretKeyStr.getBytes(StandardCharsets.UTF_8));
        this.secretKey = Keys.hmacShaKeyFor(base64EncodedSecretKey.getBytes(StandardCharsets.UTF_8));
    }

    @Override
    public SecretKey getSecretKey() {
        return secretKey;
    }
}
