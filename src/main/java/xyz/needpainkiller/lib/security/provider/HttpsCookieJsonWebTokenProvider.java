package xyz.needpainkiller.lib.security.provider;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import xyz.needpainkiller.api.team.model.Team;
import xyz.needpainkiller.api.user.domain.model.Role;
import xyz.needpainkiller.api.user.domain.model.User;
import xyz.needpainkiller.lib.security.JwtDoubleChecker;
import xyz.needpainkiller.lib.security.error.TokenValidFailedException;
import xyz.needpainkiller.lib.security.secret.JsonWebTokenSecretKeyManager;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static xyz.needpainkiller.lib.exceptions.CommonErrorCode.*;

@Profile({"jwt-cookie"})
@Slf4j
@Component
@RefreshScope
public class HttpsCookieJsonWebTokenProvider extends JsonWebTokenProvider {
    private final String baseUrl;

    public HttpsCookieJsonWebTokenProvider(
            @Autowired @Qualifier("JwtDoubleChecker") JwtDoubleChecker jwtDoubleChecker,
            @Autowired JsonWebTokenSecretKeyManager secretKeyManager,
            @Value("${jwt.expire-time-ms}") long expireTime,
            @Value("${server.domainl}") String baseUrl) {
        super(jwtDoubleChecker, secretKeyManager, expireTime);
        this.baseUrl = baseUrl;
    }

    private static final String JWT_COOKIE_NAME = "JSON_WEB_TOKEN";

    @Override
    public String createToken(HttpServletRequest request, HttpServletResponse response, User user, List<Role> roles, Team team) {
        try {
            Long tenantPk = user.getTenantPk();
            Long userPk = user.getId();
            String userId = user.getUserId();
            List<String> roleList = roles.stream().map(Role::getRoleName).toList();
            Claims claims = Jwts.claims().setSubject(userId);
            claims.put(KEY_TENANT_PK, tenantPk);
            claims.put(KEY_USER_PK, userPk);
            claims.put(KEY_USER_ID, userId);
            claims.put(KEY_USER_NAME, user.getUserName());
            claims.put(KEY_USER_EMAIL, user.getUserEmail());
            claims.put(KEY_ROLE_LIST, roleList);
            claims.put(KEY_TEAM_PK, team.getId());
            claims.put(KEY_TEAM_NAME, team.getTeamName());
            String token = generateToken(claims);
            Cookie tokenCookie = createCookie(token);
            response.addCookie(tokenCookie);
            return token;
        } catch (Exception e) {
            throw new TokenValidFailedException(TOKEN_FAILED_CREATE, e.getMessage());
        }
    }

    @Override
    public String resolveToken(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            throw new TokenValidFailedException(TOKEN_COOKIE_MUST_REQUIRED);
        }

        Cookie tokenCookie = Arrays.stream(cookies).filter(cookie -> cookie.getName().equals(JWT_COOKIE_NAME)).findFirst()
                .orElseThrow(() -> new TokenValidFailedException(TOKEN_COOKIE_MUST_REQUIRED));

        String bearerToken = tokenCookie.getValue();
        if (Strings.isBlank(bearerToken)) {
            throw new TokenValidFailedException(TOKEN_MUST_REQUIRED);
        }
        return bearerToken;
    }

    @Override
    public void validateToken(HttpServletRequest request) {
        String token = resolveToken(request);
        validateToken(token);
    }

    @Override
    public void validateToken(String token) {
        if (Strings.isBlank(token)) {
            throw new TokenValidFailedException(TOKEN_MUST_REQUIRED);
        }
        if (getExpirationDate(token).before(new Date())) {  // 요청 Token 의 만료기한이 지난경우
            throw new TokenValidFailedException(TOKEN_EXPIRED);
        }
    }

    @Override
    public void expireToken(HttpServletRequest request, HttpServletResponse response) {
        Cookie expireTokenCookie = expireCookie();
        response.addCookie(expireTokenCookie);
    }


    private Cookie createCookie(String token) {
        Cookie cookie = new Cookie(JWT_COOKIE_NAME, token);
        cookie.setPath("/");
        cookie.setSecure(true);
        cookie.setHttpOnly(true);
        cookie.setDomain(baseUrl);
        return cookie;
    }

    private Cookie expireCookie() {
        Cookie cookie = new Cookie(JWT_COOKIE_NAME, "");
        cookie.setPath("/");
        cookie.setSecure(true);
        cookie.setHttpOnly(true);
        cookie.setDomain(baseUrl);
        cookie.setMaxAge(0);
        return cookie;
    }
}
