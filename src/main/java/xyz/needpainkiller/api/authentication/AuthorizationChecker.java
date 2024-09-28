package xyz.needpainkiller.api.authentication;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationTrustResolver;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import xyz.needpainkiller.api.audit.AuditService;
import xyz.needpainkiller.api.authentication.error.ApiException;
import xyz.needpainkiller.api.authentication.model.Api;
import xyz.needpainkiller.api.user.RoleService;
import xyz.needpainkiller.api.user.domain.model.Role;
import xyz.needpainkiller.api.user.domain.model.SecurityUser;
import xyz.needpainkiller.common.model.HttpMethod;
import xyz.needpainkiller.helper.HttpHelper;
import xyz.needpainkiller.lib.exceptions.ApiErrorResponse;
import xyz.needpainkiller.lib.exceptions.ErrorCode;
import xyz.needpainkiller.lib.security.error.TokenValidFailedException;
import xyz.needpainkiller.lib.security.provider.JsonWebTokenProvider;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import static xyz.needpainkiller.lib.exceptions.CommonErrorCode.*;

@Slf4j
@Component
public class AuthorizationChecker implements AuthenticationTrustResolver, AuthorizationManager<RequestAuthorizationContext> {
    private static final String ANONYMOUS_USER = "anonymousUser";
    private static final String CONTENT_TYPE_JSON = "application/json";
    private static final AntPathMatcher apiAntPathMatcher = new AntPathMatcher();

    @Value("${spring.base-url}")
    private String baseUrl;
    @Autowired
    private AuthorizationService authorizationService;
    @Autowired
    private AuditService auditService;
    @Autowired
    private RoleService roleService;


    public boolean isAnonymous(Authentication authentication) {
        if (authentication == null) {
            return false;
        }
        return authentication.getPrincipal().equals(ANONYMOUS_USER);
    }


    public boolean isRememberMe(Authentication authentication) {
        return false;
    }


    public AuthorizationDecision check(Supplier<Authentication> authenticationSupplier, RequestAuthorizationContext context) throws TokenValidFailedException, ApiException {
        Authentication authentication = authenticationSupplier.get();
        HttpServletRequest request = context.getRequest();
        String requestURI = request.getRequestURI();
        String userAgent = request.getHeader("user-agent");
        HttpMethod httpMethod = HttpMethod.nameOf(request.getMethod());
        log.info("# ({}) {}", httpMethod.name(), requestURI);
        try {
            Api requestApi;
            if (httpMethod.equals(HttpMethod.NONE)) {
                throw new ApiException(METHOD_NOT_ALLOWED); // HTTP Method 가 확인되지 않았을 경우 > 실패
            }
            List<Api> publicApiList = authorizationService.selectPublicApiList();
            boolean isPublicApi = publicApiList.stream()
                    .anyMatch(api -> apiAntPathMatcher.match(api.getUrl(), requestURI) && api.getHttpMethod().equals(httpMethod));
            if (isPublicApi) {
                return new AuthorizationDecision(true); // 권한이 필요없는 API 일 경우 > OK
            }

            requestApi = authorizationService.selectNonPublicApiList().stream()
                    .filter(api -> apiAntPathMatcher.match(api.getUrl(), requestURI))
                    .filter(api -> api.getHttpMethod().equals(httpMethod))
                    .findFirst().orElseThrow(() -> new ApiException(API_NOT_FOUND)); //확인되지 않는 API > 실패

            if (authentication == null) {
                throw new TokenValidFailedException(TOKEN_VALIDATION_FAILED);
            }
            if (isAnonymous(authentication)) {
                /**
                 *  JwtTokenAuthenticationFilter 에서 Authentication 의 사용자 정보가 Token 검사를 통과하지 못해 등록되지 않음.
                 *  WebSecurityConfig 에서 Anonymous 가 disable 되지 않았으며, 이후 API 권한 확인은 익명유저를 제외한다.
                 */
                throw new TokenValidFailedException(TOKEN_VALIDATION_FAILED); // Token 을 통한 authentication 객체가 익명으로 처리된 경우 > 실패
            }

            ArrayList<Role> roleList = new ArrayList<>(authorizationService.selectRoleListByApiPk(requestApi.getId()));
            if (roleList.isEmpty()) {
                throw new ApiException(API_NOT_AUTHORIZED); // 해당 API 의 접근 권한이 확인되지 않는 경우 > 실패
            }


            SecurityUser securityUser = (SecurityUser) authentication.getPrincipal();
            ArrayList<Role> authorities = new ArrayList<>(securityUser.getRoleList());
            if (roleService.hasSystemAdminRole(authorities)) {
                return new AuthorizationDecision(true); // 요청자가 시스템 관리자 권한을 가졌을 경우 > 성공
            }
            roleList.retainAll(authorities);
            if (roleList.isEmpty()) {
                throw new ApiException(USER_FORBIDDEN); // 해당 API 의 접근권한에 부합하지 않음 > 실패
            }
            return new AuthorizationDecision(true); // 위 조건을 모두 통과한 경우 > 성공

        } catch (TokenValidFailedException | ApiException e) { // 인증 실패 처리 > Http 실패 로그 등록
            ErrorCode errorCode = e.getErrorCode();
            int statusCode = errorCode.getStatus().value();
            boolean methodHasPayload = HttpMethod.hasPayload(httpMethod);
            String requestIp = HttpHelper.getClientIP(request);

            String requestContentType = request.getContentType();
            String requestPayload = null;
            if (!Strings.isBlank(requestContentType)) {
                if (methodHasPayload && requestContentType.startsWith(CONTENT_TYPE_JSON)) {
                    requestPayload = HttpHelper.getRequestPayload(request);
                }
            }
            ApiErrorResponse apiErrorResponse = ApiErrorResponse.of(e.getErrorCode(), e);
            String token = request.getHeader(JsonWebTokenProvider.BEARER_TOKEN_HEADER);
            auditService.insertAuditLog(httpMethod, statusCode,
                    requestURI, requestIp, userAgent,
                    requestContentType, requestPayload,
                    CONTENT_TYPE_JSON, apiErrorResponse.toString(), token);
            throw e;
        }
    }
}