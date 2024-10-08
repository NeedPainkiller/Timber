package xyz.needpainkiller.api.user.adapter.out.persistence.repository;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import xyz.needpainkiller.api.user.domain.model.Role;

import java.util.List;

public interface RoleRepository extends JpaRepository<Role, Long>, JpaSpecificationExecutor<Role> {

    @Cacheable(value = "RoleList", key = "'findAll'")
    List<Role> findAll();

    @Cacheable(value = "RoleList", key = "'findByIdIn-' + #p0.hashCode()", condition = "#p0!=null")
    List<Role> findByIdIn(List<Long> idList);
}