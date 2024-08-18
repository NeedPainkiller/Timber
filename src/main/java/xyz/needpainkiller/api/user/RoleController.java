package xyz.needpainkiller.api.user;

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
import xyz.needpainkiller.api.user.dto.RoleCsv;
import xyz.needpainkiller.api.user_hex.adapter.in.web.data.RoleRequests;
import xyz.needpainkiller.api.user_hex.domain.error.RoleException;
import xyz.needpainkiller.api.user_hex.domain.model.Role;
import xyz.needpainkiller.api.user_hex.domain.model.User;
import xyz.needpainkiller.common.controller.CommonController;
import xyz.needpainkiller.common.dto.SearchCollectionResult;
import xyz.needpainkiller.lib.sheet.SpreadSheetService;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.springframework.http.ResponseEntity.ok;
import static org.springframework.http.ResponseEntity.status;

@Slf4j
@RestController
@RequiredArgsConstructor
public class RoleController extends CommonController implements RoleApi {
    @Autowired
    private AuthenticationService authenticationService;
    @Autowired
    private AuthorizationService authorizationService;
    @Autowired
    private RoleService roleService;
    @Autowired
    private SpreadSheetService sheetService;

    @Override

    public ResponseEntity<Map<String, Object>> selectAllRoleList(HttpServletRequest request) throws RoleException {
        Map<String, Object> model = new HashMap<>();
        model.put(KEY_ROLE_LIST, roleService.selectAll());
        return ok(model);
    }

    @Override
    public ResponseEntity<Map<String, Object>> selectRoleList(RoleRequests.SearchRoleRequest param, HttpServletRequest request) {
        Long tenantPk = authenticationService.getTenantPkByToken(request);
        param.setTenantPk(tenantPk);
        Map<String, Object> model = new HashMap<>();
        SearchCollectionResult<Role> result = roleService.selectRoleList(param);
        model.put(KEY_LIST, result.getCollection());
        model.put(KEY_TOTAL, result.getFoundRows());
        return ok(model);
    }

    @Override
    public void downloadRoleList(RoleRequests.SearchRoleRequest param, HttpServletRequest request, HttpServletResponse response) {
        Long tenantPk = authenticationService.getTenantPkByToken(request);
        param.setTenantPk(tenantPk);
        param.setIsPagination(false);
        SearchCollectionResult<Role> result = roleService.selectRoleList(param);
        Collection<Role> roleList = result.getCollection();
        List<RoleCsv> roleCsvList = roleList.stream().map(RoleCsv::new).toList();
        sheetService.downloadExcel(RoleCsv.class, roleCsvList, response);
    }

    @Override
    public ResponseEntity<Map<String, Object>> selectRole(Long rolePk, HttpServletRequest request) {
        Map<String, Object> model = new HashMap<>();
        Role role = roleService.selectRoleByRolePk(rolePk);
        model.put(KEY_ROLE, role);
        List<Division> divisionList = authorizationService.selectDivisionByRole(role);
        model.put(KEY_MENU_LIST, divisionList);
        List<Api> apiList = authorizationService.selectApiListByRole(role);
        model.put(KEY_API_LIST, apiList);
        return ok(model);

    }

    @Override
    public ResponseEntity<Map<String, Object>> selectMyRoles(HttpServletRequest request) {
        Map<String, Object> model = new HashMap<>();
        List<Role> authority = authenticationService.getRoleListByToken(request);
        model.put(KEY_ROLE_LIST, authority);
        List<Division> divisionList = authorizationService.selectDivisionByRoleList(authority);
        model.put(KEY_MENU_LIST, divisionList);
        List<Api> apiList = authorizationService.selectApiListByRoleList(authority);
        model.put(KEY_API_LIST, apiList);
        List<Api> publicApiList = authorizationService.selectPublicApiList();
        model.put(KEY_PUBLIC_API_LIST, publicApiList);
        return ok(model);
    }

    @Override
    public ResponseEntity<Map<String, Object>> createRole(RoleRequests.UpsertRoleRequest param, HttpServletRequest request) throws RoleException {
        Map<String, Object> model = new HashMap<>();
        User requester = authenticationService.getUserByToken(request);
        Long tenantPk = authenticationService.getTenantPkByToken(request);
        param.setTenantPk(tenantPk);
        Role savedRole = roleService.createRole(param, requester);
        model.put(KEY_ROLE, savedRole);
        return status(HttpStatus.CREATED).body(model);
    }

    @Override

    public ResponseEntity<Map<String, Object>> updateRole(Long rolePk, RoleRequests.UpsertRoleRequest param, HttpServletRequest request) throws RoleException {
        Map<String, Object> model = new HashMap<>();
        User requester = authenticationService.getUserByToken(request);
        Long tenantPk = authenticationService.getTenantPkByToken(request);
        param.setTenantPk(tenantPk);
        Role savedRole = roleService.updateRole(rolePk, param, requester);
        model.put(KEY_ROLE, savedRole);
        return status(HttpStatus.CREATED).body(model);
    }

    @Override
    public ResponseEntity<Map<String, Object>> deleteRole(Long rolePk, HttpServletRequest request) throws RoleException {
        Map<String, Object> model = new HashMap<>();
        User requester = authenticationService.getUserByToken(request);
        Long tenantPk = authenticationService.getTenantPkByToken(request);
        roleService.deleteRole(tenantPk, rolePk, requester);
        return status(HttpStatus.NO_CONTENT).body(model);
    }

    @Override
    public ResponseEntity<Map<String, Object>> selectAllApiList(HttpServletRequest request) {
        Map<String, Object> model = new HashMap<>();
        List<Division> divisionList = authorizationService.selectAvailableDivision();
        model.put(KEY_MENU_LIST, divisionList);
        List<Api> apiList = authorizationService.selectAvailableApiList();
        model.put(KEY_API_LIST, apiList);
        return ok(model);
    }
}
