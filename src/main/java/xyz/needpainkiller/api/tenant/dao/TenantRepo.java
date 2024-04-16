package xyz.needpainkiller.api.tenant.dao;

import org.jetbrains.annotations.NotNull;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;
import xyz.needpainkiller.api.tenant.model.TenantEntity;

import java.util.List;
import java.util.Optional;

public interface TenantRepo extends JpaRepository<TenantEntity, Long> {
    @Cacheable(value = "TenantList", key = "'findAll'")
    List<TenantEntity> findAll();

    @Override
    Optional<TenantEntity> findById(@NotNull Long teamPk);
}
