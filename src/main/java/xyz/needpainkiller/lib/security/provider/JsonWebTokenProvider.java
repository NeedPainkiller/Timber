package xyz.needpainkiller.lib.security.provider;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import xyz.needpainkiller.api.team.model.Team;
import xyz.needpainkiller.api.user.domain.model.Role;
import xyz.needpainkiller.api.user.domain.model.User;
import xyz.needpainkiller.helper.TimeHelper;
import xyz.needpainkiller.lib.security.JwtDoubleChecker;
import xyz.needpainkiller.lib.security.error.TokenValidFailedException;
import xyz.needpainkiller.lib.security.secret.JsonWebTokenSecretKeyManager;

import javax.crypto.SecretKey;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;
import java.util.function.Function;

import static xyz.needpainkiller.lib.exceptions.CommonErrorCode.*;


@Slf4j
public abstract class JsonWebTokenProvider {
    public static final String BEARER_TOKEN_HEADER = "X-Authorization";

    protected static final Long EXPIRE_SOON_RATIO_DIVIDE = 2L; // 발급 시간 의 절반이 지났을 경우

    protected static final String KEY_TENANT_PK = "tenant-pk";
    protected static final String KEY_USER_PK = "user-pk";
    protected static final String KEY_USER_ID = "user-id";
    protected static final String KEY_USER_NAME = "user-name";
    protected static final String KEY_USER_EMAIL = "user-email";
    protected static final String KEY_ROLE_LIST = "role-list";
    protected static final String KEY_TEAM_PK = "team-pk";
    protected static final String KEY_TEAM_NAME = "team-name";

    protected final JwtDoubleChecker jwtDoubleChecker;
    protected final SecretKey secretKey;
    protected final long expireTime;

    protected JsonWebTokenProvider(JwtDoubleChecker jwtDoubleChecker, JsonWebTokenSecretKeyManager secretKeyManager, long expireTime) {
        this.jwtDoubleChecker = jwtDoubleChecker;
        this.secretKey = secretKeyManager.getSecretKey();
        this.expireTime = expireTime;

        log.info("JsonWebTokenProvider instance : {}", this.getClass().getName());
    }


    public abstract String createToken(HttpServletRequest request, HttpServletResponse response, User user, List<Role> roles, Team team);

    protected String generateToken(Claims claims) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime validity = LocalDateTime.now().plus(expireTime, ChronoUnit.MILLIS);
        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(Timestamp.valueOf(now))
                .setExpiration(Timestamp.valueOf(validity))
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();
    }

    public abstract String resolveToken(HttpServletRequest request);

    public void validateToken(HttpServletRequest request) {
        String token = resolveToken(request);
        validateToken(token);
    }

    public void validateToken(String token) {
        if (Strings.isBlank(token)) {
            throw new TokenValidFailedException(TOKEN_MUST_REQUIRED);
        }
        if (getExpirationDate(token).before(new Date())) {  // 요청 Token 의 만료기한이 지난경우
            throw new TokenValidFailedException(TOKEN_EXPIRED);
        }
    }

    public void expireToken(HttpServletRequest request, HttpServletResponse response) {
    }

    public Long getTenantPk(HttpServletRequest request) {
        String token = resolveToken(request);
        return getUserPk(token);
    }

    public Long getTenantPk(String token) {
        Object claim = getClaimFromToken(token, claims -> claims.get(KEY_TENANT_PK));
        if (claim == null) {
            throw new TokenValidFailedException(TOKEN_CLAIM_TENANT_NOT_EXIST);
        }
        return ((Integer) claim).longValue();
    }


    public Long getUserPk(HttpServletRequest request) {
        String token = resolveToken(request);
        return getUserPk(token);
    }

    public Long getUserPk(String token) {
        Object claim = getClaimFromToken(token, claims -> claims.get(KEY_USER_PK));
        if (claim == null) {
            throw new TokenValidFailedException(TOKEN_CLAIM_USER_NOT_EXIST);
        }
        return ((Integer) claim).longValue();
    }

    public String getUserId(HttpServletRequest request) {
        return getUserId(resolveToken(request));
    }

    public String getUserId(String token) {
        return getClaimFromToken(token, Claims::getSubject);
    }

    public String getUserName(String token) {
        Object claim = getClaimFromToken(token, claims -> claims.get(KEY_USER_NAME));
        if (claim == null) {
            throw new TokenValidFailedException(TOKEN_CLAIM_TENANT_NOT_EXIST);
        }
        return claim.toString();
    }

    public String getUserEmail(String token) {
        Object claim = getClaimFromToken(token, claims -> claims.get(KEY_USER_EMAIL));
        if (claim == null) {
            throw new TokenValidFailedException(TOKEN_CLAIM_TENANT_NOT_EXIST);
        }
        return claim.toString();
    }

    public Long getTeamPk(String token) {
        Object claim = getClaimFromToken(token, claims -> claims.get(KEY_TEAM_PK));
        if (claim == null) {
            throw new TokenValidFailedException(TOKEN_CLAIM_TENANT_NOT_EXIST);
        }
        return ((Integer) claim).longValue();
    }

    public String getTeamName(String token) {
        Object claim = getClaimFromToken(token, claims -> claims.get(KEY_TEAM_NAME));
        if (claim == null) {
            throw new TokenValidFailedException(TOKEN_CLAIM_TENANT_NOT_EXIST);
        }
        return claim.toString();
    }

    public List<String> getRole(HttpServletRequest request) {
        return getRole(resolveToken(request));
    }

    public List<String> getRole(String token) {
        List<String> authorityList = (List<String>) getClaimFromToken(token, claims -> claims.get(KEY_ROLE_LIST));
        if (authorityList == null || authorityList.isEmpty()) {
            throw new TokenValidFailedException(TOKEN_CLAIM_AUTHORITY_NOT_EXIST);
        }
        return authorityList;
    }

    public Date getIssuedAtDate(HttpServletRequest request) {
        String token = resolveToken(request);
        return getIssuedAtDate(token);
    }


    public Date getExpirationDate(HttpServletRequest request) {
        String token = resolveToken(request);
        return getExpirationDate(token);
    }


    public Date getIssuedAtDate(String token) {
        return getClaimFromToken(token, Claims::getIssuedAt);
    }


    public Date getExpirationDate(String token) {
        return getClaimFromToken(token, Claims::getExpiration);
    }

    public Boolean isExpireSoon(HttpServletRequest request) {
        String token = resolveToken(request);
        Date expiredAt = getExpirationDate(token);
        Date now = TimeHelper.now();
        long remainTime = expiredAt.getTime() - now.getTime();
        return remainTime < expireTime / EXPIRE_SOON_RATIO_DIVIDE;
    }

    protected <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        try {
            Claims claims = Jwts.parserBuilder().setSigningKey(secretKey).build()
                    .parseClaimsJws(token).getBody();
            return claimsResolver.apply(claims);
        } catch (ExpiredJwtException e) {
            throw new TokenValidFailedException(TOKEN_EXPIRED, e.getMessage());
        } catch (UnsupportedJwtException | MalformedJwtException | SignatureException | IllegalArgumentException e) {
            throw new TokenValidFailedException(TOKEN_CLAIM_PARSE_FAILED, e.getMessage());
        }
    }
}