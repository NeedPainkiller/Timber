package xyz.needpainkiller.api.authentication;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.RestController;
import xyz.needpainkiller.api.audit.AuditService;
import xyz.needpainkiller.api.authentication.dto.AuthenticationRequests;
import xyz.needpainkiller.api.authentication.error.LoginException;
import xyz.needpainkiller.api.authentication.model.Api;
import xyz.needpainkiller.api.authentication.model.Division;
import xyz.needpainkiller.api.team.model.Team;
import xyz.needpainkiller.api.tenant.application.port.in.FindTenantUseCase;
import xyz.needpainkiller.api.tenant.domain.model.Tenant;
import xyz.needpainkiller.api.user.UserService;
import xyz.needpainkiller.api.user.dto.UserProfile;
import xyz.needpainkiller.api.user_hex.domain.model.Role;
import xyz.needpainkiller.api.user.model.SecurityUser;
import xyz.needpainkiller.api.user_hex.domain.model.User;
import xyz.needpainkiller.common.controller.CommonController;
import xyz.needpainkiller.lib.exceptions.ErrorCode;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.springframework.http.ResponseEntity.ok;
import static xyz.needpainkiller.lib.exceptions.CommonErrorCode.LOGIN_REQUEST_MISSING;

@Slf4j
@RestController
@RequiredArgsConstructor
public class AuthenticationController extends CommonController implements AuthenticationApi {

    @Autowired
    private AuditService auditService;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private AuthenticationService authenticationService;
    @Autowired
    private AuthorizationService authorizationService;
    @Autowired
    private UserService userService;
    @Autowired
    private FindTenantUseCase findTenantUseCase;

    public ResponseEntity<Map<String, Object>> login(AuthenticationRequests.LoginRequest param, HttpServletRequest request, HttpServletResponse response) throws LoginException {

        Long tenantPk = param.getTenantPk();
        Tenant tenant;

        if (tenantPk != null) {
            tenant = findTenantUseCase.selectTenant(tenantPk);
        } else {
            tenant = findTenantUseCase.selectDefatultTenant();
        }
        String userId = param.getUserId();
        String userPwd = param.getUserPwd();
        if (Strings.isBlank(userId) || Strings.isBlank(userPwd)) {
            throw new LoginException(LOGIN_REQUEST_MISSING);
        }
        userId = userId.trim();
        userPwd = userPwd.trim();

        SecurityUser securityUser;
        User user = userService.selectUserByUserId(tenant, userId);

        try {
            String loginData = String.format("%d/%s", tenantPk, userId);
            UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(loginData, userPwd);
            Authentication authentication = authenticationManager.authenticate(token);
            securityUser = (SecurityUser) authentication.getPrincipal();
        } catch (AuthenticationException e) {
            userService.increaseLoginFailedCnt(user.getId());
            ErrorCode errorCode = authenticationService.getAuthenticationExceptionType(user, e);
            throw new LoginException(errorCode, e.getMessage());
        }
        user = securityUser.getUser();
        Map<String, Object> model = new HashMap<>();
         Team team = null;
        List<Role> roleList = null;
        try {
            UserProfile userProfile = userService.selectUserProfile(user);
            model.put(KEY_USER, user);
            team = userProfile.getTeam();
            model.put(KEY_TEAM, team);
            roleList = userProfile.getRoleList();
            model.put(KEY_ROLE_LIST, roleList);
            List<Division> divisionList = authorizationService.selectDivisionByRoleList(roleList);
            model.put(KEY_MENU_LIST, divisionList);
            List<Api> apiList = authorizationService.selectApiListByRoleList(roleList);
            model.put(KEY_API_LIST, apiList);
            List<Api> publicApiList = authorizationService.selectPublicApiList();
            model.put(KEY_PUBLIC_API_LIST, publicApiList);

            auditService.insertLoginAuditLog(request, userProfile);
        } finally {
            model.put(KEY_TOKEN, authenticationService.createToken(request, response, user , roleList, team));
            userService.updateLastLoginDate(user.getId());
        }
        return ok(model);
    }

    public ResponseEntity<Map<String, Object>> logout(HttpServletRequest request, HttpServletResponse response) {
        authenticationService.logout(request, response);
        return ok().build();
    }

    public ResponseEntity<Map<String, Object>> refreshToken(HttpServletRequest request, HttpServletResponse response) {
        String refreshToken = authenticationService.refreshToken(request, response);
        Map<String, Object> model = new HashMap<>();
        model.put(KEY_TOKEN, refreshToken);
        return ok(model);
    }

    public ResponseEntity<Map<String, Object>> validateToken(HttpServletRequest request) {
        authenticationService.validateToken(request);
        return ok().build();
    }
}
