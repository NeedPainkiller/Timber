package xyz.needpainkiller.core.lib.security;

public interface JwtDoubleChecker {

    void validationTokenMatch(String userId, String sourceToken);

    String getToken(String userId);

    void putToken(String userId, String token);

    void deleteToken(String userId);
}
