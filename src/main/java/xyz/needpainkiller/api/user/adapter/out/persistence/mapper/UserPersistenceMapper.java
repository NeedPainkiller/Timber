package xyz.needpainkiller.api.user.adapter.out.persistence.mapper;

import org.mapstruct.Mapper;
import xyz.needpainkiller.api.tenant.adapter.out.persistence.entity.TenantEntity;
import xyz.needpainkiller.api.tenant.domain.model.Tenant;
import xyz.needpainkiller.api.user.adapter.out.persistence.entity.UserEntity;
import xyz.needpainkiller.api.user.domain.model.User;


/**
 * 테넌트 매퍼
 * - Persistence -> Domain / Domain -> Persistence
 *
 * @author needpainkiller6512
 * @version 1.0
 */
@Mapper(componentModel = "spring")
public interface UserPersistenceMapper {
    /**
     * 테넌트 Entity -> Domain
     *
     * @param user 유저
     * @return TenantEntity 테넌트 Entity
     */
    UserEntity toUserEntity(User user);

    /**
     * 테넌트 Entity -> Domain
     *
     * @param userEntity 유저 Entity
     * @return User 유저
     */
    User toUser(UserEntity userEntity);
}
