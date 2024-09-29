package xyz.needpainkiller.api.user.adapter.out.persistence.mapper;

import org.mapstruct.Mapper;
import xyz.needpainkiller.api.user.adapter.out.persistence.entity.RoleEntity;
import xyz.needpainkiller.api.user.adapter.out.persistence.entity.UserEntity;
import xyz.needpainkiller.api.user.domain.model.Role;
import xyz.needpainkiller.api.user.domain.model.User;


/**
 * 권한 매퍼
 * - Persistence -> Domain / Domain -> Persistence
 *
 * @author needpainkiller6512
 * @version 1.0
 */
@Mapper(componentModel = "spring")
public interface RolePersistenceMapper {
    /**
     * 테넌트 Entity -> Domain
     *
     * @param role 권한
     * @return RoleEntity 권한 Entity
     */
    RoleEntity toRoleEntity(Role role);

    /**
     * 테넌트 Entity -> Domain
     *
     * @param roleEntity 권한 Entity
     * @return Role 권한
     */
    Role toRole(RoleEntity roleEntity);
}
