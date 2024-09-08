package xyz.needpainkiller.api.user_hex.application.port.in;

import org.springframework.cache.annotation.Cacheable;
import xyz.needpainkiller.api.user_hex.adapter.in.web.data.RoleRequests;
import xyz.needpainkiller.api.user_hex.domain.model.Role;
import xyz.needpainkiller.api.user_hex.domain.model.User;
import xyz.needpainkiller.api.user_hex.domain.model.UserRoleMap;
import xyz.needpainkiller.common.dto.SearchCollectionResult;

import java.util.List;

public interface FindRoleUseCase {
    boolean isRoleExist(Long rolePk);

    boolean isRoleExist(Long tenantPk, String roleNm);

    @Cacheable(value = "RoleList", key = "'selectAll'")
    List<Role> selectAll();

    @Cacheable(value = "Role", key = "'selectRoleByRolePk-' + #p0")
    Role selectRoleByRolePk(Long rolePk);

    @Cacheable(value = "Role", key = "'selectRoleByRoleNm-' + #p0")
    Role selectRoleByRoleNm(String roleNm);

    SearchCollectionResult<Role> selectRoleList(RoleRequests.SearchRoleRequest param);

    List<UserRoleMap> selectUserRoleMap();

    List<UserRoleMap> selectUserRoleMap(List<Long> userPkList);

    @Cacheable(value = "UserRole", key = "'selectUserPkListByRolePk-' + #p0")
    List<Long> selectUserPkListByRolePk(Long rolePk);

    @Cacheable(value = "UserRole", key = "'selectUserPkListByRolePkList-' + #p0.hashCode()")
    List<Long> selectUserPkListByRolePkList(List<Long> rolePkList);

    @Cacheable(value = "RoleList", key = "'selectRolesByUser-' + #p0.getId()")
    List<Role> selectRolesByUser(User user);

    @Cacheable(value = "RoleList", key = "'selectRolesByUser-' + #p0")
    List<Role> selectRolesByUser(Long userPk);

    @Cacheable(value = "RoleList", key = "'selectRolesByPkList-' + #p0.hashCode()")
    List<Role> selectRolesByPkList(List<Long> rolePkList);

    @Cacheable(value = "RoleList", key = "'selectRolesByNameList-' + #p0.hashCode()")
    List<Role> selectRolesByNameList(List<String> roleNmList);

    @Cacheable(value = "RoleAuthorityList", key = "'selectAuthorityByUser-' + #p0.getId()")
    List<String> selectAuthorityByUser(User user);

    @Cacheable(value = "RoleAuthorityList", key = "'selectAuthorityByUser-' + #p0")
    List<String> selectAuthorityByUser(Long userPk);

    boolean hasSystemAdminRole(List<Role> roleList);

    boolean hasAdminRole(List<Role> roleList);

    boolean hasEditableRole(List<Role> roleList);

    boolean isEditableAuthority(User user, Long ownerUserPk);

    boolean isEditableAuthority(User user, List<Role> userRoles, Long ownerUserPk);

    void checkRequestRoleAuthority(List<Role> requestRoleList, List<Role> authority);
}
