package xyz.needpainkiller.api.authentication;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import xyz.needpainkiller.api.team.model.Team;
import xyz.needpainkiller.tenant.domain.error.TenantException;
import xyz.needpainkiller.api.user.RoleService;
import xyz.needpainkiller.api.user.UserService;
import xyz.needpainkiller.api.user.dto.UserProfile;
import xyz.needpainkiller.api.user.error.RoleException;
import xyz.needpainkiller.api.user.error.UserException;
import xyz.needpainkiller.api.user.model.Role;
import xyz.needpainkiller.api.user.model.SecurityUser;
import xyz.needpainkiller.api.user.model.User;
import xyz.needpainkiller.api.user.model.UserStatusType;
import xyz.needpainkiller.lib.exceptions.ErrorCode;
import xyz.needpainkiller.lib.security.error.TokenValidFailedException;
import xyz.needpainkiller.lib.security.provider.JsonWebTokenProvider;

import java.util.List;

import static xyz.needpainkiller.tenant.domain.error.TenantErrorCode.TENANT_SEARCH_EMPTY;
import static xyz.needpainkiller.lib.exceptions.CommonErrorCode.*;

@Slf4j
@Service
public class AuthenticationService implements UserDetailsService {

    @Autowired
    private UserService userService;
    @Autowired
    private RoleService roleService;
    @Autowired
//    @Qualifier("headerJsonWebTokenProvider")
    private JsonWebTokenProvider jsonWebTokenProvider;

    public String createToken(HttpServletRequest request, HttpServletResponse response, User user, List<Role> roleList, Team team) {
        return jsonWebTokenProvider.createToken(request, response, user, roleList, team);
    }


    public boolean isExpireSoon(HttpServletRequest request) {
        return jsonWebTokenProvider.isExpireSoon(request);
    }


    public String refreshToken(HttpServletRequest request, HttpServletResponse response) {
        jsonWebTokenProvider.validateToken(request);
        Authentication authentication = getAuthentication(request);
        SecurityUser securityUser = (SecurityUser) authentication.getPrincipal();
        User user = securityUser.getUser();
        UserProfile userProfile = userService.selectUserProfile(user);
        List<Role> roleList = userProfile.getRoleList();
        Team team = userProfile.getTeam();
        return createToken(request, response, user, roleList, team);
    }


    public String getTokenNonValidate(HttpServletRequest request) {
        return jsonWebTokenProvider.resolveToken(request);
    }


    public void validateToken(HttpServletRequest request) {
        jsonWebTokenProvider.validateToken(request);
    }


    public void validateToken(String token) {
        jsonWebTokenProvider.validateToken(token);
    }


    public User getUserByToken(HttpServletRequest request) {
        String token = jsonWebTokenProvider.resolveToken(request);
        return getUserByToken(token);
    }


    public User getUserByToken(String token) {
        jsonWebTokenProvider.validateToken(token);
        Authentication authentication = getAuthentication(token);
        SecurityUser securityUser = (SecurityUser) authentication.getPrincipal();
        return securityUser.getUser();
    }


    public Long getTenantPkByToken(HttpServletRequest request) {
        String token = jsonWebTokenProvider.resolveToken(request);
        return getTenantPkByToken(token);
    }


    public Long getTenantPkByToken(String token) {
        jsonWebTokenProvider.validateToken(token);
        Long tenantPk;
        try {
            tenantPk = jsonWebTokenProvider.getTenantPk(token);
        } catch (TokenValidFailedException e) {
            tenantPk = getUserByToken(token).getTenantPk();
        }
        return tenantPk;
    }


    public Long getUserPkByToken(HttpServletRequest request) {
        String token = jsonWebTokenProvider.resolveToken(request);
        return getUserPkByToken(token);
    }


    public Long getUserPkByToken(String token) {
        jsonWebTokenProvider.validateToken(token);
        Long userPk;
        try {
            userPk = jsonWebTokenProvider.getUserPk(token);
        } catch (TokenValidFailedException | UserException e) {
            userPk = getUserByToken(token).getId();
        }
        return userPk;
    }


    public List<Role> getRoleListByToken(HttpServletRequest request) {
        String token = jsonWebTokenProvider.resolveToken(request);
        return getRoleListByToken(token);
    }


    public List<Role> getRoleListByToken(String token) {
        jsonWebTokenProvider.validateToken(token);
        List<String> authorityList = jsonWebTokenProvider.getRole(token);
        return roleService.selectRolesByNameList(authorityList);
    }


    public List<Long> getTenantPkListByToken(HttpServletRequest request) {
        String token = jsonWebTokenProvider.resolveToken(request);
        return getTenantPkListByToken(token);
    }


    public List<Long> getTenantPkListByToken(String token) {
        jsonWebTokenProvider.validateToken(token);
        List<Role> authorityList = getRoleListByToken(token);
        return authorityList.stream().map(Role::getTenantPk).toList();
    }


    public void logout(HttpServletRequest request, HttpServletResponse response) {
        jsonWebTokenProvider.expireToken(request, response);
    }


    public Authentication getAuthentication(HttpServletRequest request) {
        return getAuthentication(jsonWebTokenProvider.resolveToken(request));
    }


    public Authentication getAuthentication(String token) {
        jsonWebTokenProvider.validateToken(token);
        Long tenantPk = jsonWebTokenProvider.getTenantPk(token);
        String userId = jsonWebTokenProvider.getUserId(token);

        String loginData = String.format("%d/%s", tenantPk, userId);

        SecurityUser securityUser = loadUserByUsername(loginData);
        return new UsernamePasswordAuthenticationToken(securityUser, "", securityUser.getAuthorities());
    }


    public ErrorCode getAuthenticationExceptionType(User user, AuthenticationException e) {
        log.info("getAuthenticationExceptionType : {}", e.getMessage());
        if (user == null) {
            return USER_NOT_EXIST;
        }
        if (e instanceof BadCredentialsException) {
            return LOGIN_PASSWORD_NOT_MATCH;
        } else if (e instanceof InternalAuthenticationServiceException) {
            return USER_NOT_EXIST;
        } else if (e instanceof DisabledException) {
            UserStatusType userStatusType = user.getUserStatus();
            return switch (userStatusType) {
                case NOT_VERIFIED, WITHDRAWAL -> VERIFICATION_NOT_VERIFIED;
                case LOCKED -> USER_LOCKED;
                default -> USER_DISABLED;
            };
        } else if (e instanceof CredentialsExpiredException) {
            return PASSWORD_EXPIRED;
        }
        return LOGIN_FAILED_UNKNOWN;
    }

    @Override
    public SecurityUser loadUserByUsername(String userDataString) throws UsernameNotFoundException, UserException, TenantException {
        if (userDataString == null || userDataString.isEmpty()) {
            throw new UserException(LOGIN_REQUEST_MISSING);
        }
        try {
            int index = userDataString.indexOf("/");
            String tenant = userDataString.substring(0, index);
            Long tenantPk = Long.parseLong(tenant);
            String userId = userDataString.substring(index + 1);

            log.info("tenantPk: {}, userId: {}", tenantPk, userId);

            User user = userService.selectUserByUserId(tenantPk, userId);
            log.info("user: {}", user);
            List<Role> roleList = roleService.selectRolesByUser(user);
            log.info("roleList: {}", roleList);
            return new SecurityUser(user, roleList);
        } catch (NumberFormatException e) {
            log.error(e.getMessage());
            throw new TenantException(TENANT_SEARCH_EMPTY);
        } catch (UserException | RoleException e) {
            log.error(e.getMessage());
            throw new UserException(USER_NOT_EXIST);
        }
    }
}
