package xyz.needpainkiller.api.user.adapter.out.persistence;

import jakarta.persistence.LockModeType;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.Lock;
import xyz.needpainkiller.api.tenant.adapter.out.persistence.entity.TenantEntity;
import xyz.needpainkiller.api.tenant.adapter.out.persistence.mapper.TenantPersistenceMapper;
import xyz.needpainkiller.api.tenant.application.port.out.TenantOutputPort;
import xyz.needpainkiller.api.tenant.domain.error.TenantErrorCode;
import xyz.needpainkiller.api.tenant.domain.error.TenantException;
import xyz.needpainkiller.api.tenant.domain.model.Tenant;
import xyz.needpainkiller.api.user.adapter.out.persistence.entity.UserEntity;
import xyz.needpainkiller.api.user.adapter.out.persistence.mapper.UserPersistenceMapper;
import xyz.needpainkiller.api.user.adapter.out.persistence.repository.UserRepository;
import xyz.needpainkiller.api.user.application.port.out.UserOutputPort;
import xyz.needpainkiller.api.user.domain.error.UserErrorCode;
import xyz.needpainkiller.api.user.domain.error.UserException;
import xyz.needpainkiller.api.user.domain.model.User;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static xyz.needpainkiller.api.tenant.domain.error.TenantErrorCode.TENANT_DEFAULT_CAN_NOT_DELETE;
import static xyz.needpainkiller.api.tenant.domain.error.TenantErrorCode.TENANT_DELETED;

@RequiredArgsConstructor
public class UserPersistenceAdapter implements UserOutputPort {

    /**
     * 테넌트 Repository
     */
    private final UserRepository userRepository;

    /**
     * 테넌트 Mapper ( Persistence -> Domain / Domain -> Persistence )
     */
    private final UserPersistenceMapper userPersistenceMapper;

    @Override
    public Optional<Tenant> selectTenant(Long tenantPk) {
        return tenantRepository.findById(tenantPk).map(tenantPersistenceMapper::toTenant);
    }

    @Override
    public Optional<Tenant> selectTenantAvailable(Long tenantPk) {
        return tenantRepository.findById(tenantPk).filter(TenantEntity::isUseYn).map(tenantPersistenceMapper::toTenant);
    }

    @Override
    public List<Tenant> selectTenantList() {
        return tenantRepository.findAll().stream().map(tenantPersistenceMapper::toTenant).toList();
    }

    @Override
    public List<Long> selectTenantPkList() {
        return tenantRepository.findAll().stream().map(TenantEntity::getId).toList();
    }

    @Override
    public Map<Long, Tenant> selectTenantMap() {
        return tenantRepository.findAll().stream().collect(Collectors.toMap(TenantEntity::getId, tenantPersistenceMapper::toTenant));
    }


    @Override
    public List<User> findAll() {
        return List.of();
    }

    @Override
    public List<User> findAllByIdIn(List<Long> idList) {
        return List.of();
    }

    @Override
    public Optional<User> findUserById(@NotNull Long userPk) {
        return Optional.empty();
    }

    @Override
    public List<User> findUserByUserId(@NotNull String userId) {
        return userRepository.findUserByUserId(userId).stream().map(userPersistenceMapper::toUser).toList();
    }

    @Override
    public User create(User user) throws UserException {
        /*
        * 유저 등록조건 사전 확인 필요
        * */
        
        UserEntity userEntity = userPersistenceMapper.toUserEntity(user);
        userRepository.save(userEntity);
        return userPersistenceMapper.toUser(userEntity);
    }

    @Override
    @Lock(value = LockModeType.PESSIMISTIC_WRITE)
    public User update(User user) throws UserException {
        Long userPk = user.getId();
        Optional<UserEntity> userEntity = userRepository.findById(userPk);
        if (userEntity.isEmpty()) {
            throw new TenantException(UserErrorCode.USER_NOT_EXIST);
        }

        UserEntity userEntityToCheck = userEntity.get();
        if (!userEntityToCheck.isUseYn()) {
            throw new TenantException(UserErrorCode.USER_DELETED);
        }

        UserEntity updatedUserEntity = userPersistenceMapper.toUserEntity(user);
        userRepository.save(updatedUserEntity);
        return userPersistenceMapper.toUser(updatedUserEntity);
    }

    @Override
    @Lock(value = LockModeType.PESSIMISTIC_WRITE)
    public User delete(Long userPk) throws UserException {
        Optional<UserEntity> userEntity = userRepository.findById(userPk);
        if (userEntity.isEmpty()) {
            throw new UserException(UserErrorCode.USER_NOT_EXIST);
        }
        UserEntity userEntityToDelete = userEntity.get();
        if (!userEntityToDelete.isUseYn()) {
            throw new UserException(UserErrorCode.USER_DELETED);
        }
        userEntityToDelete = userRepository.save(userEntityToDelete);
        return userPersistenceMapper.toUser(userEntityToDelete);
    }
}
