package xyz.needpainkiller.api.tenant;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import xyz.needpainkiller.api.authentication.AuthenticationService;
import xyz.needpainkiller.api.authentication.AuthorizationService;
import xyz.needpainkiller.api.authentication.model.Api;
import xyz.needpainkiller.api.authentication.model.Division;
import xyz.needpainkiller.api.team.model.Team;
import xyz.needpainkiller.api.tenant.dto.TenantRequests;
import xyz.needpainkiller.api.tenant.error.TenantException;
import xyz.needpainkiller.api.tenant.model.Tenant;
import xyz.needpainkiller.api.user.RoleService;
import xyz.needpainkiller.api.user.UserService;
import xyz.needpainkiller.api.user.dto.UserProfile;
import xyz.needpainkiller.api.user.model.Role;
import xyz.needpainkiller.api.user.model.User;
import xyz.needpainkiller.common.controller.CommonController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.springframework.http.ResponseEntity.ok;
import static org.springframework.http.ResponseEntity.status;
import static xyz.needpainkiller.api.tenant.error.TenantErrorCode.TENANT_CONFLICT;

@Slf4j
@RestController
@RequiredArgsConstructor
public class TenantController extends CommonController implements TenantApi {
    @Autowired
    private TenantService tenantService;
    @Autowired
    private AuthenticationService authenticationService;
    @Autowired
    private AuthorizationService authorizationService;
    @Autowired
    private UserService userService;
    @Autowired
    private RoleService roleService;

    @Override
    public ResponseEntity<Map<String, Object>> selectPublicTenantList(String userId, HttpServletRequest request) {
        Map<String, Object> model = new HashMap<>();
        List<Tenant> tenantList = tenantService.selectPublicTenantList();
        if (userId != null && !userId.isEmpty()) {
            List<User> userList = userService.selectUserListByIdLike(userId);
            List<Long> filteredTenantPkList = userList.stream().map(User::getTenantPk).toList();
            tenantList = tenantList.stream().filter(tenant -> filteredTenantPkList.contains(tenant.getId())).toList();
        }
        model.put(KEY_LIST, tenantList);
        return ok(model);
    }

    @Override
    public ResponseEntity<Map<String, Object>> selectSwitchableTenantList(HttpServletRequest request) {
        Map<String, Object> model = new HashMap<>();
        List<Long> switchableTenantPkList = authenticationService.getTenantPkListByToken(request);
        List<Tenant> tenantList = tenantService.selectTenantList();
        List<Tenant> switchableTenantList = tenantList.stream().filter(tenant -> switchableTenantPkList.contains(tenant.getId())).toList();
        model.put(KEY_LIST, switchableTenantList);
        return ok(model);
    }

    @Override
    public ResponseEntity<Map<String, Object>> selectTenantList(HttpServletRequest request) {
        Map<String, Object> model = new HashMap<>();

        Long tenantPk = authenticationService.getTenantPkByToken(request);
        List<Tenant> tenantList = tenantService.selectTenantList();
        List<Role> authority = authenticationService.getRoleListByToken(request);
        if (!roleService.hasSystemAdminRole(authority)) {
            tenantList = tenantList.stream().filter(tenant -> tenant.filterByTenant(tenantPk)).toList();
        }
        model.put(KEY_LIST, tenantList);
        return ok(model);
    }

    @Override
    public ResponseEntity<Map<String, Object>> selectTenant(Long tenantPk, HttpServletRequest request) {
        Map<String, Object> model = new HashMap<>();
        Tenant tenant = tenantService.selectTenant(tenantPk);
        model.put(KEY_RESULT, tenant);
        return ok(model);
    }

    @Override
    public ResponseEntity<Map<String, Object>> createTenant(TenantRequests.CreateTenantRequest param, HttpServletRequest request) {
        Map<String, Object> model = new HashMap<>();
        List<Role> authority = authenticationService.getRoleListByToken(request);
        if (!roleService.hasSystemAdminRole(authority)) {
            return status(HttpStatus.FORBIDDEN).body(model);
        }
        User requester = authenticationService.getUserByToken(request);
        Tenant tenant = tenantService.createTenant(param, requester);

        model.put(KEY_RESULT, tenant);
        return ok(model);
    }

    @Override
    public ResponseEntity<Map<String, Object>> updateTenant(Long tenantPk, TenantRequests.UpdateTenantRequest param, HttpServletRequest request) {
        Map<String, Object> model = new HashMap<>();

        List<Role> authority = authenticationService.getRoleListByToken(request);
        if (!roleService.hasSystemAdminRole(authority)) {
            Long userTenantPk = authenticationService.getTenantPkByToken(request);
            if (!tenantPk.equals(userTenantPk)) {
                throw new TenantException(TENANT_CONFLICT);
            }
        }

        User requester = authenticationService.getUserByToken(request);
        Tenant tenant = tenantService.updateTenant(tenantPk, param, requester);

        model.put(KEY_RESULT, tenant);
        return ok(model);
    }

    @Override
    public ResponseEntity<Map<String, Object>> deleteTenant(Long tenantPk, HttpServletRequest request) {
        Map<String, Object> model = new HashMap<>();

        List<Role> authority = authenticationService.getRoleListByToken(request);
        if (!roleService.hasSystemAdminRole(authority)) {
            Long userTenantPk = authenticationService.getTenantPkByToken(request);
            if (!tenantPk.equals(userTenantPk)) {
                throw new TenantException(TENANT_CONFLICT);
            }
        }
        User requester = authenticationService.getUserByToken(request);
        tenantService.deleteTenant(tenantPk, requester);

        return status(HttpStatus.NO_CONTENT).body(model);
    }

    @Override
    public ResponseEntity<Map<String, Object>> switchTenant(Long tenantPk, HttpServletRequest request, HttpServletResponse response) {
        Map<String, Object> model = new HashMap<>();
        User requester = authenticationService.getUserByToken(request);

        List<Long> switchableTenantPkList = authenticationService.getTenantPkListByToken(request);
        if (switchableTenantPkList.stream().noneMatch(pk -> pk.equals(tenantPk))) {
            return status(HttpStatus.FORBIDDEN).body(model);
        }

        Long userPk = requester.getId();
        requester = userService.selectUser(userPk);
        try {
            UserProfile userProfile = userService.selectUserProfile(requester);
            model.put(KEY_USER, requester);
            Team team = userProfile.getTeam();
            model.put(KEY_TEAM, team);
            List<Role> roleList = userProfile.getRoleList();
            model.put(KEY_ROLE_LIST, roleList);
            List<Division> divisionList = authorizationService.selectDivisionByRoleList(roleList);
            model.put(KEY_MENU_LIST, divisionList);
            List<Api> apiList = authorizationService.selectApiListByRoleList(roleList);
            model.put(KEY_API_LIST, apiList);
            List<Api> publicApiList = authorizationService.selectPublicApiList();
            model.put(KEY_PUBLIC_API_LIST, publicApiList);
        } finally {
            model.put(KEY_TOKEN, authenticationService.createToken(request, response, requester));
        }
        return ok(model);
    }

}