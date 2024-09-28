package xyz.needpainkiller.api.user.adapter.out.persistence.repository;

import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import xyz.needpainkiller.api.user.domain.model.User;

import java.util.List;

public interface UserRepo extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {
    List<User> findAll();

    List<User> findAllByIdIn(List<Long> idList);


    User findUserById(@NotNull Long userPk);

    List<User> findUserByUserId(String userId);
}