package xyz.needpainkiller.api.user_hex.application.port.in;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Caching;
import org.springframework.transaction.annotation.Transactional;
import xyz.needpainkiller.api.user_hex.adapter.in.web.data.UserRequests;
import xyz.needpainkiller.api.user_hex.domain.error.UserException;
import xyz.needpainkiller.api.user_hex.domain.model.Role;
import xyz.needpainkiller.api.user_hex.domain.model.User;

import java.util.List;

public interface ManageUserUseCase {
    void increaseLoginFailedCnt(Long userPk);

    void updateLastLoginDate(Long userPk);

    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "User", allEntries = true),
            @CacheEvict(value = "UserList", allEntries = true),
            @CacheEvict(value = "UserRole", allEntries = true)
    })
    User createUser(UserRequests.UpsertUserRequest param, List<Role> roleList, User requester) throws UserException;

    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "User", allEntries = true),
            @CacheEvict(value = "UserList", allEntries = true),
            @CacheEvict(value = "UserRole", allEntries = true)
    })
    User updateUser(Long userPk, UserRequests.UpsertUserRequest param, List<Role> roleList, User requester);

    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "User", allEntries = true),
            @CacheEvict(value = "UserList", allEntries = true),
            @CacheEvict(value = "UserRole", allEntries = true)
    })
    void updatePassword(Long userPk, Long requesterPk, String userPwd);

    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "User", allEntries = true),
            @CacheEvict(value = "UserList", allEntries = true),
            @CacheEvict(value = "UserRole", allEntries = true)
    })
    void deleteUser(Long tenantPk, Long userPk, User requester);

    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "User", allEntries = true),
            @CacheEvict(value = "UserList", allEntries = true),
            @CacheEvict(value = "UserRole", allEntries = true)
    })
    void enableUser(Long userPk);
}
