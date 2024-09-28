package xyz.needpainkiller.api.user.application.port.in;

import xyz.needpainkiller.api.user.adapter.in.web.data.RoleRequests;
import xyz.needpainkiller.api.user.domain.model.Role;
import xyz.needpainkiller.api.user.domain.model.User;
import xyz.needpainkiller.api.user.domain.model.UserRoleMap;
import xyz.needpainkiller.common.dto.SearchCollectionResult;

import java.util.List;

public interface FindRoleUseCase {
    boolean isRoleExist(Long rolePk);

    boolean isRoleExist(Long tenantPk, String roleNm);

    List<Role> selectAll();

    Role selectRoleByRolePk(Long rolePk);

    Role selectRoleByRoleNm(String roleNm);

    SearchCollectionResult<Role> selectRoleList(RoleRequests.SearchRoleRequest param);

    List<UserRoleMap> selectUserRoleMap();

    List<UserRoleMap> selectUserRoleMap(List<Long> userPkList);

    List<Long> selectUserPkListByRolePk(Long rolePk);

    List<Long> selectUserPkListByRolePkList(List<Long> rolePkList);

    List<Role> selectRolesByUser(User user);

    List<Role> selectRolesByUser(Long userPk);

    List<Role> selectRolesByPkList(List<Long> rolePkList);

    List<Role> selectRolesByNameList(List<String> roleNmList);

    List<String> selectAuthorityByUser(User user);

    List<String> selectAuthorityByUser(Long userPk);

    boolean hasSystemAdminRole(List<Role> roleList);

    boolean hasAdminRole(List<Role> roleList);

    boolean hasEditableRole(List<Role> roleList);

    boolean isEditableAuthority(User user, Long ownerUserPk);

    boolean isEditableAuthority(User user, List<Role> userRoles, Long ownerUserPk);

    void checkRequestRoleAuthority(List<Role> requestRoleList, List<Role> authority);
}
