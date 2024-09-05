package xyz.needpainkiller.api.user_hex.application.port.in;

import org.springframework.cache.annotation.Cacheable;
import xyz.needpainkiller.api.tenant.domain.model.Tenant;
import xyz.needpainkiller.api.user_hex.adapter.in.web.data.UserRequests;
import xyz.needpainkiller.api.user_hex.adapter.out.web.data.UserProfile;
import xyz.needpainkiller.api.user_hex.domain.error.UserException;
import xyz.needpainkiller.api.user_hex.domain.model.Role;
import xyz.needpainkiller.api.user_hex.domain.model.User;
import xyz.needpainkiller.common.dto.SearchCollectionResult;

import java.util.List;
import java.util.Map;

public interface FindUserUseCase {
    @Cacheable(value = "User", key = "'selectSystemUser'")
    User selectSystemUser();

    Long selectSystemUserPk();

    User selectUser(Long userPk) throws UserException;

    List<User> selectUserByUserId(String userId) throws UserException;

    User selectUserByUserId(Tenant tenant, String userId) throws UserException;

    User selectUserByUserId(Long tenantPk, String userId) throws UserException;

    boolean isUserIdExist(Long tenantPk, String userId);

    @Cacheable(value = "UserProfile", key = "'selectUserProfile-' + #p0", unless = "#result == null")
    UserProfile selectUserProfile(Long userPk) throws UserException;

    @Cacheable(value = "UserProfile", key = "'selectUserProfile-' + #p0.hashCode()", unless = "#result == null")
    UserProfile selectUserProfile(User user) throws UserException;

    @Cacheable(value = "UserList", key = "'selectUserList'")
    List<User> selectUserList();

    @Cacheable(value = "UserList", key = "'selectUserList-' + #p0")
    List<User> selectUserList(Long tenantPk);

    @Cacheable(value = "UserList", key = "'selectUserListByPkList-' + #p0.hashCode()")
    List<User> selectUserListByPkList(List<Long> userPkList);

    @Cacheable(value = "UserList", key = "'selectUserListByRole-' + #p0")
    List<User> selectUserListByRole(Long rolePk);

    @Cacheable(value = "UserList", key = "'selectUserListByRoleList-' + #p0.hashCode()")
    List<User> selectUserListByRoleList(List<Role> roleList);

    List<User> selectUserListByIdLike(String userId);

    @Cacheable(value = "UserList", key = "'selectUserListBySuperAdminRole'")
    List<User> selectUserListBySuperAdminRole();

    @Cacheable(value = "UserProfileList", key = "'mapUserProfileListByUserPkList-' + #p0.hashCode()")
    List<UserProfile> mapUserProfileListByUserPkList(List<Long> userPkList);

    @Cacheable(value = "UserProfileList", key = "'mapUserProfileList-' + #p0.hashCode()")
    List<UserProfile> mapUserProfileList(List<User> userList);

    Map<Long, User> selectUserMap();

    Map<Long, User> selectUserMapByPkList(List<Long> userPkList);

    SearchCollectionResult<User> selectUserList(UserRequests.SearchUserRequest param);

    SearchCollectionResult<UserProfile> selectUserProfileList(UserRequests.SearchUserRequest param);
}
