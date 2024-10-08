package xyz.needpainkiller.api.user.adapter.out.persistence.repository;

import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import xyz.needpainkiller.api.user.adapter.out.persistence.entity.UserEntity;
import xyz.needpainkiller.api.user.domain.model.User;

import java.util.List;

public interface UserRepository extends JpaRepository<UserEntity, Long>, JpaSpecificationExecutor<UserEntity> {
    List<UserEntity> findAll();

    List<UserEntity> findAllByIdIn(List<Long> idList);


    UserEntity findUserById(@NotNull Long userPk);

    List<UserEntity> findUserByUserId(String userId);
}