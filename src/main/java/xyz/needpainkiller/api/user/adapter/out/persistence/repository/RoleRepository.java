package xyz.needpainkiller.api.user.adapter.out.persistence.repository;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import xyz.needpainkiller.api.user.adapter.out.persistence.entity.RoleEntity;
import xyz.needpainkiller.api.user.domain.model.Role;

import java.util.List;

public interface RoleRepository extends JpaRepository<RoleEntity, Long>, JpaSpecificationExecutor<Role> {


    List<RoleEntity> findAll();


    List<RoleEntity> findByIdIn(List<Long> idList);
}