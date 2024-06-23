package xyz.needpainkiller.api.user.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import xyz.needpainkiller.api.user_hex.domain.model.UserRoleMap;
import xyz.needpainkiller.api.user_hex.domain.model.UserRoleMapId;

import java.util.List;

public interface UserRoleMapRepo extends JpaRepository<UserRoleMap, UserRoleMapId> {
    List<UserRoleMap> findByUserPk(Long userPk);

    List<UserRoleMap> findByUserPkIn(List<Long> userPkList);

    List<UserRoleMap> findByRolePk(Long rolePk);

    List<UserRoleMap> findByRolePkIn(List<Long> rolePkList);
}